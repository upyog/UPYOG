package org.upyog.chb.repository.querybuilder;

import java.util.Arrays;
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

	private static final StringBuilder bookingDetailsQuery = new StringBuilder(
			"SELECT ecbd.booking_id, booking_no, payment_date, application_date, tenant_id, community_hall_code, \n"
					+ "booking_status, special_category, purpose, purpose_description, ecbd.createdby, ecbd.createdtime, \n"
					+ "ecbd.lastmodifiedby, ecbd.lastmodifiedtime,\n" + "	\n"
					+ "appl.applicant_detail_id, applicant_name, applicant_email_id, applicant_mobile_no,\n"
					+ "applicant_alternate_mobile_no, account_no, ifsc_code, bank_name, bank_branch_name, \n"
					+ "account_holder_name, \n" + "\n" + "address_id, door_no, house_no, address_line_1, \n"
					+ "landmark, city, pincode, street_name, locality_code\n" + "	\n"
					+ "FROM public.eg_chb_booking_detail ecbd \n"
					+ "join public.eg_chb_applicant_detail appl on ecbd.booking_id = appl.booking_id\n"
					+ "join public.eg_chb_address_detail addr on appl.applicant_detail_id = addr.applicant_detail_id ");

	private static final String slotDetailsQuery = "select * from public.eg_chb_slot_detail where booking_id in (";

	private static final String documentDetailsQuery = "select * from public.eg_chb_document_detail  where booking_id in (";

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
	public String getCommunityHallBookingSearchQuery(CommunityHallBookingSearchCriteria criteria,
			List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(bookingDetailsQuery);
		// String query = "SELECT * FROM public.eg_asset_assetdetails ORDER BY
		// createdtime DESC;";

		if (criteria.getTenantId() != null) {
			if (criteria.getTenantId().split("\\.").length == 1) {

				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" ecbd.tenant_id like ?");
				preparedStmtList.add('%' + criteria.getTenantId() + '%');
			} else {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" ecbd.tenant_id=? ");
				preparedStmtList.add(criteria.getTenantId());
			}
		}

		List<String> ids = criteria.getBookingIds();
		if (!CollectionUtils.isEmpty(ids)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.booking_id IN (").append(createQueryParams(ids)).append(")");
			addToPreparedStatement(preparedStmtList, ids);
		}

		String bookingNo = criteria.getBookingNo();
		if (bookingNo != null) {
			List<String> applicationNos = Arrays.asList(bookingNo.split(","));
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.booking_no IN (").append(createQueryParams(applicationNos)).append(")");
			addToPreparedStatement(preparedStmtList, applicationNos);
		}

		// createdby search criteria
		List<String> createdBy = criteria.getCreatedBy();
		if (!CollectionUtils.isEmpty(createdBy)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.createdby IN (").append(createQueryParams(createdBy)).append(")");
			addToPreparedStatement(preparedStmtList, createdBy);
		}

		// Approval from createddate and to createddate search criteria
		if (criteria.getFromDate() != null && criteria.getToDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.createdtime BETWEEN ").append(criteria.getFromDate()).append(" AND ")
					.append(criteria.getToDate());
		} else if (criteria.getFromDate() != null && criteria.getToDate() == null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.createdtime >= ").append(criteria.getFromDate());
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

	/*
	 * @SuppressWarnings({"rawtypes" }) private StringBuilder addPagingClause(final
	 * StringBuilder selectQuery, final List preparedStatementValues, final
	 * BillSearchCriteria searchBillCriteria) {
	 * 
	 * StringBuilder finalQuery;
	 * 
	 * if (searchBillCriteria.getRetrieveOldest()) finalQuery = new
	 * StringBuilder(BILL_MIN_QUERY.replace(REPLACE_STRING, selectQuery)); else
	 * finalQuery = new StringBuilder(BILL_MAX_QUERY.replace(REPLACE_STRING,
	 * selectQuery));
	 * 
	 * if (searchBillCriteria.isOrderBy()) {
	 * finalQuery.append(" ORDER BY billresult.bd_consumercode "); }
	 * 
	 * return finalQuery; }
	 */

	public String getSlotDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(slotDetailsQuery);
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();

	}

	public String getDocumentDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(documentDetailsQuery);
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();
	}

	public String getCommunityHallSlotAvailabilityQuery(CommunityHallSlotSearchCriteria searchCriteria,
			List<Object> paramsList) {
		StringBuilder builder = new StringBuilder(COMMUNITY_HALL_SLOTS_AVAIALABILITY_QUERY);

		paramsList.add(searchCriteria.getTenantId());
		paramsList.add(searchCriteria.getCommunityHallName());
		paramsList.add(searchCriteria.getHallCode());
		paramsList.add(searchCriteria.getBookingStartDate());
		paramsList.add(searchCriteria.getBookingEndDate());

		return builder.toString();
	}

}
