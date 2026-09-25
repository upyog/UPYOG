package org.egov.echallan.repository.builder;

import lombok.extern.slf4j.Slf4j;

import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.SearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component
public class ChallanQueryBuilder {

    private final ChallanConfiguration config;

    public ChallanQueryBuilder(ChallanConfiguration config) {
        this.config = config;
    }

    private static final String INNER_JOIN_STRING = " INNER JOIN ";
    private static final String LEFT_JOIN_STRING = " LEFT JOIN ";

    private static final String QUERY = "SELECT echallan.*,chaladdr.*, echallan.id as challan_id_alias,echallan.tenantid as challan_tenantId,echallan.lastModifiedTime as " +
            "challan_lastModifiedTime,echallan.createdBy as challan_createdBy,echallan.lastModifiedBy as challan_lastModifiedBy,echallan.createdTime as " +
            "challan_createdTime,chaladdr.id as chaladdr_id," +
            "echallan.accountId as uuid,echallan.description as description,echallan.challanStatus as challanStatus," +
            "echallan.receiptnumber as receiptnumber,echallan.challan_amount as challan_amount," +
            "echallan.offence_type_name as offence_type_name,echallan.offence_category_name as offence_category_name," +
            "echallan.offence_subcategory_name as offence_subcategory_name,echallan.additionalDetail as additionalDetail," +
            "doc.document_detail_id,doc.challan_id,doc.document_type,doc.filestore_id," +
            "doc.createdby as doc_createdby,doc.lastmodifiedby as doc_lastmodifiedby," +
            "doc.createdtime as doc_createdtime,doc.lastmodifiedtime as doc_lastmodifiedtime " +
            "FROM eg_challan echallan"
            +INNER_JOIN_STRING
            +"eg_challanAddress chaladdr ON chaladdr.challanid = echallan.id"
            + LEFT_JOIN_STRING
            +"eg_challan_document_detail doc ON doc.challan_id = echallan.id";

    private static final String COUNT_QUERY = "SELECT COUNT(echallan.id) " +
            "FROM eg_challan echallan"
            +INNER_JOIN_STRING
            +"eg_challanAddress chaladdr ON chaladdr.challanid = echallan.id";

    private static final String PAGINATION_WRAPPER = "SELECT * FROM " +
            "(SELECT *, DENSE_RANK() OVER (ORDER BY challan_createdTime DESC , challan_id) offset_ FROM " +
            "({})" +
            " result) result_offset " +
            "WHERE offset_ > ? AND offset_ <= ?";

      public static final String FILESTOREID_UPDATE_SQL = "UPDATE eg_challan SET filestoreid=? WHERE id=?";

      public static final String CANCEL_RECEIPT_UPDATE_SQL = "UPDATE eg_challan SET applicationStatus='ACTIVE' WHERE challanNo=? and businessService=?";
      public static final String CHALLAN_COUNT_QUERY = "SELECT applicationstatus, count(*)  FROM eg_challan WHERE tenantid ";

      public static final String TOTAL_COLLECTION_QUERY = "SELECT sum(amountpaid) FROM egbs_billdetail_v1 INNER JOIN egcl_paymentdetail ON egbs_billdetail_v1.billid=egcl_paymentdetail.billid and egcl_paymentdetail.tenantid=egbs_billdetail_v1.tenantid INNER JOIN eg_challan ON consumercode=challanno  and egbs_billdetail_v1.tenantid=eg_challan.tenantid WHERE egbs_billdetail_v1.tenantid=? AND eg_challan.applicationstatus='PAID' AND egcl_paymentdetail.createdtime>? ";

      public static final String TOTAL_SERVICES_QUERY = "SELECT count(distinct(businessservice)) FROM eg_challan WHERE tenantid=? AND createdtime>? ";


