package org.egov.refund.querybuilder;

import static java.util.stream.Collectors.toSet;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.web.contracat.PaymentSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class PaymentQueryBuilder {
	
	@Autowired
	private ApplicationProperties config;

	public static final String SELECT_PAYMENT_SQL = "SELECT py.*,pyd.*,"
			+ "py.id as py_id,py.tenantId as py_tenantId,py.totalAmountPaid as py_totalAmountPaid,py.createdBy as py_createdBy,py.createdtime as py_createdtime,"
			+ "py.lastModifiedBy as py_lastModifiedBy,py.lastmodifiedtime as py_lastmodifiedtime,py.additionalDetails as py_additionalDetails,"
			+ "pyd.id as pyd_id, pyd.tenantId as pyd_tenantId, pyd.manualreceiptnumber as manualreceiptnumber,pyd.manualreceiptdate as manualreceiptdate, pyd.createdBy as pyd_createdBy,pyd.createdtime as pyd_createdtime,pyd.lastModifiedBy as pyd_lastModifiedBy,"
			+ "pyd.lastmodifiedtime as pyd_lastmodifiedtime,pyd.additionalDetails as pyd_additionalDetails"
			+ " FROM egcl_payment py  " + " INNER JOIN egcl_paymentdetail pyd ON pyd.paymentid = py.id ";

	public static final String BILL_BASE_QUERY = "SELECT b.id AS b_id, b.tenantid AS b_tenantid, b.iscancelled AS b_iscancelled, b.businessservice AS b_businessservice, "
			+ "b.billnumber AS b_billnumber, b.billdate AS b_billdate, b.consumercode AS b_consumercode, b.createdby AS b_createdby, b.status as b_status, b.minimumamounttobepaid AS b_minimumamounttobepaid, "
			+ "b.totalamount AS b_totalamount, b.partpaymentallowed AS b_partpaymentallowed, b.isadvanceallowed as b_isadvanceallowed, "
			+ "b.collectionmodesnotallowed AS b_collectionmodesnotallowed, b.createdtime AS b_createdtime, b.lastmodifiedby AS b_lastmodifiedby, "
			+ "b.lastmodifiedtime AS b_lastmodifiedtime, bd.id AS bd_id, bd.billid AS bd_billid, bd.tenantid AS bd_tenantid, bd.demandid, "
			+ "bd.fromperiod, bd.toperiod, bd.billdescription AS bd_billdescription, bd.displaymessage AS bd_displaymessage, bd.amount AS bd_amount, bd.amountpaid AS bd_amountpaid, "
			+ "bd.callbackforapportioning AS bd_callbackforapportioning, bd.expirydate AS bd_expirydate, ad.id AS ad_id, ad.tenantid AS ad_tenantid, "
			+ "ad.billdetailid AS ad_billdetailid, ad.order AS ad_order, ad.amount AS ad_amount, ad.adjustedamount AS ad_adjustedamount, "
			+ "ad.taxheadcode AS ad_taxheadcode, ad.demanddetailid as ad_demanddetailid, ad.isactualdemand AS ad_isactualdemand, b.additionaldetails as b_additionaldetails,  "
			+ "bd.additionaldetails as bd_additionaldetails,  ad.additionaldetails as ad_additionaldetails "
			+ "FROM egcl_bill b LEFT OUTER JOIN egcl_billdetial bd ON b.id = bd.billid AND b.tenantid = bd.tenantid "
			+ "LEFT OUTER JOIN egcl_billaccountdetail ad ON bd.id = ad.billdetailid AND bd.tenantid = ad.tenantid "
			+ "WHERE b.id IN (:id);";
	
	 public static final String ID_QUERY = "WITH py_filtered as (" +
	            "select id from egcl_payment as py_inner {{WHERE_CLAUSE}} ) " +
	            " SELECT py.id as id FROM py_filtered as py " +
	            " INNER JOIN egcl_paymentdetail as pyd ON pyd.paymentid = py.id and pyd.tenantid {{operator}} :tenantId " +
	            " INNER JOIN egcl_bill bill ON bill.id = pyd.billid " +
	            " INNER JOIN egcl_billdetial bd ON bd.billid = bill.id and bd.tenantid {{operator}} :tenantId; ";

	public String getPaymentSearchQuery(List<String> ids, Map<String, Object> preparedStatementValues) {
		StringBuilder selectQuery = new StringBuilder(SELECT_PAYMENT_SQL);
		addClauseIfRequired(preparedStatementValues, selectQuery);
		selectQuery.append(" py.id IN (:id)  ");
		preparedStatementValues.put("id", ids);
		return addOrderByClause(selectQuery);
	}

	private static String addOrderByClause(StringBuilder selectQuery) {
		return selectQuery.append(" ORDER BY py.transactiondate DESC ").toString();

	}

	private static void addClauseIfRequired(Map<String, Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND");
		}
	}

	public static String getBillQuery() {
		return BILL_BASE_QUERY;
	}

	public String getIdQuery(PaymentSearchCriteria searchCriteria, Map<String, Object> preparedStatementValues) {
		StringBuilder whereClause = new StringBuilder();
		addWhereClause(whereClause, preparedStatementValues, searchCriteria);
		whereClause.append(" ORDER BY py_inner.transactiondate DESC ").toString();
		addPagination(whereClause, preparedStatementValues, searchCriteria);
		String query = ID_QUERY.replace("{{WHERE_CLAUSE}}", whereClause.toString());
		if (searchCriteria.getTenantId().split("\\.").length > 1) {
			query = query.replace("{{operator}}", "=");
		} else
			query = query.replace("{{operator}}", "LIKE");

		return query;
	}

	private static void addWhereClause(StringBuilder selectQuery, Map<String, Object> preparedStatementValues,
			PaymentSearchCriteria searchCriteria) {

		if (StringUtils.isNotBlank(searchCriteria.getTenantId())) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			if (searchCriteria.getTenantId().split("\\.").length > 1) {
				selectQuery.append(" py_inner.tenantId =:tenantId");
				preparedStatementValues.put("tenantId", searchCriteria.getTenantId());
			} else {
				selectQuery.append(" py_inner.tenantId LIKE :tenantId");
				preparedStatementValues.put("tenantId", searchCriteria.getTenantId() + "%");
			}

		}

		if (!CollectionUtils.isEmpty(searchCriteria.getIds())) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(" py_inner.id IN (:id)  ");
			preparedStatementValues.put("id", searchCriteria.getIds());
		}

		if (!CollectionUtils.isEmpty(searchCriteria.getStatus())) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(" UPPER(py_inner.paymentstatus) in (:status)");
			preparedStatementValues.put("status",
					searchCriteria.getStatus().stream().map(String::toUpperCase).collect(toSet()));
		}

		if (!CollectionUtils.isEmpty(searchCriteria.getInstrumentStatus())) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(" UPPER(py_inner.instrumentStatus) in (:instrumentStatus)");
			preparedStatementValues.put("instrumentStatus",
					searchCriteria.getInstrumentStatus().stream().map(String::toUpperCase).collect(toSet()));
		}

		if (!CollectionUtils.isEmpty(searchCriteria.getPaymentModes())) {

			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(" UPPER(py_inner.paymentMode) in (:paymentMode)");
			preparedStatementValues.put("paymentMode",
					searchCriteria.getPaymentModes().stream().map(String::toUpperCase).collect(toSet()));
		}

		if (StringUtils.isNotBlank(searchCriteria.getMobileNumber())) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(" py_inner.mobileNumber = :mobileNumber");
			preparedStatementValues.put("mobileNumber", searchCriteria.getMobileNumber());
		}

		if (StringUtils.isNotBlank(searchCriteria.getTransactionNumber())) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(" py_inner.transactionNumber = :transactionNumber");
			preparedStatementValues.put("transactionNumber", searchCriteria.getTransactionNumber());
		}

		if (searchCriteria.getFromDate() != null) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(" py_inner.transactionDate >= :fromDate");
			preparedStatementValues.put("fromDate", searchCriteria.getFromDate());
		}

		if (searchCriteria.getToDate() != null) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(" py_inner.transactionDate <= :toDate");
			Calendar c = Calendar.getInstance();
			c.setTime(new Date(searchCriteria.getToDate()));
			c.add(Calendar.DATE, 1);
			searchCriteria.setToDate(c.getTime().getTime());

			preparedStatementValues.put("toDate", searchCriteria.getToDate());
		}

		if (!CollectionUtils.isEmpty(searchCriteria.getPayerIds())) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(" py_inner.payerid IN (:payerid)  ");
			preparedStatementValues.put("payerid", searchCriteria.getPayerIds());
		}

		addPaymentDetailWhereClause(selectQuery, preparedStatementValues, searchCriteria);
		addBillWhereCluase(selectQuery, preparedStatementValues, searchCriteria);

	}

	private static void addPaymentDetailWhereClause(StringBuilder selectQuery,
			Map<String, Object> preparedStatementValues, PaymentSearchCriteria searchCriteria) {

		StringBuilder paymentDetailQuery = new StringBuilder(
				" id in (select pyd.paymentid from egcl_paymentdetail as pyd ");
		Map<String, Object> paymentDetailPreparedStatementValues = new HashMap<>();

		if (!CollectionUtils.isEmpty(searchCriteria.getBusinessServices())) {
			addClauseIfRequired(paymentDetailPreparedStatementValues, paymentDetailQuery);
			paymentDetailQuery.append(" pyd.businessService IN (:businessService)  ");
			preparedStatementValues.put("businessService", searchCriteria.getBusinessServices());
			paymentDetailPreparedStatementValues.put("businessService", searchCriteria.getBusinessServices());
		}

		if (!CollectionUtils.isEmpty(searchCriteria.getBillIds())) {
			addClauseIfRequired(paymentDetailPreparedStatementValues, paymentDetailQuery);
			paymentDetailQuery.append(" pyd.billid in (:billid)");
			preparedStatementValues.put("billid", searchCriteria.getBillIds());
			paymentDetailPreparedStatementValues.put("billid", searchCriteria.getBillIds());
		}

		if (searchCriteria.getReceiptNumbers() != null && !searchCriteria.getReceiptNumbers().isEmpty()) {
			addClauseIfRequired(paymentDetailPreparedStatementValues, paymentDetailQuery);
			paymentDetailQuery.append(" pyd.receiptNumber IN (:receiptnumber)  ");
			preparedStatementValues.put("receiptnumber", searchCriteria.getReceiptNumbers());
			paymentDetailPreparedStatementValues.put("receiptnumber", searchCriteria.getReceiptNumbers());
		}

		if (!paymentDetailPreparedStatementValues.isEmpty()) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(paymentDetailQuery).append(") ");
		}

	}

	private static void addBillWhereCluase(StringBuilder selectQuery, Map<String, Object> preparedStatementValues,
			PaymentSearchCriteria searchCriteria) {
		if (!CollectionUtils.isEmpty(searchCriteria.getConsumerCodes())) {
			addClauseIfRequired(preparedStatementValues, selectQuery);
			selectQuery.append(
					" id in (select paymentid from egcl_paymentdetail as pyd where pyd.billid in ( select id from egcl_bill as bill where bill.consumercode in (:consumerCodes)) )");
			preparedStatementValues.put("consumerCodes", searchCriteria.getConsumerCodes());
		}
	}

	 private void addPagination(StringBuilder query,Map<String, Object> preparedStatementValues,PaymentSearchCriteria criteria){
	        int limit = config.getSearchDefaultLimit();
	        int offset = 0;
	        query.append(" OFFSET :offset ");
	        query.append(" LIMIT :limit ");

	        if(criteria.getLimit()!=null && criteria.getLimit()<=config.getSearchMaxLimit())
	            limit = criteria.getLimit();

	        if(criteria.getLimit()!=null && criteria.getLimit()>config.getSearchMaxLimit())
	            limit = config.getSearchMaxLimit();

	        if(criteria.getOffset()!=null)
	            offset = criteria.getOffset();

	        preparedStatementValues.put("offset", offset);
	        preparedStatementValues.put("limit", limit);

	    }
}