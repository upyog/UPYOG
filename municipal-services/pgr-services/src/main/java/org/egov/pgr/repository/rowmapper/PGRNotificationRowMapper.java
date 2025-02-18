package org.egov.pgr.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.pgr.web.models.AuditDetails;
import org.egov.pgr.web.models.PGRNotification;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PGRNotificationRowMapper implements RowMapper<PGRNotification> {

	@Override
	public PGRNotification mapRow(ResultSet rs, int rowNum) throws SQLException {

		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
				.lastModifiedBy(rs.getString("lastmodifiedby")).createdTime(rs.getLong("createdtime"))
				.lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

		// Use the builder to construct the object
		return PGRNotification.builder().uuid(rs.getString("uuid")).serviceRequestId(rs.getString("servicerequestid"))
				.tenantId(rs.getString("tenantid")).applicationStatus(rs.getString("applicationstatus"))
				.recipientName(rs.getString("recipientname")).emailId(rs.getString("emailid"))
				.mobileNumber(rs.getString("mobilenumber")).isEmailSent(rs.getBoolean("isemailsent"))
				.isSmsSent(rs.getBoolean("issmssent")).auditDetails(auditDetails).build();
	}
}
