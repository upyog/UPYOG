package org.upyog.adv.repository.querybuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AdvertisementBookingQueryBuilder {
	@Autowired
	private BookingConfiguration bookingConfiguration;

	private static final StringBuilder bookingDetailsQuery = new StringBuilder(
			"SELECT ecbd.booking_id, booking_no, payment_date, application_date, tenant_id,\n"
					+ "ecbd.advertisementId ,booking_status,receipt_no, ecbd.createdby, ecbd.createdtime, \n"
					+ "ecbd.lastmodifiedby, ecbd.lastmodifiedtime,ecbd.permission_letter_filestore_id, ecbd.payment_receipt_filestore_id, \n"
					+ "appl.applicant_detail_id, applicant_name, applicant_email_id, applicant_mobile_no,\n"
					+ "applicant_alternate_mobile_no, \n"
					+ "address_id, door_no, house_no, address_line_1,address_line_2, \n"
					+ "landmark, city, city_code, pincode, street_name, locality, locality_code \n"
					+ "FROM public.eg_adv_booking_detail ecbd \n"
					+ "join public.eg_adv_applicant_detail appl on ecbd.booking_id = appl.booking_id \n"
					+ "join public.eg_adv_address_detail addr on appl.applicant_detail_id = addr.applicant_detail_id");
				  

	private static final String slotDetailsQuery = "select * from public.eg_adv_cart_detail where booking_id in (";

	private static final String documentDetailsQuery = "select * from public.eg_adv_document_detail  where booking_id in (";

	private static final String ADVERTISEMENT_SLOTS_AVAILABILITY_QUERY =
			"SELECT eabd.tenant_id, eabd.booking_id, eacd.advertisementId, eacd.add_type, eacd.face_area, eacd.location, eacd.night_light, eacd.status, eacd.booking_date\n"
					+ "FROM eg_adv_booking_detail eabd\n"
					+ "JOIN eg_adv_cart_detail eacd ON eabd.booking_id = eacd.booking_id\n"
					+ "WHERE eabd.tenant_id = ?\n"
					+ "AND eabd.booking_status NOT IN ('REMOVED','BOOKING_CREATED','CANCELLED', 'REJECTED', 'DRAFT', 'BOOKING_EXPIRED', 'PAYMENT_FAILED')\n"
					+ "AND eacd.booking_date >= ?::DATE\n"
					+ "AND eacd.booking_date <= ?::DATE\n";

	public static final String BOOKING_UPDATE_QUERY = "UPDATE public.eg_adv_booking_detail "
	        + "SET booking_status= ?, receipt_no = ?,payment_date = ?, lastmodifiedby = ?, lastmodifiedtime = ?, "
	        + "permission_letter_filestore_id = ?, payment_receipt_filestore_id = ? WHERE booking_id = ?";

	public static final String CART_UPDATE_QUERY = "UPDATE public.eg_adv_cart_detail "
	        + "SET status=?, lastmodifiedby=?, lastmodifiedtime=? WHERE booking_id=?";
	
	public static final String UPDATE_BOOKING_STATUS =  "update eg_adv_booking_detail set booking_status = ?, lastmodifiedby=?, lastmodifiedtime=? "
			+ " where booking_id = ?";

	public static final String PAYMENT_TIMER_QUERY = "INSERT INTO eg_adv_payment_timer(booking_id, createdby, createdtime, status, booking_no, lastmodifiedby, lastmodifiedtime, add_type, location, face_area, night_light,advertisementId, booking_start_date, booking_end_date, booking_date) VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?::DATE, ?::DATE, ?::DATE);\n";

	public static final String DRAFT_QUERY = "INSERT INTO eg_adv_draft_detail(draft_id, tenant_id, user_uuid, draft_application_data, createdby, lastmodifiedby, createdtime, lastmodifiedtime) VALUES (?, ?, ?,CAST(? AS jsonb), ?, ?, ?, ?);\n";

	private static final String PAYMENT_TIMER_DELETE_QUERY = "DELETE FROM eg_adv_payment_timer WHERE booking_id = ?";

	public static final String DraftID_DELETE_QUERY = "DELETE FROM eg_adv_draft_detail WHERE draft_id = ?";
	
	public static final String Draft_DELETE_QUERY = "DELETE FROM eg_adv_draft_detail WHERE user_uuid = ?";
	
	public static final String TIMER_DELETE_QUERY_BY_UUID = "DELETE FROM eg_adv_payment_timer WHERE createdby = ?";
	
	public static final String TIMER_DELETE_QUERY = "DELETE FROM eg_adv_payment_timer WHERE booking_id = ?";

	private static final String PAYMENT_TIMER_DELETE_BOOKINGID = 
		    "DELETE FROM eg_adv_payment_timer " +	
		    		"WHERE ? - createdtime > ? AND booking_id = ?";
	
	public static final String DRAFTID_DELETE_TIMER = 
		    "DELETE FROM eg_adv_draft_detail " +	
		    		"WHERE ? - createdtime > ? AND draft_id = ?";

	public static final String GET_TIMER_DATA =
			"SELECT booking_id, booking_date, booking_start_date, booking_end_date, createdby, add_type, location, face_area, night_light, advertisementId, status\n"
					+ " FROM eg_adv_payment_timer "
					+ " WHERE booking_date >= ?::DATE "
					+ " AND booking_date <= ?::DATE ";
		    
	private static final String FETCH_BOOKINGID_TO_DELETE = "SELECT booking_id FROM eg_adv_payment_timer WHERE ? - createdtime > ?";
	
	public static final String FETCH_DRAFTID_TO_DELETE = "SELECT draft_id FROM eg_adv_draft_detail WHERE ? - createdtime > ?";

	private static final String FETCH_TIMER = "SELECT booking_id, createdtime FROM eg_adv_payment_timer WHERE booking_id IN (%s)";

	private static final String FETCH_TIMER_VALUE = "SELECT booking_id, createdtime FROM eg_adv_payment_timer WHERE booking_id = ?";
	
	private static final String FETCH_BOOKING_NO = "SELECT booking_no FROM eg_adv_booking_detail WHERE booking_id = ?";

	public static final String INSERT_BOOKING_DETAIL_AUDIT_QUERY = 
		    "INSERT INTO public.eg_adv_booking_detail_audit " +
		    "SELECT * FROM public.eg_adv_booking_detail WHERE booking_id = ?";

	public static final String INSERT_CART_DETAIL_AUDIT_QUERY = 
		    "INSERT INTO public.eg_adv_cart_detail_audit " +
		    "SELECT cart_id, booking_id, booking_date::date, booking_from_time, booking_to_time, add_type, location, " +
		    "face_area, night_light, status, createdby, createdtime, lastmodifiedby, lastmodifiedtime " +
		    "FROM public.eg_adv_cart_detail WHERE booking_id = ?";
		
	public static final String BOOKING_ID_EXISTS_CHECK = "SELECT * FROM eg_adv_payment_timer WHERE booking_id = ?";

	public static final String DRAFT_ID_EXISTS_CHECK = "SELECT * FROM eg_adv_draft_detail WHERE user_uuid = ?";
	
	public static final String UPDATE_TIMER = "UPDATE eg_adv_payment_timer SET booking_id = ?, booking_no = ? WHERE booking_id = ?";
	
	public static final String UPDATE_TIMER_STATUS = "UPDATE eg_adv_payment_timer SET status = ? WHERE booking_no = ?";

    // Update a specific cart row to REMOVED by matching all slot-identifying columns
    public static final String CART_MARK_REMOVED_BY_SLOT = "UPDATE public.eg_adv_cart_detail SET status=?, lastmodifiedby=?, lastmodifiedtime=? "
	    + "WHERE booking_id = ? AND advertisementId = ? AND booking_date = ?::DATE AND add_type = ? AND face_area = ? AND night_light = ?";

    // Delete timer entries for a specific slot owned by a booking/draft id
    public static final String PAYMENT_TIMER_DELETE_BY_SLOT = "DELETE FROM eg_adv_payment_timer WHERE booking_id = ? "
	    + "AND advertisementId = ? AND booking_date = ?::DATE AND add_type = ? AND face_area = ? AND night_light = ?";

	// Find bookings that are BOOKED and whose max cart.booking_date < CURRENT_DATE
	// This ensures we only pick bookings whose latest slot date is in the past.
    public static final String FETCH_BOOKINGS_ELIGIBLE_FOR_VERIFICATION =
            "SELECT eabd.booking_id, eabd.booking_no, eabd.tenant_id " +
                    "FROM eg_adv_booking_detail eabd " +
                    "INNER JOIN ( " +
                    "    SELECT booking_id, MAX(booking_date) as max_booking_date " +
                    "    FROM eg_adv_cart_detail " +
                    "    GROUP BY booking_id " +
                    ") eacd ON eabd.booking_id = eacd.booking_id " +
                    "WHERE eabd.booking_status = 'BOOKED' " +
                    "AND eacd.max_booking_date < CURRENT_DATE";

	public static final String UPDATE_BOOKING_STATUS_BY_ID =
	    "UPDATE eg_adv_booking_detail SET booking_status = ?, lastmodifiedby = ?, lastmodifiedtime = ? WHERE booking_id = ?";


	
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

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY application_date DESC) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";

	private static final String bookingDetailsCountCount = "SELECT count(ecbd.booking_id) \n"
			+ "FROM public.eg_adv_booking_detail ecbd \n"
			+ "join public.eg_adv_applicant_detail appl on ecbd.booking_id = appl.booking_id \n";

	public String insertBookingIdForTimer(String bookingId) {
		return PAYMENT_TIMER_QUERY;
	}

	public String deleteBookingIdForTimer(String bookingId) {
		return PAYMENT_TIMER_DELETE_QUERY;
	}
	
	public String getBookingNo() {
		return FETCH_BOOKING_NO;
	}

	public String deleteBookingIdPaymentTimer() {
		return PAYMENT_TIMER_DELETE_BOOKINGID;
	}
	
	public String updateBookingDetail() {
		return BOOKING_UPDATE_QUERY;
	}
	
	public String updateCartDetail() {
		return CART_UPDATE_QUERY;
	}
	
	public String createBookingDetailAudit() {
		return INSERT_BOOKING_DETAIL_AUDIT_QUERY;
	}
	
	public String createCartDetailAudit() {
		return INSERT_CART_DETAIL_AUDIT_QUERY;
	}

	
	public String checkBookingIdExists(String bookingId) {
		return BOOKING_ID_EXISTS_CHECK;
	}
	
	public String checkDraftIdExists( String uuid) {
		return DRAFT_ID_EXISTS_CHECK;
	}
	
