package org.egov.digitaldoorplate.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class DdpConstants {

	public static final String DUTY_STATUS_STARTED = "STARTED";

	public static final String DUTY_STATUS_ENDED = "ENDED";

	public static final String WASTE_TYPE_WET = "WET";

	public static final String WASTE_TYPE_DRY = "DRY";

	public static final String WASTE_TYPE_MIXED = "MIXED";

	public static final String TIMEZONE = "Asia/Kolkata";

	public static final String PLATE_STATUS_QR_GENERATED = "QR_GENERATED";

	public static final String PLATE_STATUS_PRINT_VERIFIED = "PRINT_VERIFIED";

	public static final String PLATE_STATUS_INSTALLED = "INSTALLED";

	public static final String SYNC_STATUS_CREATED = "CREATED";

	public static final String SYNC_STATUS_QUEUED = "QUEUED";

	public static final String SYNC_STATUS_DUPLICATE = "DUPLICATE";

	public static final String SYNC_STATUS_FAILED = "FAILED";

	public static final String CONTRACTOR_STATUS_ONBOARDED = "ONBOARDED";

	public static final String CONTRACTOR_STATUS_ACTIVE = "ACTIVE";

	public static final String CONTRACTOR_STATUS_INACTIVE = "INACTIVE";

	public static final String COLLECTOR_ASSIGNMENT_STATUS_ASSIGNED = "ASSIGNED";

	public static final String COLLECTOR_ASSIGNMENT_STATUS_UNASSIGNED = "UNASSIGNED";

	public static final String USER_TYPE_EMPLOYEE = "EMPLOYEE";

	public static final String USER_ROLE_EMPLOYEE = "EMPLOYEE";

	public static final String USER_ROLE_GARBAGE_COLLECTOR = "DDP_GRBG_COLLECTOR";

	public static final String USER_ROLE_GARBAGE_SUPERVISOR = "DDP_SUPERVISOR";

	public static final String USER_ROLE_CONTRACTOR = "DDP_CONTRACTOR";

	public static final String EMPLOYEE_TYPE_PERMANENT = "PERMANENT";
	
	public static final String DEFAULT_EMPLOYEE_PASSWORD = "CitiSeva@0225";

	/**
	 * Placeholder department/designation until real HRMS master codes exist for
	 * this tenant; every DDP-onboarded employee (contractor/collector/
	 * supervisor) is assigned the same pair.
	 */
	public static final String DDP_DEFAULT_DEPARTMENT = "DEPT_1";

	public static final String DDP_DEFAULT_DESIGNATION = "DESIG_01";

	/**
	 * Placeholder dob (01-01-1990) for HRMS's mandatory user.dob when the
	 * onboarded person's actual date of birth isn't captured/provided.
	 */
	public static final Long DEFAULT_DOB = 631152000000L;

	@Value("${state.level.tenant.id}")
	private String stateLevelTenantId;

	@Value("${kafka.topics.save.garbage.collection}")
	private String saveGarbageCollectionTopic;

	@Value("${egov.garbage.service.host}")
	private String garbageServiceHostUrl;

	@Value("${egov.garbage.account.search.endpoint}")
	private String garbageAccountSearchEndpoint;

	@Value("${egov.garbage.account.update.ddp.workflow.endpoint}")
	private String garbageAccountUpdateDdpWorkflowEndpoint;

	@Value("${egov.property.context.host}")
	private String propertyServiceHostUrl;

	@Value("${egov.property.search.endpoint}")
	private String propertySearchEndpoint;

	public static final String VENDOR_PRINT_VERIFIED_STATUS_VERIFIED = "VERIFIED";

	public static final String VENDOR_PRINT_VERIFIED_STATUS_REJECTED = "REJECTED";
}
