package org.upyog.chb.repository.querybuilder;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;
import org.upyog.chb.web.models.VenueSlotSearchCriteria;

/**
 * This class is responsible for building SQL queries for the Community Hall Booking module.
 * 
 * Purpose:
 * - To centralize and manage all SQL query construction logic for the module.
 * - To dynamically build queries based on search criteria and other parameters.
 * 
 * Dependencies:
 * - CommunityHallBookingConfiguration: Provides configuration properties for query construction.
 * - CommunityHallBookingSearchCriteria: Contains search parameters for booking-related queries.
 * - CommunityHallSlotSearchCriteria: Contains search parameters for slot-related queries.
 * 
 * Features:
 * - Provides static query templates for booking details, slot details, and document details.
 * - Dynamically appends conditions to the base queries based on the provided search criteria.
 * - Ensures that queries are optimized and secure by using parameterized inputs.
 * 
 * Query Templates:
 * 1. bookingDetailsQuery:
 *    - Retrieves booking details along with applicant and address information.
 *    - Joins multiple tables to fetch comprehensive booking data.
 * 
 * 2. slotDetailsQuery:
 *    - Retrieves slot details for the specified booking IDs.
 * 
 * 3. documentDetailsQuery:
 *    - Retrieves document details for the specified booking IDs.
 * 
 * Usage:
 * - This class is used by the repository layer to execute database operations.
 * - It ensures consistency and reusability of query logic across the application.
 */
@Component
public class CommunityHallBookingQueryBuilder {

	private static final String SLOT_DETAIL_JOIN =
			" join public.eg_chb_slot_detail ecsd ON ecsd.booking_id = ecbd.booking_id ";

	private static final String DATE_CAST = " ?::DATE ";

	private static final String BOOKING_DETAILS_QUERY = """
			SELECT ecbd.booking_id, booking_no, payment_date, application_date, tenant_id, venue_code,venue_type,
			booking_status, special_category, purpose, purpose_description, receipt_no, ecbd.createdby, ecbd.createdtime,
			ecbd.lastmodifiedby, ecbd.lastmodifiedtime,ecbd.permission_letter_filestore_id, ecbd.payment_receipt_filestore_id,
			appl.applicant_detail_id, applicant_name, applicant_email_id, applicant_mobile_no,
			applicant_alternate_mobile_no, account_no, ifsc_code, bank_name, bank_branch_name,
			account_holder_name, address_id, door_no, house_no, address_line_1,
			landmark, city, city_code, pincode, street_name, locality, locality_code
			FROM public.eg_chb_booking_detail ecbd
			join public.eg_chb_applicant_detail appl on ecbd.booking_id = appl.booking_id
			join public.eg_chb_address_detail addr on appl.applicant_detail_id = addr.applicant_detail_id """;

	private static final String SLOT_DETAILS_QUERY =
			"select * from public.eg_chb_slot_detail where booking_id in (";

	private static final String DOCUMENT_DETAILS_QUERY =
			"select * from public.eg_chb_document_detail  where booking_id in (";

	private static final String PAGINATION_WRAPPER =
			"SELECT * FROM (SELECT *, DENSE_RANK() OVER (ORDER BY application_date DESC) offset_ FROM ({}) result) result_offset WHERE offset_ > ? AND offset_ <= ?";

	private static final String COMMUNITY_HALL_SLOTS_AVAILABILITY_QUERY = """
			SELECT ecbd.tenant_id, ecbd.venue_code, ecsd.capacity, ecsd.unit_code, ecsd.status, ecsd.booking_date, ecsd.booking_from_time, ecsd.booking_to_time
			FROM eg_chb_booking_detail ecbd
			join eg_chb_slot_detail ecsd on ecbd.booking_id = ecsd.booking_id
			LEFT JOIN eg_chb_payment_timer ecpt ON ecbd.booking_id = ecpt.booking_id
			 where  ecbd.tenant_id= ? and ecbd.venue_code = ?
			 and ecsd.status in ('BOOKED', 'PENDING_FOR_PAYMENT','BOOKING_CREATED') and
			ecsd.booking_date >= CAST(? AS DATE) and ecsd.booking_date <= CAST(? AS DATE) """;

	private static final String BOOKING_DETAILS_COUNT_QUERY = """
			SELECT count(ecbd.booking_id)
			FROM public.eg_chb_booking_detail ecbd
			join public.eg_chb_applicant_detail appl on ecbd.booking_id = appl.booking_id
			""";

