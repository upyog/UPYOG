package org.ksmart.birth.utils;


import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.common.model.AuditDetails;
//import org.ksmart.birth.death.model.EgDeathDtl;
import org.springframework.stereotype.Component;

import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Getter
public class CommonUtils {

	public static Long currentDateTime() {
		ZoneId zone = ZoneId.of( "Asia/Calcutta") ;
		ZonedDateTime zdt = ZonedDateTime.now(zone) ;
		Long currentTime = zdt.toInstant().toEpochMilli();
		return currentTime;
	}

	public static ZonedDateTime currentDate() {
		ZoneId zone = ZoneId.of( "Asia/Calcutta") ;
		ZonedDateTime zdt = ZonedDateTime.now(zone) ;
		return zdt;
	}

	public static  ZonedDateTime LongToDate(Long dateTime) {
		Instant instant = Instant.ofEpochSecond(dateTime);
		ZoneId zone = ZoneId.of( "Asia/Calcutta") ;
		ZonedDateTime z = ZonedDateTime.ofInstant(instant, zone);
		return z;
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
