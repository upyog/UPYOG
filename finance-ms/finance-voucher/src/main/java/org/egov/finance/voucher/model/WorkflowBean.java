package org.egov.finance.voucher.model;

import java.util.List;

import org.egov.finance.voucher.entity.Department;
import org.egov.finance.voucher.entity.Designation;
import org.egov.finance.voucher.entity.User;

import lombok.Data;

@Data
public class WorkflowBean {

	private String actionName;
	private String actionState;
	private List<User> appoverUserList;
	private Long approverUserId;
	private String approverComments;
	private Integer departmentId;
	private List<Department> departmentList;
	private Integer designationId;
	private List<Designation> designationList;
	private String workFlowAction;
	private Long approverPositionId;
	private String currentState;

}
