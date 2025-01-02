package org.upyog.adv.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.kafka.Producer;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.repository.querybuilder.AdvertisementBookingQueryBuilder;
import org.upyog.adv.repository.rowmapper.AdvertisementDraftApplicationRowMapper;
import org.upyog.adv.repository.rowmapper.AdvertisementSlotAvailabilityRowMapper;
import org.upyog.adv.repository.rowmapper.BookingCartDetailRowmapper;
import org.upyog.adv.repository.rowmapper.BookingDetailRowmapper;
import org.upyog.adv.repository.rowmapper.DocumentDetailsRowMapper;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.AdvertisementDraftDetail;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.AuditDetails;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.CartDetail;
import org.upyog.adv.web.models.DocumentDetail;
import org.upyog.adv.web.models.PersisterWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentDetail;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BookingRepositoryImpl implements BookingRepository {

	@Autowired
	private Producer producer;

	@Autowired
	private BookingConfiguration bookingConfiguration;	
	@Lazy
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private BookingDetailRowmapper bookingRowmapper;
	@Autowired
	private BookingCartDetailRowmapper cartDetailRowmapper;
	@Autowired
	private DocumentDetailsRowMapper detailsRowMapper;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private AdvertisementBookingQueryBuilder queryBuilder;
	@Autowired
	private AdvertisementSlotAvailabilityRowMapper availabilityRowMapper;
	@Autowired
	private AdvertisementDraftApplicationRowMapper draftApplicationRowMapper;
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void saveBooking(BookingRequest bookingRequest) {
		log.info("Saving Advertisement booking request data for booking no : "
				+ bookingRequest.getBookingApplication().getBookingNo());
		producer.push(bookingConfiguration.getAdvertisementBookingSaveTopic(), bookingRequest);

	}

	@Override
	public List<BookingDetail> getBookingDetails(AdvertisementSearchCriteria bookingSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getAdvertisementSearchQuery(bookingSearchCriteria, preparedStmtList);

		log.info("getBookingDetails : Final query: " + query);
		log.info("preparedStmtList :  " + preparedStmtList);
		List<BookingDetail> bookingDetails = jdbcTemplate.query(query, preparedStmtList.toArray(), bookingRowmapper);

		log.info("Fetched booking details size : " + bookingDetails.size());

		if (bookingDetails.size() == 0) {
			return bookingDetails;
		}

		HashMap<String, BookingDetail> bookingMap = bookingDetails.stream().collect(Collectors
				.toMap(BookingDetail::getBookingId, Function.identity(), (left, right) -> left, HashMap::new));
		log.info("Fetched booking details bookingMap : " + bookingMap);
		List<String> bookingIds = new ArrayList<String>();
		bookingIds.addAll(bookingMap.keySet());
		log.info("Fetched booking details bookingIds : " + bookingIds);
		List<CartDetail> cartDetails = jdbcTemplate.query(queryBuilder.getSlotDetailsQuery(bookingIds),
				bookingIds.toArray(), cartDetailRowmapper);
		cartDetails.stream().forEach(slotDetail -> {
			log.info("fetched cartDetails " + bookingMap.get(slotDetail.getBookingId()));
			bookingMap.get(slotDetail.getBookingId()).addBookingSlots(slotDetail);
		});
		log.info("Fetched booking details cartDetails : " + cartDetails);
		List<DocumentDetail> documentDetails = jdbcTemplate.query(queryBuilder.getDocumentDetailsQuery(bookingIds),
				bookingIds.toArray(), detailsRowMapper);

		documentDetails.stream().forEach(documentDetail -> {
			bookingMap.get(documentDetail.getBookingId()).addUploadedDocumentDetailsItem(documentDetail);
		});
		return bookingDetails;
	}

	@Override
	public Integer getBookingCount(@Valid AdvertisementSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getAdvertisementSearchQuery(criteria, preparedStatement);

		if (query == null)
			return 0;

		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
	}

	@Override
	public void insertBookingIdForTimer(AdvertisementSlotSearchCriteria criteria, RequestInfo requestInfo,
			AdvertisementSlotAvailabilityDetail availabiltityDetailsResponse) {
		String bookingId = "";
		String bookindIdFromCriteria = criteria.getBookingId();
		String tenantId = requestInfo.getUserInfo().getTenantId();
		String uuid = requestInfo.getUserInfo().getUuid();
		String createdBy = requestInfo.getUserInfo().getUuid();
		long createdTime = BookingUtil.getCurrentTimestamp();
		String lastModifiedBy = requestInfo.getUserInfo().getUuid();
		long lastModifiedTime = BookingUtil.getCurrentTimestamp();
		String status = BookingConstants.ACTIVE;
		String addType = criteria.getAddType();
		String location = criteria.getLocation();
		String faceArea = criteria.getFaceArea();
		Boolean nightLight = criteria.getNightLight();
		String bookingStartDate = criteria.getBookingStartDate();
		String bookingEndDate = criteria.getBookingEndDate();
		String bookingNo = "";

		// Check if bookingId already exists in the timer table
		String checkQuery = queryBuilder.checkBookingIdExists(bookindIdFromCriteria);
		List<Map<String, Object>> result = jdbcTemplate.queryForList(checkQuery, bookindIdFromCriteria);
//	    
		// Check if draft ID exists in the timer table (separate from bookingId check)
		List<Map<String, Object>> draftResult = isDraftIdExistInDraft(uuid);

//	    if (result.isEmpty()) {
//	        // Booking ID does not exist, insert new booking entry
//	        String query = queryBuilder.insertBookingIdForTimer(bookingId);
//	        jdbcTemplate.update(query, bookingId, createdBy, createdTime, status, bookingNo, lastModifiedBy,
//	                lastModifiedTime, addType, location, faceArea, nightLight);
//
//	        // Set timer value for new booking
//	        long timerValue = bookingConfiguration.getPaymentTimer();
//	        availabiltityDetailsResponse.setTimerValue(timerValue / 1000);
//	    } else
//	    	if (!result.isEmpty()) {
//	        // Booking ID exists, get remaining time and set it in the response
//	        Map<String, Long> remainingTime = getRemainingTimerValues(bookingId);
//	        long remainingTimeValue = remainingTime.get(bookingId);
//	        availabiltityDetailsResponse.setTimerValue(remainingTimeValue / 1000);
//	    }

		if (draftResult.isEmpty() && bookindIdFromCriteria.isEmpty()) {
			// Handle the case when a draft ID exists for the booking
			BookingRequest draftBooking = new BookingRequest();
			BookingDetail bookingApplication = new BookingDetail();
			bookingApplication.setTenantId(tenantId);
			bookingApplication.setDraftId(BookingUtil.getRandonUUID());
			bookingApplication.setAuditDetails(BookingUtil.getAuditDetails(uuid, true));
			draftBooking.setBookingApplication(bookingApplication);

			// bookingRepository.saveDraftApplication(draftBooking);
			String draftQuery = AdvertisementBookingQueryBuilder.DRAFT_QUERY;
			String emptyJsonb = "{}";
			jdbcTemplate.update(draftQuery, draftBooking.getBookingApplication().getDraftId(), tenantId, uuid,
					emptyJsonb, createdBy, lastModifiedBy, createdTime, lastModifiedTime);

			// Use draft ID as booking ID
			bookingId = draftBooking.getBookingApplication().getDraftId();

			String query = queryBuilder.insertBookingIdForTimer(bookingId);
			jdbcTemplate.update(query, bookingId, createdBy, createdTime, status, bookingNo, lastModifiedBy,
					lastModifiedTime, addType, location, faceArea, nightLight, bookingStartDate, bookingEndDate);
			long timerValue = bookingConfiguration.getPaymentTimer();
			availabiltityDetailsResponse.setTimerValue(timerValue / 1000);

		} else if (!draftResult.isEmpty() && bookindIdFromCriteria.isEmpty()) {
			if (result.isEmpty())
				bookingId = (String) draftResult.get(0).get("draft_id");
			Map<String, Long> remainingTime = getRemainingTimerValues(bookingId);
			if (!remainingTime.isEmpty()) {
				long remainingTimeValue = remainingTime.get(bookingId);
				availabiltityDetailsResponse.setTimerValue(remainingTimeValue / 1000);
			}
		} else if (!result.isEmpty() && result.get(0).get("booking_no") != null) {

			Map<String, Long> remainingTime = getRemainingTimerValues(bookindIdFromCriteria);
			long remainingTimeValue = remainingTime.get(bookindIdFromCriteria);
			availabiltityDetailsResponse.setTimerValue(remainingTimeValue / 1000);
		}

	}

	
	public List<Map<String, Object>> isDraftIdExistInDraft(String uuid) {
	    String query = queryBuilder.checkDraftIdExists(uuid);
		List<Map<String, Object>> resultDraft = jdbcTemplate.queryForList(query, uuid);
		return resultDraft;
	  
	}


	public void deleteBookingIdForTimer(String bookingId, RequestInfo requestInfo) {
		String query = queryBuilder.deleteBookingIdForTimer(bookingId);
		// String draftQuery = queryBuilder.deleteDraftIdForTimer(bookingId);

		jdbcTemplate.update(query, bookingId);

//		String uuid = requestInfo.getUserInfo().getUuid();
//		List<Map<String, Object>> result1 = isDraftIdExistInDraft(uuid);
//		bookingId = (String) result1.get(0).get("draft_id");
//		jdbcTemplate.update(draftQuery, bookingId);
	}

	public Map<String, Long> getRemainingTimerValues(String bookingId) {
		if (bookingId == null || bookingId.isEmpty()) {
			log.warn("No booking details provided");
			return Collections.emptyMap();
		}

		String query = queryBuilder.fetchBookingIdForTimer(bookingId);

		if (query == null) {
			log.warn("No query generated, returning empty result");
			return Collections.emptyMap();
		}

		long currentTimeMillis = BookingUtil.getCurrentTimestamp();
		// Execute query only if booking IDs exist
		List<Map<String, Object>> bookings = jdbcTemplate.queryForList(query, new Object[] { bookingId });
		Map<String, Long> remainingTimers = new HashMap<>();

		for (Map<String, Object> booking : bookings) {
			String bookingID = (String) booking.get("booking_id");
			Long createdTime = (Long) booking.get("createdtime");

			if (createdTime != null) {
				long elapsedTime = currentTimeMillis - createdTime;
				if (elapsedTime <= bookingConfiguration.getPaymentTimer()) {
					long remainingTime = Math.max(bookingConfiguration.getPaymentTimer() - elapsedTime, 0L);
					log.info("Booking ID: {}, Remaining Time: {}", bookingID, remainingTime);
					remainingTimers.put(bookingID, remainingTime);
				} else {
					log.info("Booking ID: {}, Timer Expired", bookingID);
					remainingTimers.put(bookingID, 0L);
				}
			} else {
				log.info("Booking ID: {}, No Created Time, Default Remaining Time: 0", bookingID);
				remainingTimers.put(bookingID, 0L);
			}
		}

		log.info("Remaining Timers Map: {}", remainingTimers);
		return remainingTimers;
	}

	// Upadtes booking request data for the given booking number
	@Override
	public void updateBooking(@Valid BookingRequest advertisementBookingRequest) {
		log.info("Updating advertisement booking request data for booking no : "
				+ advertisementBookingRequest.getBookingApplication().getBookingNo());
		producer.push(bookingConfiguration.getAdvertisementBookingUpdateTopic(), advertisementBookingRequest);
	}

	@Transactional
	public void updateBookingSynchronously(String bookingId, String uuid, PaymentDetail paymentDetail, String status) {

		String lastUpdateBy = uuid;
		long lastUpdatedTime = BookingUtil.getCurrentTimestamp();
		String receiptNo = null;
		long receiptDate = 0l;

		if (paymentDetail != null) {
			jdbcTemplate.update(AdvertisementBookingQueryBuilder.BOOKING_UPDATE_QUERY, status, lastUpdateBy,
					lastUpdatedTime, receiptNo, receiptDate, bookingId);
		} else {
			jdbcTemplate.update(AdvertisementBookingQueryBuilder.UPDATE_BOOKING_STATUS, status, bookingId);
		}

		jdbcTemplate.update(AdvertisementBookingQueryBuilder.CART_UPDATE_QUERY, status,
				bookingId);

		jdbcTemplate.update(AdvertisementBookingQueryBuilder.INSERT_BOOKING_DETAIL_AUDIT_QUERY, bookingId);

		jdbcTemplate.update(AdvertisementBookingQueryBuilder.INSERT_CART_DETAIL_AUDIT_QUERY, bookingId);
	}
	
	@Override
	public void updateTimerBookingId(String bookingId, String bookingNo, String draftId) {
		jdbcTemplate.update(AdvertisementBookingQueryBuilder.UPDATE_TIMER, bookingId, bookingNo, draftId);

	}

	@Override
	public List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			AdvertisementSlotSearchCriteria criteria) {
		List<Object> paramsList = new ArrayList<>();

		StringBuilder query = queryBuilder.getAdvertisementSlotAvailabilityQuery(criteria, paramsList);

		String addTypeQuery = " AND eacd.add_type ";
		String faceAreaQuery = " AND eacd.face_area ";
		String location = " AND eacd.location ";
		String nightLight = " AND eacd.night_light ";

		if (StringUtils.isNotBlank(criteria.getAddType())) {
			query.append(addTypeQuery).append(" = ? ");
			paramsList.add(criteria.getAddType());

		}
		if (StringUtils.isNotBlank(criteria.getFaceArea())) {
			query.append(faceAreaQuery).append(" = ? ");
			paramsList.add(criteria.getFaceArea());
		}
		if (StringUtils.isNotBlank(criteria.getLocation())) {
			query.append(location).append(" = ? ");
			paramsList.add(criteria.getLocation());
		}
		if (StringUtils.isNotBlank(criteria.getAddType())) {
			query.append(nightLight).append(" = ? ");
			paramsList.add(criteria.getNightLight());
		}

		log.info("getBookingDetails : Final query: " + query);
		log.info("paramsList : " + paramsList);
		List<AdvertisementSlotAvailabilityDetail> availabiltityDetails = jdbcTemplate.query(query.toString(),
				paramsList.toArray(), availabilityRowMapper);

		log.info("Fetched slot availabilty details : " + availabiltityDetails);
		return availabiltityDetails;
	}
	
	public List<AdvertisementSlotAvailabilityDetail> getBookedSlotsFromTimer(AdvertisementSlotSearchCriteria criteria) {
	    List<Object> paramsList = new ArrayList<>();

	    StringBuilder query = new StringBuilder(
	        "SELECT createdby, add_type, location, face_area, night_light, booking_start_date, booking_end_date " +
	        "FROM eg_adv_payment_timer WHERE 1=1");

	    // Dynamically append conditions based on non-null criteria
	    if (StringUtils.isNotBlank(criteria.getAddType())) {
	        query.append(" AND add_type = ?");
	        paramsList.add(criteria.getAddType());
	    }
	    if (StringUtils.isNotBlank(criteria.getFaceArea())) {
	        query.append(" AND face_area = ?");
	        paramsList.add(criteria.getFaceArea());
	    }
	    if (StringUtils.isNotBlank(criteria.getLocation())) {
	        query.append(" AND location = ?");
	        paramsList.add(criteria.getLocation());
	    }
	    if (criteria.getNightLight() != null) {
	        query.append(" AND night_light = ?");
	        paramsList.add(criteria.getNightLight());
	    }
	    
	    if (criteria.getBookingStartDate() != null) {
	        query.append(" AND booking_start_date = ?");
	       
	        paramsList.add(java.sql.Date.valueOf(criteria.getBookingStartDate())); // Ensure correct type

	    }
	    
	    if (criteria.getBookingEndDate() != null) {
	        query.append(" AND booking_end_date = ?");
	       
	        paramsList.add(java.sql.Date.valueOf(criteria.getBookingStartDate())); // Ensure correct type

	    }
	    

	    log.info("getBookedSlotsFromTimer: Final query: {}", query);
	    log.info("Parameters: {}", paramsList);

	    return jdbcTemplate.query(query.toString(), paramsList.toArray(), (rs, rowNum) -> {
	        AdvertisementSlotAvailabilityDetail detail = new AdvertisementSlotAvailabilityDetail();
	        detail.setAddType(rs.getString("add_type"));
	        detail.setLocation(rs.getString("location"));
	        detail.setFaceArea(rs.getString("face_area"));
	        detail.setNightLight(rs.getBoolean("night_light"));
	        detail.setBookingDate(rs.getString("booking_start_date"));
	        detail.setUuid(rs.getString("createdby"));
	        return detail;
	    });
	}



	@Override
	public void saveDraftApplication(BookingRequest bookingRequest) {
		AdvertisementDraftDetail advertisementDraftDetail = convertToDraftDetailsObject(bookingRequest);
		PersisterWrapper<AdvertisementDraftDetail> persisterWrapper = new PersisterWrapper<AdvertisementDraftDetail>(
				advertisementDraftDetail);
		producer.push(bookingConfiguration.getAdvertisementDraftApplicationSaveTopic(), persisterWrapper);
	}

	@Override
	public List<BookingDetail> getAdvertisementDraftApplications(@NonNull RequestInfo requestInfo,
			@Valid AdvertisementSearchCriteria advertisementSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = "SELECT draft_id, draft_application_data FROM eg_adv_draft_detail where user_uuid = ? and tenant_id = ?";
		preparedStmtList.add(requestInfo.getUserInfo().getUuid());
		preparedStmtList.add(advertisementSearchCriteria.getTenantId());

		log.info("Final query for getAdvertisementApplications {} and paramsList {} : ", preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), draftApplicationRowMapper);
	}

	@Override
	public void updateDraftApplication(BookingRequest bookingRequest) {
		AdvertisementDraftDetail advertisementDraftDetail = convertToDraftDetailsObject(bookingRequest);
		PersisterWrapper<AdvertisementDraftDetail> persisterWrapper = new PersisterWrapper<AdvertisementDraftDetail>(
				advertisementDraftDetail);
		producer.push(bookingConfiguration.getAdvertisementDraftApplicationUpdateTopic(), persisterWrapper);

	}

	public void deleteDraftApplication(String draftId) {
		AdvertisementDraftDetail advertisementDraftDetail = AdvertisementDraftDetail.builder().draftId(draftId).build();

		PersisterWrapper<AdvertisementDraftDetail> persisterWrapper = new PersisterWrapper<AdvertisementDraftDetail>(
				advertisementDraftDetail);
		producer.push(bookingConfiguration.getAdvertisementDraftApplicationDeleteTopic(), persisterWrapper);

	}

	private AdvertisementDraftDetail convertToDraftDetailsObject(BookingRequest bookingRequest) {
		BookingDetail advertisementDetail = bookingRequest.getBookingApplication();
		String draftApplicationData = null;
		try {
			draftApplicationData = objectMapper.writeValueAsString(bookingRequest.getBookingApplication());
		} catch (JsonProcessingException e) {
			log.error("Serialization error for AdvertisementDraftDetail with ID: {} and Tenant: {}",
					bookingRequest.getBookingApplication().getDraftId(),
					bookingRequest.getBookingApplication().getTenantId(), e);

		}
		AdvertisementDraftDetail advertisementDraftDetail = AdvertisementDraftDetail.builder()
				.draftId(advertisementDetail.getDraftId()).tenantId(advertisementDetail.getTenantId())
				.userUuid(bookingRequest.getRequestInfo().getUserInfo().getUuid())
				.draftApplicationData(draftApplicationData).auditDetails(advertisementDetail.getAuditDetails()).build();
		return advertisementDraftDetail;
	}

	public String getStatusFromTimerTable(String bookingId) {
		String status = null;
		if (bookingId != null && !bookingId.isEmpty()) {
			String checkQuery = queryBuilder.checkBookingIdExists(bookingId);
			List<Map<String, Object>> result = jdbcTemplate.queryForList(checkQuery, bookingId);
			if (!result.isEmpty()) {
				status = (String) result.get(0).get("status");
			} else {
				System.out.println("No records found for bookingId: " + bookingId);
			}
		}
		return status;
	}

	public void scheduleTimerDelete() {
		long currentTimeMillis = BookingUtil.getCurrentTimestamp();
		List<String> bookingIds = fetchBookingIds(currentTimeMillis);

		if (bookingIds.isEmpty()) {
			log.warn("No valid booking IDs found for deletion.");
			return;
		}

		for (String bookingId : bookingIds) {
			String status = getStatusFromTimerTable(bookingId);

			if (!"active".equalsIgnoreCase(status)) {
				log.warn("Booking ID " + bookingId + " is not active.");
				continue;
			}

			int rowsDeleted = deleteBookingId(currentTimeMillis, bookingId);
			if (rowsDeleted > 0) {
				log.info(rowsDeleted + " expired entry(ies) deleted for booking ID: " + bookingId);
				//updateBookingSynchronously(bookingId, "", null, BookingStatusEnum.BOOKING_CREATED.toString());
				updateBookingSynchronously(bookingId, "", null, BookingStatusEnum.BOOKING_EXPIRED.toString());

				// statusUpdateForTimer(bookingId, BookingStatusEnum.BOOKING_CREATED);
			} else {
				log.warn("No entries deleted for booking ID: " + bookingId);
			}
		}
	}

	public List<String> fetchBookingIds(long currentTimeMillis) {
		String query = queryBuilder.getBookingIdToDelete();
		if (query == null || query.isEmpty()) {
			log.error("Query to fetch booking IDs is null or empty.");
			return Collections.emptyList();
		}

		try {
			return jdbcTemplate.queryForList(query,
					new Object[] { currentTimeMillis, bookingConfiguration.getPaymentTimer() }, String.class);
		} catch (DataAccessException e) {
			log.warn("Error fetching booking IDs: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	private int deleteBookingId(long currentTimeMillis, String bookingId) {
		String deleteQuery = queryBuilder.deleteBookingIdPaymentTimer();
		String deleteDraftQuery = queryBuilder.deleteDraftIdPaymentTimer();
		if (deleteQuery == null || deleteQuery.isEmpty()) {
			log.error("Delete query is null or empty for booking ID: " + bookingId);
			return 0;
		}
		
		if (deleteDraftQuery == null || deleteDraftQuery.isEmpty()) {
			log.error("Delete query is null or empty for booking ID: " + bookingId);
			return 0;
		}
		jdbcTemplate.update(deleteDraftQuery, currentTimeMillis, bookingConfiguration.getPaymentTimer(), bookingId);
		return jdbcTemplate.update(deleteQuery, currentTimeMillis, bookingConfiguration.getPaymentTimer(), bookingId);
	}

	@Override
	public void updateBookingSynchronously(@Valid BookingRequest advertisementBookingRequest) {

		log.info("Updating advertisement booking request data for booking no : "
				+ advertisementBookingRequest.getBookingApplication().getBookingNo());
		BookingDetail bookingapplication = advertisementBookingRequest.getBookingApplication();
		List<CartDetail> cartDetails = advertisementBookingRequest.getBookingApplication().getCartDetails();
		CartDetail cartDetail = cartDetails.get(0);
		if (cartDetails == null || cartDetails.isEmpty()) {
			throw new IllegalArgumentException(
					"Cart details are missing for booking ID: " + bookingapplication.getBookingId());
		}

		AuditDetails auditDetails = advertisementBookingRequest.getBookingApplication().getAuditDetails();

		String bookingQuery = queryBuilder.updateBookingDetail();
		String cartQuery = queryBuilder.updateCartDetail();
		String bookingAuditQuery = queryBuilder.createBookingDetailAudit();
		String cartAuditQuery = queryBuilder.createCartDetailAudit();

		jdbcTemplate.update(bookingQuery, bookingapplication.getBookingStatus(), bookingapplication.getPaymentDate(),
				auditDetails.getLastModifiedBy(), auditDetails.getLastModifiedTime(),
				bookingapplication.getPermissionLetterFilestoreId(), bookingapplication.getPaymentReceiptFilestoreId(),
				bookingapplication.getBookingId());

		jdbcTemplate.update(cartQuery, cartDetail.getStatus(), auditDetails.getLastModifiedBy(),
				auditDetails.getLastModifiedTime(), cartDetail.getCartId());

		jdbcTemplate.update(bookingAuditQuery, bookingapplication.getBookingId());

		jdbcTemplate.update(cartAuditQuery, cartDetail.getCartId());
	}
	
	

}
