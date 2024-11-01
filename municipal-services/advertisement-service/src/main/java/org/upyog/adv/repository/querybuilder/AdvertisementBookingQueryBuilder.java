package org.upyog.adv.repository.querybuilder;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.repository.impl.BookingRepositoryImpl;
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
					+ "booking_status,receipt_no, ecbd.createdby, ecbd.createdtime, \n"
					+ "ecbd.lastmodifiedby, ecbd.lastmodifiedtime,ecbd.permission_letter_filestore_id, ecbd.payment_receipt_filestore_id, \n" 
					+ "appl.applicant_detail_id, applicant_name, applicant_email_id, applicant_mobile_no,\n"
					+ "applicant_alternate_mobile_no, \n"
					+ "address_id, door_no, house_no, address_line_1,address_line_2, \n"
					+ "landmark, city, city_code, pincode, street_name, locality, locality_code \n" 
					+ "FROM public.eg_adv_booking_detail ecbd \n"
					+ "join public.eg_adv_applicant_detail appl on ecbd.booking_id = appl.booking_id \n"
					+ "join public.eg_adv_address_detail addr on appl.applicant_detail_id = addr.applicant_detail_id ");

		
	private static final String slotDetailsQuery = "select * from public.eg_adv_cart_detail where booking_id in (";

	private static final String documentDetailsQuery = "select * from public.eg_adv_document_detail  where booking_id in (";

	private static final String ADVERTISEMENT_SLOTS_AVAILABILITY_QUERY = "SELECT eabd.tenant_id, eacd.add_type, eacd.face_area, eacd.location, eacd.night_light, eacd.status, eacd.booking_date \n"
	        + "FROM eg_adv_booking_detail eabd, eg_adv_cart_detail eacd\n"
	        + "WHERE eabd.booking_id = eacd.booking_id AND eabd.tenant_id = ? \n"
	        + "AND eacd.status IN ('BOOKED', 'PENDING_FOR_PAYMENT') AND \n"
	        + "eacd.booking_date >= ?::DATE AND eacd.booking_date <= ?::DATE \n";
	 
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
	private final String paginationWrapper = "SELECT * FROM " + "(SELECT *, DENSE_RANK() OVER (ORDER BY application_date DESC) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";

	
	private static final String bookingDetailsCountCount = "SELECT count(ecbd.booking_id) \n" 
			+ "FROM public.eg_adv_booking_detail ecbd \n"
	+ "join public.eg_adv_applicant_detail appl on ecbd.booking_id = appl.booking_id \n";
	

	/**
	 * To give the Search query based on the requirements.
	 * 
	 * @param criteria         Community Hall booking search criteria
	 * @param preparedStmtList values to be replaced on the query
	 * @return Final Search Query
	 */
	public String getAdvertisementSearchQuery(AdvertisementSearchCriteria criteria,
			List<Object> preparedStmtList) {
		StringBuilder builder;
		
		if(criteria.isCountCall()) {
			builder = new StringBuilder(bookingDetailsCountCount);
		}else {
			builder = new StringBuilder(bookingDetailsQuery);
		}
		
		if(criteria.getFromDate() != null || criteria.getToDate() != null) {
			builder.append(" join public.eg_adv_cart_detail ecsd ON ecsd.booking_id = ecbd.booking_id ");
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

		//createdby search criteria
		List<String> createdBy = criteria.getCreatedBy();
		if (!CollectionUtils.isEmpty(createdBy)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecbd.createdby IN (").append(createQueryParams(createdBy)).append(")");
			addToPreparedStatement(preparedStmtList, createdBy);
		}

		//From booking date to booking date search criteria
		final String DATE_CAST = " ?::DATE ";
		if (criteria.getFromDate() != null && criteria.getToDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecsd.booking_date BETWEEN ").append(DATE_CAST).append(" AND ")
					.append(DATE_CAST);
			preparedStmtList.add(criteria.getFromDate());
			preparedStmtList.add(criteria.getToDate());
		} else if (criteria.getFromDate() != null && criteria.getToDate() == null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecsd.booking_date >= ").append(DATE_CAST);
			preparedStmtList.add(criteria.getFromDate());
		} else if (criteria.getFromDate() == null && criteria.getToDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ecsd.booking_date <= ").append(DATE_CAST);
			preparedStmtList.add(criteria.getToDate());
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

	public StringBuilder getAdvertisementSlotAvailabilityQuery(AdvertisementSlotSearchCriteria searchCriteria, List<Object> paramsList) {
	    StringBuilder builder = new StringBuilder(ADVERTISEMENT_SLOTS_AVAILABILITY_QUERY);
	    paramsList.add(searchCriteria.getTenantId());
	    paramsList.add(searchCriteria.getBookingStartDate());
	    paramsList.add(searchCriteria.getBookingEndDate());


	    return builder;
	}


}
