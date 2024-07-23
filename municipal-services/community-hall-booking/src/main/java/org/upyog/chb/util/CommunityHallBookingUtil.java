package org.upyog.chb.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.ResponseInfo;
import org.upyog.chb.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

public class CommunityHallBookingUtil {

	public static ResponseInfo createReponseInfo(final RequestInfo requestInfo, String resMsg, StatusEnum status) {

		final String apiId = requestInfo != null ? requestInfo.getApiId() : StringUtils.EMPTY;
		final String ver = requestInfo != null ? requestInfo.getVer() : StringUtils.EMPTY;
		Long ts = null;
		if (requestInfo != null)
			ts = requestInfo.getTs();
		final String msgId = requestInfo != null ? requestInfo.getMsgId() : StringUtils.EMPTY;

		ResponseInfo responseInfo = ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).msgId(msgId).resMsgId(resMsg)
				.status(status).build();

		return responseInfo;
	}

	public static Long getCurrentDateTime() {
		return Instant.now().toEpochMilli();
	}

	public static AuditDetails getAuditDetails(String by, Boolean isCreate) {
		Long time = System.currentTimeMillis();
		if (isCreate)
			// TODO: check if we can set lastupdated details to empty
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}

	public static String getRandonUUID() {
		return UUID.randomUUID().toString();
	}

	public static LocalDate parseStringToLocalDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate;
	}

	public static String parseLocalDateToString(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		// Format the LocalDate
		String formattedDate = date.format(formatter);
		return formattedDate;
	}
	
	public static AuditDetails getAuditDetails(ResultSet rs) throws SQLException {
		AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdBy"))
				.createdTime(rs.getLong("createdTime"))
				.lastModifiedBy(rs.getString("lastModifiedBy"))
				.lastModifiedTime(rs.getLong("lastModifiedTime")).build();
		return auditdetails;
	}
	
	public static String beuatifyJson(Object result) {
		ObjectMapper mapper = new ObjectMapper();
		String data = null;
		try {
			data = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public static String getTenantId(String tenantId) {
		return tenantId.split("\\.")[0];
	}
	

}
