package com.example.hpgarbageservice.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.hpgarbageservice.model.GarbageBill;
import com.example.hpgarbageservice.repository.rowmapper.GarbageBillRowMapper;

@Repository
public class GarbageBillRepository {

    private static final String GET_BILL_BY_ID = "SELECT * FROM hpudd_grbg_bill WHERE id = :id";
    private static final String INSERT_BILL = "INSERT INTO hpudd_grbg_bill (id, bill_ref_no, garbage_id, bill_amount, arrear_amount, panelty_amount, discount_amount, total_bill_amount, total_bill_amount_after_due_date, bill_generated_by, bill_generated_date, bill_due_date, bill_period, bank_discount_amount, payment_id, payment_status, created_by, created_date, last_modified_by, last_modified_date) VALUES (:id, :billRefNo, :garbageId, :billAmount, :arrearAmount, :paneltyAmount, :discountAmount, :totalBillAmount, :totalBillAmountAfterDueDate, :billGeneratedBy, :billGeneratedDate, :billDueDate, :billPeriod, :bankDiscountAmount, :paymentId, :paymentStatus, :createdBy, :createdDate, :lastModifiedBy, :lastModifiedDate)";
//    private static final String SELECT_NEXT_SEQUENCE = "SELECT nextval('seq_hpudd_grbg_bill')";
    private static final String UPDATE_BILL_BY_ID = "UPDATE hpudd_grbg_bill SET bill_ref_no = :billRefNo, garbage_id = :garbageId, bill_amount = :billAmount, arrear_amount = :arrearAmount, panelty_amount = :paneltyAmount, discount_amount = :discountAmount, total_bill_amount = :totalBillAmount, total_bill_amount_after_due_date = :totalBillAmountAfterDueDate, bill_generated_by = :billGeneratedBy, bill_generated_date = :billGeneratedDate, bill_due_date = :billDueDate, bill_period = :billPeriod, bank_discount_amount = :bankDiscountAmount, payment_id = :paymentId, payment_status = :paymentStatus, last_modified_by = :lastModifiedBy, last_modified_date = :lastModifiedDate WHERE id = :id";

	public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_id_hpudd_grbg_bill')";
	
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    public GarbageBillRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public GarbageBill create(GarbageBill bill) {
        Map<String, Object> billInputs = new HashMap<>();
        billInputs.put("id", getNextSequence());
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

    private Object getNextSequence() {
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

    public GarbageBill findById(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return namedParameterJdbcTemplate.queryForObject(GET_BILL_BY_ID, params, new GarbageBillRowMapper());
    }
}
