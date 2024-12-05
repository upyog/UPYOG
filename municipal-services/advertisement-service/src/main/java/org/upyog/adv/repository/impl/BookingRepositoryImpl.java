package org.upyog.adv.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.adv.config.BookingConfiguration;
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

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BookingRepositoryImpl implements BookingRepository {

	@Autowired
	private Producer producer;

	@Autowired
	private BookingConfiguration bookingConfiguration;
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
		String bookingId = criteria.getBookingId();
		String createdBy = requestInfo.getUserInfo().getUuid();
		long createdTime = BookingUtil.getCurrentTimestamp();
		String lastModifiedBy = requestInfo.getUserInfo().getUuid();
		long lastModifiedTime = BookingUtil.getCurrentTimestamp();

		// Check if bookingId already exists in timer table
		String checkQuery = queryBuilder.checkBookingIdExists(bookingId);
		List<Map<String, Object>> result = jdbcTemplate.queryForList(checkQuery, bookingId);
		if (result.isEmpty()) {
			String query = queryBuilder.insertBookingIdForTimer(bookingId);
			jdbcTemplate.update(query, bookingId, createdBy, createdTime, lastModifiedBy, lastModifiedTime);
			long timerValue = bookingConfiguration.getPaymentTimer();
	        availabiltityDetailsResponse.setTimerValue(timerValue / 1000);
		} else {
			Map<String, Long> remainingTime = getRemainingTimerValues(bookingId);
			long remainingTimeValue = remainingTime.get(bookingId);
			availabiltityDetailsResponse.setTimerValue(remainingTimeValue / 1000);

		}
	}
	    
	public void deleteBookingIdForTimer(String bookingId) {
		String query = queryBuilder.deleteBookingIdForTimer(bookingId);
		jdbcTemplate.update(query, bookingId);
	}

	@Override
	public Map<String, Long> getRemainingTimerValues(List<BookingDetail> bookingDetails) {
		if (bookingDetails == null || bookingDetails.isEmpty()) {
			log.warn("No booking details provided");
			return Collections.emptyMap();
		}

		List<String> bookingIds = bookingDetails.stream().map(BookingDetail::getBookingId).filter(Objects::nonNull)
				.collect(Collectors.toList());
		String query = queryBuilder.fetchBookingIdForTImer(bookingIds);

		if (query == null) {
			log.warn("No query generated, returning empty result");
			return Collections.emptyMap();
		}

		long currentTimeMillis = BookingUtil.getCurrentTimestamp();
		// Execute query only if booking IDs exist
		List<Map<String, Object>> bookings = jdbcTemplate.queryForList(query, bookingIds.toArray());
		Map<String, Long> remainingTimers = new HashMap<>();

		for (Map<String, Object> booking : bookings) {
			String bookingId = (String) booking.get("booking_id");
			Long createdTime = (Long) booking.get("createdtime");

			if (createdTime != null) {
				long elapsedTime = currentTimeMillis - createdTime;
				if (elapsedTime <= bookingConfiguration.getPaymentTimer()) {
					long remainingTime = Math.max(bookingConfiguration.getPaymentTimer() - elapsedTime, 0L);
					log.info("Booking ID: {}, Remaining Time: {}", bookingId, remainingTime);
					remainingTimers.put(bookingId, remainingTime);
				} else {
					log.info("Booking ID: {}, Timer Expired", bookingId);
					remainingTimers.put(bookingId, 0L);
				}
			} else {
				log.info("Booking ID: {}, No Created Time, Default Remaining Time: 0", bookingId);
				remainingTimers.put(bookingId, 0L);
			}
		}

		log.info("Remaining Timers Map: {}", remainingTimers);
		return remainingTimers;
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
		List<Map<String, Object>> bookings = jdbcTemplate.queryForList(query, new Object[]{bookingId});
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
	public void updateBookingSynchronously(@Valid BookingRequest advertisementBookingRequest) {
		
		log.info("Updating advertisement booking request data for booking no : "
		+ advertisementBookingRequest.getBookingApplication().getBookingNo());
		BookingDetail bookingapplication = advertisementBookingRequest.getBookingApplication();
		List<CartDetail> cartDetails = advertisementBookingRequest.getBookingApplication().getCartDetails();
		if (cartDetails == null || cartDetails.isEmpty()) {
			throw new IllegalArgumentException(
					"Cart details are missing for booking ID: " + bookingapplication.getBookingId());
		}
		CartDetail cartDetail = cartDetails.get(0);
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
		
		jdbcTemplate.update(
		        bookingAuditQuery, 
		        bookingapplication.getBookingId()
		    );
		
		 jdbcTemplate.update(
			        cartAuditQuery, 
			        cartDetail.getCartId()
			    );
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
	
	@Override
	public void saveDraftApplication(BookingRequest bookingRequest) {
		AdvertisementDraftDetail advertisementDraftDetail = convertToDraftDetailsObject(bookingRequest);
		PersisterWrapper<AdvertisementDraftDetail> persisterWrapper = new PersisterWrapper<AdvertisementDraftDetail>(advertisementDraftDetail);
		producer.push(bookingConfiguration.getAdvertisementDraftApplicationSaveTopic(), persisterWrapper);
    }

	@Override
	public List<BookingDetail> getAdvertisementDraftApplications(@NonNull RequestInfo requestInfo,
			@Valid AdvertisementSearchCriteria advertisementSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = "SELECT draft_id, draft_application_data FROM eg_adv_draft_detail where user_uuid = ? and tenant_id = ?";
		preparedStmtList.add(requestInfo.getUserInfo().getUuid());
		preparedStmtList.add(advertisementSearchCriteria.getTenantId());
		
		log.info("Final query for getAdvertisementApplications {} and paramsList {} : " , preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), draftApplicationRowMapper);
	}

	@Override
	public void updateDraftApplication(BookingRequest bookingRequest) {
		AdvertisementDraftDetail advertisementDraftDetail = convertToDraftDetailsObject(bookingRequest);
		PersisterWrapper<AdvertisementDraftDetail> persisterWrapper = new PersisterWrapper<AdvertisementDraftDetail>(advertisementDraftDetail);
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
		} catch (JsonProcessingException e) {log.error("Serialization error for AdvertisementDraftDetail with ID: {} and Tenant: {}",
				bookingRequest.getBookingApplication().getDraftId(),
				bookingRequest.getBookingApplication().getTenantId(), e);

	}
		AdvertisementDraftDetail advertisementDraftDetail = AdvertisementDraftDetail.builder()
				.draftId(advertisementDetail.getDraftId()).tenantId(advertisementDetail.getTenantId())
				.userUuid(bookingRequest.getRequestInfo().getUserInfo().getUuid())
				.draftApplicationData(draftApplicationData).auditDetails(advertisementDetail.getAuditDetails()).build();
		return advertisementDraftDetail;
	}
	
	
	
}
