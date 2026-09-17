package org.egov.garbageservice.repository.builder;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.web.models.SearchCriteriaGarbageAccount;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GarbageAccountQueryBuilder {

    public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_id_udd_grbg_account')";
    public static final String DELETE_QUERY = "UPDATE ug_grbg_account SET is_active = false WHERE garbage_id = ?";
    public static final String SELECT_NEXT_GARBAGE_ID = "select nextval('seq_ug_grbg_account_id')";
    public static final String REPLACE_STRING = "{replace}";
    public static final String GET_APPROVER_FOR_TENANT = "select code from ug_hrms_employee ehe "
            + "join ug_userrole_v1 eur on eur.user_id = ehe.id WHERE role_tenantid = ? AND role_code = 'GB_APPROVER'";
    private static final String SELECT_GRBG_ACC = " SELECT acc.* FROM ug_grbg_account acc"
            + " LEFT JOIN ug_grbg_old_details old_dtl ON old_dtl.garbage_id = acc.garbage_id"
            + " JOIN ug_grbg_collection_unit unit ON unit.garbage_id = acc.garbage_id"
            + " JOIN ug_grbg_address address ON address.garbage_id = acc.garbage_id"
            + " JOIN ug_grbg_application app ON app.garbage_id = acc.garbage_id";
    private static final String SELECT_QUERY_ACCOUNT = "SELECT acc.*, acc.due_date as acc_due_date "
            + ", old_dtl.uuid as old_dtl_uuid, old_dtl.garbage_id as old_dtl_garbage_id, old_dtl.old_garbage_id as old_dtl_old_garbage_id"
            + ", address.uuid as address_uuid, address.address_type as address_address_type, address.address1 as address_address1, address.address2 as address_address2, address.city as address_city, address.state as address_state, address.pincode as address_pincode, address.is_active as address_is_active, address.zone as address_zone, address.ulb_name as address_ulb_name, address.ulb_type as address_ulb_type, address.ward_name as address_ward_name, address.additional_detail as address_additional_detail, address.garbage_id as address_garbage_id"
            + ", unit.uuid as unit_uuid, unit.unit_name as unit_unit_name, unit.unit_ward as unit_unit_ward, unit.ulb_name as unit_ulb_name, unit.type_of_ulb as unit_type_of_ulb, unit.garbage_id as unit_garbage_id, unit.unit_type as unit_unit_type, unit.category as unit_category, unit.sub_category as unit_sub_category, unit.sub_category_type as unit_sub_category_type, unit.is_active as unit_is_active,unit.isbplunit as unit_isbplunit,unit.isbulkgeneration as unit_isbulkgeneration,unit.isvariablecalculation as unit_isvariablecalculation,unit.no_of_units as unit_no_of_units,unit.ismonthlybilling as unit_is_monthly_billing, unit.owner_type as unit_owner_type, unit.is_inheritance as unit_is_inheritance, unit.special_Category as unit_special_Category"
            + ", doc.uuid as doc_uuid, doc.document_uid as doc_document_uid, doc.file_store_id as doc_file_store_id, doc.document_type as doc_document_type, doc.garbage_id as doc_garbage_id"
            + ", sub_acc.id as sub_acc_id, sub_acc.uuid as sub_acc_uuid, sub_acc.garbage_id as sub_acc_garbage_id, sub_acc.property_id as sub_acc_property_id, sub_acc.type as sub_acc_type "
            + ", sub_acc.name as sub_acc_name, sub_acc.mobile_number as sub_acc_mobile_number, sub_acc.gender as sub_acc_gender, sub_acc.email_id as sub_acc_email_id, sub_acc.is_owner as sub_acc_is_owner"
            + ", sub_acc.user_uuid as sub_acc_user_uuid, sub_acc.declaration_uuid as sub_acc_declaration_uuid, sub_acc.status as sub_acc_status, sub_acc.business_service as sub_acc_business_service"
            + ", sub_acc.approval_date as sub_acc_approval_date, sub_acc.channel as sub_acc_channel"
            + ", sub_acc.created_by as sub_acc_created_by, sub_acc.created_date as sub_acc_created_date, sub_acc.last_modified_by as sub_acc_last_modified_by"
            + ", sub_acc.last_modified_date as sub_acc_last_modified_date, sub_acc.additional_detail as sub_acc_additional_detail, sub_acc.tenant_id as sub_acc_tenant_id, sub_acc.parent_account as sub_acc_parent_account, sub_acc.is_active as sub_acc_is_active, sub_acc.sub_account_count as sub_acc_sub_account_count"
            + ", sub_old_dtl.uuid as sub_old_dtl_uuid, sub_old_dtl.garbage_id as sub_old_dtl_garbage_id, sub_old_dtl.old_garbage_id as sub_old_dtl_old_garbage_id"
            + ", sub_address.uuid as sub_address_uuid, sub_address.address_type as sub_address_address_type, sub_address.address1 as sub_address_address1, sub_address.address2 as sub_address_address2, sub_address.city as sub_address_city, sub_address.state as sub_address_state, sub_address.pincode as sub_address_pincode, sub_address.is_active as sub_address_is_active, sub_address.zone as sub_address_zone, sub_address.ulb_name as sub_address_ulb_name, sub_address.ulb_type as sub_address_ulb_type, sub_address.ward_name as sub_address_ward_name, sub_address.additional_detail as sub_address_additional_detail, sub_address.garbage_id as sub_address_garbage_id"
            + ", app.uuid as app_uuid, app.application_no as app_application_no , app.status as app_status, app.garbage_id as app_garbage_id "
            + ", sub_app.uuid as sub_app_uuid, sub_app.application_no as sub_app_application_no , sub_app.status as sub_app_status, sub_app.garbage_id as sub_app_garbage_id "
            + ", sub_unit.uuid as sub_unit_uuid, sub_unit.unit_name as sub_unit_unit_name, sub_unit.unit_ward as sub_unit_unit_ward, sub_unit.ulb_name as sub_unit_ulb_name, sub_unit.type_of_ulb as sub_unit_type_of_ulb, sub_unit.garbage_id as sub_unit_garbage_id, sub_unit.unit_type as sub_unit_unit_type, sub_unit.category as sub_unit_category, sub_unit.sub_category as sub_unit_sub_category, sub_unit.sub_category_type as sub_unit_sub_category_type, sub_unit.is_active as sub_unit_is_active,sub_unit.isbplunit as sub_unit_isbplunit,sub_unit.isbulkgeneration as sub_unit_isbulkgeneration,sub_unit.isvariablecalculation as sub_unit_isvariablecalculation,sub_unit.no_of_units as sub_unit_no_of_units,sub_unit.ismonthlybilling as sub_unit_is_monthly_billing, sub_unit.owner_type as sub_unit_owner_type, sub_unit.is_inheritance as sub_unit_is_inheritance, sub_unit.special_Category as sub_unit_special_Category"
            + ", sub_doc.uuid as sub_doc_uuid, sub_doc.document_uid as sub_doc_document_uid, sub_doc.file_store_id as sub_doc_file_store_id, sub_doc.document_type as sub_doc_document_type, sub_doc.garbage_id as sub_doc_garbage_id"
            + " FROM filtered_acc as acc"
            + " LEFT OUTER JOIN ug_grbg_application as app ON app.garbage_id = acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_old_details as old_dtl ON old_dtl.garbage_id = acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_collection_unit as unit ON unit.garbage_id = acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_address as address ON address.garbage_id = acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_document as doc ON doc.garbage_id = acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_account sub_acc ON acc.uuid = sub_acc.parent_account"
            + " LEFT OUTER JOIN ug_grbg_application as sub_app ON sub_app.garbage_id = sub_acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_old_details as sub_old_dtl ON sub_old_dtl.garbage_id = sub_acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_collection_unit as sub_unit ON sub_unit.garbage_id = sub_acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_address as sub_address ON sub_address.garbage_id = sub_acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_document as sub_doc ON sub_doc.garbage_id = sub_acc.garbage_id";
    public static final String WITH_SUB_QUERY = " WITH filtered_acc AS ({replace}) "
            + SELECT_QUERY_ACCOUNT;
    private static final String SELECT_QUERY_ACCOUNT_INDEX = "SELECT acc.* "
            + ", old_dtl.uuid as old_dtl_uuid, old_dtl.garbage_id as old_dtl_garbage_id, old_dtl.old_garbage_id as old_dtl_old_garbage_id"
            + ", address.uuid as address_uuid, address.address_type as address_address_type, address.address1 as address_address1, address.address2 as address_address2, address.city as address_city, address.state as address_state, address.pincode as address_pincode, address.is_active as address_is_active, address.zone as address_zone, address.ulb_name as address_ulb_name, address.ulb_type as address_ulb_type, address.ward_name as address_ward_name, address.additional_detail as address_additional_detail, address.garbage_id as address_garbage_id"
            + ", unit.uuid as unit_uuid, unit.unit_name as unit_unit_name, unit.unit_ward as unit_unit_ward, unit.ulb_name as unit_ulb_name, unit.type_of_ulb as unit_type_of_ulb, unit.garbage_id as unit_garbage_id, unit.unit_type as unit_unit_type, unit.category as unit_category, unit.sub_category as unit_sub_category, unit.sub_category_type as unit_sub_category_type, unit.is_active as unit_is_active,unit.isbplunit as unit_isbplunit,unit.isbulkgeneration as unit_isbulkgeneration,unit.isvariablecalculation as unit_isvariablecalculation,unit.no_of_units as unit_no_of_units,unit.ismonthlybilling as unit_is_monthly_billing, unit.owner_type as unit_owner_type, unit.is_inheritance as unit_is_inheritance, unit.special_Category as unit_special_Category"
            + ", app.uuid as app_uuid, app.application_no as app_application_no , app.status as app_status, app.garbage_id as app_garbage_id "
            + ", doc.uuid as doc_uuid, doc.document_uid as doc_document_uid, doc.file_store_id as doc_file_store_id, doc.document_type as doc_document_type, doc.document_type as doc_document_type"
            + " FROM filtered_acc as acc"
            + " LEFT OUTER JOIN ug_grbg_application as app ON app.garbage_id = acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_old_details as old_dtl ON old_dtl.garbage_id = acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_collection_unit as unit ON unit.garbage_id = acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_address as address ON address.garbage_id = acc.garbage_id"
            + " LEFT OUTER JOIN ug_grbg_document as doc ON doc.garbage_id = acc.garbage_id";
    public static final String WITH_SUB_QUERY_INDEX = " WITH filtered_acc AS ({replace}) "
            + SELECT_QUERY_ACCOUNT_INDEX;
    public static final String INSERT_ACCOUNT = "INSERT INTO ug_grbg_account (id, uuid, garbage_id, property_id, type, name"
            + ", mobile_number, gender, email_id, is_owner, user_uuid, declaration_uuid, status, additional_detail, created_by, created_date, "
            + "last_modified_by, last_modified_date, tenant_id, parent_account, business_service, approval_date, is_active, channel) "
            + "VALUES (:id, :uuid, :garbageId, :propertyId, :type, :name, :mobileNumber, :gender, :emailId, :isOwner, :userUuid, :declarationUuid, "
            + ":status, :additionalDetail :: JSONB, :createdBy, :createdDate, "
            + ":lastModifiedBy, :lastModifiedDate, :tenantId, :parentAccount, :businessService, :approvalDate, :isActive, :channel)";
    public static final String UPDATE_ACCOUNT_BY_ID = "UPDATE ug_grbg_account SET garbage_id = :garbageId, uuid =:uuid"
            + ", property_id = :propertyId, type = :type, name = :name, mobile_number = :mobileNumber, is_owner = :isOwner"
            + ", user_uuid = :userUuid, declaration_uuid = :declarationUuid, status = :status"
            + ", gender = :gender, email_id = :emailId, additional_detail = :additionalDetail :: JSONB, last_modified_by = :lastModifiedBy, last_modified_date = :lastModifiedDate,"
            + " tenant_id = :tenantId, business_service = :businessService, approval_date = :approvalDate , channel= :channel, due_date = :dueDate WHERE id = :id";
    public static final String INSERT_ACCOUNT_AUDIT = "INSERT INTO ug_grbg_account_audit (auditid, grbg_application_no, status, type"
            + ", grbg_account_details, auditcreatedtime) VALUES ((select nextval('seq_ug_grbg_account_audit')), :grbgApplicationNo, :status"
            + ", :type, :grbgAccountDetails, (SELECT extract(epoch from now())))";

    public StringBuilder getSearchQueryByCriteria(StringBuilder searchQuery,
                                                   SearchCriteriaGarbageAccount searchCriteriaGarbageAccount, List<Object> preparedStatementValues,
                                                   Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap) {
        searchQuery = new StringBuilder(SELECT_GRBG_ACC);

        searchQuery.append(" WHERE");
        searchQuery.append(" 1=1 ");

        String whereClause = "";
        if (null != garbageCriteriaMap && !garbageCriteriaMap.isEmpty()) {
            List<String> clause = new ArrayList<>();
            garbageCriteriaMap.entrySet().forEach(garbageCriteriaValue -> {
                clause.add("(" + addWhereClause(preparedStatementValues, garbageCriteriaValue.getValue()) + ")");
            });
            if (!CollectionUtils.isEmpty(clause) && !clause.contains("()")) {
                addAndClauseIfRequired(true, searchQuery);
                whereClause = String.join(" OR ", clause);
            }
        } else {
            addAndClauseIfRequired(true, searchQuery);
            whereClause = addWhereClause(preparedStatementValues, searchCriteriaGarbageAccount);
        }

        searchQuery.append(whereClause);

        // Apply pagination and sorting inside the CTE subquery to limit unique parent accounts
        searchQuery = addOrderByClause(searchQuery, searchCriteriaGarbageAccount);
        if (!searchCriteriaGarbageAccount.getIsSchedulerCall()) {
            searchQuery = addPaginationWrapper(searchQuery, preparedStatementValues, searchCriteriaGarbageAccount);
        }

        String withClauseQuery = WITH_SUB_QUERY.replace(REPLACE_STRING, searchQuery);

        StringBuilder sb = new StringBuilder(withClauseQuery);

        sb = addOrderByClause(sb, searchCriteriaGarbageAccount);

        return sb;
    }

    public StringBuilder getSearchQueryByCriteriaForIndex(StringBuilder searchQuery,
                                                           SearchCriteriaGarbageAccount searchCriteriaGarbageAccount, List<Object> preparedStatementValues,
                                                           Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap) {
        searchQuery = new StringBuilder(SELECT_GRBG_ACC);

        searchQuery.append(" WHERE");

        String whereClause = "";
        if (null != garbageCriteriaMap && !garbageCriteriaMap.isEmpty()) {
            searchQuery.append(" acc.parent_account IS NULL ");
            List<String> clause = new ArrayList<>();
            garbageCriteriaMap.entrySet().forEach(garbageCriteriaValue -> {
                clause.add("(" + addWhereClause(preparedStatementValues, garbageCriteriaValue.getValue()) + ")");
            });
            if (!CollectionUtils.isEmpty(clause) && !clause.contains("()")) {
                addAndClauseIfRequired(true, searchQuery);
                whereClause = "(" + String.join(" OR ", clause) + ")";
            }
        } else {
            searchQuery.append(" 1=1 ");
            addAndClauseIfRequired(true, searchQuery);
            whereClause = addWhereClause(preparedStatementValues, searchCriteriaGarbageAccount);
        }

        searchQuery.append(whereClause);

        // Apply pagination and sorting inside the CTE subquery to limit unique parent accounts
        searchQuery = addOrderByClause(searchQuery, searchCriteriaGarbageAccount);
        if (!searchCriteriaGarbageAccount.getIsSchedulerCall()) {
            searchQuery = addPaginationWrapper(searchQuery, preparedStatementValues, searchCriteriaGarbageAccount);
        }

        String withClauseQuery = WITH_SUB_QUERY_INDEX.replace(REPLACE_STRING, searchQuery);

        StringBuilder sb = new StringBuilder(withClauseQuery);

        // Apply sorting to the outer query as well to ensure consistent output order
        sb = addOrderByClause(sb, searchCriteriaGarbageAccount);

        return sb;
    }

    private StringBuilder addPaginationWrapper(StringBuilder searchQuery, List<Object> preparedStatementValues,
                                               SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
        Long limit = 5000L;
        Long offset = 0L;

        if (null != searchCriteriaGarbageAccount.getLimit()) {
            limit = searchCriteriaGarbageAccount.getLimit();
        }
        if (null != searchCriteriaGarbageAccount.getOffset()) {
            offset = searchCriteriaGarbageAccount.getOffset();
        }

        searchQuery.append(" limit ? ");
        searchQuery.append(" offset ? ");

        preparedStatementValues.add(limit + offset);
        preparedStatementValues.add(offset);

        return searchQuery;
    }

    private StringBuilder addOrderByClause(StringBuilder searchQuery, SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
        if (StringUtils.isNotEmpty(searchCriteriaGarbageAccount.getOrderBy())) {
            searchQuery = searchQuery.append(" ORDER BY acc.id " + searchCriteriaGarbageAccount.getOrderBy());
            return searchQuery;
        }

        return searchQuery;
    }

    private String addWhereClause(List<Object> preparedStatementValues,
                                  SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
        StringBuilder whereClause = new StringBuilder();
        boolean isAppendAndClause = false;

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getId())) {
            isAppendAndClause = addAndClauseIfRequired(false, whereClause);
            whereClause.append(" acc.id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getId(),
                    preparedStatementValues)).append(" )");
        }

        if (searchCriteriaGarbageAccount.getUserType() != null) {
            if (searchCriteriaGarbageAccount.getUserType().equalsIgnoreCase(GrbgConstants.USER_TYPE_EMPLOYEE)) {
                if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getCreatedBy())) {
                    isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
                    whereClause.append(" acc.created_by IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getCreatedBy(),
                            preparedStatementValues)).append(" )");
                }

                if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUser_uuid())) {
                    isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
                    whereClause.append(" acc.user_uuid IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getUser_uuid(),
                            preparedStatementValues)).append(" )");
                }
            } else {
                boolean hasCreatedBy = !CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getCreatedBy());
                boolean hasUserUuid = !CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUser_uuid());

                if (hasCreatedBy || hasUserUuid) {
                    isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
                    whereClause.append(" ( ");

                    boolean nestedOr = false;
                    if (hasCreatedBy) {
                        whereClause.append(" acc.created_by IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getCreatedBy(),
                                preparedStatementValues)).append(" )");
                        nestedOr = true;
                    }

                    if (hasUserUuid) {
                        if (nestedOr) {
                            whereClause.append(" OR ");
                        }
                        whereClause.append(" acc.user_uuid IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getUser_uuid(),
                                preparedStatementValues)).append(" )");
                    }

                    whereClause.append(" ) ");
                }
            }
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getGarbageId())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.garbage_id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getGarbageId(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getPropertyId())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.property_id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getPropertyId(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUuid())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.uuid IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getUuid(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getType())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.type IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getType(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getName())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.name IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getName(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getMobileNumber())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.mobile_number IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getMobileNumber(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getApplicationNumber())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" app.application_no IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getApplicationNumber(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getStatus())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.status IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getStatus(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getStatusList())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.status IN ( ").append(
                            getQueryForCollection(searchCriteriaGarbageAccount.getStatusList(), preparedStatementValues))
                    .append(" )");
        }

        if (null != searchCriteriaGarbageAccount.getTenantId()) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.tenant_id = ?");
            preparedStatementValues.add(searchCriteriaGarbageAccount.getTenantId());
        }

        if (null != searchCriteriaGarbageAccount.getIsOwner()) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.is_owner = ?");
            preparedStatementValues.add(searchCriteriaGarbageAccount.getIsOwner());
        }

        if (null != searchCriteriaGarbageAccount.getParentAccount()) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.parent_account = ?");
            preparedStatementValues.add(searchCriteriaGarbageAccount.getParentAccount());
        }

        if (null != searchCriteriaGarbageAccount.getStartId()) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.id >= ?");
            preparedStatementValues.add(searchCriteriaGarbageAccount.getStartId());
        }

        if (null != searchCriteriaGarbageAccount.getEndId()) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.id <= ?");
            preparedStatementValues.add(searchCriteriaGarbageAccount.getEndId());
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getChannels())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.channel IN ( ")
                    .append(getQueryForCollection(searchCriteriaGarbageAccount.getChannels(), preparedStatementValues))
                    .append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getWardNames())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" address.ward_name IN ( ")
                    .append(getQueryForCollection(searchCriteriaGarbageAccount.getWardNames(), preparedStatementValues))
                    .append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getOldGarbageIds())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" old_dtl.old_garbage_id IN ( ").append(
                            getQueryForCollection(searchCriteriaGarbageAccount.getOldGarbageIds(), preparedStatementValues))
                    .append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUnitCategories())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" unit.category IN ( ").append(
                            getQueryForCollection(searchCriteriaGarbageAccount.getUnitCategories(), preparedStatementValues))
                    .append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUnitTypes())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" unit.unit_type IN ( ")
                    .append(getQueryForCollection(searchCriteriaGarbageAccount.getUnitTypes(), preparedStatementValues))
                    .append(" )");
        }

        if (searchCriteriaGarbageAccount.getIsUserUuidNull() != null) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);

            if (searchCriteriaGarbageAccount.getIsUserUuidNull()) {
                whereClause.append(" acc.user_uuid IS NULL ");
            } else {
                whereClause.append(" acc.user_uuid IS NOT NULL ");
            }
        }

        if (searchCriteriaGarbageAccount.getFromDate() != null) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.created_date >= ?");
            preparedStatementValues.add(searchCriteriaGarbageAccount.getFromDate());
        }

        if (searchCriteriaGarbageAccount.getToDate() != null) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.created_date <= ?");
            preparedStatementValues.add(searchCriteriaGarbageAccount.getToDate());
        }

        return whereClause.toString();
    }

    private boolean addAndClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
        if (appendAndClauseFlag)
            queryString.append(" AND ");

        return true;
    }

    private String getQueryForCollection(List<?> ids, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = ids.iterator();
        while (iterator.hasNext()) {
            builder.append(" ?");
            preparedStmtList.add(iterator.next());

            if (iterator.hasNext())
                builder.append(",");
        }
        return builder.toString();
    }
}
