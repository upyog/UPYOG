package org.upyog.chb.repository.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.enums.BookingStatusEnum;
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

/**
 * This class implements the CommunityHallBookingRepository interface and provides
 * the database interaction logic for the Community Hall Booking module.
 * 
 * Purpose:
 * - To handle all database operations related to community hall bookings, such as
 *   creating, updating, retrieving, and deleting booking records.
 * 
 * Dependencies:
 * - Producer: Used for publishing events to Kafka topics.
 * - CommunityHallBookingConfiguration: Provides configuration properties for the module.
 * - CommunityHallBookingQueryBuilder: Constructs SQL queries for database operations.
 * - CommunityHallBookingRowmapper: Maps result sets to CommunityHallBookingDetail objects.
 * - BookingSlotDetailRowmapper: Maps result sets to slot detail objects.
 * - DocumentDetailsRowMapper: Maps result sets to document detail objects.
 * - JdbcTemplate: Executes SQL queries and updates against the database.
 * - CommunityHallSlotAvailabilityRowMapper: Maps result sets to slot availability objects.
 * 
 * Features:
 * - Uses Spring's @Repository annotation to mark this class as a data access component.
 * - Uses Lombok's @Slf4j annotation for logging database operations and errors.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever the
 *   CommunityHallBookingRepository interface is required.
 */

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
	public void createBookingTimer(CommunityHallSlotSearchCriteria criteria, RequestInfo requestInfo,
			boolean updateBookingStatus) {
		String bookingId = criteria.getBookingId();
		String createdBy = requestInfo.getUserInfo().getUuid();
		long createdTime = CommunityHallBookingUtil.getCurrentTimestamp();
		String communitycode = criteria.getCommunityHallCode();
		String hallcode = criteria.getHallCode();
		String lastModifiedBy = requestInfo.getUserInfo().getUuid();
		long lastModifiedTime = CommunityHallBookingUtil.getCurrentTimestamp();
		String tenantId = criteria.getTenantId();

		// Parse bookingStartDate and bookingEndDate into LocalDate
		LocalDate startDate = LocalDate.parse(criteria.getBookingStartDate());
		LocalDate endDate = LocalDate.parse(criteria.getBookingEndDate());

//		// Log the information at the beginning of the method
//		log.info("Executing Insert Query with the following details: ");
//		log.info("Booking ID: {}", bookingId);
//		log.info("Created By: {}", createdBy);
//		log.info("Created Time: {}", createdTime);
//
//		// Query execution
//		String query = CommunityHallBookingQueryBuilder.PAYMENT_TIMER_INSERT_QUERY;
//		jdbcTemplate.update(query, bookingId, createdBy, createdTime, "ACTIVE", null, communitycode, hallcode, lastModifiedBy, lastModifiedTime);

		// Log the information
		log.info("Executing Insert Query with the following details: ");
		log.info("Booking ID: {}", bookingId);
		log.info("Created By: {}", createdBy);
		log.info("Created Time: {}", createdTime);
		log.info("Community Code: {}", communitycode);
		log.info("Hall Code: {}", hallcode);
		log.info("Date Range: {} to {}", startDate, endDate);

		// Iterate through the date range
		List<Object[]> batchArgs = new ArrayList<>();
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			batchArgs.add(new Object[] { bookingId, createdBy, createdTime, "ACTIVE", // Status
					null, // Booking No (optional, replace with actual value if available)
					communitycode, hallcode, date, // Booking Date
					tenantId, lastModifiedBy, lastModifiedTime });
		}

		// Execute batch insert
		String query = CommunityHallBookingQueryBuilder.PAYMENT_TIMER_INSERT_QUERY;
		jdbcTemplate.batchUpdate(query, batchArgs);

		// Log after the query execution
		log.info("Insert Query Executed Successfully for Booking ID: {}", bookingId);

		if (updateBookingStatus) {
			updateBookingSynchronously(bookingId, createdBy, null, BookingStatusEnum.PENDING_FOR_PAYMENT.toString());
		}

	}
	
