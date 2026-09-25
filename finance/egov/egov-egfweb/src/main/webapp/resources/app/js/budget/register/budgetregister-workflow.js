/**
 * JavaScript for BudgetRegister workflow validation and form submission handling.
 *
 * Handles workflow actions: Submit, Forward, Approve, Reject, Cancel, Revert, Create And Approve.
 * Validates required fields based on workflow action selected.
 * Manages dynamic field requirement rules for approval department, designation, position, and comments.
 * Ensures form validation before workflow transition submission.
 */


// Workflow button click handler - validates form and fields based on action type


$('.btn-wf-primary').click(function(){
	var button = $(this).attr('id');
	if (button != null && (button == 'Forward')) {
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			deleteHiddenSubledgerRow();
			return true;
		}else
			return false;

	}else if (button != null && (button == 'Create And Approve')) {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').removeAttr('required');
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			deleteHiddenSubledgerRow();
			return true;
		}else
			return false;
	} else{
		if(!validateWorkFlowApprover(button))
			return false;
		if($("form").valid()){
			deleteHiddenSubledgerRow();
			return true;
		}else
			return false;
	}
	return false;
});


/**
 * Validates workflow approver fields based on the selected workflow action.
 * Dynamically sets required/optional attributes for department, designation, position, and comments.
 *
 * Action-specific validation rules:
 * - Submit/Forward: Requires department, designation, position (comment optional)
 * - Reject/Cancel/Revert: Requires comment (department, designation, position optional)
 * - Approve: All fields optional except inherent form validations
 * **/

function validateWorkFlowApprover(name) {
	document.getElementById("workFlowAction").value = name;
	var button = document.getElementById("workFlowAction").value;
	if (button != null && button == 'Submit') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Reject') {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Cancel') {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Forward') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Approve') {
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Revert') {
        $('#approvalDepartment').removeAttr('required');
        $('#approvalDesignation').removeAttr('required');
        $('#approvalPosition').removeAttr('required');
	    $('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Create And Approve') {
		return validateCutOff();
	}else
		return true;

	return true;
}