    public String getChallanSearchQuery(SearchCriteria criteria, List<Object> preparedStmtList, boolean isCountQuery) {
        StringBuilder builder = new StringBuilder(isCountQuery ? COUNT_QUERY : QUERY);
        appendSearchClauses(criteria, preparedStmtList, builder);
        return buildSearchQueryResult(builder, preparedStmtList, criteria, isCountQuery);
    }

    private void appendSearchClauses(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        addBusinessServiceClause(criteria, preparedStmtList, builder);
        addTenantIdClause(criteria, preparedStmtList, builder);
        addIdsClause(criteria, preparedStmtList, builder);
        addAccountIdClause(criteria, preparedStmtList, builder);
        addOwnerIdsClause(criteria, preparedStmtList, builder);
        addChallanNoClause(criteria, preparedStmtList, builder);
        addStatusClause(criteria, preparedStmtList, builder);
        addReceiptNumberClause(criteria, preparedStmtList, builder);
        addOffenceTypeNameClause(criteria, preparedStmtList, builder);
        addOffenceCategoryNameClause(criteria, preparedStmtList, builder);
        addOffenceSubCategoryNameClause(criteria, preparedStmtList, builder);
        addChallanStatusClause(criteria, preparedStmtList, builder);
    }

    private String buildSearchQueryResult(StringBuilder builder, List<Object> preparedStmtList,
                                          SearchCriteria criteria, boolean isCountQuery) {
        if (isCountQuery) {
            return builder.toString();
        }
        builder.append(" ORDER BY echallan.createdTime DESC, echallan.id DESC");
        return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
    }

