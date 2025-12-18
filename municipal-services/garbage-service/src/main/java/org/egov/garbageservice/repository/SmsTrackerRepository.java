package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.SmsTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;


@Repository
public interface SmsTrackerRepository extends JpaRepository<SmsTracker, String> {

	@Modifying
	@Transactional
	@Query(
		    value = "INSERT INTO sms_tracker(" +
		            "uuid, tenant_id, amount, application_no, service, month, year, from_date, to_date, " +
		            "financial_year, created_by, created_time, last_modified_by, last_modified_time, ward, bill_id, additional_detail, " +
		            "owner_mobile_no, owner_name, sms_request, sms_response, sms_status) " +
		            "VALUES (" +
		            ":uuid, :tenantId, :amount, :applicationNo, :service, :month, :year, :fromDate, :toDate, " +
		            ":financialYear, :createdBy, :createdTime, :lastModifiedBy, :lastModifiedTime, :ward, :billId, " +
		            "cast(:additionalDetail as jsonb), " +
		            ":mobileNumber, :ownerName, " +
		            "cast(:smsRequest as jsonb), " +
		            "cast(:smsResponse as jsonb), " +
		            ":smsStatus)",
		    nativeQuery = true
		)
	void insertSmsTracker(
	    @Param("uuid") String uuid,
	    @Param("tenantId") String tenantId,
	    @Param("amount") BigDecimal amount,
	    @Param("applicationNo") String applicationNo,
	    @Param("service") String service,
	    @Param("month") String month,
	    @Param("year") String year,
	    @Param("fromDate") String fromDate,
	    @Param("toDate") String toDate,
	    @Param("financialYear") String financialYear,
	    @Param("createdBy") String createdBy,
	    @Param("createdTime") Long createdTime,
	    @Param("lastModifiedBy") String lastModifiedBy,
	    @Param("lastModifiedTime") Long lastModifiedTime,
	    @Param("ward") String ward,
	    @Param("billId") String billId,
	    @Param("additionalDetail") String additionalDetail,
	    @Param("mobileNumber") String mobileNumber,
	    @Param("ownerName") String ownerName,
	    @Param("smsRequest") String smsRequest,
	    @Param("smsResponse") String smsResponse,
	    @Param("smsStatus") Boolean smsStatus
	);
}
