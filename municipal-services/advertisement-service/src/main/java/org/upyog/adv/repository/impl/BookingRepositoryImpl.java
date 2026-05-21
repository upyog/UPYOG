package org.upyog.adv.repository.impl;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
import org.upyog.adv.repository.rowmapper.AdvertisementDraftIdRowMapper;
import org.upyog.adv.repository.rowmapper.AdvertisementSlotAvailabilityRowMapper;
import org.upyog.adv.repository.rowmapper.AdvertisementUpdateSlotAvailabilityRowMapper;
import org.upyog.adv.repository.rowmapper.BookingCartDetailRowmapper;
import org.upyog.adv.repository.rowmapper.BookingDetailIdRowmapper;
import org.upyog.adv.repository.rowmapper.BookingDetailRowmapper;
import org.upyog.adv.repository.rowmapper.DocumentDetailsRowMapper;
import org.upyog.adv.service.UserService;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.*;

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
	private AdvertisementUpdateSlotAvailabilityRowMapper availabilityUpdateRowMapper;
	@Autowired
	private AdvertisementDraftApplicationRowMapper draftApplicationRowMapper;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	UserService userService;

	@Override
	public void saveBooking(BookingRequest bookingRequest) {
		log.info("Saving Advertisement booking request data for booking no : "
				+ bookingRequest.getBookingApplication().getBookingNo());
		String key = bookingRequest.getBookingApplication().getBookingNo();
		producer.push(bookingConfiguration.getAdvertisementBookingSaveTopic(), key, bookingRequest);

	}


	public List<OwnerInfo> getOwnerByBookingId(String bookingId) {
//		String sql = "SELECT * FROM eg_adv_owner WHERE booking_id = ?";
		List<String> bookingIds = new ArrayList<>();
		bookingIds.add(bookingId);
		String query = queryBuilder.getOwnerUuidsQuery(bookingIds);
		return jdbcTemplate.query(query, new Object[]{bookingId}, new BeanPropertyRowMapper<>(OwnerInfo.class));
	}



	@Override
	public List<BookingDetail> getBookingDetails(AdvertisementSearchCriteria bookingSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getAdvertisementSearchQuery(bookingSearchCriteria, preparedStmtList);

		log.info("getBookingDetails : Final query: " + query);
		log.info("preparedStmtList :  " + preparedStmtList);
		List<BookingDetail> bookingDetails = jdbcTemplate.query(query, preparedStmtList.toArray(), bookingRowmapper);



	log.info("Fetched booking details size : " + (bookingDetails != null ? bookingDetails.size() : null));

	if (bookingDetails == null || bookingDetails.isEmpty()) {
	    return bookingDetails;
	}




		Map<String, BookingDetail> bookingMap = bookingDetails.stream()
		.collect(Collectors.toMap(BookingDetail::getBookingId, Function.identity(), (left, right) -> left, HashMap::new));
	log.info("Fetched booking details bookingMap : " + bookingMap);
		List<String> bookingIds = new ArrayList<String>();
		bookingIds.addAll(bookingMap.keySet());
		log.info("Fetched booking details bookingIds : " + bookingIds);
        String slotQuery = queryBuilder.getSlotDetailsQuery(bookingIds) + " AND status != 'REMOVED' ORDER BY booking_date, createdtime";
	List<CartDetail> cartDetails = jdbcTemplate.query(slotQuery,
				bookingIds.toArray(), cartDetailRowmapper);
	if (cartDetails != null) cartDetails.stream().forEach(slotDetail -> {
			log.info("fetched cartDetails " + bookingMap.get(slotDetail.getBookingId()));
			bookingMap.get(slotDetail.getBookingId()).addBookingSlots(slotDetail);
		});
		log.info("Fetched booking details cartDetails : " + cartDetails);
		List<DocumentDetail> documentDetails = jdbcTemplate.query(queryBuilder.getDocumentDetailsQuery(bookingIds),
				bookingIds.toArray(), detailsRowMapper);
	if (documentDetails != null) documentDetails.stream().forEach(documentDetail -> {
			bookingMap.get(documentDetail.getBookingId()).addUploadedDocumentDetailsItem(documentDetail);
		});
		
		// Fetch and set owners for each booking
		for (String bid : bookingIds) {
			List<OwnerInfo> ownersForBooking = getOwnerByBookingId(bid);
			if (ownersForBooking != null && !ownersForBooking.isEmpty()) {
				bookingMap.get(bid).setOwners(ownersForBooking);
				log.info("Set owners for booking {}: {}", bid, ownersForBooking.size());
			}
		}
		
		return bookingDetails;
	}
	public Integer getBookingCount(@Valid AdvertisementSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getAdvertisementSearchQuery(criteria, preparedStatement);

		if (query == null)
			return 0;
		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
	}

	@Override
	public void deleteDataFromTimerAndDraft(String uuid, String draftId, String bookingId) {

		if (StringUtils.isBlank(draftId) && StringUtils.isBlank(bookingId)) {
			log.info("Deleting Timer and draft entry: {}", bookingId);

			String draftDeleteQuery = AdvertisementBookingQueryBuilder.Draft_DELETE_QUERY;
			String timerDeleteQuery = AdvertisementBookingQueryBuilder.TIMER_DELETE_QUERY_BY_UUID;

			jdbcTemplate.update(draftDeleteQuery, uuid);
			jdbcTemplate.update(timerDeleteQuery, uuid);
		}
	}

	public void insertBookingIdForTimer(List<AdvertisementSlotSearchCriteria> criteriaList, RequestInfo requestInfo,
			AdvertisementSlotAvailabilityDetail availabilityDetailsResponse) {

		String tenantId = requestInfo.getUserInfo().getTenantId();
		String uuid = requestInfo.getUserInfo().getUuid();

		// Step 1: Fetch or create draft ID
		String draftId = fetchDraftId(criteriaList, uuid, tenantId);

		// Step 2: If no existing draft ID, perform the batch insert
		if (draftId == null) {
			draftId = insertNewDraftId(criteriaList, uuid, tenantId);
			processBatchInsert(criteriaList, draftId, uuid);
			setTimerValue(availabilityDetailsResponse);
		}

		// Step 3: getAndInsertTimerData timer data
		getAndInsertTimerData(draftId, criteriaList, requestInfo, availabilityDetailsResponse);
	}

	/**
	 * Insert timer rows using the provided ownerId as booking_id (ownerId can be a real booking id or a draft id)
	 */
	@Override
	public void insertBookingIdForTimerWithOwner(List<AdvertisementSlotSearchCriteria> criteriaList, String BookingNo,String ownerId,
			RequestInfo requestInfo, AdvertisementSlotAvailabilityDetail availabilityDetailsResponse) {

		if (criteriaList == null || criteriaList.isEmpty() || StringUtils.isBlank(ownerId)) {
			log.warn("No criteria or ownerId provided for insertBookingIdForTimerWithOwner");
			return;
		}

		long createdTime = BookingUtil.getCurrentTimestamp();
		String status = BookingConstants.ACTIVE;

		List<Object[]> batchArgs = new ArrayList<>();
		for (AdvertisementSlotSearchCriteria criteria : criteriaList) {
			LocalDate startDate = LocalDate.parse(criteria.getBookingStartDate());
			LocalDate endDate = LocalDate.parse(criteria.getBookingEndDate());

			while (!startDate.isAfter(endDate)) {
				batchArgs.add(new Object[] { ownerId,requestInfo.getUserInfo().getUuid(), createdTime, status,
					"", requestInfo.getUserInfo().getUuid(), createdTime, criteria.getAddType(), criteria.getLocation(),
					criteria.getFaceArea(), criteria.getNightLight(), criteria.getAdvertisementId(),
					criteria.getBookingStartDate(), criteria.getBookingEndDate(), startDate.toString() });
				startDate = startDate.plusDays(1);
			}
		}

		jdbcTemplate.batchUpdate(AdvertisementBookingQueryBuilder.PAYMENT_TIMER_QUERY, batchArgs);


		// Populate timer value to response (use provided availabilityDetailsResponse)
		setTimerValue(availabilityDetailsResponse);

		// Fetch and set remaining timer values for the ownerId
		getAndInsertTimerData(ownerId, criteriaList, requestInfo, availabilityDetailsResponse);
	}

	private String fetchDraftId(List<AdvertisementSlotSearchCriteria> criteriaList, String uuid, String tenantId) {

		// To insert the same drfatId/bookingId in timer table
		String draftId = criteriaList.stream()
				.filter(criteria -> criteria.getBookingId() != null && !criteria.getBookingId().isEmpty())
				.map(AdvertisementSlotSearchCriteria::getBookingId).findFirst().orElse(null);

		// Check if the booking ID exists in the timer table
		List<BookingDetail> bookingListFromTimer = jdbcTemplate.query(
			    AdvertisementBookingQueryBuilder.BOOKING_ID_EXISTS_CHECK, 
			    new Object[] { draftId }, 
			    new BookingDetailIdRowmapper() 
			);


		if (bookingListFromTimer != null && !bookingListFromTimer.isEmpty()) {
			return draftId;
		}

		// Check if draft ID exists in the draft table
		List<AdvertisementDraftDetail> draftList = getDraftData(uuid);
		if (!draftList.isEmpty()) {
			return draftList.get(0).getDraftId();
		}

		return null;
	}

	private String insertNewDraftId(List<AdvertisementSlotSearchCriteria> criteriaList, String uuid, String tenantId) {
		long createdTime = BookingUtil.getCurrentTimestamp();
		String draftId = BookingUtil.getRandonUUID();

		jdbcTemplate.update(AdvertisementBookingQueryBuilder.DRAFT_QUERY, draftId, tenantId, uuid, "{}", uuid, uuid,
				createdTime, createdTime);

		return draftId;
	}

	private void processBatchInsert(List<AdvertisementSlotSearchCriteria> criteriaList, String draftId, String uuid) {
		long createdTime = BookingUtil.getCurrentTimestamp();
		String status = BookingConstants.ACTIVE;

		List<Object[]> batchArgs = new ArrayList<>();
		for (AdvertisementSlotSearchCriteria criteria : criteriaList) {
			LocalDate startDate = LocalDate.parse(criteria.getBookingStartDate());
			LocalDate endDate = LocalDate.parse(criteria.getBookingEndDate());

			while (!startDate.isAfter(endDate)) {
				batchArgs.add(new Object[] { draftId, uuid, createdTime, status, "", uuid, createdTime,
						criteria.getAddType(), criteria.getLocation(), criteria.getFaceArea(), criteria.getNightLight(),criteria.getAdvertisementId(),
						criteria.getBookingStartDate(), criteria.getBookingEndDate(), startDate.toString() });
				startDate = startDate.plusDays(1);
			}
		}

		jdbcTemplate.batchUpdate(AdvertisementBookingQueryBuilder.PAYMENT_TIMER_QUERY, batchArgs);
	}

	private void setTimerValue(AdvertisementSlotAvailabilityDetail availabilityDetailsResponse) {
		long timerValue = bookingConfiguration.getPaymentTimer();
		availabilityDetailsResponse.setTimerValue(timerValue / 1000); // Convert milliseconds to seconds
	}

	@Override
	public void getAndInsertTimerData(String draftId, List<AdvertisementSlotSearchCriteria> criteriaList,
			RequestInfo requestInfo, AdvertisementSlotAvailabilityDetail availabilityDetailsResponse) {

		for (AdvertisementSlotSearchCriteria criteria : criteriaList) {
			getTimerData(draftId, criteria, requestInfo, availabilityDetailsResponse, criteriaList);
		}
	}

	@Override
	public void getTimerData(String bookingId, AdvertisementSlotSearchCriteria criteria, RequestInfo requestInfo,
			AdvertisementSlotAvailabilityDetail availabilityDetailsResponse,
			List<AdvertisementSlotSearchCriteria> criteriaList) {
		
		
		Map<String, Long> remainingTime = getRemainingTimerValues(bookingId);
		if (!remainingTime.isEmpty() && remainingTime.containsKey(bookingId)) {
			long remainingTimeValue = remainingTime.get(bookingId);
			availabilityDetailsResponse.setTimerValue(remainingTimeValue / 1000);
			
		}
		/*
		 * List<AdvertisementSlotAvailabilityDetail> blockedSlots =
		 * getBookedSlots(criteria, requestInfo);
		 * 
		 * if (!blockedSlots.isEmpty()) { log.info("Matched slot found: {}",
		 * blockedSlots);
		 * 
		 * boolean dateMatched = blockedSlots.stream() .anyMatch(slot ->
		 * slot.getBookingStartDate().equals(criteria.getBookingStartDate()) &&
		 * slot.getBookingEndDate().equals(criteria.getBookingEndDate()));
		 * 
		 * if (!dateMatched) { log.info("Dates do not match, deleting old entry: {}",
		 * bookingId);
		 * 
		 * String draftDeleteQuery =
		 * AdvertisementBookingQueryBuilder.DraftID_DELETE_QUERY; String
		 * timerDeleteQuery = AdvertisementBookingQueryBuilder.TIMER_DELETE_QUERY;
		 * 
		 * jdbcTemplate.update(draftDeleteQuery, bookingId);
		 * jdbcTemplate.update(timerDeleteQuery, bookingId);
		 * 
		 * insertBookingIdForTimer(criteriaList, requestInfo,
		 * availabilityDetailsResponse); } else {
		 * 
		 * } else { log.
		 * info("No Matched slots found. Deleting non-matching booking entry with ID: {}"
		 * , bookingId);
		 * 
		 * String draftDeleteQuery =
		 * AdvertisementBookingQueryBuilder.DraftID_DELETE_QUERY; String
		 * timerDeleteQuery = AdvertisementBookingQueryBuilder.TIMER_DELETE_QUERY;
		 * 
		 * jdbcTemplate.update(draftDeleteQuery, bookingId);
		 * jdbcTemplate.update(timerDeleteQuery, bookingId);
		 * 
		 * insertBookingIdForTimer(criteriaList, requestInfo,
		 * availabilityDetailsResponse); }
		 */
	}

	@Override
	public List<AdvertisementSlotAvailabilityDetail> getBookedSlots(AdvertisementSlotSearchCriteria criteria,
			RequestInfo requestInfo) {

		// Fetch the already booked slots from the timer table
		List<AdvertisementSlotAvailabilityDetail> bookedSlots = getBookedSlotsFromTimer(criteria, requestInfo);

		// If there are no booked slots, return an empty list
		if (bookedSlots.isEmpty()) {
			return Collections.emptyList();
		}

		return bookedSlots;

	}

	public List<AdvertisementDraftDetail> getDraftData(String uuid) {
		String query = queryBuilder.checkDraftIdExists(uuid);
		return jdbcTemplate.query(query, new Object[] { uuid }, new AdvertisementDraftIdRowMapper());
	}

	public void deleteBookingIdForTimer(String bookingId) {
		String query = queryBuilder.deleteBookingIdForTimer(bookingId);
		// String draftQuery = queryBuilder.deleteDraftIdForTimer(bookingId);

		jdbcTemplate.update(query, bookingId);

	}

	@Override
	public void markCartSlotsRemoved(String bookingId, List<CartDetail> removed, String modifiedBy, long modifiedTime) {
		if (removed == null || removed.isEmpty()) return;
		for (CartDetail slot : removed) {
			try {
				jdbcTemplate.update(AdvertisementBookingQueryBuilder.CART_MARK_REMOVED_BY_SLOT,
					"REMOVED", modifiedBy, modifiedTime,
					bookingId, slot.getAdvertisementId(), slot.getBookingDate(), slot.getAddType(), slot.getFaceArea(), slot.getNightLight());
			} catch (Exception ex) {
				log.warn("Failed to mark cart slot removed for bookingId {} slot {} : {}", bookingId, slot, ex.getMessage());
			}
		}
		// create audit for booking_id (will insert snapshot rows into audit table)
		try {
			jdbcTemplate.update(AdvertisementBookingQueryBuilder.INSERT_CART_DETAIL_AUDIT_QUERY, bookingId);
		} catch (Exception ex) {
			log.warn("Failed to insert cart detail audit for booking {} : {}", bookingId, ex.getMessage());
		}
	}

	@Override
	public void deleteTimerEntriesForSlots(String ownerId, List<CartDetail> slots) {
		if (StringUtils.isBlank(ownerId) || slots == null || slots.isEmpty()) return;
		for (CartDetail slot : slots) {
			try {
				jdbcTemplate.update(AdvertisementBookingQueryBuilder.PAYMENT_TIMER_DELETE_BY_SLOT,
					ownerId, slot.getAdvertisementId(), slot.getBookingDate(), slot.getAddType(), slot.getFaceArea(), slot.getNightLight());
			} catch (Exception ex) {
				log.warn("Failed to delete timer entry for owner {} slot {} : {}", ownerId, slot, ex.getMessage());
			}
		}
	}

	@Override
	public void updateStatusForTimer(String status, String bookingNo) {
		String updateTimerStatusQuery = AdvertisementBookingQueryBuilder.UPDATE_TIMER_STATUS;

		try {
			int rowsUpdated = jdbcTemplate.update(updateTimerStatusQuery, status, bookingNo);
			if (rowsUpdated > 0) {
				log.info("Successfully updated status to '{}' for booking_no '{}'.", status, bookingNo);
			} else {
				log.warn("No rows updated for booking_no '{}'.", bookingNo);
			}
		} catch (Exception e) {
			log.error("Error while updating status for booking_no '{}'. Exception: {}", bookingNo, e.getMessage());
		}
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
		String key = advertisementBookingRequest.getBookingApplication().getBookingNo();
		producer.push(bookingConfiguration.getAdvertisementBookingUpdateTopic(), key, advertisementBookingRequest);
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
			jdbcTemplate.update(AdvertisementBookingQueryBuilder.UPDATE_BOOKING_STATUS, status, lastUpdateBy,
					lastUpdatedTime, bookingId);
		}

		jdbcTemplate.update(AdvertisementBookingQueryBuilder.CART_UPDATE_QUERY, status, lastUpdateBy, lastUpdatedTime,
				bookingId);

		jdbcTemplate.update(AdvertisementBookingQueryBuilder.INSERT_BOOKING_DETAIL_AUDIT_QUERY, bookingId);

		jdbcTemplate.update(AdvertisementBookingQueryBuilder.INSERT_CART_DETAIL_AUDIT_QUERY, bookingId);
	}

	@Override
	public void updateTimerBookingId(String bookingId, String bookingNo, String draftId) {
		jdbcTemplate.update(AdvertisementBookingQueryBuilder.UPDATE_TIMER, bookingId, bookingNo, draftId);

	}

	@Override
	public List<String> findBookingsEligibleForVerification() {
		return jdbcTemplate.query(AdvertisementBookingQueryBuilder.FETCH_BOOKINGS_ELIGIBLE_FOR_VERIFICATION,
				rs -> {
				List<String> ids = new ArrayList<>();
				while (rs.next()) {
					ids.add(rs.getString("booking_no"));
				}
				return ids;
			});
	}

	@Override
	public void bulkUpdateBookingStatusById(List<String> bookingIds, String status, String lastModifiedBy) {
		if (bookingIds == null || bookingIds.isEmpty()) return;
		long now = BookingUtil.getCurrentTimestamp();
		for (String id : bookingIds) {
			jdbcTemplate.update(AdvertisementBookingQueryBuilder.UPDATE_BOOKING_STATUS_BY_ID, status, lastModifiedBy, now, id);
			jdbcTemplate.update(AdvertisementBookingQueryBuilder.CART_UPDATE_QUERY, status, lastModifiedBy, now, id);
			jdbcTemplate.update(AdvertisementBookingQueryBuilder.INSERT_BOOKING_DETAIL_AUDIT_QUERY, id);
			jdbcTemplate.update(AdvertisementBookingQueryBuilder.INSERT_CART_DETAIL_AUDIT_QUERY, id);
		}
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
	if(StringUtils.isNotBlank((criteria.getAdvertisementId()))){
		query.append(" AND eacd.advertisementId = ? ");
		paramsList.add(criteria.getAdvertisementId());
	}


		log.info("getBookingDetails : Final query: " + query);
		log.info("paramsList : " + paramsList);
		List<AdvertisementSlotAvailabilityDetail> availabiltityDetails = jdbcTemplate.query(query.toString(),
				paramsList.toArray(), availabilityRowMapper);

		log.info("Fetched slot availabilty details : " + availabiltityDetails);
		return availabiltityDetails;
	}

	public List<AdvertisementSlotAvailabilityDetail> getBookedSlotsFromTimer(AdvertisementSlotSearchCriteria criteria,
			RequestInfo requestInfo) {
		List<Object> paramsList = new ArrayList<>();

		StringBuilder query = queryBuilder.getTimerData(criteria, paramsList);

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
		if (criteria.getAdvertisementId() != null) {
			query.append(" AND advertisementId = ?");
			paramsList.add(criteria.getAdvertisementId());
		}

//		if (criteria.getBookingStartDate() != null && criteria.getBookingEndDate() != null) {
//			query.append(" AND booking_start_date <= ? AND booking_end_date >= ?");
//			paramsList.add(java.sql.Date.valueOf(criteria.getBookingEndDate()));
//			paramsList.add(java.sql.Date.valueOf(criteria.getBookingStartDate()));
//		}

		log.info("getBookedSlotsFromTimer: Final query: {}", query);
		log.info("Parameters: {}", paramsList);

		List<AdvertisementSlotAvailabilityDetail> availabiltityDetails = jdbcTemplate.query(query.toString(),
				paramsList.toArray(), availabilityUpdateRowMapper);

		log.info("Fetched slot availabilty details : " + availabiltityDetails);

		return availabiltityDetails;

	}

	@Override
	public void saveDraftApplication(BookingRequest bookingRequest) {
		AdvertisementDraftDetail advertisementDraftDetail = convertToDraftDetailsObject(bookingRequest);
		PersisterWrapper<AdvertisementDraftDetail> persisterWrapper = new PersisterWrapper<AdvertisementDraftDetail>(
				advertisementDraftDetail);
		String key = advertisementDraftDetail.getDraftId();
		producer.push(bookingConfiguration.getAdvertisementDraftApplicationSaveTopic(), key, persisterWrapper);
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
		String key = advertisementDraftDetail.getDraftId();
		producer.push(bookingConfiguration.getAdvertisementDraftApplicationUpdateTopic(), key, persisterWrapper);
	}

	public void deleteDraftApplication(String draftId) {
		AdvertisementDraftDetail advertisementDraftDetail = AdvertisementDraftDetail.builder().draftId(draftId).build();

		PersisterWrapper<AdvertisementDraftDetail> persisterWrapper = new PersisterWrapper<AdvertisementDraftDetail>(
				advertisementDraftDetail);
		producer.push(bookingConfiguration.getAdvertisementDraftApplicationDeleteTopic(), draftId, persisterWrapper);

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
		List<String> draftIds = fetchDraftId(currentTimeMillis);

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

				updateBookingSynchronously(bookingId, "", null, BookingStatusEnum.BOOKING_EXPIRED.toString());

			}
		}

		for (String draftId : draftIds) {

			int rowsDeleted = deletDraftId(currentTimeMillis, draftId);
			if (rowsDeleted > 0) {
				log.info(rowsDeleted + " expired entry(ies) deleted for booking ID: " + draftId);

			}
		}
	}

	public List<String> fetchBookingIds(long currentTimeMillis) {
		String queryBookingId = queryBuilder.getBookingIdToDelete();
		String queryDraftId = AdvertisementBookingQueryBuilder.FETCH_DRAFTID_TO_DELETE;
		if (queryBookingId == null || queryBookingId.isEmpty()) {
			log.error("Query to fetch booking IDs is null or empty.");
			return Collections.emptyList();
		}

		if (queryDraftId == null || queryDraftId.isEmpty()) {
			log.error("Query to fetch draftId IDs is null or empty.");
			return Collections.emptyList();
		}

		try {
			return jdbcTemplate.queryForList(queryBookingId,
					new Object[] { currentTimeMillis, bookingConfiguration.getPaymentTimer() }, String.class);
		} catch (DataAccessException e) {
			log.warn("Error fetching booking IDs: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	public List<String> fetchDraftId(long currentTimeMillis) {

		String queryDraftId = AdvertisementBookingQueryBuilder.FETCH_DRAFTID_TO_DELETE;

		if (queryDraftId == null || queryDraftId.isEmpty()) {
			log.error("Query to fetch draftId IDs is null or empty.");
			return Collections.emptyList();
		}

		try {
			return jdbcTemplate.queryForList(queryDraftId,
					new Object[] { currentTimeMillis, bookingConfiguration.getPaymentTimer() }, String.class);
		} catch (DataAccessException e) {
			log.warn("Error fetching booking IDs: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	private int deleteBookingId(long currentTimeMillis, String bookingId) {
		String deleteQuery = queryBuilder.deleteBookingIdPaymentTimer();

		if (deleteQuery == null || deleteQuery.isEmpty()) {
			log.error("Delete query is null or empty for booking ID: " + bookingId);
			return 0;
		}

		return jdbcTemplate.update(deleteQuery, currentTimeMillis, bookingConfiguration.getPaymentTimer(), bookingId);
	}

	private int deletDraftId(long currentTimeMillis, String draftId) {

		String deleteDraftQuery = AdvertisementBookingQueryBuilder.DRAFTID_DELETE_TIMER;

		if (deleteDraftQuery == null || deleteDraftQuery.isEmpty()) {
			log.error("Delete query is null or empty for booking ID: " + draftId);
			return 0;
		}

		return jdbcTemplate.update(deleteDraftQuery, currentTimeMillis, bookingConfiguration.getPaymentTimer(),
				draftId);
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



		jdbcTemplate.update(bookingQuery, bookingapplication.getBookingStatus(),bookingapplication.getReceiptNo(), bookingapplication.getPaymentDate(),
				auditDetails.getLastModifiedBy(), auditDetails.getLastModifiedTime(),
				bookingapplication.getPermissionLetterFilestoreId(), bookingapplication.getPaymentReceiptFilestoreId(),
				bookingapplication.getBookingId());

		jdbcTemplate.update(cartQuery, cartDetail.getStatus(), auditDetails.getLastModifiedBy(),
				auditDetails.getLastModifiedTime(), bookingapplication.getBookingId());

		jdbcTemplate.update(bookingAuditQuery, bookingapplication.getBookingId());

		jdbcTemplate.update(cartAuditQuery, bookingapplication.getBookingId());
	}

}
