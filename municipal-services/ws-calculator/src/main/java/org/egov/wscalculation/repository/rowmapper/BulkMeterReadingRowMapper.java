package org.egov.wscalculation.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.wscalculation.web.models.AuditDetails;
import org.egov.wscalculation.web.models.BulkMeterReading;
import org.egov.wscalculation.web.models.BulkMeterReading.MeterStatusEnum;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class BulkMeterReadingRowMapper implements ResultSetExtractor<List<BulkMeterReading>> {

	@Override
	public List<BulkMeterReading> extractData(ResultSet rs) throws SQLException, DataAccessException {

	    Map<String, BulkMeterReading> maxReadingMap = new HashMap<>();

	    while (rs.next()) {

	        String consumerCode = rs.getString("connectionId");
	        double currentReading = rs.getDouble("currentReading");

	        if (!maxReadingMap.containsKey(consumerCode) ||
	        		currentReading > maxReadingMap.get(consumerCode).getCurrentReading()) {

	            BulkMeterReading BulkMeterReading = new BulkMeterReading();
	            BulkMeterReading.setId(rs.getString("id"));
	            BulkMeterReading.setConnectionNo(rs.getString("connectionId"));
	            BulkMeterReading.setUsageCategory(rs.getString("usageCategory"));
	            BulkMeterReading.setBillingPeriod(rs.getString("billingPeriod"));
	            BulkMeterReading.setCurrentReading(currentReading);
	            BulkMeterReading.setCurrentReadingDate(rs.getLong("currentReadingDate"));
	            BulkMeterReading.setLastReading(rs.getDouble("lastReading"));
	            BulkMeterReading.setLastReadingDate(rs.getLong("lastReadingDate"));
	            BulkMeterReading.setMeterStatus(
	                    MeterStatusEnum.fromValue(rs.getString("meterStatus")));
	            BulkMeterReading.setTenantId(rs.getString("tenantid"));

	            AuditDetails auditdetails = AuditDetails.builder()
	                    .createdBy(rs.getString("mr_createdBy"))
	                    .createdTime(rs.getLong("mr_createdTime"))
	                    .lastModifiedBy(rs.getString("mr_lastModifiedBy"))
	                    .lastModifiedTime(rs.getLong("mr_lastModifiedTime"))
	                    .build();

	            BulkMeterReading.setAuditDetails(auditdetails);
	            maxReadingMap.put(consumerCode, BulkMeterReading);
	        }
	    }
	    return new ArrayList<>(maxReadingMap.values());
	}
}