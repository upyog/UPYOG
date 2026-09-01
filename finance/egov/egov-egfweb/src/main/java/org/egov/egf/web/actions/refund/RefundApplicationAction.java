package org.egov.egf.web.actions.refund;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.eis.web.actions.workflow.GenericWorkFlowAction;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.model.refund.RefundApplication;
import org.egov.services.refund.RefundApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.List;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.workflow.entity.State;
import org.egov.pims.service.EisUtilService;
import org.egov.infra.microservice.models.Assignment;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;

@ParentPackage("egov")
@Results({ @Result(name = RefundApplicationAction.VIEW, location = "refundApplication-view.jsp"),
		@Result(name = RefundApplicationAction.MESSAGE, location = "refundApplication-message.jsp"),
		@Result(name = RefundApplicationAction.UNAUTHORIZED, location = "../workflow/unauthorized.jsp") })
public class RefundApplicationAction extends GenericWorkFlowAction {

	private static final long serialVersionUID = 1L;

	protected static final String VIEW = "view";

	protected static final String MESSAGE = "message";

	protected static final String UNAUTHORIZED = "unauthorized";

	private RefundApplication refundApplication = new RefundApplication();

	private Long id;

	private String message;

	private String actionName;

	private String comments;

	private static final String ACTION_REJECT = "Reject";

	private static final String STATUS_REJECTED = "REJECTED";

	private static final String ACTION_APPROVE = "Approve";

	@Autowired
	@Qualifier("refundApplicationService")
	private RefundApplicationService refundApplicationService;

	@Autowired
	private MicroserviceUtils microserviceUtils;

	@Override
	public StateAware getModel() {
		return refundApplication;
	}

	/**
	 * Loads the refund application selected from the existing EGF Inbox.
	 */
	@SkipValidation
	@Action(value = "/refund/refundApplication-view")
	public String view() {

		if (id == null)
			throw new ApplicationRuntimeException("Refund application ID is mandatory");

		refundApplication = refundApplicationService.findById(id, false);

		if (refundApplication == null)
			throw new ApplicationRuntimeException("Refund application was not found for ID: " + id);

		if (refundApplication.getState() == null)
			throw new ApplicationRuntimeException("Workflow state is not available for refund application: "
					+ refundApplication.getRefundApplicationNumber());

		if (!validateOwner(refundApplication.getState()))
			return UNAUTHORIZED;

		return VIEW;
	}

	@SkipValidation
	@Action("/refund/refundApplication-action")
	public String processAction() {

		if (id == null)
			throw new ApplicationRuntimeException("Refund application ID is required");

		refundApplication = refundApplicationService.findById(id, false);

		if (refundApplication == null)
			throw new ApplicationRuntimeException("Refund application was not found for ID: " + id);

		if (refundApplication.getState() == null)
			throw new ApplicationRuntimeException("Workflow state is not available for refund application: "
					+ refundApplication.getRefundApplicationNumber());

		if (!validateOwner(refundApplication.getState()))
			return UNAUTHORIZED;

		if (actionName == null || actionName.trim().isEmpty())
			throw new ApplicationRuntimeException("Workflow action is required");

		if (ACTION_APPROVE.equalsIgnoreCase(actionName.trim()))
			return approveRefundApplication();

		if (ACTION_REJECT.equalsIgnoreCase(actionName.trim()))
			return rejectRefundApplication();

		throw new ApplicationRuntimeException("Unsupported refund workflow action: " + actionName);
	}

	private String approveRefundApplication() {

		refundApplication = refundApplicationService.approveRefundApplication(refundApplication, comments);

		message = "Refund application " + refundApplication.getRefundApplicationNumber()
				+ " has been approved successfully. " + "Journal Voucher " + refundApplication.getVoucherNumber()
				+ " has been created and forwarded for approval.";

		return MESSAGE;
	}

	private String rejectRefundApplication() {

		refundApplication = refundApplicationService.rejectRefundApplication(refundApplication, comments);

		message = "Refund application " + refundApplication.getRefundApplicationNumber()
				+ " has been rejected successfully";

		return MESSAGE;
	}

	protected Boolean validateOwner(final State state) {

		if (state == null || state.getOwnerPosition() == null) {
			return false;
		}

		final Long currentUserId = ApplicationThreadLocals.getUserId();

		if (currentUserId == null || currentUserId <= 0) {
			return false;
		}

		final List<EmployeeInfo> employees = microserviceUtils.getEmployee(currentUserId, null, null, null);

		if (employees == null || employees.size() != 1) {
			return false;
		}

		final EmployeeInfo employee = employees.get(0);

		if (employee == null || employee.getAssignments() == null) {
			return false;
		}

		for (final Assignment assignment : employee.getAssignments()) {
			if (assignment != null && Boolean.TRUE.equals(assignment.getIsCurrentAssignment())
					&& state.getOwnerPosition().equals(assignment.getPosition())) {
				return true;
			}
		}

		return false;
	}

	public RefundApplication getRefundApplication() {
		return refundApplication;
	}

	public void setRefundApplication(final RefundApplication refundApplication) {

		this.refundApplication = refundApplication;
	}

	public RefundApplicationService getRefundApplicationService() {

		return refundApplicationService;
	}

	public void setRefundApplicationService(final RefundApplicationService refundApplicationService) {

		this.refundApplicationService = refundApplicationService;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {

		this.message = message;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(final String actionName) {
		this.actionName = actionName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

}