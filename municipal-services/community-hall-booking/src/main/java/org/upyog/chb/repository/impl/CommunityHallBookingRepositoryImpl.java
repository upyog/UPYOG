package org.upyog.chb.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.kafka.producer.Producer;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.repository.GenericRowMapper;
import org.upyog.chb.repository.querybuilder.CommunityHallBookingQueryBuilder;
import org.upyog.chb.repository.rowmapper.BookingSlotDetailRowmapper;
import org.upyog.chb.repository.rowmapper.CommunityHallBookingRowmapper;
import org.upyog.chb.repository.rowmapper.CommunityHallSlotAvailabilityRowMapper;
import org.upyog.chb.repository.rowmapper.DocumentDetailsRowMapper;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;
import org.upyog.chb.web.models.BookingSlotDetail;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingInitDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingRequestInit;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;
import org.upyog.chb.web.models.DocumentDetail;

import digit.models.coremodels.PaymentDetail;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CommunityHallBookingRepositoryImpl implements CommunityHallBookingRepository {

	@Autowired
	private Producer producer;
	@Autowired
	private CommunityHallBookingConfiguration bookingConfiguration;
	@Autowired
	private CommunityHallBookingQueryBuilder queryBuilder;
	@Autowired
	private CommunityHallBookingRowmapper bookingRowmapper;
	
	@Autowired
	private BookingSlotDetailRowmapper slotDetailRowmapper;

	@Autowired
	private DocumentDetailsRowMapper detailsRowMapper;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private CommunityHallSlotAvailabilityRowMapper availabilityRowMapper;

	@Override
	public void saveCommunityHallBooking(CommunityHallBookingRequest bookingRequest) {
		log.info("Saving community hall booking request data for booking no : "
				+ bookingRequest.getHallsBookingApplication().getBookingNo());
		producer.push(bookingConfiguration.getCommunityHallBookingSaveTopic(), bookingRequest);

	}

	@Override
	public void saveCommunityHallBookingInit(CommunityHallBookingRequest bookingRequest) {
		log.info("Saving community hall booking init data : "
				+ bookingRequest.getHallsBookingApplication().getBookingId());
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		CommunityHallBookingRequestInit testPersist = CommunityHallBookingRequestInit.builder()
				.bookingId(bookingDetail.getBookingId()).tenantId(bookingDetail.getTenantId())
				.bookingStatus(bookingDetail.getBookingStatus())
				.bookingDetails(bookingRequest.getHallsBookingApplication())
				.createdBy(requestInfo.getUserInfo().getUuid())
				.createdDate(CommunityHallBookingUtil.getCurrentTimestamp())
				.lastModifiedBy(requestInfo.getUserInfo().getUuid())
				.lastModifiedDate(CommunityHallBookingUtil.getCurrentTimestamp()).build();
		CommunityHallBookingInitDetail bookingPersiter = CommunityHallBookingInitDetail.builder()
				.hallsBookingApplication(testPersist).build();
		producer.push(bookingConfiguration.getCommunityHallBookingInitSaveTopic(), bookingPersiter);

	}

	@Override
	public List<CommunityHallBookingDetail> getBookingDetails(
			CommunityHallBookingSearchCriteria bookingSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getCommunityHallBookingSearchQuery(bookingSearchCriteria, preparedStmtList);

		log.info("getBookingDetails : Final query: " + query);
		log.info("preparedStmtList :  " + preparedStmtList);
		List<CommunityHallBookingDetail> bookingDetails = jdbcTemplate.query(query, preparedStmtList.toArray(),
				bookingRowmapper);

		log.info("Fetched booking details size : " + bookingDetails.size());

		if (bookingDetails.size() == 0) {
			return bookingDetails;
		}

		HashMap<String, CommunityHallBookingDetail> bookingMap = bookingDetails.stream().collect(Collectors.toMap(
				CommunityHallBookingDetail::getBookingId, Function.identity(), (left, right) -> left, HashMap::new));

		List<String> bookingIds = new ArrayList<String>();
		bookingIds.addAll(bookingMap.keySet());

		List<BookingSlotDetail> slotDetails = jdbcTemplate.query(queryBuilder.getSlotDetailsQuery(bookingIds),
				bookingIds.toArray(), slotDetailRowmapper);
		slotDetails.stream().forEach(slotDetail -> {
			bookingMap.get(slotDetail.getBookingId()).addBookingSlots(slotDetail);
		});

		List<DocumentDetail> documentDetails = jdbcTemplate.query(queryBuilder.getDocumentDetailsQuery(bookingIds),
				bookingIds.toArray(), detailsRowMapper);

		documentDetails.stream().forEach(documentDetail -> {
			bookingMap.get(documentDetail.getBookingId()).addUploadedDocumentDetailsItem(documentDetail);
		});
		return bookingDetails;
	}

	@Override
	public Integer getBookingCount(@Valid CommunityHallBookingSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getCommunityHallBookingSearchQuery(criteria, preparedStatement);

		if (query == null)
			return 0;

		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
	}

	@Override
	public void updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
		log.info("Updating community hall booking request data for booking no : "
				+ communityHallsBookingRequest.getHallsBookingApplication().getBookingNo());
		producer.push(bookingConfiguration.getCommunityHallBookingUpdateTopic(), communityHallsBookingRequest);
	}

	@Override
	public List<CommunityHallSlotAvailabilityDetail> getCommunityHallSlotAvailability(
			CommunityHallSlotSearchCriteria criteria) {
		List<Object> paramsList = new ArrayList<>();

		StringBuilder query = queryBuilder.getCommunityHallSlotAvailabilityQuery(criteria, paramsList);

		String hallCodeQuery = " AND ecsd.hall_code ";

		if (StringUtils.isNotBlank(criteria.getHallCode())) {
			query.append(hallCodeQuery).append(" = ? ");
			paramsList.add(criteria.getHallCode());
		} else {
			List<String> hallCodes = criteria.getHallCodes();
			query.append(hallCodeQuery).append(" IN ( ");
			int i = 0;
			while (i < hallCodes.size()) {
				query.append(" ? ");
				if (i != hallCodes.size() - 1) {
					query.append(" , ");
				}

				paramsList.add(hallCodes.get(i));

				i++;
			}
			query.append(" ) ");
		}

		log.info("getBookingDetails : Final query: " + query);
		log.info("paramsList : " + paramsList);
		List<CommunityHallSlotAvailabilityDetail> availabiltityDetails = jdbcTemplate.query(query.toString(),
				paramsList.toArray(), availabilityRowMapper);

		log.info("Fetched slot availabilty details : " + availabiltityDetails);
		return availabiltityDetails;
	}

	@Override
	public void createBookingTimer(CommunityHallSlotSearchCriteria criteria, RequestInfo requestInfo) {
		String bookingId = criteria.getBookingId();
		String createdBy = requestInfo.getUserInfo().getUuid();
		long createdTime = CommunityHallBookingUtil.getCurrentTimestamp();
		String lastModifiedBy = requestInfo.getUserInfo().getUuid();
		long lastModifiedTime = CommunityHallBookingUtil.getCurrentTimestamp();

		// Log the information at the beginning of the method
		log.info("Executing Insert Query with the following details: ");
		log.info("Booking ID: {}", bookingId);
		log.info("Created By: {}", createdBy);
		log.info("Created Time: {}", createdTime);
		log.info("Last Modified By: {}", lastModifiedBy);
		log.info("Last Modified Time: {}", lastModifiedTime);

		// Query execution
		String query = CommunityHallBookingQueryBuilder.PAYMENT_TIMER_INSERT_QUERY;
		jdbcTemplate.update(query, bookingId, createdBy, createdTime, lastModifiedBy, lastModifiedTime);

		// Log after the query execution
		log.info("Insert Query Executed Successfully for Booking ID: {}", bookingId);
	}

	@Override
	public void deleteBookingTimer(String bookingId) {
		log.info("Deleting booking timer for booking id : {}", bookingId);
		jdbcTemplate.update(CommunityHallBookingQueryBuilder.PAYMENT_TIMER_DELETE_FOR_BOOKING_ID_QUERY, bookingId);

	}
	
	@Override
	public int deleteExpiredBookingTimer() {
		long currentTimeMillis = CommunityHallBookingUtil.getCurrentTimestamp();

		long timerValueInMilleconds = Long.parseLong(bookingConfiguration.getBookingPaymentTimerValue());

		timerValueInMilleconds = timerValueInMilleconds * 60 * 1000;

		int rows = jdbcTemplate.update(CommunityHallBookingQueryBuilder.PAYMENT_TIMER_DELETE_EXPIRED_QUERY, currentTimeMillis,
				timerValueInMilleconds);
		return rows;

	}

	@Override
	public void updateBookingSynchronously(CommunityHallBookingRequest communityHallsBookingRequest, PaymentDetail paymentDetail, String status) {
		String bookingId = communityHallsBookingRequest.getHallsBookingApplication().getBookingId();
		String lastUpdateBy = communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid();
		long lastUpdatedTime = CommunityHallBookingUtil.getCurrentTimestamp();
		String receiptNo = null;
		long receiptDate = 0l;
		//Update payment date and receipt no on successful payment when payment detail object is received
		if (paymentDetail != null) {
			receiptNo =	paymentDetail.getReceiptNumber();
			receiptDate=	paymentDetail.getReceiptDate();
		}
		/*set booking_status = ?, lastmodifiedby = ?, lastmodifiedtime = ? "
			+ ", receipt_no = ?, payment_date = ? where booking_id = ?*/
		
		jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPDATE_BOOKING_DETAIL_QUERY, status, lastUpdateBy, lastUpdatedTime, receiptNo, receiptDate, bookingId);
		/*set status = ?, lastmodifiedby = ?, lastmodifiedtime = ? "
			+ " where booking_id = ?*/
		jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPDATE_BOOKING_SLOT_QUERY, status, lastUpdateBy, lastUpdatedTime, bookingId);
		
		jdbcTemplate.update(CommunityHallBookingQueryBuilder.INSERT_BOOKING_DETAIL_AUDIT_QUERY, bookingId);
		jdbcTemplate.update(CommunityHallBookingQueryBuilder.INSERT_SLOT_DETAIL_AUDIT_QUERY, bookingId);
	}

	@Override
	public List<BookingPaymentTimerDetails> getBookingTimer(CommunityHallSlotSearchCriteria criteria) {
		
		List<BookingPaymentTimerDetails> paymentTimerList = jdbcTemplate.query(CommunityHallBookingQueryBuilder.GET_BOOKING_PAYMENT_TIMER_VALUE_QUERY, 
				new Object[]{criteria.getBookingId()},
				new GenericRowMapper<>(BookingPaymentTimerDetails.class));
		return paymentTimerList;
	}
	
    /**
     * Updates the lastModifiedBy and lastModifiedTime fields for a given booking.
  
     */
	@Override
    public int updateBookingTimer(String bookingId, String lastModifiedBy) {
       long lastModifiedTime = CommunityHallBookingUtil.getCurrentTimestamp();
       return jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPADTE_BOOKING_PAYMENT_TIMER_VALUE_QUERY, lastModifiedBy, lastModifiedTime, bookingId);
    }

}
