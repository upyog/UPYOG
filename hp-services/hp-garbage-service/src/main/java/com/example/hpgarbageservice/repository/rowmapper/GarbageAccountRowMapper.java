package com.example.hpgarbageservice.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.AuditDetails;
import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.GarbageBill;

@Component
public class GarbageAccountRowMapper implements ResultSetExtractor<List<GarbageAccount>> {

    @Override
    public List<GarbageAccount> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<Long, GarbageAccount> accountsMap = new LinkedHashMap<>();

        while (rs.next()) {

            Long accountId = rs.getLong("id");
            GarbageAccount garbageAccount = accountsMap.get(accountId);

            if (null == garbageAccount) {

                AuditDetails audit = AuditDetails.builder()
                        .createdBy(rs.getString("created_by"))
                        .createdDate(rs.getLong("created_date"))
                        .lastModifiedBy(rs.getString("last_modified_by"))
                        .lastModifiedDate(rs.getLong("last_modified_date"))
                        .build();

                garbageAccount = GarbageAccount.builder()
                        .id(rs.getLong("id"))
                        .garbageId(rs.getLong("garbage_id"))
                        .propertyId(rs.getLong("property_id"))
                        .type(rs.getString("type"))
                        .name(rs.getString("name"))
                        .mobileNumber(rs.getString("mobile_number"))
                        .parentId(rs.getLong("parent_id"))
                        .garbageBills(new ArrayList<>())
                        .childGarbageAccounts(new ArrayList<>())
                        .auditDetails(audit)
                        .build();

                accountsMap.put(accountId, garbageAccount);
            }

            if (null != rs.getString("bill_id")) {
                String billId = rs.getString("bill_id");
                GarbageBill garbageBill = findBillByUuid(garbageAccount.getGarbageBills(), billId);
                if (null == garbageBill) {
                    GarbageBill garbageBill1 = populateGarbageBill(rs, "bill_");
                    garbageAccount.getGarbageBills().add(garbageBill1);
                }
            }

            if (null != rs.getString("sub_acc_id")) {
                Long subAccId = rs.getLong("sub_acc_id");
                GarbageAccount subGarbageAccount = findSubAccById(garbageAccount.getChildGarbageAccounts(), subAccId);
                if (null == subGarbageAccount) {
                    subGarbageAccount = populateGarbageAccount(rs, "sub_acc_");
                    garbageAccount.getChildGarbageAccounts().add(subGarbageAccount);
                }

                if (null != rs.getString("sub_acc_bill_id")) {
                    String subAccBillId = rs.getString("sub_acc_bill_id");
                    GarbageBill subAccGarbageBill = findBillByUuid(subGarbageAccount.getGarbageBills(), subAccBillId);
                    if (null == subAccGarbageBill) {
                        GarbageBill subAccGarbageBill1 = populateGarbageBill(rs, "sub_acc_bill_");
                        subGarbageAccount.getGarbageBills().add(subAccGarbageBill1);
                    }
                }
            }
        }

        return new ArrayList<>(accountsMap.values());
    }

    private GarbageAccount populateGarbageAccount(ResultSet rs, String prefix) throws SQLException {

        GarbageAccount garbageAccount = GarbageAccount.builder()
                .id(rs.getLong(prefix + "id"))
                .garbageId(rs.getLong(prefix + "garbage_id"))
                .propertyId(rs.getLong(prefix + "property_id"))
                .type(rs.getString(prefix + "type"))
                .name(rs.getString(prefix + "name"))
                .mobileNumber(rs.getString(prefix + "mobile_number"))
                .parentId(rs.getLong(prefix + "parent_id"))
                .garbageBills(new ArrayList<>())
                .auditDetails(AuditDetails.builder()
                        .createdBy(rs.getString(prefix + "created_by"))
                        .createdDate(rs.getLong(prefix + "created_date"))
                        .lastModifiedBy(rs.getString(prefix + "last_modified_by"))
                        .lastModifiedDate(rs.getLong(prefix + "last_modified_date"))
                        .build())
                .build();

        return garbageAccount;
    }

    private GarbageAccount findSubAccById(List<GarbageAccount> subGarbageAccounts, Long subAccId) {

        if (!CollectionUtils.isEmpty(subGarbageAccounts)) {
            return subGarbageAccounts.stream()
                    .filter(acc -> acc.getId().equals(subAccId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private GarbageBill findBillByUuid(List<GarbageBill> garbageBills, String bill_id) {

        if (!CollectionUtils.isEmpty(garbageBills)) {
            return garbageBills.stream()
                    .filter(bill -> bill.getId().toString().equals(bill_id)) // Adjusted to compare as string
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private GarbageBill populateGarbageBill(ResultSet rs, String prefix) throws SQLException {

        GarbageBill bill = GarbageBill.builder()
                .id(rs.getLong(prefix + "id"))
                .billRefNo(rs.getString(prefix + "bill_ref_no"))
                .garbageId(rs.getLong(prefix + "garbage_id"))
                .billAmount(rs.getDouble(prefix + "bill_amount"))
                .arrearAmount(rs.getDouble(prefix + "arrear_amount"))
                .paneltyAmount(rs.getDouble(prefix + "panelty_amount"))
                .discountAmount(rs.getDouble(prefix + "discount_amount"))
                .totalBillAmount(rs.getDouble(prefix + "total_bill_amount"))
                .totalBillAmountAfterDueDate(rs.getDouble(prefix + "total_bill_amount_after_due_date"))
                .billGeneratedBy(rs.getString(prefix + "bill_generated_by"))
                .billGeneratedDate(rs.getLong(prefix + "bill_generated_date"))
                .billDueDate(rs.getLong(prefix + "bill_due_date"))
                .billPeriod(rs.getString(prefix + "bill_period"))
                .bankDiscountAmount(rs.getDouble(prefix + "bank_discount_amount"))
                .paymentId(rs.getString(prefix + "payment_id"))
                .paymentStatus(rs.getString(prefix + "payment_status"))
                .auditDetails(AuditDetails.builder()
                        .createdBy(rs.getString(prefix + "created_by"))
                        .createdDate(rs.getLong(prefix + "created_date"))
                        .lastModifiedBy(rs.getString(prefix + "last_modified_by"))
                        .lastModifiedDate(rs.getLong(prefix + "last_modified_date"))
                        .build())
                .build();

        return bill;
    }
}