	public static final String PAYMENT_TIMER_INSERT_QUERY = "INSERT INTO eg_chb_payment_timer(booking_id, createdby, createdtime, status, booking_no, venue_code, unit_code, booking_date, tenant_id, lastmodifiedby, lastmodifiedtime,start_time , end_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? ,?);";

	public static final String PAYMENT_TIMER_DELETE_FOR_BOOKING_ID_QUERY = "DELETE FROM eg_chb_payment_timer WHERE booking_id IN (%s)";
	
	public static final String PAYMENT_TIMER_SELECT_EXPIRED_QUERY = "SELECT * FROM eg_chb_payment_timer WHERE ? - createdtime > ? and status = ?";
	
	public static final String UPDATE_BOOKING_DETAIL_QUERY = "update eg_chb_booking_detail set booking_status = ?, lastmodifiedby = ?, lastmodifiedtime = ? "
			+ ", receipt_no = ?, payment_date = ? where booking_id in (?) ";
	
	public static final String UPDATE_BOOKING_SLOT_QUERY = "update eg_chb_slot_detail set status = ?, lastmodifiedby = ?, lastmodifiedtime = ? "
			+ " where booking_id in (?)  ";
	
	public static final String INSERT_BOOKING_DETAIL_AUDIT_QUERY = "INSERT INTO public.eg_chb_booking_detail_audit SELECT * FROM public.eg_chb_booking_detail WHERE booking_id in (?) ";
	
	public static final String INSERT_SLOT_DETAIL_AUDIT_QUERY = "INSERT INTO public.eg_chb_slot_detail_audit SELECT * FROM public.eg_chb_slot_detail WHERE booking_id in (?) ";
	
	public static final String GET_BOOKING_PAYMENT_TIMER_VALUE_QUERY = "SELECT * from eg_chb_payment_timer where  booking_id in (?)";
	
	
	public static final String UPADTE_BOOKING_PAYMENT_TIMER_VALUE_QUERY = "UPDATE eg_chb_payment_timer " +
            " SET status = ? WHERE booking_id = ?";

	public static final String UPDATE_TIMER_BOOKING_ID_QUERY = "UPDATE eg_chb_payment_timer "
			+ " SET booking_id = ?, booking_no = ? WHERE booking_id = ?";
	
	public static final String UPDATE_BOOKING_STATUS =  "update eg_chb_booking_detail set booking_status = ?, lastmodifiedby = ?, lastmodifiedtime = ? "
			+ " where booking_id in (?) ";
	
	public static final String SELECT_TIMER_QUERY = """
			SELECT *
			FROM eg_chb_payment_timer
			WHERE tenant_id = ?
			  AND venue_code = ?
			  AND unit_code = ?
			  AND start_time = ? AND end_time = ?
			  AND booking_date BETWEEN ? AND ?;""";

	private final CommunityHallBookingConfiguration bookingConfiguration;

	public CommunityHallBookingQueryBuilder(CommunityHallBookingConfiguration bookingConfiguration) {
		this.bookingConfiguration = bookingConfiguration;
	}
	
	/**
	 * To give the Search query based on the requirements.
	 * 
	 * @param criteria         Community Hall booking search criteria
	 * @param preparedStmtList values to be replaced on the query
	 * @return Final Search Query
	 */
	public String getCommunityHallBookingSearchQuery(VenueBookingSearchCriteria criteria,
			List<Object> preparedStmtList) {
		StringBuilder builder = createSearchQueryBuilder(criteria);
		appendSearchFilters(criteria, builder, preparedStmtList);

		if (criteria.isCountCall()) {
			return builder.toString();
		}
		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
	}

	private StringBuilder createSearchQueryBuilder(VenueBookingSearchCriteria criteria) {
		StringBuilder builder = criteria.isCountCall()
				? new StringBuilder(BOOKING_DETAILS_COUNT_QUERY)
				: new StringBuilder(BOOKING_DETAILS_QUERY);

		if (criteria.getFromDate() != null || criteria.getToDate() != null) {
			builder.append(SLOT_DETAIL_JOIN);
		}
		return builder;
	}

