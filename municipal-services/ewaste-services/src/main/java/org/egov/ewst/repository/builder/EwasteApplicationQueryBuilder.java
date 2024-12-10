package org.egov.ewst.repository.builder;

import java.util.List;

import org.egov.ewst.models.EwasteApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class EwasteApplicationQueryBuilder {

	private static final String BASE_EW_QUERY = "SELECT RQ.id AS rqid, RQ.tenantId AS rqtenantid, RQ.requestId AS rqrequestid, RQ.calculatedAmount AS rqcalculatedamount, "
			+ "RQ.vendorUuid AS rqvendoruuid, RQ.transactionId AS rqtransactionid, RQ.pickUpDate AS rqpickupdate, RQ.finalAmount AS rqfinalamount, "
			+ "RQ.requestStatus AS rqrequeststatus, RQ.createdBy AS rqcreatedby, RQ.lastModifiedBy AS rqlastmodifiedby, RQ.createdTime AS rqcreatedtime, "
			+ "RQ.lastModifiedTime AS rqlastmodifiedtime, ";

	private static final String EWASTE_SELECT_QUERY = "EWD.id AS ewdid, EWD.productId AS ewdproductid, EWD.productName AS ewdproductname, EWD.quantity AS ewdquantity, EWD.price AS ewdprice, ";

	private static final String APPLICANT_SELECT_QUERY = "APP.id AS appid, APP.applicantName AS appapplicantname, APP.mobileNumber AS appmobilenumber, APP.altMobileNumber AS appaltmobilenumber, "
			+ "APP.emailId AS appemailid,";

	private static final String ADDRESS_SELECT_QUERY = "ADR.id AS adrid, ADR.tenantId AS adrtenantid, ADR.doorNo AS adrdoorno, ADR.latitude AS adrlatitude, ADR.longitude AS adrlongitude, "
			+ "ADR.buildingName AS adrbuildingname, ADR.addressId AS adraddressid, ADR.addressNumber AS adraddressnumber, ADR.type AS adrtype, "
			+ "ADR.addressLine1 AS adraddressline1, ADR.addressLine2 AS adraddressline2, ADR.landmark AS adrlandmark, ADR.street AS adrstreet, "
			+ "ADR.city AS adrcity, ADR.locality AS adrlocality, ADR.pincode AS adrpincode, ADR.detail AS adrdetail, ";

	private static final String DOCUMENTS_SELECT_QUERY = "DOC.id AS docid, DOC.tenantId AS doctenantid, DOC.documentType AS docdocumenttype, DOC.filestoreId AS docfilestoreid, "
			+ "DOC.documentUid AS docdocumentuid, DOC.active AS docactive, DOC.createdBy AS doccreatedby, DOC.lastModifiedBy AS doclastmodifiedby, "
			+ "DOC.createdTime AS doccreatedtime, DOC.lastModifiedTime AS doclastmodifiedtime ";

	private static final String FROM_TABLES = " FROM EG_EW_REQUESTS RQ LEFT JOIN EG_EW_APPLICANTDETAILS APP ON APP.ewId = RQ.id "
			+ "LEFT JOIN EG_EW_EWASTEDETAILS EWD ON EWD.ewId = RQ.id "
			+ "LEFT JOIN EG_EW_ADDRESS ADR ON ADR.ewId = RQ.id "
			+ "LEFT JOIN EG_EW_EWASTEDOCUMENTS DOC ON DOC.ewId = RQ.id ";

	private final String ORDERBY_CREATEDTIME = " ORDER BY RQ.createdTime DESC ";

	public String getEwasteApplicationSearchQuery(EwasteApplicationSearchCriteria criteria,
			List<Object> preparedStmtList) {
		StringBuilder query = new StringBuilder(BASE_EW_QUERY);
		query.append(EWASTE_SELECT_QUERY);
		query.append(APPLICANT_SELECT_QUERY);
		query.append(ADDRESS_SELECT_QUERY);
		query.append(DOCUMENTS_SELECT_QUERY);
		query.append(FROM_TABLES);

		if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" rq.tenantid = ? ");
			preparedStmtList.add(criteria.getTenantId());
		}
		if (!ObjectUtils.isEmpty(criteria.getMobileNumber())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" app.mobilenumber = ? ");
			preparedStmtList.add(criteria.getMobileNumber());
		}
		if (!ObjectUtils.isEmpty(criteria.getRequestId())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" rq.requestid = ? ");
			preparedStmtList.add(criteria.getRequestId());
		}
		if (!ObjectUtils.isEmpty(criteria.getRequestStatus())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" rq.requeststatus = ? ");
			preparedStmtList.add(criteria.getRequestStatus());
		}

		if (!ObjectUtils.isEmpty(criteria.getFromDate())) {
			addClauseIfRequired(query, preparedStmtList);
			// query.append(" ptr.createdtime >= ? ");
			query.append(" rq.createdtime >= CAST(? AS bigint) ");
			preparedStmtList.add(criteria.getFromDate());
		}
		if (!ObjectUtils.isEmpty(criteria.getToDate())) {
			addClauseIfRequired(query, preparedStmtList);
			// query.append(" ptr.createdtime <= ? ");
			query.append(" rq.createdtime <= CAST(? AS bigint) ");
			preparedStmtList.add(criteria.getToDate());
		}
		query.append(ORDERBY_CREATEDTIME);

		return query.toString();
	}

	private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
		if (preparedStmtList.isEmpty()) {
			query.append(" WHERE ");
		} else {
			query.append(" AND ");
		}
	}

	private String createQuery(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});
	}
}
