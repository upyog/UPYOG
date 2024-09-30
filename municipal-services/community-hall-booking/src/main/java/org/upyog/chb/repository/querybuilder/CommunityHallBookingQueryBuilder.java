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
					+ "booking_status, special_category, purpose, purpose_description, receipt_no, ecbd.createdby, ecbd.createdtime, \n"
					+ "ecbd.lastmodifiedby, ecbd.lastmodifiedtime,ecbd.permission_letter_filestore_id, ecbd.payment_receipt_filestore_id, \n" 
					+ "appl.applicant_detail_id, applicant_name, applicant_email_id, applicant_mobile_no,\n"
					+ "applicant_alternate_mobile_no, account_no, ifsc_code, bank_name, bank_branch_name, \n"
					+ "account_holder_name, address_id, door_no, house_no, address_line_1, \n"
					+ "landmark, city, city_code, pincode, street_name, locality, locality_code \n" 
					+ "FROM public.eg_chb_booking_detail ecbd \n"
					+ "join public.eg_chb_applicant_detail appl on ecbd.booking_id = appl.booking_id \n"
					+ "join public.eg_chb_address_detail addr on appl.applicant_detail_id = addr.applicant_detail_id ");

	private static final String slotDetailsQuery = "select * from public.eg_chb_slot_detail where booking_id in (";

	private static final String documentDetailsQuery = "select * from public.eg_chb_document_detail  where booking_id in (";

	private final String paginationWrapper = "SELECT * FROM " + "(SELECT *, DENSE_RANK() OVER (ORDER BY application_date DESC) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";

	private static final String COMMUNITY_HALL_SLOTS_AVAIALABILITY_QUERY = " SELECT ecbd.tenant_id, ecbd.community_hall_code, ecsd.capacity, ecsd.hall_code, ecsd.status,ecsd.booking_date \n"
			+ "	FROM eg_chb_booking_detail ecbd, eg_chb_slot_detail ecsd\n"
			+ "where ecbd.booking_id = ecsd.booking_id and ecbd.tenant_id= ? and ecbd.community_hall_code = ?\n"
			+ " and ecsd.status in ('BOOKED', 'PENDING_FOR_PAYMENT') and \n"
			+ "	ecsd.booking_date >= ?::DATE and ecsd.booking_date <=  ?::DATE ";
		//	+ "	AND ecsd.hall_code in (?)";
	
	//private static final String COUNT_WRAPPER = " SELECT COUNT(*) FROM ({INTERNAL_QUERY}) AS count ";
	
	private static final String bookingDetailsCountCount = "SELECT count(ecbd.booking_id) \n" 
			+ "FROM public.eg_chb_booking_detail ecbd \n"
	+ "join public.eg_chb_applicant_detail appl on ecbd.booking_id = appl.booking_id \n";
	

	/**
	 * To give the Search query based on the requirements.
	 * 
	 * @param criteria         Community Hall booking search criteria
	 * @param preparedStmtList values to be replaced on the query
	 * @return Final Search Query
	 */
	public String getCommunityHallBookingSearchQuery(CommunityHallBookingSearchCriteria criteria,
			List<Object> preparedStmtList) {
		StringBuilder builder;
		
		if(criteria.isCountCall()) {
			builder = new StringBuilder(bookingDetailsCountCount);
		}else {
			builder = new StringBuilder(bookingDetailsQuery);
		}
		
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
		
		String status = criteria.getStatus();
		if (status != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.booking_status =  ? ");
			preparedStmtList.add(status);
		}
		
		String mobileNo = criteria.getMobileNumber();
		if (mobileNo != null) {
			List<String> mobileNos = Arrays.asList(mobileNo.split(","));
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" appl.applicant_mobile_no IN (").append(createQueryParams(mobileNos)).append(")");
			addToPreparedStatement(preparedStmtList, mobileNos);
		}

		// createdby search criteria
		List<String> createdBy = criteria.getCreatedBy();
		if (!CollectionUtils.isEmpty(createdBy)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.createdby IN (").append(createQueryParams(createdBy)).append(")");
			addToPreparedStatement(preparedStmtList, createdBy);
		}

		// From payment_date and to payment_date search criteria
		//TODO: check payment date between condition
		if (criteria.getFromDate() != null && criteria.getToDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.payment_date BETWEEN ").append(criteria.getFromDate()).append(" AND ")
					.append(criteria.getToDate());
		} else if (criteria.getFromDate() != null && criteria.getToDate() == null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.payment_date >= ").append(criteria.getFromDate());
		} else if (criteria.getFromDate() == null && criteria.getToDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.payment_date < ").append(criteria.getToDate());
		}
		
		String query = null;
		
		if(criteria.isCountCall()) {
			//pagination attributes not required for count query
			query = builder.toString();
		} else {
			//Add pagination attributes for booking details query
			query = addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
		}
		
		return query;
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

	public StringBuilder getCommunityHallSlotAvailabilityQuery(CommunityHallSlotSearchCriteria searchCriteria,
			List<Object> paramsList) {
		StringBuilder builder = new StringBuilder(COMMUNITY_HALL_SLOTS_AVAIALABILITY_QUERY);

		paramsList.add(searchCriteria.getTenantId());
		paramsList.add(searchCriteria.getCommunityHallCode());
//		paramsList.add(SlotStatusEnum.BOOKED.toString());
		paramsList.add(searchCriteria.getBookingStartDate());
		paramsList.add(searchCriteria.getBookingEndDate());

		return builder;
	}

}
