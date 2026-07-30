package org.egov.garbageservice.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.model.AuditDetails;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Row mapper that converts a SQL ResultSet row into a GrbgBillTracker domain object.
 */
@Component
public class GrbgBillTrackerRowMapper implements RowMapper<GrbgBillTracker> {

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
     * @param rs     the rs parameter for this operation
     * @param rowNum the rowNum parameter for this operation
     * @return the output result of type {@link GrbgBillTracker}
     */

    @Override
    public GrbgBillTracker mapRow(ResultSet rs, int rowNum) throws SQLException {

        AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("created_by"))
                .lastModifiedBy(rs.getString("last_modified_by")).createdDate(rs.getLong("created_time"))
                .lastModifiedDate(rs.getLong("last_modified_time")).build();

        JsonNode additionalDetail = null;
        String additionalDetailStr = rs.getString("additionaldetail");
        if (additionalDetailStr != null) {
            try {
                additionalDetail = new ObjectMapper().readTree(additionalDetailStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return GrbgBillTracker.builder().uuid(rs.getString("uuid"))
                .grbgApplicationId(rs.getString("grbg_application_id")).tenantId(rs.getString("tenant_id"))
                .month(rs.getString("month")).year(rs.getString("year")).fromDate(rs.getString("from_date"))
                .toDate(rs.getString("to_date")).grbgBillAmount(rs.getBigDecimal("grbg_bill_amount"))
                .billId(rs.getString("bill_id"))
                .demandId(rs.getString("demand_id"))
                .type(rs.getString("type"))
                .status(rs.getString("status"))
                .auditDetails(auditDetails)
                .rebateAmount(rs.getBigDecimal("rebate_amount"))
                .garbageBillWithoutRebate(rs.getBigDecimal("garbage_bill_without_rebate"))
                .additionaldetail(additionalDetail).build();
    }

    /**
     * Executes the purseToDate database operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates method parameters.</li>
     *   <li>Executes repository database operation.</li>
     *   <li>Processes and returns the resulting output.</li>
     * </ol>
     *
     * @param dateString the dateString parameter for this operation
     * @return the output result of type {@link Date}
     */

    private Date purseToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (!StringUtils.isEmpty(dateString)) {
            try {
                // Parse the date string to a Date object
                Date date = dateFormat.parse(dateString);
                return date;
            } catch (Exception e) {
                e.printStackTrace(); // Handle parsing errors
            }
        }
        return null;
    }

}