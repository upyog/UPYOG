package com.example.hpgarbageservice.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccount;
import com.example.hpgarbageservice.repository.rowmapper.GarbageAccountRowMapper;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GarbageAccountRepository {
	
	@Autowired
	GarbageAccountRowMapper garbageAccountRowMapper;

	private static final String SELECT_QUERY_ACCOUNT = "SELECT acc.*, bill.id as bill_id, bill.bill_ref_no as bill_bill_ref_no, bill.garbage_id as bill_garbage_id"
			+ ", bill.bill_amount as bill_bill_amount, bill.arrear_amount as bill_arrear_amount, bill.panelty_amount as bill_panelty_amount, bill.discount_amount as bill_discount_amount"
			+ ", bill.total_bill_amount as bill_total_bill_amount, bill.total_bill_amount_after_due_date as bill_total_bill_amount_after_due_date"
			+ ", bill.bill_generated_by as bill_bill_generated_by, bill.bill_generated_date as bill_bill_generated_date, bill.bill_due_date as bill_bill_due_date"
			+ ", bill.bill_period as bill_bill_period, bill.bank_discount_amount as bill_bank_discount_amount, bill.payment_id as bill_payment_id"
			+ ", bill.payment_status as bill_payment_status, bill.created_by as bill_created_by, bill.created_date as bill_created_date, bill.last_modified_by as bill_last_modified_by"
			+ ", bill.last_modified_date as bill_last_modified_date "
			+ ", sub_acc.id as sub_acc_id, sub_acc.garbage_id as sub_acc_garbage_id, sub_acc.property_id as sub_acc_property_id, sub_acc.type as sub_acc_type "
			+ ", sub_acc.name as sub_acc_name, sub_acc.mobile_number as sub_acc_mobile_number, sub_acc.parent_id as sub_acc_parent_id"
			+ ", sub_acc.created_by as sub_acc_created_by, sub_acc.created_date as sub_acc_created_date, sub_acc.last_modified_by as sub_acc_last_modified_by"
			+ ", sub_acc.last_modified_date as sub_acc_last_modified_date"
			+ " FROM hpudd_grbg_account as acc "
			+ " LEFT OUTER JOIN hpudd_grbg_bill bill ON acc.garbage_id = bill.garbage_id"
			+ " LEFT OUTER JOIN hpudd_grbg_account sub_acc ON acc.garbage_id = sub_acc.parent_id";

    
    private static final String INSERT_ACCOUNT = "INSERT INTO hpudd_grbg_account (id, garbage_id, property_id, type, name"
    		+ ", mobile_number, parent_id, created_by, created_date, last_modified_by, last_modified_date) "
    		+ "VALUES (:id, :garbageId, :propertyId, :type, :name, :mobileNumber, :parentId, :createdBy, :createdDate, "
    		+ ":lastModifiedBy, :lastModifiedDate)";
    
    private static final String UPDATE_ACCOUNT_BY_ID = "UPDATE hpudd_grbg_account SET garbage_id = :garbageId"
    		+ ", property_id = :propertyId, type = :type, name = :name, mobile_number = :mobileNumber, parent_id = :parentId"
    		+ ", last_modified_by = :lastModifiedBy, last_modified_date = :lastModifiedDate WHERE id = :id";

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
        accountInputs.put("garbageId", account.getGarbageId());
        accountInputs.put("propertyId", account.getPropertyId());
        accountInputs.put("type", account.getType());
        accountInputs.put("name", account.getName());
        accountInputs.put("mobileNumber", account.getMobileNumber());
        accountInputs.put("parentId", account.getParentId());
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
        accountInputs.put("garbageId", newGarbageAccount.getGarbageId());
        accountInputs.put("propertyId", newGarbageAccount.getPropertyId());
        accountInputs.put("type", newGarbageAccount.getType());
        accountInputs.put("name", newGarbageAccount.getName());
        accountInputs.put("mobileNumber", newGarbageAccount.getMobileNumber());
        accountInputs.put("parentId", newGarbageAccount.getParentId());
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
        
        log.debug(searchQuery.toString());

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
        		&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getParentId())) {
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

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getParentId())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" acc.parent_id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getParentId(),
                    preparedStatementValues)).append(" )");
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
