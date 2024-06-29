package org.upyog.chb.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.ResponseInfo;
import org.upyog.chb.web.models.ResponseInfo.StatusEnum;

public class CommunityHallBookingUtil {

	public static ResponseInfo createReponseInfo(final RequestInfo requestInfo, String resMsg, StatusEnum status) {

		final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
		final String ver = requestInfo != null ? requestInfo.getVer() : "";
		Long ts = null;
		if (requestInfo != null)
			ts = requestInfo.getTs();
		final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";

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
		// Locale specifies human language for translating, and cultural norms for
		// lowercase/uppercase and abbreviations and such. Example: Locale.US or
		// Locale.CANADA_FRENCH
		// formatter = formatter.withLocale( ;
		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate;
	}

	public static String parseLocalDateToString(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		// Format the LocalDate
		String formattedDate = date.format(formatter);
		return formattedDate;
	}

}
