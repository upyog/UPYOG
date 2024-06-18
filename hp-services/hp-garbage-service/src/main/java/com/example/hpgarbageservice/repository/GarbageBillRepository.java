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

import com.example.hpgarbageservice.model.GarbageBill;
import com.example.hpgarbageservice.model.GarbageBillSearchCriteria;
import com.example.hpgarbageservice.repository.rowmapper.GarbageBillRowMapper;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GarbageBillRepository {

    private static final String SELECT_QUERY_BILL = "SELECT bill.* FROM hpudd_grbg_bill as bill ";
    private static final String INSERT_BILL = "INSERT INTO hpudd_grbg_bill (id, bill_ref_no, garbage_id, bill_amount, arrear_amount, panelty_amount, discount_amount, total_bill_amount, total_bill_amount_after_due_date, bill_generated_by, bill_generated_date, bill_due_date, bill_period, bank_discount_amount, payment_id, payment_status, created_by, created_date, last_modified_by, last_modified_date) VALUES (:id, :billRefNo, :garbageId, :billAmount, :arrearAmount, :paneltyAmount, :discountAmount, :totalBillAmount, :totalBillAmountAfterDueDate, :billGeneratedBy, :billGeneratedDate, :billDueDate, :billPeriod, :bankDiscountAmount, :paymentId, :paymentStatus, :createdBy, :createdDate, :lastModifiedBy, :lastModifiedDate)";
//    private static final String SELECT_NEXT_SEQUENCE = "SELECT nextval('seq_hpudd_grbg_bill')";
    private static final String UPDATE_BILL_BY_ID = "UPDATE hpudd_grbg_bill SET bill_ref_no = :billRefNo, garbage_id = :garbageId, bill_amount = :billAmount, arrear_amount = :arrearAmount, panelty_amount = :paneltyAmount, discount_amount = :discountAmount, total_bill_amount = :totalBillAmount, total_bill_amount_after_due_date = :totalBillAmountAfterDueDate, bill_generated_by = :billGeneratedBy, bill_generated_date = :billGeneratedDate, bill_due_date = :billDueDate, bill_period = :billPeriod, bank_discount_amount = :bankDiscountAmount, payment_id = :paymentId, payment_status = :paymentStatus, last_modified_by = :lastModifiedBy, last_modified_date = :lastModifiedDate WHERE id = :id";

	public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_id_hpudd_grbg_bill')";
	
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    GarbageBillRowMapper garbageBillRowMapper;

    public GarbageBillRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public GarbageBill create(GarbageBill bill) {
    	
    	bill.setId(getNextSequence());
    	
        Map<String, Object> billInputs = new HashMap<>();
        billInputs.put("id", bill.getId());
        billInputs.put("billRefNo", bill.getBillRefNo());
        billInputs.put("garbageId", bill.getGarbageId());
        billInputs.put("billAmount", bill.getBillAmount());
        billInputs.put("arrearAmount", bill.getArrearAmount());
        billInputs.put("paneltyAmount", bill.getPaneltyAmount());
        billInputs.put("discountAmount", bill.getDiscountAmount());
        billInputs.put("totalBillAmount", bill.getTotalBillAmount());
        billInputs.put("totalBillAmountAfterDueDate", bill.getTotalBillAmountAfterDueDate());
        billInputs.put("billGeneratedBy", bill.getBillGeneratedBy());
        billInputs.put("billGeneratedDate", bill.getBillGeneratedDate());
        billInputs.put("billDueDate", bill.getBillDueDate());
        billInputs.put("billPeriod", bill.getBillPeriod());
        billInputs.put("bankDiscountAmount", bill.getBankDiscountAmount());
        billInputs.put("paymentId", bill.getPaymentId());
        billInputs.put("paymentStatus", bill.getPaymentStatus());
        billInputs.put("createdBy", bill.getAuditDetails().getCreatedBy());
        billInputs.put("createdDate", bill.getAuditDetails().getCreatedDate());
        billInputs.put("lastModifiedBy", bill.getAuditDetails().getLastModifiedBy());
        billInputs.put("lastModifiedDate", bill.getAuditDetails().getLastModifiedDate());

        namedParameterJdbcTemplate.update(INSERT_BILL, billInputs);
        return bill;
    }

//    private Long getNextSequence() {
//        return jdbcTemplate.queryForObject(SELECT_NEXT_SEQUENCE, Long.class);
//    }

    private Long getNextSequence() {
    	return jdbcTemplate.queryForObject(SELECT_NEXT_SEQUENCE, Long.class);
	}

	public void update(GarbageBill bill) {
        Map<String, Object> billInputs = new HashMap<>();
        billInputs.put("id", bill.getId());
        billInputs.put("billRefNo", bill.getBillRefNo());
        billInputs.put("garbageId", bill.getGarbageId());
        billInputs.put("billAmount", bill.getBillAmount());
        billInputs.put("arrearAmount", bill.getArrearAmount());
        billInputs.put("paneltyAmount", bill.getPaneltyAmount());
        billInputs.put("discountAmount", bill.getDiscountAmount());
        billInputs.put("totalBillAmount", bill.getTotalBillAmount());
        billInputs.put("totalBillAmountAfterDueDate", bill.getTotalBillAmountAfterDueDate());
        billInputs.put("billGeneratedBy", bill.getBillGeneratedBy());
        billInputs.put("billGeneratedDate", bill.getBillGeneratedDate());
        billInputs.put("billDueDate", bill.getBillDueDate());
        billInputs.put("billPeriod", bill.getBillPeriod());
        billInputs.put("bankDiscountAmount", bill.getBankDiscountAmount());
        billInputs.put("paymentId", bill.getPaymentId());
        billInputs.put("paymentStatus", bill.getPaymentStatus());
        billInputs.put("lastModifiedBy", bill.getAuditDetails().getLastModifiedBy());
        billInputs.put("lastModifiedDate", bill.getAuditDetails().getLastModifiedDate());

        namedParameterJdbcTemplate.update(UPDATE_BILL_BY_ID, billInputs);
    }

//    public GarbageBill findById(Long id) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("id", id);
//        return namedParameterJdbcTemplate.queryForObject(SELECT_QUERY_BILL, params, new GarbageBillRowMapper());
//    }

	public List<GarbageBill> searchGarbageBills(GarbageBillSearchCriteria garbageBillSearchCriteria) {
    	
    	StringBuilder searchQuery = null;
		final List preparedStatementValues = new ArrayList<>();

		//generate search query
    	searchQuery = getSearchQueryByCriteria(searchQuery, garbageBillSearchCriteria, preparedStatementValues);
        
        log.debug(searchQuery.toString());

        List<GarbageBill> garbageBills = jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), garbageBillRowMapper);
        
        return garbageBills;
	}

	private StringBuilder getSearchQueryByCriteria(StringBuilder searchQuery,
			GarbageBillSearchCriteria garbageBillSearchCriteria, List preparedStatementValues) {
		
		searchQuery = new StringBuilder(SELECT_QUERY_BILL);
		searchQuery = addWhereClause(searchQuery, preparedStatementValues, garbageBillSearchCriteria);
		return searchQuery;
	}

	private StringBuilder addWhereClause(StringBuilder searchQuery, List preparedStatementValues,
			GarbageBillSearchCriteria garbageBillSearchCriteria) {


        if (CollectionUtils.isEmpty(garbageBillSearchCriteria.getIds()) 
        		&& CollectionUtils.isEmpty(garbageBillSearchCriteria.getGarbageIds())
        		&& CollectionUtils.isEmpty(garbageBillSearchCriteria.getBillRefNos())
        		&& CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentIds())
        		&& CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentStatus())) {
        	return null;
        }

        searchQuery.append(" WHERE");
        boolean isAppendAndClause = false;

        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getIds())) {
            isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
            searchQuery.append(" bill.id IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getIds(),
                    preparedStatementValues)).append(" )");
        }
        

        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getBillRefNos())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" bill.bill_ref_no IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getBillRefNos(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getGarbageIds())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" bill.garbage_id IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getGarbageIds(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentIds())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" bill.payment_id IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getPaymentIds(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentStatus())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" bill.payment_status IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getPaymentStatus(),
                    preparedStatementValues)).append(" )");
        }

        return searchQuery;
	}

	private Object getQueryForCollection(List<?> ids, List<Object> preparedStatementValues) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = ids.iterator();
        while (iterator.hasNext()) {
            builder.append(" ?");
            preparedStatementValues.add(iterator.next());

            if (iterator.hasNext())
                builder.append(",");
        }
        return builder.toString();
    }

	private boolean addAndClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
        if (appendAndClauseFlag)
            queryString.append(" AND");

        return true;
    }
}