    private void addTenantIdClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        if (criteria.getTenantId() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.tenantid=? ");
            preparedStmtList.add(criteria.getTenantId());
        }
    }

    private void addIdsClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        List<String> ids = criteria.getIds();
        if (!CollectionUtils.isEmpty(ids)) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.id IN (").append(createQuery(ids)).append(")");
            addToPreparedStatement(preparedStmtList, ids);
        }
    }

    private void addAccountIdClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        if(criteria.getAccountId() != null && !criteria.getAccountId().trim().isEmpty()) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.accountid = ? ");
            preparedStmtList.add(criteria.getAccountId());
        }
    }

    private void addOwnerIdsClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        List<String> ownerIds = criteria.getUserIds();
        if (!CollectionUtils.isEmpty(ownerIds)) {
            addClauseIfRequired(preparedStmtList, builder);
            if(criteria.getAccountId() != null && !criteria.getAccountId().trim().isEmpty()) {
                builder.append(" OR echallan.accountid IN (").append(createQuery(ownerIds)).append(")");
            } else {
                builder.append(" echallan.accountid IN (").append(createQuery(ownerIds)).append(")");
            }
            addToPreparedStatement(preparedStmtList, ownerIds);
        }
    }

    private void addChallanNoClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        if (criteria.getChallanNo() != null && !criteria.getChallanNo().trim().isEmpty()) {
            List<String> challanNos = Arrays.asList(criteria.getChallanNo().split(","));
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.challanno IN (").append(createQuery(challanNos)).append(")");
            addToPreparedStatement(preparedStmtList, challanNos);
        }
    }

    private void addStatusClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
            List<String> status = Arrays.asList(criteria.getStatus().split(","));
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.applicationstatus IN (").append(createQuery(status)).append(")");
            addToPreparedStatement(preparedStmtList, status);
        }
    }

    private void addReceiptNumberClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        if (criteria.getReceiptNumber() != null && !criteria.getReceiptNumber().trim().isEmpty()) {
            List<String> receiptNumbers = Arrays.asList(criteria.getReceiptNumber().split(","));
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.receiptnumber IN (").append(createQuery(receiptNumbers)).append(")");
            addToPreparedStatement(preparedStmtList, receiptNumbers);
        }
    }

    private void addOffenceTypeNameClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        if (criteria.getOffenceTypeName() != null && !criteria.getOffenceTypeName().trim().isEmpty()) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.offence_type_name ILIKE ? ");
            preparedStmtList.add("%" + criteria.getOffenceTypeName().trim() + "%");
        }
    }

    private void addOffenceCategoryNameClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        if (criteria.getOffenceCategoryName() != null && !criteria.getOffenceCategoryName().trim().isEmpty()) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.offence_category_name ILIKE ? ");
            preparedStmtList.add("%" + criteria.getOffenceCategoryName().trim() + "%");
        }
    }

    private void addOffenceSubCategoryNameClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        if (criteria.getOffenceSubCategoryName() != null && !criteria.getOffenceSubCategoryName().trim().isEmpty()) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.offence_subcategory_name ILIKE ? ");
            preparedStmtList.add("%" + criteria.getOffenceSubCategoryName().trim() + "%");
        }
    }

    private void addChallanStatusClause(SearchCriteria criteria, List<Object> preparedStmtList, StringBuilder builder) {
        if (criteria.getChallanStatus() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.challanStatus = ? ");
            preparedStmtList.add(criteria.getChallanStatus().name());
        }
    }

    private void addBusinessServiceClause(SearchCriteria criteria,List<Object> preparedStmtList,StringBuilder builder){
    	if(criteria.getBusinessService()!=null) {
    	List<String> businessServices = Arrays.asList(criteria.getBusinessService().split(","));
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" echallan.businessservice IN (").append(createQuery(businessServices)).append(")");
            addToPreparedStatement(preparedStmtList, businessServices);
    }
    }

    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for( int i = 0; i< length; i++){
            builder.append(" ?");
            if(i != length -1) builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids)
    {
        ids.forEach(preparedStmtList::add);
    }


    private String addPaginationWrapper(String query,List<Object> preparedStmtList,
                                      SearchCriteria criteria){
        int limit = config.getDefaultLimit();
        int offset = config.getDefaultOffset();
        String finalQuery = PAGINATION_WRAPPER.replace("{}",query);

        if(criteria.getLimit()!=null && criteria.getLimit()<=config.getMaxSearchLimit())
            limit = criteria.getLimit();

        if(criteria.getLimit()!=null && criteria.getLimit()>config.getMaxSearchLimit())
            limit = config.getMaxSearchLimit();

        if(criteria.getOffset()!=null)
            offset = criteria.getOffset();

        preparedStmtList.add(offset);
        preparedStmtList.add(limit+offset);

       return finalQuery;
    }


    private static void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
        if (values.isEmpty())
            queryString.append(" WHERE ");
        else {
            queryString.append(" AND");
        }
    }

    public String getChallanCountQuery(String tenantId, List <Object> preparedStmtList ) {
        StringBuilder builder = new StringBuilder(CHALLAN_COUNT_QUERY);
        if(tenantId.equalsIgnoreCase(config.stateLevelTenantId)){
            builder.append("LIKE ? ");
            preparedStmtList.add(tenantId+"%");
        }
        else{
            builder.append("= ? ");
            preparedStmtList.add(tenantId);
        }
        builder.append("GROUP BY applicationstatus");
        return builder.toString();
    }


	public String getTotalCollectionQuery(String tenantId, List<Object> preparedStmtListTotalCollection) {

		StringBuilder query = new StringBuilder("");
		query.append(TOTAL_COLLECTION_QUERY);

		preparedStmtListTotalCollection.add(tenantId);
		preparedStmtListTotalCollection.add(getTimestampMonthsAgo());

		return query.toString();
	}


	public String getTotalServicesQuery(String tenantId, List<Object> preparedStmtListTotalServices) {

		StringBuilder query = new StringBuilder("");
		query.append(TOTAL_SERVICES_QUERY);

		preparedStmtListTotalServices.add(tenantId);
		preparedStmtListTotalServices.add(getTimestampMonthsAgo());

		return query.toString();

	}

	private long getTimestampMonthsAgo() {
		int months = Integer.parseInt(config.getNumberOfMonths());
		return Instant.now().minus(months, ChronoUnit.MONTHS).toEpochMilli();
	}




}