//
//	public static String updateTimerTableWithBookingId(String tenantId, String booking String uuid) {
//		return UPDATE_TIMER;
//	}
	
	public String getBookingIdToDelete() {
		return FETCH_BOOKINGID_TO_DELETE;
	}
	public String fetchBookingIdForTImer(List<String> bookingIds) {
	    if (bookingIds == null || bookingIds.isEmpty()) {
	        log.warn("No booking IDs provided for query");
	        return null;
	    }

	    String bookingId = bookingIds.stream()
	                                 .map(id -> "?")
	                                 .collect(Collectors.joining(", "));
	    return String.format(FETCH_TIMER, bookingId);
	}
	
	public String fetchBookingIdForTimer(String bookingId) {
	    if (bookingId == null || bookingId.isEmpty()) {
	        log.warn("No booking IDs provided for query");
	        return null;
	    }
	    return String.format(FETCH_TIMER_VALUE, bookingId);
	}


	/**
	 * To give the Search query based on the requirements.
	 * 
	 * @param criteria         Community Hall booking search criteria
	 * @param preparedStmtList values to be replaced on the query
	 * @return Final Search Query
	 */

	public String getAdvertisementSearchQuery(AdvertisementSearchCriteria criteria, List<Object> preparedStmtList) {
		StringBuilder builder = criteria.isCountCall() ? new StringBuilder(bookingDetailsCountCount)
				: new StringBuilder(bookingDetailsQuery);

		builder.append(" join public.eg_adv_cart_detail ecsd ON ecsd.booking_id = ecbd.booking_id ");

		// Tenant ID
		if (criteria.getTenantId() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			if (criteria.getTenantId().split("\\.").length == 1) {
				builder.append(" ecbd.tenant_id like ?");
				preparedStmtList.add('%' + criteria.getTenantId() + '%');
			} else {
				builder.append(" ecbd.tenant_id=? ");
				preparedStmtList.add(criteria.getTenantId());
			}
		}

		// Booking IDs
		if (!CollectionUtils.isEmpty(criteria.getBookingIds())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.booking_id IN (").append(createQueryParams(criteria.getBookingIds())).append(") ");
			addToPreparedStatement(preparedStmtList, criteria.getBookingIds());
		}

		// Booking No
		if (criteria.getBookingNo() != null) {
			List<String> bookingNos = Arrays.asList(criteria.getBookingNo().split(","));
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.booking_no IN (").append(createQueryParams(bookingNos)).append(")");
			addToPreparedStatement(preparedStmtList, bookingNos);
		}

		// Status
		if (criteria.getStatus() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.booking_status = ? ");
			preparedStmtList.add(criteria.getStatus());
		}

		if (criteria.getAdvertisementId() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.advertisementId = ? ");
			preparedStmtList.add(criteria.getAdvertisementId());
		}



		// Applicant Name
		if (criteria.getApplicantName() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" appl.applicant_name = ? ");
			preparedStmtList.add(criteria.getApplicantName().trim());
		}

		// Face Area
		if (criteria.getFaceArea() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecsd.face_area = ? ");
			preparedStmtList.add(criteria.getFaceArea());
		}

		// Mobile Numbers
		if (criteria.getMobileNumber() != null) {
			List<String> mobileNos = Arrays.asList(criteria.getMobileNumber().split(","));
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" appl.applicant_mobile_no IN (").append(createQueryParams(mobileNos)).append(")");
			addToPreparedStatement(preparedStmtList, mobileNos);
		}

		// Created By
		if (!CollectionUtils.isEmpty(criteria.getCreatedBy())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.createdby IN (").append(createQueryParams(criteria.getCreatedBy())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getCreatedBy());
		}

		// Date Filtering
		appendDateFilters(criteria, preparedStmtList, builder);

		// Pagination
		String query = criteria.isCountCall() ? builder.toString()
				: addPaginationWrapper(builder.toString(), preparedStmtList, criteria);

		return query;
	}

	public String getOwnerUuidsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder("select * from public.eg_adv_owner where booking_id in (");
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();
	}


	private void appendDateFilters(AdvertisementSearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
	    if (criteria.getFromDate() != null && criteria.getToDate() != null) {
	        addClauseIfRequired(preparedStmtList, builder);

	        // Booking date: it's a DATE field
	        builder.append(" (ecsd.booking_date BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD')");

	        // Application date: it's a BIGINT(Timestamp) storing epoch milliseconds
	        builder.append(" OR TO_TIMESTAMP(ecbd.application_date / 1000) BETWEEN ")
	               .append("TO_TIMESTAMP(? || ' 00:00:00', 'YYYY-MM-DD HH24:MI:SS') AND ")
	               .append("TO_TIMESTAMP(? || ' 23:59:59', 'YYYY-MM-DD HH24:MI:SS'))");
	        preparedStmtList.add(criteria.getFromDate());
	        preparedStmtList.add(criteria.getToDate());
	        preparedStmtList.add(criteria.getFromDate());
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
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});
	}

	private String addPaginationWrapper(String query, List<Object> preparedStmtList,
			AdvertisementSearchCriteria criteria) {

		int limit = bookingConfiguration.getDefaultLimit();
		int offset = bookingConfiguration.getDefaultOffset();
		String finalQuery = paginationWrapper.replace("{}", query);

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

	public String getSlotDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(slotDetailsQuery);
		builder.append(createQueryParams(bookingIds)).append(")");
		log.info("Fetched booking details size : " + builder);
		return builder.toString();

	}

	public String getDocumentDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(documentDetailsQuery);
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();
	}

	public StringBuilder getAdvertisementSlotAvailabilityQuery(AdvertisementSlotSearchCriteria searchCriteria,
			List<Object> paramsList) {
		StringBuilder builder = new StringBuilder(ADVERTISEMENT_SLOTS_AVAILABILITY_QUERY);
		paramsList.add(searchCriteria.getTenantId());
		paramsList.add(searchCriteria.getBookingStartDate());
		paramsList.add(searchCriteria.getBookingEndDate());

		return builder;
	}

	public StringBuilder getTimerData(AdvertisementSlotSearchCriteria searchCriteria, List<Object> paramsList) {
		StringBuilder builder = new StringBuilder(GET_TIMER_DATA);

	    paramsList.add(java.sql.Date.valueOf(searchCriteria.getBookingStartDate()));
	    paramsList.add(java.sql.Date.valueOf(searchCriteria.getBookingEndDate()));

		/* Check if both bookingStartDate and bookingEndDate are provided
		if (searchCriteria.getBookingStartDate() != null && searchCriteria.getBookingEndDate() != null) {
			builder.append(" AND (booking_start_date <= ?::DATE AND booking_end_date >= ?::DATE)");
			paramsList.add(java.sql.Date.valueOf(searchCriteria.getBookingEndDate())); // End date
			paramsList.add(java.sql.Date.valueOf(searchCriteria.getBookingStartDate())); // Start date

		} */

		return builder;
	}


}
