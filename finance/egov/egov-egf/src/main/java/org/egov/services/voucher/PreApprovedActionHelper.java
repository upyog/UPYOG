/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.services.voucher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.egov.billsaccounting.services.CreateVoucher;
import org.egov.commons.CVoucherHeader;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.StringUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.entity.State;
import org.egov.model.voucher.WorkflowBean;
import org.egov.services.refund.RefundServiceCallbackService;
import org.egov.utils.FinancialConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PreApprovedActionHelper {

	private static final Logger LOG = LoggerFactory.getLogger(PreApprovedActionHelper.class);

	@Autowired
	@Qualifier("journalVoucherActionHelper")
	private JournalVoucherActionHelper journalVoucherActionHelper;

	@Autowired
	@Qualifier("voucherService")
	private VoucherService voucherService;

	@Autowired
	@Qualifier("createVoucher")
	private CreateVoucher createVoucher;

	@Autowired
	private MicroserviceUtils microserviceUtils;

	@Autowired
	private RefundServiceCallbackService refundServiceCallbackService;

	@Transactional
	public CVoucherHeader createVoucherFromBill(CVoucherHeader voucherHeader, final WorkflowBean workflowBean,
			final Long billId, final String voucherNumber, final Date voucherDate) {

		try {
			final Long voucherHeaderId = createVoucher.createVoucherFromBill(billId.intValue(), null, voucherNumber,
					voucherDate);

			voucherHeader = voucherService.findById(voucherHeaderId, false);

			voucherHeader = sendForApproval(voucherHeader, workflowBean);

		} catch (final ValidationException exception) {

			if (exception.getErrors().get(0).getMessage() != null
					&& !exception.getErrors().get(0).getMessage().equals(StringUtils.EMPTY)) {

				throw new ValidationException(exception.getErrors().get(0).getMessage(),
						exception.getErrors().get(0).getMessage());
			}

			throw new ValidationException("Voucher creation failed", "Voucher creation failed");
		}

		return voucherHeader;
	}

	@Transactional
	public CVoucherHeader sendForApproval(CVoucherHeader voucherHeader, final WorkflowBean workflowBean) {

		try {
			if (FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())
					&& voucherHeader.getState() == null) {

				voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);

			} else {
				voucherHeader = journalVoucherActionHelper.transitionWorkFlow(voucherHeader, workflowBean);

				voucherService.applyAuditing(voucherHeader.getState());
			}

			/*
			 * Preserve the existing voucher persistence flow.
			 */
			voucherService.persist(voucherHeader);

			/*
			 * The callback service first checks whether this voucher belongs to a refund
			 * application. For ordinary JVs, the callback is skipped.
			 */
			registerRefundCallback(voucherHeader, workflowBean);

		} catch (final ValidationException exception) {

			final List<ValidationError> errors = new ArrayList<>();

			errors.add(new ValidationError("exp", exception.getErrors().get(0).getMessage()));

			throw new ValidationException(errors);
		}

		return voucherHeader;
	}

	/**
	 * Registers a refund-service callback only for JV approval and rejection. Any
	 * callback preparation problem is logged without interrupting the existing
	 * voucher workflow.
	 */
	private void registerRefundCallback(final CVoucherHeader voucherHeader, final WorkflowBean workflowBean) {

		if (voucherHeader == null || workflowBean == null || workflowBean.getWorkFlowAction() == null) {
			return;
		}

		final String workflowAction = workflowBean.getWorkFlowAction();

		String financeStatus = null;

		if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workflowAction)) {

			financeStatus = RefundServiceCallbackService.FINANCE_APPROVED;

		} else if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workflowAction)) {

			financeStatus = RefundServiceCallbackService.FINANCE_REJECTED;
		}

		/*
		 * Forward, cancel, create-and-approve and other workflow actions do not produce
		 * this refund callback.
		 */
		if (financeStatus == null) {
			return;
		}

		try {
			refundServiceCallbackService.notifyRefundStatusAfterCommit(voucherHeader.getVoucherNumber(), financeStatus,
					workflowBean.getApproverComments());

		} catch (final Exception exception) {
			/*
			 * Callback preparation must never break the existing normal JV approval or
			 * rejection flow.
			 */
			LOG.error("Unable to register refund-service callback " + "for voucher {}",
					voucherHeader.getVoucherNumber(), exception);
		}
	}

	private Boolean validateOwner(final State state) {

		boolean ownerMatched = false;

		final List<Long> positions = new ArrayList<>();

		final Long employeeId = ApplicationThreadLocals.getUserId();

		final List<EmployeeInfo> employees = microserviceUtils.getEmployee(employeeId, null, null, null);

		if (employees != null && !employees.isEmpty()) {
			employees.get(0).getAssignments().forEach(assignment -> positions.add(assignment.getPosition()));
		}

		for (final Long position : positions) {
			if (Objects.equals(state.getOwnerPosition(), position)) {

				ownerMatched = true;
				break;
			}
		}

		return ownerMatched;
	}
}