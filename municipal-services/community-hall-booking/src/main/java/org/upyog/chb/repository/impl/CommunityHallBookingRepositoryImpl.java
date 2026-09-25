package org.upyog.chb.repository.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingInitDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingRequestInit;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;
import org.upyog.chb.web.models.VenueSlotSearchCriteria;
import org.upyog.chb.web.models.VenueSlotAvailabilityDetail;
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

	private static final String TIMER_STATUS_ACTIVE = "ACTIVE";

	private final Producer producer;
	private final CommunityHallBookingConfiguration bookingConfiguration;
	private final CommunityHallBookingQueryBuilder queryBuilder;
	private final CommunityHallBookingRowmapper bookingRowmapper;
	private final BookingSlotDetailRowmapper slotDetailRowmapper;
	private final DocumentDetailsRowMapper detailsRowMapper;
	private final JdbcTemplate jdbcTemplate;
	private final CommunityHallSlotAvailabilityRowMapper availabilityRowMapper;

	/**
	 * Creates the repository with required persistence, query, and row-mapping collaborators.
	 *
	 * @param producer Kafka producer for asynchronous booking persistence events
	 * @param bookingConfiguration module configuration (topics, timer values, etc.)
	 * @param queryBuilder SQL query builder for booking and timer operations
	 * @param bookingRowmapper maps booking header rows from search queries
	 * @param slotDetailRowmapper maps slot detail rows for enriched booking responses
	 * @param detailsRowMapper maps uploaded document rows for enriched booking responses
	 * @param jdbcTemplate JDBC executor for synchronous reads and timer maintenance
	 * @param availabilityRowMapper maps slot availability query results
	 */
	public CommunityHallBookingRepositoryImpl(Producer producer,
			CommunityHallBookingConfiguration bookingConfiguration,
			CommunityHallBookingQueryBuilder queryBuilder, CommunityHallBookingRowmapper bookingRowmapper,
			BookingSlotDetailRowmapper slotDetailRowmapper, DocumentDetailsRowMapper detailsRowMapper,
			JdbcTemplate jdbcTemplate, CommunityHallSlotAvailabilityRowMapper availabilityRowMapper) {
		this.producer = producer;
		this.bookingConfiguration = bookingConfiguration;
		this.queryBuilder = queryBuilder;
		this.bookingRowmapper = bookingRowmapper;
		this.slotDetailRowmapper = slotDetailRowmapper;
		this.detailsRowMapper = detailsRowMapper;
		this.jdbcTemplate = jdbcTemplate;
		this.availabilityRowMapper = availabilityRowMapper;
	}

	@Override
	public void saveCommunityHallBooking(VenueBookingRequest bookingRequest) {
		log.info("Saving community hall booking request data for booking no : "
				+ bookingRequest.getVenueBookingApplication().getBookingNo());
		producer.push(bookingConfiguration.getCommunityHallBookingSaveTopic(), bookingRequest);

	}

	@Override
	public void saveCommunityHallBookingInit(VenueBookingRequest bookingRequest) {
		log.info("Saving community hall booking init data : "
				+ bookingRequest.getVenueBookingApplication().getBookingId());
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		VenueBookingDetail bookingDetail = bookingRequest.getVenueBookingApplication();
		CommunityHallBookingRequestInit testPersist = CommunityHallBookingRequestInit.builder()
				.bookingId(bookingDetail.getBookingId()).tenantId(bookingDetail.getTenantId())
				.bookingStatus(bookingDetail.getBookingStatus())
				.bookingDetails(bookingRequest.getVenueBookingApplication())
				.createdBy(requestInfo.getUserInfo().getUuid())
				.createdDate(CommunityHallBookingUtil.getCurrentTimestamp())
				.lastModifiedBy(requestInfo.getUserInfo().getUuid())
				.lastModifiedDate(CommunityHallBookingUtil.getCurrentTimestamp()).build();
		CommunityHallBookingInitDetail bookingPersiter = CommunityHallBookingInitDetail.builder()
				.hallsBookingApplication(testPersist).build();
		producer.push(bookingConfiguration.getCommunityHallBookingInitSaveTopic(), bookingPersiter);

	}

	@Override
	public List<VenueBookingDetail> getBookingDetails(
			VenueBookingSearchCriteria bookingSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getCommunityHallBookingSearchQuery(bookingSearchCriteria, preparedStmtList);

		log.info("getBookingDetails : Final query: " + query);
		log.info("preparedStmtList :  " + preparedStmtList);
		List<VenueBookingDetail> bookingDetails = queryWithExtractor(query, bookingRowmapper, preparedStmtList);

		log.info("Fetched booking details size : " + bookingDetails.size());

		if (bookingDetails.isEmpty()) {
			return bookingDetails;
		}

		HashMap<String, VenueBookingDetail> bookingMap = bookingDetails.stream().collect(Collectors.toMap(
				VenueBookingDetail::getBookingId, Function.identity(), (left, right) -> left, HashMap::new));

		List<String> bookingIds = new ArrayList<>(bookingMap.keySet());

		List<BookingSlotDetail> slotDetails = queryWithExtractor(queryBuilder.getSlotDetailsQuery(bookingIds),
				slotDetailRowmapper, bookingIds);
		slotDetails.forEach(slotDetail ->
			bookingMap.get(slotDetail.getBookingId()).addBookingSlots(slotDetail));

		List<DocumentDetail> documentDetails = queryWithExtractor(queryBuilder.getDocumentDetailsQuery(bookingIds),
				detailsRowMapper, bookingIds);

		documentDetails.forEach(documentDetail ->
			bookingMap.get(documentDetail.getBookingId()).addUploadedDocumentDetailsItem(documentDetail));
		return bookingDetails;
	}

	@Override
	public Integer getBookingCount(@Valid VenueBookingSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getCommunityHallBookingSearchQuery(criteria, preparedStatement);

		if (query == null)
			return 0;

		return queryForInteger(query, preparedStatement);
	}

	@Override
	public void updateBooking(@Valid VenueBookingRequest communityHallsBookingRequest) {
		log.info("Updating community hall booking request data for booking no : "
				+ communityHallsBookingRequest.getVenueBookingApplication().getBookingNo());
		producer.push(bookingConfiguration.getCommunityHallBookingUpdateTopic(), communityHallsBookingRequest);
	}

	@Override
	public List<VenueSlotAvailabilityDetail> getCommunityHallSlotAvailability(
			VenueSlotSearchCriteria criteria) {
		List<Object> paramsList = new ArrayList<>();

		StringBuilder query = queryBuilder.getCommunityHallSlotAvailabilityQuery(criteria, paramsList);

		String hallCodeQuery = " AND ecsd.unit_code ";

		if (StringUtils.isNotBlank(criteria.getUnitCode())) {
			query.append(hallCodeQuery).append(" = ? ");
			paramsList.add(criteria.getUnitCode());
		} else {
			List<String> hallCodes = criteria.getUnitCodes();
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
		List<VenueSlotAvailabilityDetail> availabiltityDetails = queryWithExtractor(query.toString(),
				availabilityRowMapper, paramsList);

		log.info("Fetched slot availabilty details : " + availabiltityDetails);
		return availabiltityDetails;
	}

	@Override
	public void createBookingTimer(VenueSlotSearchCriteria criteria, RequestInfo requestInfo,
			boolean updateBookingStatus) {
		createBookingTimer(criteria, requestInfo, updateBookingStatus, null);
	}

	@Override
	public void createBookingTimer(VenueSlotSearchCriteria criteria, RequestInfo requestInfo,
			boolean updateBookingStatus, List<BookingPaymentTimerDetails> timerDetails) {
		String bookingId = getTimerBookingReference(criteria);
		String createdBy = requestInfo.getUserInfo().getUuid();
		long createdTime = CommunityHallBookingUtil.getCurrentTimestamp();
		String lastModifiedBy = requestInfo.getUserInfo().getUuid();
		long lastModifiedTime = CommunityHallBookingUtil.getCurrentTimestamp();
		LocalTime startTime = LocalTime.parse(criteria.getFromTime());
		LocalTime endTime = LocalTime.parse(criteria.getToTime());
		
		List<Object[]> batchArgs = new ArrayList<>();
		if (timerDetails != null && !timerDetails.isEmpty()) {
			for (BookingPaymentTimerDetails detail : timerDetails) {
				batchArgs.add(new Object[] { detail.getBookingId(), detail.getCreatedBy(), detail.getCreatedTime(),
						detail.getStatus() != null ? detail.getStatus() : TIMER_STATUS_ACTIVE, null, detail.getVenueCode(),
						detail.getUnitCode(), detail.getBookingDate(), detail.getTenantId(), lastModifiedBy,
						lastModifiedTime,startTime , endTime });
			}
		} else {
			var hallCodes = org.upyog.chb.util.CommunityHallSlotCriteriaUtil.resolveHallCodes(criteria);
			var bookingDates = org.upyog.chb.util.CommunityHallSlotCriteriaUtil.resolveBookingDates(criteria);
			for (var date : bookingDates) {
				for (var hallcode : hallCodes) {
					batchArgs.add(new Object[] { bookingId, createdBy, createdTime, TIMER_STATUS_ACTIVE, null,
							criteria.getVenueCode(), hallcode, date, criteria.getTenantId(), lastModifiedBy,
							lastModifiedTime,startTime , endTime});
				}
			}
		}

		log.info("Insert payment timer rows bookingId={} count={}", bookingId, batchArgs.size());
		jdbcTemplate.batchUpdate(CommunityHallBookingQueryBuilder.PAYMENT_TIMER_INSERT_QUERY, batchArgs);

		if (updateBookingStatus) {
			updateBookingSynchronously(bookingId, createdBy, null, BookingStatusEnum.PENDING_FOR_PAYMENT.toString());
		}
	}

	private String getTimerBookingReference(VenueSlotSearchCriteria criteria) {
		return StringUtils.isNotBlank(criteria.getBookingId()) ? criteria.getBookingId() : criteria.getDraftId();
	}
	
	@Override
	public void deleteBookingTimer(String bookingIds, boolean updateBookingStatus) {
		if (bookingIds == null || bookingIds.isEmpty()) {
			log.warn("No booking IDs provided for deletion");
			return;
		}

		List<String> bookingIdList = Arrays.asList(bookingIds.split(","));

		String placeholders = String.join(",", Collections.nCopies(bookingIdList.size(), "?"));
		String deleteQuery = String.format(CommunityHallBookingQueryBuilder.PAYMENT_TIMER_DELETE_FOR_BOOKING_ID_QUERY,
				placeholders);

		log.info("Executing booking timer delete query: {} for booking IDs: {}", deleteQuery, bookingIdList);

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

		return queryWithExtractor(CommunityHallBookingQueryBuilder.PAYMENT_TIMER_SELECT_EXPIRED_QUERY,
				new GenericRowMapper<>(BookingPaymentTimerDetails.class), currentTimeMillis,
				timerValueInMilleconds, TIMER_STATUS_ACTIVE);

	}

	@Override
	@Transactional
	public void updateBookingSynchronously(String bookingId, String uuid, PaymentDetail paymentDetail, String status) {
		
		log.info("updateBookingSynchronously for booking id : {} by uuid : ", bookingId, uuid);
		
		String lastUpdateBy = uuid;
		long lastUpdatedTime = CommunityHallBookingUtil.getCurrentTimestamp();
		String receiptNo = null;
		long receiptDate = 0l;
		if (paymentDetail != null) {
			receiptNo =	paymentDetail.getReceiptNumber();
			receiptDate=	paymentDetail.getReceiptDate();
		}
		
		log.info("Updating payment status of booking id : {} to status : {}", bookingId, status);

		log.info("Params -> status: {}, lastUpdateBy: {}, lastUpdatedTime: {}, receiptNo: {}, receiptDate: {}, bookingId: {}, paymentDetail: {}",
				status, lastUpdateBy, lastUpdatedTime, receiptNo, receiptDate, bookingId, paymentDetail);
		if(paymentDetail != null) {
			Integer updatedRows=jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPDATE_BOOKING_DETAIL_QUERY, status, lastUpdateBy, lastUpdatedTime, receiptNo, receiptDate, bookingId);
			log.info("Updated rows in booking detail table: {}", updatedRows);
		} else {
			Integer updatedRows=jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPDATE_BOOKING_STATUS, status, lastUpdateBy, lastUpdatedTime, bookingId);
			log.info("Updated rows in booking detail table: {}", updatedRows);
		}

		Integer updatedSlotQueryRows=jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPDATE_BOOKING_SLOT_QUERY, status, lastUpdateBy, lastUpdatedTime, bookingId);
		log.info("Updated rows in booking slot table: {}", updatedSlotQueryRows);

		Integer updatedAuditQueryRows=jdbcTemplate.update(CommunityHallBookingQueryBuilder.INSERT_BOOKING_DETAIL_AUDIT_QUERY, bookingId);
		log.info("Updated rows in booking detail audit table: {}", updatedAuditQueryRows);

		Integer updatedSlotAuditQueryRows=jdbcTemplate.update(CommunityHallBookingQueryBuilder.INSERT_SLOT_DETAIL_AUDIT_QUERY, bookingId);
		log.info("Updated rows in slot detail audit table: {}", updatedSlotAuditQueryRows);
	}

	@Override
	public void updateTimerBookingId(String bookingId, String bookingNo, String draftId) {
		if (StringUtils.isBlank(draftId)) {
			return;
		}
		jdbcTemplate.update(CommunityHallBookingQueryBuilder.UPDATE_TIMER_BOOKING_ID_QUERY, bookingId, bookingNo,
				draftId);
	}

	@Override
	public List<BookingPaymentTimerDetails> getBookingTimer(VenueSlotSearchCriteria criteria) {
		
		List<BookingPaymentTimerDetails> paymentTimerList = queryWithExtractor(
				CommunityHallBookingQueryBuilder.GET_BOOKING_PAYMENT_TIMER_VALUE_QUERY,
				new GenericRowMapper<>(BookingPaymentTimerDetails.class), getTimerBookingReference(criteria));
		
		log.info("Booking payment timer query : {} and parmas : {}", CommunityHallBookingQueryBuilder.GET_BOOKING_PAYMENT_TIMER_VALUE_QUERY, getTimerBookingReference(criteria));
		
		return paymentTimerList;
	}
	
	@Override
	public List<BookingPaymentTimerDetails> getBookingTimer(List<String> bookingIds) {
		
		String bookingIdString = String.join(",", bookingIds);
		
		List<BookingPaymentTimerDetails> paymentTimerList = queryWithExtractor(
				CommunityHallBookingQueryBuilder.GET_BOOKING_PAYMENT_TIMER_VALUE_QUERY,
				new GenericRowMapper<>(BookingPaymentTimerDetails.class), bookingIdString);
		
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
	        VenueSlotSearchCriteria criteria) {

	    LocalDate startDate = LocalDate.parse(criteria.getBookingStartDate());
	    LocalDate endDate = LocalDate.parse(criteria.getBookingEndDate());
	    LocalTime startTime = LocalTime.parse(criteria.getFromTime());
	    LocalTime endTime = LocalTime.parse(criteria.getToTime()) ;
	    
	    return queryWithRowMapper(
	        CommunityHallBookingQueryBuilder.SELECT_TIMER_QUERY,
	        (rs, rowNum) -> {
	            BookingPaymentTimerDetails details = new BookingPaymentTimerDetails();
	            details.setBookingId(rs.getString("booking_id"));
	            details.setCreatedBy(rs.getString("createdby"));
	            details.setCreatedTime(rs.getLong("createdtime"));
	            details.setStatus(rs.getString("status"));
	            details.setVenueCode(rs.getString("venue_code"));
	            details.setUnitCode(rs.getString("unit_code"));
	            details.setLastModifiedBy(rs.getString("lastmodifiedby"));
	            details.setLastModifiedTime(rs.getObject("lastmodifiedtime", Long.class));
	            details.setTenantId(rs.getString("tenant_id"));
	            java.sql.Date sqlDate = rs.getDate("booking_date");
	            if (sqlDate != null) {
	                details.setBookingDate(sqlDate.toLocalDate());
	            }
	            return details;
	        },
	        criteria.getTenantId(),
	        criteria.getVenueCode(),
	        criteria.getUnitCode(),
	        startTime,
	        endTime,
	        startDate,
	        endDate
	    );
	}

	/**
	 * Binds positional JDBC parameters on a prepared statement.
	 *
	 * @param ps prepared statement with {@code ?} placeholders
	 * @param parameters values to bind in statement order
	 * @throws SQLException when parameter binding fails
	 */
	private static void bindParameters(PreparedStatement ps, Object[] parameters) throws SQLException {
		for (int i = 0; i < parameters.length; i++) {
			ps.setObject(i + 1, parameters[i]);
		}
	}

	/**
	 * Executes a query using a {@link ResultSetExtractor} and a list of bind parameters.
	 *
	 * @param <T> result type produced by the extractor
	 * @param sql SQL with positional placeholders
	 * @param extractor maps the full result set to a single object (for example a list)
	 * @param parameters bind values in statement order
	 * @return extracted result, never {@code null} for list-based extractors used in this repository
	 */
	private <T> T queryWithExtractor(String sql, ResultSetExtractor<T> extractor, List<?> parameters) {
		Object[] args = parameters.toArray();
		return jdbcTemplate.query(sql, ps -> bindParameters(ps, args), extractor);
	}

	/**
	 * Executes a query using a {@link ResultSetExtractor} and varargs bind parameters.
	 *
	 * @param <T> result type produced by the extractor
	 * @param sql SQL with positional placeholders
	 * @param extractor maps the full result set to a single object
	 * @param parameters bind values in statement order
	 * @return extracted result
	 */
	private <T> T queryWithExtractor(String sql, ResultSetExtractor<T> extractor, Object... parameters) {
		return jdbcTemplate.query(sql, ps -> bindParameters(ps, parameters), extractor);
	}

	/**
	 * Executes a query using a {@link RowMapper} and varargs bind parameters.
	 *
	 * @param <T> row type
	 * @param sql SQL with positional placeholders
	 * @param rowMapper maps each row to {@code T}
	 * @param parameters bind values in statement order
	 * @return list of mapped rows, empty when no rows match
	 */
	private <T> List<T> queryWithRowMapper(String sql, RowMapper<T> rowMapper, Object... parameters) {
		return jdbcTemplate.query(sql, ps -> bindParameters(ps, parameters), rowMapper);
	}

	/**
	 * Runs a scalar integer query (for example {@code COUNT(*)}) and returns {@code 0} when no row is returned.
	 *
	 * @param sql SQL with positional placeholders
	 * @param parameters bind values in statement order
	 * @return first integer column from the first row, or {@code 0} if the result set is empty
	 */
	private int queryForInteger(String sql, List<Object> parameters) {
		Object[] args = parameters.toArray();
		List<Integer> results = jdbcTemplate.query(sql, ps -> bindParameters(ps, args), (rs, rowNum) -> rs.getInt(1));
		return results.isEmpty() ? 0 : results.get(0);
	}


	
}
