package org.upyog.chb.repository.querybuilder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

@Component
public class CommunityHallBookingQueryBuilder {

	@Autowired
	private CommunityHallBookingConfiguration bookingConfiguration;

	private static final StringBuilder bookingAndSlotDetailsQuery = new StringBuilder(
			"select chbd.booking_id, chbd.booking_no, chbd.booking_date,\n"
					+ "chbd.tenant_id, chbd.community_hall_id, chbd.community_hall_name, chbd.booking_status, chbd.resident_type,"
					+ " chbd.special_category,chbd.applicant_name, chbd.applicant_email_id, chbd.applicant_mobile_no, \n"
					+ " chbd.applicant_alternate_mobile_no, chbd.purpose, chbd.purpose_description, chbd.event_name, chbd.event_organized_by, \n"
					+ "chbd.createdby as booking_created_by, chbd.createdtime as booking_created_time, \n"
					+ "chbd.lastmodifiedby as booking_last_modified_by, chbd.lastmodifiedtime as booking_last_modified_time\n"
					+ ",bsd.slot_id, bsd.booking_id as slot_booking_id,bsd.hall_code, bsd.hall_name, bsd.booking_date, bsd.booking_from_time"
					+ " , bsd.booking_to_time, bsd.status as slot_status, bsd.createdby as slot_created_by\n"
					+ ", bsd.createdtime as slot_created_time, bsd.lastmodifiedby as slot_last_modified_by, bsd.lastmodifiedtime as \n"
					+ "slot_last_modified_time from community_hall_booking_details chbd\n"
					+ "join booking_slot_details bsd on  bsd.booking_id = chbd.booking_id ");

	private static final String bankDetailsQuery = "select * from BANK_ACCOUNT_DETAILS where booking_id in (";

	private static final String documentDetailsQuery = "select * from COMMUNITY_HALL_BOOKING_DOCUMENT_DETAILS  where booking_id in (";

	private final String paginationWrapper = "SELECT * FROM " + "(SELECT *, DENSE_RANK() OVER () offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";
	
	private static final String COMMUNITY_HALL_SLOTS_AVAIALABILITY_QUERY = "SELECT chbd.tenant_id, chbd.community_hall_name, bsd.hall_code, bsd.status,bsd.booking_date \n"
			+ "	FROM community_hall_booking_details chbd, booking_slot_details bsd\n"
			+ "where chbd.booking_id = bsd.booking_id and chbd.tenant_id= ? and chbd.community_hall_name = ? \n"
			+ "AND bsd.hall_code = ? and bsd.status = 'BOOKED' and \n"
			+ "	bsd.booking_date >= ? and bsd.booking_date <=  ? ";

	/**
	 * To give the Search query based on the requirements.
	 * 
	 * @param criteria         ASSET search criteria
	 * @param preparedStmtList values to be replaced on the query
	 * @return Final Search Query
	 */
	public String getCommunityHallBookingSearchQuery(CommunityHallBookingSearchCriteria criteria, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(bookingAndSlotDetailsQuery);
		// String query = "SELECT * FROM public.eg_asset_assetdetails ORDER BY
		// createdtime DESC;";

		if (criteria.getTenantId() != null) {
			if (criteria.getTenantId().split("\\.").length == 1) {

				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" chbd.tenant_id like ?");
				preparedStmtList.add('%' + criteria.getTenantId() + '%');
			} else {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" chbd.tenant_id=? ");
				preparedStmtList.add(criteria.getTenantId());
			}
		}

		List<String> ids = criteria.getBookingIds();
		if (!CollectionUtils.isEmpty(ids)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" chbd.booking_id IN (").append(createQueryParams(ids)).append(")");
			addToPreparedStatement(preparedStmtList, ids);
		}

		String bookingNo = criteria.getBookingNo();
		if (bookingNo != null) {
			List<String> applicationNos = Arrays.asList(bookingNo.split(","));
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" chbd.booking_no IN (").append(createQueryParams(applicationNos)).append(")");
			addToPreparedStatement(preparedStmtList, applicationNos);
		}

		// createdby search criteria
		List<String> createdBy = criteria.getCreatedBy();
		if (!CollectionUtils.isEmpty(createdBy)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" chbd.createdby IN (").append(createQueryParams(createdBy)).append(")");
			addToPreparedStatement(preparedStmtList, createdBy);
		}


		// Approval from createddate and to createddate search criteria
		if (criteria.getFromDate() != null && criteria.getToDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" chbd.createdtime BETWEEN ").append(criteria.getFromDate()).append(" AND ")
					.append(criteria.getToDate());
		} else if (criteria.getFromDate() != null && criteria.getToDate() == null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" chbd.createdtime >= ").append(criteria.getFromDate());
		}
		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
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
			CommunityHallBookingSearchCriteria criteria) {

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
	
	public String getBankDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(bankDetailsQuery);
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();
		
	}
	
	public String getDocumentDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(documentDetailsQuery);
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();
	}
	
	public String getCommunityHallSlotAvailabilityQuery(CommunityHallSlotSearchCriteria searchCriteria, List<Object> paramsList) {
		StringBuilder builder = new StringBuilder(COMMUNITY_HALL_SLOTS_AVAIALABILITY_QUERY);
		
		paramsList.add(searchCriteria.getTenantId());
		paramsList.add(searchCriteria.getCommunityHallName());
		paramsList.add(searchCriteria.getHallCode());
		paramsList.add(searchCriteria.getBookingStartDate());
		paramsList.add(searchCriteria.getBookingEndDate());
		
		return builder.toString();
	}

}
