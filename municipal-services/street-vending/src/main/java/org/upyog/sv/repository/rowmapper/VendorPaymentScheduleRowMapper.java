package org.upyog.sv.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.upyog.sv.web.models.PaymentScheduleStatus;
import org.upyog.sv.web.models.VendorPaymentSchedule;

public class VendorPaymentScheduleRowMapper implements RowMapper<VendorPaymentSchedule> {
    @Override
    public VendorPaymentSchedule mapRow(ResultSet rs, int rowNum) throws SQLException {
        return VendorPaymentSchedule.builder()
                .id(rs.getString("id"))
                .certificateNo(rs.getString("certificate_no"))
                .vendorId(rs.getString("vendor_id"))
                .applicationNo(rs.getString("application_no"))
                .lastPaymentDate(rs.getDate("last_payment_date") != null ? rs.getDate("last_payment_date").toLocalDate() : null)
                .dueDate(rs.getDate("due_date").toLocalDate())
                .paymentReceiptNo(rs.getString("payment_receipt_no"))
                .status(PaymentScheduleStatus.valueOf(rs.getString("status")))
                .build();
    }
}