	private void appendSearchFilters(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		appendTenantFilter(criteria, builder, preparedStmtList);
		appendBookingIdsFilter(criteria, builder, preparedStmtList);
		appendBookingNoFilter(criteria, builder, preparedStmtList);
		appendStatusFilter(criteria, builder, preparedStmtList);
		appendVenueCodeFilter(criteria, builder, preparedStmtList);
		appendMobileNumberFilter(criteria, builder, preparedStmtList);
		appendCreatedByFilter(criteria, builder, preparedStmtList);
		appendBookingDateRangeFilter(criteria, builder, preparedStmtList);
		appendVenueTypeFilter(criteria,builder,preparedStmtList);
		
	}

	
	private void appendVenueTypeFilter(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		if (criteria.getVenueType() == null) {
			return;
		}

		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" ecbd.venue_type=? ");
		preparedStmtList.add(criteria.getVenueType());
	}
	
	private void appendTenantFilter(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		if (criteria.getTenantId() == null) {
			return;
		}
		if (criteria.getTenantId().split("\\.").length == 1) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.tenant_id like ?");
			preparedStmtList.add('%' + criteria.getTenantId() + '%');
			return;
		}
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" ecbd.tenant_id=? ");
		preparedStmtList.add(criteria.getTenantId());
	}

	private void appendBookingIdsFilter(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		List<String> ids = criteria.getBookingIds();
		if (CollectionUtils.isEmpty(ids)) {
			return;
		}
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" ecbd.booking_id IN (").append(createQueryParams(ids)).append(")");
		addToPreparedStatement(preparedStmtList, ids);
	}

	private void appendBookingNoFilter(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		String bookingNo = criteria.getBookingNo();
		if (bookingNo == null) {
			return;
		}
		List<String> applicationNos = Arrays.asList(bookingNo.split(","));
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" ecbd.booking_no IN (").append(createQueryParams(applicationNos)).append(")");
		addToPreparedStatement(preparedStmtList, applicationNos);
	}

	private void appendStatusFilter(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		String status = criteria.getStatus();
		if (status == null) {
			return;
		}
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" ecbd.booking_status =  ? ");
		preparedStmtList.add(status);
	}

	private void appendVenueCodeFilter(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		if (criteria.getVenueCode() == null) {
			return;
		}
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" ecbd.venue_code =  ? ");
		preparedStmtList.add(criteria.getVenueCode());
	}

	private void appendMobileNumberFilter(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		String mobileNo = criteria.getMobileNumber();
		if (mobileNo == null) {
			return;
		}
		List<String> mobileNos = Arrays.asList(mobileNo.split(","));
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" appl.applicant_mobile_no IN (").append(createQueryParams(mobileNos)).append(")");
		addToPreparedStatement(preparedStmtList, mobileNos);
	}

	private void appendCreatedByFilter(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		List<String> createdBy = criteria.getCreatedBy();
		if (CollectionUtils.isEmpty(createdBy)) {
			return;
		}
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" ecbd.createdby IN (").append(createQueryParams(createdBy)).append(")");
		addToPreparedStatement(preparedStmtList, createdBy);
	}

	private void appendBookingDateRangeFilter(VenueBookingSearchCriteria criteria, StringBuilder builder,
			List<Object> preparedStmtList) {
		if (criteria.getFromDate() != null && criteria.getToDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecsd.booking_date BETWEEN ").append(DATE_CAST).append(" AND ")
					.append(DATE_CAST);
			preparedStmtList.add(criteria.getFromDate());
			preparedStmtList.add(criteria.getToDate());
			return;
		}
		if (criteria.getFromDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecsd.booking_date >= ").append(DATE_CAST);
			preparedStmtList.add(criteria.getFromDate());
			return;
		}
		if (criteria.getToDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecsd.booking_date <= ").append(DATE_CAST);
			preparedStmtList.add(criteria.getToDate());
		}
	}
	

	/**
	 * add if clause to the Statement if required or else AND
	 * 
	 * @param values
	 * @param queryString
	 */
	private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND");
		}
	}

	/**
	 * add values to the preparedStatment List
	 * 
	 * @param preparedStmtList
	 * @param ids
	 */
	private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
		ids.forEach(preparedStmtList::add);
	}

	/**
	 * produce a query input for the multiple values
	 * 
	 * @param ids
	 * @return
	 */
	private Object createQueryParams(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	/**
	 * 
	 * @param query            prepared Query
	 * @param preparedStmtList values to be replased on the query
	 * @param criteria         bpa search criteria
	 * @return the query by replacing the placeholders with preparedStmtList
	 */
	private String addPaginationWrapper(String query, List<Object> preparedStmtList,
			VenueBookingSearchCriteria criteria) {

		int limit = bookingConfiguration.getDefaultLimit();
		int offset = bookingConfiguration.getDefaultOffset();
		String finalQuery = PAGINATION_WRAPPER.replace("{}", query);

		if (criteria.getLimit() == null && criteria.getOffset() == null) {
			limit = bookingConfiguration.getMaxSearchLimit();
		}

		if (criteria.getLimit() != null && criteria.getLimit() <= bookingConfiguration.getMaxSearchLimit())
			limit = criteria.getLimit();

		if (criteria.getLimit() != null && criteria.getLimit() > bookingConfiguration.getMaxSearchLimit()) {
			limit = bookingConfiguration.getMaxSearchLimit();
		}

		if (criteria.getOffset() != null)
			offset = criteria.getOffset();

		if (limit == -1) {
			finalQuery = finalQuery.replace("WHERE offset_ > ? AND offset_ <= ?", "");
		} else {
			preparedStmtList.add(offset);
			preparedStmtList.add(limit + offset);
		}

		return finalQuery;

	}

	/**
	 * Builds a query to fetch slot rows for the provided booking ids.
	 *
	 * @param bookingIds booking ids to include in the query
	 * @return SQL query string with parameter placeholders for the booking ids
	 */
	public String getSlotDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(SLOT_DETAILS_QUERY);
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();

	}

	/**
	 * Builds a query to fetch document rows for the provided booking ids.
	 *
	 * @param bookingIds booking ids to include in the query
	 * @return SQL query string with parameter placeholders for the booking ids
	 */
	public String getDocumentDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(DOCUMENT_DETAILS_QUERY);
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();
	}




	/**
	 * Builds the query used to fetch community hall slot availability.
	 *
	 * <p>
	 * The query uses placeholders for tenant/venue and booking window, and it also
	 * appends optional time-range filters based on {@link VenueSlotSearchCriteria}.
	 * </p>
	 *
	 * @param searchCriteria slot availability search criteria
	 * @param paramsList list to be populated with placeholder parameters (mutated)
	 * @return SQL query string builder
	 */
	public StringBuilder getCommunityHallSlotAvailabilityQuery(VenueSlotSearchCriteria searchCriteria,
			List<Object> paramsList) {

		StringBuilder builder = new StringBuilder(COMMUNITY_HALL_SLOTS_AVAILABILITY_QUERY);

		paramsList.add(searchCriteria.getTenantId());
		paramsList.add(searchCriteria.getVenueCode());
		paramsList.add(searchCriteria.getBookingStartDate());
		paramsList.add(searchCriteria.getBookingEndDate());
		appendTimeFilters(searchCriteria, builder, paramsList);

		return builder;
	}


	
	/**
	 * Appends time-based filtering conditions to the dynamic SQL query based on the
	 * provided search criteria.
	 *
	 * <p>
	 * This method applies proper time range overlap logic to ensure correct booking
	 * slot filtering. It supports filtering based on:
	 * <ul>
	 * <li>Both fromTime and toTime (overlap condition)</li>
	 * <li>Only fromTime (end-time based filtering)</li>
	 * <li>Only toTime (start-time based filtering)</li>
	 * </ul>
	 *
	 * <p>
	 * When both times are provided, the method uses interval overlap logic:
	 * 
	 * <pre>
	 * existing_from_time < requested_to_time
	 * AND existing_to_time > requested_from_time
	 * </pre>
	 *
	 * @param searchCriteria contains fromTime and/or toTime filter values
	 * @param builder        StringBuilder used to construct the SQL query
	 *                       dynamically
	 * @param paramsList     list of query parameters to be bound in
	 *                       PreparedStatement
	 */
	private void appendTimeFilters(VenueSlotSearchCriteria searchCriteria, StringBuilder builder,
			List<Object> paramsList) {
		if (StringUtils.isNotBlank(searchCriteria.getFromTime())
				&& StringUtils.isNotBlank(searchCriteria.getToTime())) {

			builder.append(
					" AND ecsd.booking_to_time >= CAST(? AS TIME) "
							+ " AND ecsd.booking_from_time <= CAST(? AS TIME) ");

			paramsList.add(searchCriteria.getToTime());
			paramsList.add(searchCriteria.getFromTime());

		} else if (StringUtils.isNotBlank(searchCriteria.getFromTime())) {

			builder.append(" AND ecsd.booking_to_time > CAST(? AS TIME) ");
			paramsList.add(searchCriteria.getFromTime());

		} else if (StringUtils.isNotBlank(searchCriteria.getToTime())) {

			builder.append(" AND ecsd.booking_from_time < CAST(? AS TIME) ");
			paramsList.add(searchCriteria.getToTime());
		}

	}

}
