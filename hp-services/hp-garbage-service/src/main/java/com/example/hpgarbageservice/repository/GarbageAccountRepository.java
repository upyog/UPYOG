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
			+ ", sub_acc.id as sub_acc_id, sub_acc.uuid as sub_acc_uuid, sub_acc.garbage_id as sub_acc_garbage_id, sub_acc.property_id as sub_acc_property_id, sub_acc.type as sub_acc_type "
			+ ", sub_acc.name as sub_acc_name, sub_acc.mobile_number as sub_acc_mobile_number, sub_acc.gender as sub_acc_gender, sub_acc.email_id as sub_acc_email_id, sub_acc.is_owner as sub_acc_is_owner"
			+ ", sub_acc.user_uuid as sub_acc_user_uuid, sub_acc.declaration_uuid as sub_acc_declaration_uuid, sub_acc.grbg_coll_address_uuid as sub_acc_grbg_coll_address_uuid, sub_acc.status as sub_acc_status"
			+ ", sub_acc.created_by as sub_acc_created_by, sub_acc.created_date as sub_acc_created_date, sub_acc.last_modified_by as sub_acc_last_modified_by"
			+ ", sub_acc.last_modified_date as sub_acc_last_modified_date, sub_acc.additional_detail as sub_acc_additional_detail"
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
		    + ", app.uuid as app_uuid, app.application_no as app_application_no , app.status as app_status, app.garbage_id as app_garbage_id " 
		    + ", sub_app.uuid as sub_app_uuid, sub_app.application_no as sub_app_application_no , sub_app.status as sub_app_status, sub_app.garbage_id as sub_app_garbage_id " 
		    + ", comm.uuid as comm_uuid, comm.garbage_id as comm_garbage_id, comm.business_name as comm_business_name, comm.business_type as comm_business_type, comm.owner_user_uuid as comm_owner_user_uuid " 
		    + ", sub_comm.uuid as sub_comm_uuid, sub_comm.garbage_id as sub_comm_garbage_id, sub_comm.business_name as sub_comm_business_name, sub_comm.business_type as sub_comm_business_type, sub_comm.owner_user_uuid as sub_comm_owner_user_uuid " 
		    + " FROM hpudd_grbg_account as acc "
			+ " LEFT OUTER JOIN hpudd_grbg_bill bill ON acc.garbage_id = bill.garbage_id"
			+ " LEFT OUTER JOIN hpudd_grbg_account sub_acc ON acc.property_id = sub_acc.property_id"
		    + " LEFT OUTER JOIN hpudd_grbg_bill as sub_acc_bill ON sub_acc.garbage_id = sub_acc_bill.garbage_id"
		    + " LEFT OUTER JOIN grbg_application as app ON app.garbage_id = acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_application as sub_app ON sub_app.garbage_id = sub_acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_commercial_details as comm ON comm.garbage_id = acc.garbage_id"
		    + " LEFT OUTER JOIN grbg_commercial_details as sub_comm ON sub_comm.garbage_id = sub_acc.garbage_id"
			+ " LEFT OUTER JOIN grbg_document doc ON (acc.uuid = doc.tbl_ref_uuid"
			+ " OR app.uuid = doc.tbl_ref_uuid)"// Don't include bill docs
			+ " LEFT OUTER JOIN grbg_document sub_doc ON (sub_acc.uuid = sub_doc.tbl_ref_uuid"
			+ " OR sub_app.uuid = sub_doc.tbl_ref_uuid)";

    
    private static final String INSERT_ACCOUNT = "INSERT INTO hpudd_grbg_account (id, uuid, garbage_id, property_id, type, name"
    		+ ", mobile_number, is_owner, user_uuid, declaration_uuid, grbg_coll_address_uuid, status, gender, email_id, additional_detail, created_by, created_date, last_modified_by, last_modified_date) "
    		+ "VALUES (:id, :uuid, :garbageId, :propertyId, :type, :name, :mobileNumber, :isOwner, :userUuid, :declarationUuid, :grbgCollectionAddressUuid, :status, :gender, :emailId, :additionalDetail :: JSONB, :createdBy, :createdDate, "
    		+ ":lastModifiedBy, :lastModifiedDate)";
    
    private static final String UPDATE_ACCOUNT_BY_ID = "UPDATE hpudd_grbg_account SET garbage_id = :garbageId, uuid =:uuid"
    		+ ", property_id = :propertyId, type = :type, name = :name, mobile_number = :mobileNumber, is_owner = :isOwner"
    		+ ", user_uuid = :userUuid, declaration_uuid = :declarationUuid, grbg_coll_address_uuid = :grbgCollectionAddressUuid, status = :status"
    		+ ", gender = :gender, email_id = :emaiId, additional_detail = :additionalDetail :: JSONB, last_modified_by = :lastModifiedBy, last_modified_date = :lastModifiedDate WHERE id = :id";

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
        accountInputs.put("grbgCollectionAddressUuid", account.getGrbgCollectionAddressUuid());
        accountInputs.put("status", account.getStatus());
        accountInputs.put("gender", account.getGender());
        accountInputs.put("emailId", account.getEmailId());
        accountInputs.put("additionalDetail", objectMapper.convertValue(account.getAdditionalDetail(), ObjectNode.class).toString());
//        accountInputs.put("parentId", account.getParentId());
        accountInputs.put("createdBy", account.getAuditDetails().getCreatedBy());
        accountInputs.put("createdDate", account.getAuditDetails().getCreatedDate());
        accountInputs.put("lastModifiedBy", account.getAuditDetails().getLastModifiedBy());
        accountInputs.put("lastModifiedDate", account.getAuditDetails().getLastModifiedDate());

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
        accountInputs.put("grbgCollectionAddressUuid", newGarbageAccount.getGrbgCollectionAddressUuid());
        accountInputs.put("status", newGarbageAccount.getStatus());
        accountInputs.put("gender", newGarbageAccount.getGender());
        accountInputs.put("emailId", newGarbageAccount.getEmailId());
        accountInputs.put("additionalDetail", newGarbageAccount.getAdditionalDetail());
//        accountInputs.put("parentId", newGarbageAccount.getParentId());
//        accountInputs.put("createdBy", newGarbageAccount.getAuditDetails().getCreatedBy());
//        accountInputs.put("createdDate", newGarbageAccount.getAuditDetails().getCreatedDate());
        accountInputs.put("lastModifiedBy", newGarbageAccount.getAuditDetails().getLastModifiedBy());
        accountInputs.put("lastModifiedDate", newGarbageAccount.getAuditDetails().getLastModifiedDate());

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
        		&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getMobileNumber())){
//        		&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getParentId())) {
        	return null;
        }

        searchQuery.append(" WHERE");
        boolean isAppendAndClause = false;

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getId())) {
            isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
            searchQuery.append(" acc.id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getId(),
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