//	@Override
//	public void deleteBookingTimer(String bookingId, boolean updateBookingStatus) {
//		log.info("Deleting booking timer query : {} booking id : {} ", CommunityHallBookingQueryBuilder.PAYMENT_TIMER_DELETE_FOR_BOOKING_ID_QUERY, bookingId);
//		jdbcTemplate.update(CommunityHallBookingQueryBuilder.PAYMENT_TIMER_DELETE_FOR_BOOKING_ID_QUERY, bookingId);
//		if(updateBookingStatus) {
//			log.info("updating booking status of booking ids : {} ", bookingId);
//			updateBookingSynchronously(bookingId, "SYSTEM_USER", null, BookingStatusEnum.EXPIRED.toString());
//		}
//
//	}
	
	@Override
	public void deleteBookingTimer(String bookingIds, boolean updateBookingStatus) {
		if (bookingIds == null || bookingIds.isEmpty()) {
			log.warn("No booking IDs provided for deletion");
			return;
		}

		List<String> bookingIdList = Arrays.asList(bookingIds.split(","));

		// Generate placeholders for the number of booking IDs
		String placeholders = String.join(",", Collections.nCopies(bookingIdList.size(), "?"));
		String deleteQuery = String.format(CommunityHallBookingQueryBuilder.PAYMENT_TIMER_DELETE_FOR_BOOKING_ID_QUERY,
				placeholders);

		log.info("Executing booking timer delete query: {} for booking IDs: {}", deleteQuery, bookingIdList);

		// Convert the list to an array for use in the query
		jdbcTemplate.update(deleteQuery, bookingIdList.toArray());

		if (updateBookingStatus) {
			log.info("Updating booking status for booking IDs: {}", bookingIdList);

			for (String bookingId : bookingIdList) {
				updateBookingSynchronously(bookingId.trim(), "SYSTEM_USER", null, BookingStatusEnum.EXPIRED.toString());
			}
		}
	}

	
	@Override
	public List<BookingPaymentTimerDetails> getExpiredBookingTimer() {
		log.info("Deleting booking timer for expired bookings : deleteExpiredBookingTimer");
		long currentTimeMillis = CommunityHallBookingUtil.getCurrentTimestamp();

		long timerValueInMilleconds = Long.parseLong(bookingConfiguration.getBookingPaymentTimerValue());

		timerValueInMilleconds = timerValueInMilleconds * 60 * 1000;

		List<BookingPaymentTimerDetails> bookingPaymentTimerDetails = jdbcTemplate.query(CommunityHallBookingQueryBuilder.PAYMENT_TIMER_SELECT_EXPIRED_QUERY, new Object[]{currentTimeMillis,
				timerValueInMilleconds, "ACTIVE"},
				new GenericRowMapper<>(BookingPaymentTimerDetails.class));
		return bookingPaymentTimerDetails;

	}

	@Override
	public void updateBookingSynchronously(String bookingId, String uuid, PaymentDetail paymentDetail, String status) {
		
		log.info("updateBookingSynchronously for booking id : {} by uuid : ", bookingId, uuid);
		
		String lastUpdateBy = uuid;
		long lastUpdatedTime = CommunityHallBookingUtil.getCurrentTimestamp();
		String receiptNo = null;
		long receiptDate = 0l;
		//Update payment date and receipt no on successful payment when payment detail object is received
		if (paymentDetail != null) {
			receiptNo =	paymentDetail.getReceiptNumber();
			receiptDate=	paymentDetail.getReceiptDate();
		}
		
		log.info("Updating payment status of booking id : {} to status : {}", bookingId, status);
		
		if(paymentDetail != null) {
			jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPDATE_BOOKING_DETAIL_QUERY, status, lastUpdateBy, lastUpdatedTime, receiptNo, receiptDate, bookingId);
		} else {
			jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPDATE_BOOKING_STATUS, status, lastUpdateBy, lastUpdatedTime, bookingId);
		}
		
		jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPDATE_BOOKING_SLOT_QUERY, status, lastUpdateBy, lastUpdatedTime, bookingId);
		
		jdbcTemplate.update(CommunityHallBookingQueryBuilder.INSERT_BOOKING_DETAIL_AUDIT_QUERY, bookingId);
		jdbcTemplate.update(CommunityHallBookingQueryBuilder.INSERT_SLOT_DETAIL_AUDIT_QUERY, bookingId);
	}

	@Override
	public List<BookingPaymentTimerDetails> getBookingTimer(CommunityHallSlotSearchCriteria criteria) {
		
		List<BookingPaymentTimerDetails> paymentTimerList = jdbcTemplate.query(CommunityHallBookingQueryBuilder.GET_BOOKING_PAYMENT_TIMER_VALUE_QUERY, 
				new Object[]{criteria.getBookingId()},
				new GenericRowMapper<>(BookingPaymentTimerDetails.class));
		
		log.info("Booking payment timer query : {} and parmas : {}", CommunityHallBookingQueryBuilder.GET_BOOKING_PAYMENT_TIMER_VALUE_QUERY, criteria.getBookingId());
		
		return paymentTimerList;
	}
	
	@Override
	public List<BookingPaymentTimerDetails> getBookingTimer(List<String> bookingIds) {
		
		String bookingIdString = String.join(",", bookingIds);
		
		List<BookingPaymentTimerDetails> paymentTimerList = jdbcTemplate.query(CommunityHallBookingQueryBuilder.GET_BOOKING_PAYMENT_TIMER_VALUE_QUERY, 
				new Object[]{bookingIdString},
				new GenericRowMapper<>(BookingPaymentTimerDetails.class));
		
		log.info("Booking payment timer query : {} and parmas : {}", CommunityHallBookingQueryBuilder.GET_BOOKING_PAYMENT_TIMER_VALUE_QUERY, bookingIdString);
		return paymentTimerList;
	}
	
    /**
     * Updates the createdTime field for a given booking.
     */
	@Override
    public int updateBookingTimer(String bookingId) {
       return jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPADTE_BOOKING_PAYMENT_TIMER_VALUE_QUERY, "PENDING", bookingId);
    }

	@Override
	public List<BookingPaymentTimerDetails> getBookingTimerByCreatedBy(RequestInfo info,
	        CommunityHallSlotSearchCriteria criteria) {

	    // Parse bookingStartDate and bookingEndDate into SQL-compatible Date objects
	    Date startDate = Date.valueOf(criteria.getBookingStartDate()); // String to SQL Date
	    Date endDate = Date.valueOf(criteria.getBookingEndDate());     // String to SQL Date

	    // Execute query with updated criteria
	    List<BookingPaymentTimerDetails> timerDetails = jdbcTemplate.query(
	        CommunityHallBookingQueryBuilder.SELECT_TIMER_QUERY,
	        new Object[] {
	            criteria.getTenantId(), 
	            criteria.getCommunityHallCode(),
	            criteria.getHallCode(),
	            startDate,
	            endDate
	        },
	        (rs, rowNum) -> {
	            BookingPaymentTimerDetails details = new BookingPaymentTimerDetails();
	            details.setBookingId(rs.getString("booking_id"));
	            details.setCreatedBy(rs.getString("createdby"));
	            details.setCreatedTime(rs.getLong("createdtime"));
	            details.setStatus(rs.getString("status"));
	            details.setCommunityHallcode(rs.getString("community_hall_code"));
	            details.setHallcode(rs.getString("hall_code"));
	            details.setLastModifiedBy(rs.getString("lastmodifiedby"));
	            details.setLastModifiedTime(rs.getObject("lastmodifiedtime", Long.class));
	            details.setTenantId(rs.getString("tenant_id"));
	            java.sql.Date sqlDate = rs.getDate("booking_date");
	            if (sqlDate != null) {
	                details.setBookingDate(sqlDate.toLocalDate());
	            }
	            return details;
	        }
	    );

	    return timerDetails;
	}


	
}
