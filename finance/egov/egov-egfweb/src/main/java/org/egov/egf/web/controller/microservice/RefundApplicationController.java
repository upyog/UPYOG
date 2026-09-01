package org.egov.egf.web.controller.microservice;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.egov.egf.contract.model.RefundApplicationRequest;
import org.egov.egf.contract.model.RefundApplicationResponse;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.Assignment;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.model.refund.RefundApplication;
import org.egov.pims.commons.Position;
import org.egov.services.refund.RefundApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefundApplicationController {

	private static final Logger LOGGER = Logger.getLogger(RefundApplicationController.class);

	private static final String EGF_MODULE = "EGF";

	private static final String REFUND_APPROVER_CONFIG_KEY = "REFUND_FINANCE_APPROVER_USER_ID";

	@Autowired
	@Qualifier("refundApplicationService")
	private RefundApplicationService refundApplicationService;

	@Autowired
	private AppConfigValueService appConfigValueService;

	@Autowired
	private MicroserviceUtils microserviceUtils;

	@PostMapping(value = "/rest/refund/_create")
	public RefundApplicationResponse create(@Valid @RequestBody final RefundApplicationRequest request) {

		try {
			if (request == null || request.getRefundApplication() == null) {
				throw new ApplicationRuntimeException("Refund application is mandatory");
			}

			if (request.getTenantId() == null || request.getTenantId().trim().isEmpty()) {
				throw new ApplicationRuntimeException("Tenant ID is mandatory");
			}

			final RefundApplication refundApplication = request.getRefundApplication();

			/*
			 * Finance controls the processing status and voucher details. These values must
			 * not be accepted from the bridge.
			 */
			refundApplication.setTenantId(request.getTenantId());
			refundApplication.setStatus(null);
			refundApplication.setVoucherNumber(null);
			refundApplication.setRejectionReason(null);

			final Position approverPosition = getRefundApproverPosition();

			final RefundApplication savedApplication = refundApplicationService
					.createRefundApplication(refundApplication, approverPosition);

			final RefundApplicationResponse response = new RefundApplicationResponse();

			response.setRefundApplications(Collections.singletonList(savedApplication));

			response.setResponseInfo(
					MicroserviceUtils.getResponseInfo(request.getRequestInfo(), HttpStatus.SC_CREATED, null));

			return response;

		} catch (final ApplicationRuntimeException e) {
			LOGGER.error(e.getMessage(), e);
			throw e;

		} catch (final Exception e) {
			LOGGER.error("Error while creating Finance refund application", e);

			final String errorMessage = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();

			throw new ApplicationRuntimeException("Failed to create Finance refund application: " + errorMessage);
		}
	}

	/**
	 * Resolves the configured UPYOG approver through HRMS rather than local EIS
	 * employee and assignment tables.
	 *
	 * Uses HRMS's isCurrentAssignment flag to select the workflow owner.
	 */
	private Position getRefundApproverPosition() {

		final List<AppConfigValues> approverConfig = appConfigValueService.getConfigValuesByModuleAndKey(EGF_MODULE,
				REFUND_APPROVER_CONFIG_KEY);

		if (approverConfig == null || approverConfig.isEmpty() || approverConfig.get(0) == null
				|| approverConfig.get(0).getValue() == null || approverConfig.get(0).getValue().trim().isEmpty()) {

			throw new ApplicationRuntimeException("AppConfig " + REFUND_APPROVER_CONFIG_KEY + " is not configured");
		}

		final Long approverUserId;

		try {
			approverUserId = Long.valueOf(approverConfig.get(0).getValue().trim());
		} catch (final NumberFormatException e) {
			throw new ApplicationRuntimeException("Invalid user ID configured in " + REFUND_APPROVER_CONFIG_KEY);
		}

		if (approverUserId <= 0) {
			throw new ApplicationRuntimeException("Invalid user ID configured in " + REFUND_APPROVER_CONFIG_KEY);
		}

		/*
		 * Search by user ID, matching the existing Inbox lookup. Do not supply asOnDate
		 * without the department/designation required by the HRMS API.
		 */
		final List<EmployeeInfo> employees = microserviceUtils.getEmployee(approverUserId, null, null, null);

		if (employees == null || employees.isEmpty()) {
			throw new ApplicationRuntimeException(
					"HRMS employee was not found for Finance approver " + "user ID " + approverUserId);
		}

		if (employees.size() != 1) {
			throw new ApplicationRuntimeException(
					"Multiple HRMS employees returned for Finance " + "approver user ID " + approverUserId);
		}

		final EmployeeInfo employee = employees.get(0);

		if (employee == null || employee.getAssignments() == null || employee.getAssignments().isEmpty()) {

			throw new ApplicationRuntimeException(
					"No HRMS assignments found for Finance approver " + "user ID " + approverUserId);
		}

		Assignment currentAssignment = null;

		for (final Assignment assignment : employee.getAssignments()) {

			if (assignment == null || !Boolean.TRUE.equals(assignment.getIsCurrentAssignment())) {
				continue;
			}

			if (currentAssignment != null) {
				throw new ApplicationRuntimeException("Multiple current HRMS assignments returned "
						+ "for Finance approver user ID " + approverUserId);
			}

			currentAssignment = assignment;
		}

		if (currentAssignment == null || currentAssignment.getPosition() == null
				|| currentAssignment.getPosition() <= 0) {

			throw new ApplicationRuntimeException("Current HRMS assignment with a valid position "
					+ "was not found for Finance approver user ID " + approverUserId);
		}

		/*
		 * withOwner() extracts getId() and stores the numeric owner position. This
		 * object is not persisted as a local position.
		 */
		final Position approverPosition = new Position();
		approverPosition.setId(currentAssignment.getPosition());

		LOGGER.info("Resolved Finance refund approver user ID " + approverUserId + " to HRMS position ID "
				+ approverPosition.getId());

		return approverPosition;
	}
}