<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/EGF'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/EGF'/>">
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/css/datatables.responsive.css' context='/services/EGF'/>" />

<style>
.finance-inbox-filters {
	margin-bottom: 18px;
	padding: 15px 18px;
	border: 1px solid #ddd;
	border-radius: 3px;
	background: #fff;
}

.finance-inbox-filters .filter-title {
	margin: 0 0 15px;
	color: #ff8000;
	font-size: 18px;
	font-weight: normal;
}

.finance-inbox-filters label {
	margin-bottom: 6px;
	color: #333;
	font-size: 13px;
	font-weight: normal;
}

.finance-inbox-filters select.form-control {
	height: 30px;
	padding: 4px 8px;
	color: #333;
	background-color: #fff;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-family: inherit;
	font-size: 13px;
	box-shadow: none;
}

.finance-inbox-filters .form-group {
	margin-bottom: 0;
}

@media ( max-width : 767px) {
	.finance-inbox-filters .form-group {
		margin-bottom: 12px;
	}
}
</style>

<div>
	<div class="finance-inbox-filters">
		<h3 class="filter-title">Inbox Filters</h3>

		<div class="row">
			<div class="col-sm-3 form-group">
				<label for="taskFilter">Nature of Task</label> <select
					id="taskFilter" class="form-control">
					<option value="">All tasks</option>
				</select>
			</div>

			<div class="col-sm-3 form-group" id="sourceModuleFilterContainer"
				style="display: none;">
				<label for="sourceModuleFilter">Refund Module</label> <select
					id="sourceModuleFilter" class="form-control">
					<option value="">All modules</option>
				</select>
			</div>
		</div>
	</div>

	<table class="table table-bordered datatable" id="official_inbox">
		<thead>
			<tr>
				<th><spring:message code="lbl.created.date" /></th>
				<th><spring:message code="lbl.sender" /></th>
				<th><spring:message code="lbl.natureoftask" /></th>
				<th><spring:message code="lbl.status" /></th>
				<th><spring:message code="lbl.details" /></th>
				<th><spring:message code="lbl.elapsed.days" /></th>
				<th></th>
			</tr>
		</thead>
	</table>
</div>
<div class="modal fade history-inbox">
	<div class="modal-dialog history">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="lbl.task.history" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-12">
						<table id="historyTable"
							class="table table-bordered datatable dataTable no-footer">
							<thead>
								<tr>
									<th><spring:message code="lbl.created.date" /></th>
									<th><spring:message code="lbl.sender" /></th>
									<th><spring:message code="lbl.natureoftask" /></th>
									<th><spring:message code="lbl.status" /></th>
									<th><spring:message code="lbl.comments" /></th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="lbl.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/services/EGF'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/EGF'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/js/datatables.responsive.js' context='/services/EGF'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/moment.min.js' context='/services/EGF'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/datetime-moment.js' context='/services/EGF'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/inbox/inbox.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
