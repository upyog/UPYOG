package com.example.hpgarbageservice.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.hpgarbageservice.model.GarbageBill;
import com.example.hpgarbageservice.model.AuditDetails;

public class GarbageBillRowMapper implements RowMapper<GarbageBill> {

    @Override
    public GarbageBill mapRow(ResultSet rs, int rowNum) throws SQLException {
        GarbageBill bill = new GarbageBill();
        bill.setId(rs.getLong("id"));
        bill.setBillRefNo(rs.getString("bill_ref_no"));
        bill.setGarbageId(rs.getLong("garbage_id"));
        bill.setBillAmount(rs.getDouble("bill_amount"));
        bill.setArrearAmount(rs.getDouble("arrear_amount"));
        bill.setPaneltyAmount(rs.getDouble("panelty_amount"));
        bill.setDiscountAmount(rs.getDouble("discount_amount"));
        bill.setTotalBillAmount(rs.getDouble("total_bill_amount"));
        bill.setTotalBillAmountAfterDueDate(rs.getDouble("total_bill_amount_after_due_date"));
        bill.setBillGeneratedBy(rs.getString("bill_generated_by"));
        bill.setBillGeneratedDate(rs.getLong("bill_generated_date"));
        bill.setBillDueDate(rs.getLong("bill_due_date"));
        bill.setBillPeriod(rs.getString("bill_period"));
        bill.setBankDiscountAmount(rs.getDouble("bank_discount_amount"));
        bill.setPaymentId(rs.getString("payment_id"));
        bill.setPaymentStatus(rs.getString("payment_status"));

        AuditDetails audit = new AuditDetails();
        audit.setCreatedBy(rs.getString("created_by"));
        audit.setCreatedDate(rs.getLong("created_date"));
        audit.setLastModifiedBy(rs.getString("last_modified_by"));
        audit.setLastModifiedDate(rs.getLong("last_modified_date"));
        bill.setAuditDetails(audit);

        return bill;
    }
}
