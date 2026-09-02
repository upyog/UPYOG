<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="refundc" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Refund Approval</title>

<script type="text/javascript">
	function showValidationMessage(message) {
		if (typeof bootbox !== 'undefined') {
			bootbox.alert(message);
		} else {
			window.alert(message);
		}
	}

	function setWorkflowAction(actionName) {
		document.getElementById('actionName').value = actionName;
		return true;
	}

	function validateComments() {
		var actionName = document.getElementById('actionName').value.trim();
		var comments = document.getElementById('comments').value;
		var csrfInput = document.getElementById('refundCsrfToken');

		if (!csrfInput || !csrfInput.name || !csrfInput.value) {
			showValidationMessage('Security token is missing. Please reopen this page from Inbox.');
			return false;
		}

		if (actionName !== 'Approve' && actionName !== 'Reject') {
			showValidationMessage('Please select Approve or Reject.');
			return false;
		}

		if (actionName === 'Reject' && comments.trim() === '') {
			showValidationMessage('Comments are mandatory for rejection.');
			return false;
		}

		if (comments.length > 1000) {
			showValidationMessage('Comments must not exceed 1000 characters.');
			return false;
		}

		return true;
	}

	function refreshRefundInbox() {
		if (typeof refreshInbox === 'function') {
			refreshInbox();
		}
	}
</script>
</head>

<body onload="refreshRefundInbox();">

	<s:form action="refundApplication-action" namespace="/refund"
		method="post" theme="simple" onsubmit="return validateComments();">

		<%-- Submit the session CSRF token exposed by CsrfFilter. --%>
		<refundc:set var="refundCsrf"
			value="${requestScope['org.springframework.security.web.csrf.CsrfToken']}" />

		<input type="hidden" id="refundCsrfToken"
			name="<refundc:out value='${refundCsrf.parameterName}' />"
			value="<refundc:out value='${refundCsrf.token}' />" />

		<span class="mandatory1"> <s:actionerror /> <s:fielderror />
			<s:actionmessage />
		</span>

		<s:hidden id="id" name="id" value="%{refundApplication.id}" />

		<s:hidden id="actionName" name="actionName" value="" />

		<div class="formmainbox">
			<div class="subheadnew">Refund Approval</div>

			<table border="0" width="100%" cellspacing="0">
				<tr>
					<td width="25%" class="greybox"><b>Refund Application
							Number</b></td>
					<td width="25%" class="greybox"><s:property
							value="%{refundApplication.refundApplicationNumber}" /></td>
					<td width="25%" class="greybox"><b>Status</b></td>
					<td width="25%" class="greybox"><s:property
							value="%{refundApplication.status}" /></td>
				</tr>

				<tr>
					<td class="bluebox"><b>Source Module</b></td>
					<td class="bluebox"><s:property
							value="%{refundApplication.moduleName}" /></td>
					<td class="bluebox"><b>Business Service</b></td>
					<td class="bluebox"><s:property
							value="%{refundApplication.businessService}" /></td>
				</tr>

				<tr>
					<td class="greybox"><b>Reference Number</b></td>
					<td class="greybox"><s:property
							value="%{refundApplication.referenceNumber}" /></td>
					<td class="greybox"><b>Payment ID</b></td>
					<td class="greybox"><s:property
							value="%{refundApplication.paymentId}" /></td>
				</tr>

				<tr>
					<td class="bluebox"><b>Receipt Number</b></td>
					<td class="bluebox"><s:property
							value="%{refundApplication.receiptNumber}" /></td>
					<td class="bluebox"><b>Refund Amount</b></td>
					<td class="bluebox"><s:property
							value="%{refundApplication.refundAmount}" /></td>
				</tr>

				<tr>
					<td class="greybox"><b>Debit GL Code</b></td>
					<td class="greybox"><s:property
							value="%{refundApplication.debitGlCode}" /></td>
					<td class="greybox"><b>Credit GL Code</b></td>
					<td class="greybox"><s:property
							value="%{refundApplication.creditGlCode}" /></td>
				</tr>

				<tr>
					<td class="bluebox"><b>Refund Reason</b></td>
					<td colspan="3" class="bluebox"><s:property
							value="%{refundApplication.refundReason}" /></td>
				</tr>

				<tr>
					<td class="greybox"><b>Comments</b></td>
					<td colspan="3" class="greybox"><s:textarea name="comments"
							id="comments" cols="80" rows="4" maxlength="1000" /></td>
				</tr>
			</table>
		</div>

		<div id="wfHistoryDiv">
			<s:if
				test="%{refundApplication.state != null && refundApplication.state.id != null}">
				<jsp:include page="../workflow/workflowHistory.jsp" />
			</s:if>
		</div>

		<div align="center" class="buttonbottom">
			<s:if test="%{refundApplication.status == 'PENDING_APPROVAL'}">
				<s:iterator value="%{getValidActions()}" var="workflowAction"
					status="workflowStatus">

					<s:if
						test="%{#workflowAction == 'Approve' || #workflowAction == 'Reject'}">
						<s:submit type="submit" cssClass="buttonsubmit"
							value="%{#workflowAction}" id="wfBtn%{#workflowStatus.index}"
							onclick="return setWorkflowAction('%{#workflowAction}');" />
					</s:if>

				</s:iterator>
			</s:if>

			<input type="button" id="buttonClose" value="Close"
				onclick="window.close();" class="button" />
		</div>

	</s:form>

</body>
</html>