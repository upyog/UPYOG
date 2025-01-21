package org.egov.web.notification.sms.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.web.notification.sms.models.AuditDetails;
import org.egov.web.notification.sms.models.SMSTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class SMSTemplateRowMapper implements RowMapper<SMSTemplate> {

	@Override
	public SMSTemplate mapRow(ResultSet resultSet, int rowNum) throws SQLException {

		AuditDetails auditDetails = new AuditDetails(resultSet.getString("createdby"), resultSet.getLong("createddate"),
				resultSet.getString("lastmodifiedby"), resultSet.getLong("lastmodifieddate"));

		return SMSTemplate.builder().id(resultSet.getString("id")).templateName(resultSet.getString("template_name"))
				.templateId(resultSet.getString("template_id")).smsBody(resultSet.getString("sms_body"))
				.tenantId(resultSet.getString("tenant_id")).isActive(resultSet.getBoolean("is_active"))
				.auditDetails(auditDetails).build();
	}

}
