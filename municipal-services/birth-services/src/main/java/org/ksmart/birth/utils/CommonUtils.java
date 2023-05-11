package org.ksmart.birth.utils;


import lombok.Data;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.common.model.AuditDetails;
//import org.ksmart.birth.death.model.EgDeathDtl;
import org.springframework.stereotype.Component;

import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@Getter
public class CommonUtils {

	public static Long currentDateTime() {
		LocalDateTime instance = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
		return instance.atZone(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli();
	}

	public static LocalDate currentDate() {
		LocalDate instance = LocalDate.now(ZoneId.of("Asia/Kolkata"));
		return instance;
	}


//	public static LocalDate currentDate() {
//		LocalDate instance = LocalDate.now(ZoneId.of("Asia/Kolkata"));
////		ZoneId zone = ZoneId.of( "Asia/Calcutta") ;
////		ZonedDateTime zdt = ZonedDateTime.now(zone) ;
//		return instance;
//	}
public static String currentTime() {
	LocalTime instance = LocalTime.now(ZoneId.of("Asia/Kolkata"));
	DateTimeFormatter formatter	= DateTimeFormatter.ofPattern("hh:mm");
	String formattedString = formatter.format(instance);
	return formattedString;
}

	public static Long timeStringToLong(LocalDateTime zonedDateTime) {
		return zonedDateTime.atZone(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli();
	}
	public static String timeLongToString(Long time) {
		DateTimeFormatter formatter	= DateTimeFormatter.ofPattern("HH:mm");
		return formatter.format(LongToDate(time));
	}

	public static  LocalDateTime LongToDate(Long dateTime) {
		LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.of("Asia/Kolkata"));
		return localDateTime;
	}

	public static AuditDetails buildAuditDetails(String by, Boolean create) {
		AuditDetails auditDetails;
		Long currentTime = currentDateTime();
		if (create) {
			auditDetails = AuditDetails.builder()
					.createdBy(by)
					.createdTime(currentTime)
					.lastModifiedBy(by)
					.lastModifiedTime(currentTime)
					.build();
		} else {
			auditDetails = AuditDetails.builder()
					.lastModifiedBy(by)
					.lastModifiedTime(currentTime)
					.build();
		}
		return auditDetails;
	}
}
