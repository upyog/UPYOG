package com.example.hpgarbageservice.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccount;
import com.example.hpgarbageservice.repository.rowmapper.GarbageAccountRowMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.hpgarbageservice.repository.rowmapper.GarbageAccountRowMapper;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GarbageAccountRepository {
	
	@Autowired
	GarbageAccountRowMapper garbageAccountRowMapper;
	
	@Autowired
	private ObjectMapper objectMapper;

	private static final String SELECT_QUERY_ACCOUNT = "SELECT acc.*, doc.uuid as doc_uuid, doc.doc_ref_id as doc_doc_ref_id, doc.doc_name as doc_doc_name, doc.doc_type as doc_doc_type, doc.doc_category as doc_doc_category, doc.tbl_ref_uuid as doc_tbl_ref_uuid "
			+ ", bill.id as bill_id, bill.bill_ref_no as bill_bill_ref_no, bill.garbage_id as bill_garbage_id"
			+ ", bill.bill_amount as bill_bill_amount, bill.arrear_amount as bill_arrear_amount, bill.panelty_amount as bill_panelty_amount, bill.discount_amount as bill_discount_amount"
			+ ", bill.total_bill_amount as bill_total_bill_amount, bill.total_bill_amount_after_due_date as bill_total_bill_amount_after_due_date"
			+ ", bill.bill_generated_by as bill_bill_generated_by, bill.bill_generated_date as bill_bill_generated_date, bill.bill_due_date as bill_bill_due_date"
			+ ", bill.bill_period as bill_bill_period, bill.bank_discount_amount as bill_bank_discount_amount, bill.payment_id as bill_payment_id"
			+ ", bill.payment_status as bill_payment_status, bill.created_by as bill_created_by, bill.created_date as bill_created_date, bill.last_modified_by as bill_last_modified_by"
			+ ", bill.last_modified_date as bill_last_modified_date "
			+ ", old_dtl.uuid as old_dtl_uuid, old_dtl.garbage_id as old_dtl_garbage_id, old_dtl.old_garbage_id as old_dtl_old_garbage_id"
			+ ", address.uuid as address_uuid, address.address_type as address_address_type, address.address1 as address_address1, address.address2 as address_address2, address.city as address_city, address.state as address_state, address.pincode as address_pincode, address.is_active as address_is_active, address.zone as address_zone, address.ulb_name as address_ulb_name, address.ulb_type as address_ulb_type, address.ward_name as address_ward_name, address.additional_detail as address_additional_detail, address.garbage_id as address_garbage_id"
			+ ", unit.uuid as unit_uuid, unit.unit_name as unit_unit_name, unit.unit_ward as unit_unit_ward, unit.ulb_name as unit_ulb_name, unit.type_of_ulb as unit_type_of_ulb, unit.garbage_id as unit_garbage_id, unit.unit_type as unit_unit_type, unit.category as unit_category, unit.sub_category as unit_sub_category, unit.sub_category_type as unit_sub_category_type, unit.is_active as unit_is_active"
			+ ", sub_acc.id as sub_acc_id, sub_acc.uuid as sub_acc_uuid, sub_acc.garbage_id as sub_acc_garbage_id, sub_acc.property_id as sub_acc_property_id, sub_acc.type as sub_acc_type "
			+ ", sub_acc.name as sub_acc_name, sub_acc.mobile_number as sub_acc_mobile_number, sub_acc.gender as sub_acc_gender, sub_acc.email_id as sub_acc_email_id, sub_acc.is_owner as sub_acc_is_owner"
			+ ", sub_acc.user_uuid as sub_acc_user_uuid, sub_acc.declaration_uuid as sub_acc_declaration_uuid, sub_acc.status as sub_acc_status"
			+ ", sub_acc.created_by as sub_acc_created_by, sub_acc.created_date as sub_acc_created_date, sub_acc.last_modified_by as sub_acc_last_modified_by"
			+ ", sub_acc.last_modified_date as sub_acc_last_modified_date, sub_acc.additional_detail as sub_acc_additional_detail, sub_acc.tenant_id as sub_acc_tenant_id"
			+ ", sub_acc_bill.id as sub_acc_bill_id, sub_acc_bill.bill_ref_no as sub_acc_bill_bill_ref_no, sub_acc_bill.garbage_id as sub_acc_bill_garbage_id " 
			+ ", sub_doc.uuid as sub_doc_uuid, sub_doc.doc_ref_id as sub_doc_doc_ref_id, sub_doc.doc_name as sub_doc_doc_name, sub_doc.doc_type as sub_doc_doc_type, sub_doc.doc_category as sub_doc_doc_category, sub_doc.tbl_ref_uuid as sub_doc_tbl_ref_uuid "
		    + ", sub_acc_bill.bill_amount as sub_acc_bill_bill_amount, sub_acc_bill.arrear_amount as sub_acc_bill_arrear_amount, sub_acc_bill.panelty_amount as sub_acc_bill_panelty_amount " 
		    + ", sub_acc_bill.discount_amount as sub_acc_bill_discount_amount, sub_acc_bill.total_bill_amount as sub_acc_bill_total_bill_amount " 
		    + ", sub_acc_bill.total_bill_amount_after_due_date as sub_acc_bill_total_bill_amount_after_due_date " 
		    + ", sub_acc_bill.bill_generated_by as sub_acc_bill_bill_generated_by, sub_acc_bill.bill_generated_date as sub_acc_bill_bill_generated_date " 
		    + ", sub_acc_bill.bill_due_date as sub_acc_bill_bill_due_date, sub_acc_bill.bill_period as sub_acc_bill_bill_period " 
		    + ", sub_acc_bill.bank_discount_amount as sub_acc_bill_bank_discount_amount, sub_acc_bill.payment_id as sub_acc_bill_payment_id " 
		    + ", sub_acc_bill.payment_status as sub_acc_bill_payment_status, sub_acc_bill.created_by as sub_acc_bill_created_by " 
		    + ", sub_acc_bill.created_date as sub_acc_bill_created_date, sub_acc_bill.last_modified_by as sub_acc_bill_last_modified_by " 
		    + ", sub_acc_bill.last_modified_date as sub_acc_bill_last_modified_date "
			+ ", sub_old_dtl.uuid as sub_old_dtl_uuid, sub_old_dtl.garbage_id as sub_old_dtl_garbage_id, sub_old_dtl.old_garbage_id as sub_old_dtl_old_garbage_id"
			+ ", sub_address.uuid as sub_address_uuid, sub_address.address_type as sub_address_address_type, sub_address.address1 as sub_address_address1, sub_address.address2 as sub_address_address2, sub_address.city as sub_address_city, sub_address.state as sub_address_state, sub_address.pincode as sub_address_pincode, sub_address.is_active as sub_address_is_active, sub_address.zone as sub_address_zone, sub_address.ulb_name as sub_address_ulb_name, sub_address.ulb_type as sub_address_ulb_type, sub_address.ward_name as sub_address_ward_name, sub_address.additional_detail as sub_address_additional_detail, sub_address.garbage_id as sub_address_garbage_id"
		    + ", app.uuid as app_uuid, app.application_no as app_application_no , app.status as app_status, app.garbage_id as app_garbage_id " 
		    + ", sub_app.uuid as sub_app_uuid, sub_app.application_no as sub_app_application_no , sub_app.status as sub_app_status, sub_app.garbage_id as sub_app_garbage_id "
			+ ", sub_unit.uuid as sub_unit_uuid, sub_unit.unit_name as sub_unit_unit_name, sub_unit.unit_ward as sub_unit_unit_ward, sub_unit.ulb_name as sub_unit_ulb_name, sub_unit.type_of_ulb as sub_unit_type_of_ulb, sub_unit.garbage_id as sub_unit_garbage_id, sub_unit.unit_type as sub_unit_unit_type, sub_unit.category as sub_unit_category, sub_unit.sub_category as sub_unit_sub_category, sub_unit.sub_category_type as sub_unit_sub_category_type, sub_unit.is_active as sub_unit_is_active"
		    + ", comm.uuid as comm_uuid, comm.garbage_id as comm_garbage_id, comm.business_name as comm_business_name, comm.business_type as comm_business_type, comm.owner_user_uuid as comm_owner_user_uuid " 
		    + ", sub_comm.uuid as sub_comm_uuid, sub_comm.garbage_id as sub_comm_garbage_id, sub_comm.business_name as sub_comm_business_name, sub_comm.business_type as sub_comm_business_type, sub_comm.owner_user_uuid as sub_comm_owner_user_uuid " 
		    + " FROM hpudd_grbg_account as acc "
			+ " LEFT OUTER JOIN hpudd_grbg_bill bill ON acc.garbage_id = bill.garbage_id"
		    + " LEFT OUTER JOIN grbg_application as app ON app.garbage_id = acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_commercial_details as comm ON comm.garbage_id = acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_old_details as old_dtl ON old_dtl.garbage_id = acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_collection_unit as unit ON unit.garbage_id = acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_address as address ON address.garbage_id = acc.garbage_id"
			+ " LEFT OUTER JOIN grbg_document doc ON (acc.uuid = doc.tbl_ref_uuid"
			+ " OR app.uuid = doc.tbl_ref_uuid)"// Don't include bill docs
			+ " LEFT OUTER JOIN hpudd_grbg_account sub_acc ON acc.property_id = sub_acc.property_id"
		    + " LEFT OUTER JOIN hpudd_grbg_bill as sub_acc_bill ON sub_acc.garbage_id = sub_acc_bill.garbage_id"
		    + " LEFT OUTER JOIN grbg_application as sub_app ON sub_app.garbage_id = sub_acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_commercial_details as sub_comm ON sub_comm.garbage_id = sub_acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_old_details as sub_old_dtl ON sub_old_dtl.garbage_id = sub_acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_collection_unit as sub_unit ON sub_unit.garbage_id = sub_acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_address as sub_address ON sub_address.garbage_id = sub_acc.garbage_id"
			+ " LEFT OUTER JOIN grbg_document sub_doc ON (sub_acc.uuid = sub_doc.tbl_ref_uuid"
			+ " OR sub_app.uuid = sub_doc.tbl_ref_uuid)";

    
    private static final String INSERT_ACCOUNT = "INSERT INTO hpudd_grbg_account (id, uuid, garbage_id, property_id, type, name"
    		+ ", mobile_number, gender, email_id, is_owner, user_uuid, declaration_uuid, status, additional_detail, created_by, created_date, last_modified_by, last_modified_date, tenant_id) "
    		+ "VALUES (:id, :uuid, :garbageId, :propertyId, :type, :name, :mobileNumber, :gender, :emailId, :isOwner, :userUuid, :declarationUuid, :status, :additionalDetail :: JSONB, :createdBy, :createdDate, "
    		+ ":lastModifiedBy, :lastModifiedDate, :tenantId)";
    
    private static final String UPDATE_ACCOUNT_BY_ID = "UPDATE hpudd_grbg_account SET garbage_id = :garbageId, uuid =:uuid"
    		+ ", property_id = :propertyId, type = :type, name = :name, mobile_number = :mobileNumber, is_owner = :isOwner"
    		+ ", user_uuid = :userUuid, declaration_uuid = :declarationUuid, status = :status"
    		+ ", gender = :gender, email_id = :emailId, additional_detail = :additionalDetail :: JSONB, last_modified_by = :lastModifiedBy, last_modified_date = :lastModifiedDate, tenant_id = :tenantId WHERE id = :id";

	public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_id_hpudd_grbg_account')";
    
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    public GarbageAccountRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public GarbageAccount create(GarbageAccount account) {
    	
    	account.setId(getNextSequence());
    	
        Map<String, Object> accountInputs = new HashMap<>();
        accountInputs.put("id", account.getId());
        accountInputs.put("uuid", account.getUuid());
        accountInputs.put("garbageId", account.getGarbageId());
        accountInputs.put("propertyId", account.getPropertyId());
        accountInputs.put("type", account.getType());
        accountInputs.put("name", account.getName());
        accountInputs.put("mobileNumber", account.getMobileNumber());
        accountInputs.put("isOwner", account.getIsOwner());
        accountInputs.put("userUuid", account.getUserUuid());
        accountInputs.put("declarationUuid", account.getDeclarationUuid());
//        accountInputs.put("grbgCollectionAddressUuid", account.getGrbgCollectionAddressUuid());
        accountInputs.put("status", account.getStatus());
        accountInputs.put("gender", account.getGender());
        accountInputs.put("emailId", account.getEmailId());
        accountInputs.put("additionalDetail", objectMapper.convertValue(account.getAdditionalDetail(), ObjectNode.class).toString());
//        accountInputs.put("parentId", account.getParentId());
        accountInputs.put("createdBy", account.getAuditDetails().getCreatedBy());
        accountInputs.put("createdDate", account.getAuditDetails().getCreatedDate());
        accountInputs.put("lastModifiedBy", account.getAuditDetails().getLastModifiedBy());
        accountInputs.put("lastModifiedDate", account.getAuditDetails().getLastModifiedDate());
        accountInputs.put("tenantId", account.getTenantId());

        namedParameterJdbcTemplate.update(INSERT_ACCOUNT, accountInputs);
        return account;
    }

    private Long getNextSequence() {
    	return jdbcTemplate.queryForObject(SELECT_NEXT_SEQUENCE, Long.class);
	}

	public void update(GarbageAccount newGarbageAccount) {
        Map<String, Object> accountInputs = new HashMap<>();
        accountInputs.put("id", newGarbageAccount.getId());
        accountInputs.put("uuid", newGarbageAccount.getUuid());
        accountInputs.put("garbageId", newGarbageAccount.getGarbageId());
        accountInputs.put("propertyId", newGarbageAccount.getPropertyId());
        accountInputs.put("type", newGarbageAccount.getType());
        accountInputs.put("name", newGarbageAccount.getName());
        accountInputs.put("mobileNumber", newGarbageAccount.getMobileNumber());
        accountInputs.put("isOwner", newGarbageAccount.getIsOwner());
        accountInputs.put("userUuid", newGarbageAccount.getUserUuid());
        accountInputs.put("declarationUuid", newGarbageAccount.getDeclarationUuid());
//        accountInputs.put("grbgCollectionAddressUuid", newGarbageAccount.getGrbgCollectionAddressUuid());
        accountInputs.put("status", newGarbageAccount.getStatus());
        accountInputs.put("gender", newGarbageAccount.getGender());
        accountInputs.put("emailId", newGarbageAccount.getEmailId());
        accountInputs.put("additionalDetail", objectMapper.convertValue(newGarbageAccount.getAdditionalDetail(), ObjectNode.class).toString());
//        accountInputs.put("parentId", newGarbageAccount.getParentId());
//        accountInputs.put("createdBy", newGarbageAccount.getAuditDetails().getCreatedBy());
//        accountInputs.put("createdDate", newGarbageAccount.getAuditDetails().getCreatedDate());
        accountInputs.put("lastModifiedBy", newGarbageAccount.getAuditDetails().getLastModifiedBy());
        accountInputs.put("lastModifiedDate", newGarbageAccount.getAuditDetails().getLastModifiedDate());
        accountInputs.put("tenantId", newGarbageAccount.getTenantId());

        namedParameterJdbcTemplate.update(UPDATE_ACCOUNT_BY_ID, accountInputs);
    }

    public List<GarbageAccount> searchGarbageAccount(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
    	
    	StringBuilder searchQuery = null;
		final List preparedStatementValues = new ArrayList<>();

		//generate search query
    	searchQuery = getSearchQueryByCriteria(searchQuery, searchCriteriaGarbageAccount, preparedStatementValues);
        
        log.debug("### search garbage account: "+searchQuery.toString());

        List<GarbageAccount> garbageAccounts = jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), garbageAccountRowMapper);

        return garbageAccounts;
    }

	private StringBuilder getSearchQueryByCriteria(StringBuilder searchQuery,
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount, List preparedStatementValues) {
		
		searchQuery = new StringBuilder(SELECT_QUERY_ACCOUNT);
		searchQuery = addWhereClause(searchQuery, preparedStatementValues, searchCriteriaGarbageAccount);
		return searchQuery;
	}

	private StringBuilder addWhereClause(StringBuilder searchQuery, List preparedStatementValues,
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {


        if (CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getId()) 
        		&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getGarbageId())
        		&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getPropertyId())
        		&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getType())
        		&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getName())
        		&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getMobileNumber())
        		&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getCreatedBy())) {
        	throw new RuntimeException("Provide criteria to search garbage account.");
        }

        searchQuery.append(" WHERE");
        boolean isAppendAndClause = false;

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getId())) {
            isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
            searchQuery.append(" acc.id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getId(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getCreatedBy())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" acc.created_by IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getCreatedBy(),
                    preparedStatementValues)).append(" )");
        }
        

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getGarbageId())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" acc.garbage_id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getGarbageId(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getPropertyId())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" acc.property_id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getPropertyId(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getType())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" acc.type IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getType(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getName())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" acc.name IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getName(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getMobileNumber())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" acc.mobile_number IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getMobileNumber(),
                    preparedStatementValues)).append(" )");
        }

//        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getParentId())) {
//            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
//            searchQuery.append(" acc.parent_id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getParentId(),
//                    preparedStatementValues)).append(" )");
//        }
        
        if (null != searchCriteriaGarbageAccount.getIsOwner()) {
        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" acc.is_owner = ").append(searchCriteriaGarbageAccount.getIsOwner());
        }
		
        return searchQuery;
	}
	
	private boolean addAndClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
        if (appendAndClauseFlag)
            queryString.append(" AND");

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
