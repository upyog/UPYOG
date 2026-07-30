package org.egov.garbageservice.repository.rowmapper;

import org.egov.garbageservice.model.AuditDetails;
import org.egov.garbageservice.model.GarbageBill;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ResultSet extractor that maps SQL query results into a list of GarbageBill domain objects.
 */
@Component
public class GarbageBillRowMapper implements ResultSetExtractor<List<GarbageBill>> {

    /**
     * Maps JDBC ResultSet rows into domain model objects.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Iterates through the JDBC {@link java.sql.ResultSet}.</li>
     *   <li>Extracts column values and maps database attributes to object properties.</li>
     *   <li>Populates nested child domain models and collection attributes.</li>
     *   <li>Returns the mapped domain entity object or collection.</li>
     * </ol>
     *
     * @param rs the rs parameter for this operation
     * @return the output result of type {@link List{@code <GarbageBill>}}
     */

    @Override
    public List<GarbageBill> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<Long, GarbageBill> billsMap = new LinkedHashMap<>();

        while (rs.next()) {


            Long billId = rs.getLong("id");
            GarbageBill garbageBill = billsMap.get(billId);

            if (null == garbageBill) {

                AuditDetails audit = AuditDetails.builder()
                        .createdBy(rs.getString("created_by"))
                        .createdDate(rs.getLong("created_date"))
                        .lastModifiedBy(rs.getString("last_modified_by"))
                        .lastModifiedDate(rs.getLong("last_modified_date"))
                        .build();

                garbageBill = GarbageBill.builder()
                        .id(rs.getLong("id"))
                        .billRefNo(rs.getString("bill_ref_no"))
                        .garbageId(rs.getLong("garbage_id"))
                        .billAmount(rs.getDouble("bill_amount"))
                        .arrearAmount(rs.getDouble("arrear_amount"))
                        .paneltyAmount(rs.getDouble("panelty_amount"))
                        .discountAmount(rs.getDouble("discount_amount"))
                        .totalBillAmount(rs.getDouble("total_bill_amount"))
                        .totalBillAmountAfterDueDate(rs.getDouble("total_bill_amount_after_due_date"))
                        .billGeneratedBy(rs.getString("bill_generated_by"))
                        .billGeneratedDate(rs.getLong("bill_generated_date"))
                        .billDueDate(rs.getLong("bill_due_date"))
                        .billPeriod(rs.getString("bill_period"))
                        .bankDiscountAmount(rs.getDouble("bank_discount_amount"))
                        .paymentId(rs.getString("payment_id"))
                        .paymentStatus(rs.getString("payment_status"))
                        .auditDetails(audit)
                        .build();

                billsMap.put(billId, garbageBill);
            }
        }

        return new ArrayList<>(billsMap.values());
    }
}
