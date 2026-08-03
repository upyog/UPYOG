<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<form:form role="form" action="create" modelAttribute="budgetForm" id="budgetItemFunction"
	cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">

	<form:errors cssClass="error-msg" />

	<div class="main-content">
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary" data-collapsed="0">
					<div class="panel-heading">
						<div class="panel-title">
							<!-- <spring:message code="lbl.budget.input" text="Budget Input" /> -->
							Function:
							${function.name} (<span class="text-muted">${function.code}</span>)
							<input type="hidden" id="functionCode" name="functionCode" value="${function.code}" />
							<input type="hidden" id="functionid" name="functionid" value="${function.id}" />
							<input type="hidden" id="currentFinancialYear" name="currentFinancialYear"
								value="${currentFy.id}" />
							<input type="hidden" id="financialYear" name="financialYear" value="${nextFy.id}" />
							<form:hidden path="" id="budgetRegisterId" name="budgetRegisterId"
								value="${budgetRegisterId}" />
						</div>
					</div>

					<div class="panel-body">
						<!-- Function Info -->
						<!-- <div class="col-sm-9 add-margin pb-6">
							<strong>Function:</strong>
							${function.name} (<span class="text-muted">${function.code}</span>)
							<input type="hidden" id="functionCode" name="functionCode" value="${function.code}" />
						</div> -->

						<!-- Opening Table -->
						<table class="table table-bordered" id="openingBalanceTable">
							<thead>
								<tr>
									<th></th>
									<th>BE <strong>${currentFy.finYearRange}</strong></th>
									<th>Actuals <strong>${currentFy.finYearRange}</strong> (9 months)</th>
									<th>RE <strong>${currentFy.finYearRange}</strong></th>
									<th>BE <strong>${nextFy.finYearRange}</strong></th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<input type="hidden" id="opening.budgetgroup" name="opening.budgetgroup"
										value="Opening_Balance" />
									<input type="hidden" id="opening.functionid" name="opening.functionid"
										value="${function.id}" />
									<td style="width: 40%;">Opening Balance as on
										<fmt:formatDate value="${currentFy.startingDate}" pattern="dd/MM/yyyy" />
									</td>
									<td style="width: 15%;"><input type="number" step="0.01"
											name="opening.currentEstimate" data-pattern="decimalvalue" maxlength="12"
											class="form-control" required="required"></td>
									<td style="width: 15%;"><input type="number" step="0.01"
											name="opening.currentActual" data-pattern="decimalvalue" maxlength="12"
											class="form-control" required="required"></td>
									<td style="width: 15%;"><input type="number" step="0.01"
											name="opening.currentRevisedEstimate" data-pattern="decimalvalue"
											maxlength="12" class="form-control" required="required"></td>
									<td style="width: 15%;"><input type="number" step="0.01" name="opening.nextEstimate"
											data-pattern="decimalvalue" maxlength="12" class="form-control"
											required="required"></td>
								</tr>
							</tbody>
						</table>

						<table class="table table-bordered" id="dynamicTable">
							<thead>
								<tr>
									<th>Budget Head</th>
									<th>BE <strong>${currentFy.finYearRange}</strong></th>
									<th>Actuals <strong>${currentFy.finYearRange}</strong> (9 months)</th>
									<th>RE <strong>${currentFy.finYearRange}</strong></th>
									<th>BE <strong>${nextFy.finYearRange}</strong></th>
									<th>Actions</th>
								</tr>
							</thead>
							<tbody>
								<tr id="budgetdetailsrow">
									<td style="width: 30%;">
										<div style="margin-bottom: 8px;">
											<input type="text" id="items[0].budgetcode" name="items[0].budgetcode"
												class="form-control table-input budgetHeadcode budgetcode"
												placeholder="Type first 3 letters of Budget code"
												data-errormsg="Budget Code is mandatory!" data-idx="0"
												data-optional="0">
										</div>

										<div class="scheme-container" style="display:none;">
											<!-- <input type="text" id="items[0].schemeCode" name="items[0].schemeCode"
												class="scheme-input form-control table-input"
												placeholder="Type Scheme code"> -->

											<form:select path="items[0].scheme.id" id="items[0].schemeId"
												class="form-control scheme-input">

												<form:option value="">
													<spring:message code="lbl.select" />
												</form:option>

												<c:forEach items="${schemes}" var="scheme">
													<option value="${scheme.id}" data-statecode="${scheme.stateCode}">
														${scheme.code} - ${scheme.name}
													</option>
												</c:forEach>

											</form:select>

										</div>
									</td>

									<!-- <form:hidden path="items[0].scheme.id" name="items[0].scheme.id"
										id="items[0].scheme.id"
										class="form-control table-input hidden-input schemeId" /> -->

									<form:hidden path="" name="items[0].budgetheadcode" id="items[0].budgetheadcode"
										class="form-control table-input hidden-input budgetheadcode" />
									<form:hidden path="" name="items[0].budgetCode" id="items[0].genBudgetCode"
										class="form-control table-input hidden-input genBudgetCode" />
									<form:hidden path="" name="items[0].budgetGroup" id="items[0].budgetGroup"
										class="form-control table-input hidden-input budgetGroup" />

									<form:hidden path="" name="items[0].stateCode" id="items[0].stateCode"
										class="form-control table-input hidden-input stateCode" />

									<form:hidden path="" name="items[0].stateBudgetCode" id="items[0].stateBudgetCode"
										class="form-control table-input hidden-input stateBudgetCode" />

									<form:hidden path="items[0].budgetHead.id" name="items[0].budgetHead.id"
										id="items[0].budgetHead.id"
										class="form-control table-input hidden-input budgetHeadId" />

									<form:hidden path="" name="items[0].budgetHeadId" id="items[0].budgetHeadId"
										class="form-control table-input hidden-input budgetHeadId" />

									<td style="width: 15%;"><input type="number" step="0.01"
											name="items[0].currentEstimate" class="form-control"
											data-errormsg="Current budget estimate is mandatory!" data-idx="0"
											data-optional="0" required="required"></td>
									<td style="width: 15%;"><input type="number" step="0.01"
											name="items[0].currentActual" class="form-control"
											data-errormsg="Actuals is mandatory!" data-idx="0" data-optional="0"
											required="required"></td>
									<td style="width: 15%;"><input type="number" step="0.01"
											name="items[0].currentRevisedEstimate" class="form-control"
											data-errormsg="Current revised estimate is mandatory!" data-idx="0"
											data-optional="0" required="required"></td>
									<td style="width: 15%;"><input type="number" step="0.01"
											name="items[0].nextEstimate" class="form-control"
											data-errormsg="Next budget estimate is mandatory!" data-idx="0"
											data-optional="0" required="required"></td>

									<td class="text-center" style="width: 10%;">
										<span style="cursor:pointer;" onclick="addBudgetDetailsRow();" tabindex="0"
											id="items[0].addButton" data-toggle="tooltip" title=""
											data-original-title="press SPACE to Add!" aria-hidden="true"><i
												class="fa fa-plus"></i></span>
										<span class="add-padding debit-delete-row"
											onclick="deleteBudgetDetailsRow(this);"><i class="fa fa-trash"
												aria-hidden="true" data-toggle="tooltip" title=""
												data-original-title="Delete!"></i></span>
									</td>
								</tr>
							</tbody>
						</table>

						<!--<table class="table table-bordered" id="closingBalanceTable">
							<thead>
								<tr>
									<th></th>
									<th>BE <strong>${currentFy.finYearRange}</strong></th>
									<th>Actuals <strong>${currentFy.finYearRange}</strong> (9 months)</th>
									<th>RE <strong>${currentFy.finYearRange}</strong></th>
									<th>BE <strong>${nextFy.finYearRange}</strong></th>
								</tr>
							</thead>
							<tbody>
								<tr id="closingBalancerow">
									<input type="hidden" id="budgetGroup" name="budgetGroup" value="Closing_Balance" />
									<td style="width: 40%;">Closing Balance as on
										<fmt:formatDate value="${currentFy.endingDate}" pattern="dd/MM/yyyy" />
									</td>
									<td style="width: 15%;"><input type="text" name="closing.currentEstimate"
											class="form-control"></td>
									<td style="width: 15%;"><input type="text" name="closing.currentActual"
											class="form-control"></td>
									<td style="width: 15%;"><input type="text" name="closing.currentRevisedEstimate"
											class="form-control"></td>
									<td style="width: 15%;"><input type="text" name="closing.nextEstimate"
											class="form-control"></td>
								</tr>
							</tbody>
						</table>-->

						<!-- Submit Button -->
						<div class="text-center mt-4">
							<button type='submit' class='btn btn-primary' id="buttonSubmit">
								<spring:message code='lbl.create' text="Create" />
							</button>
						</div>

						<div class="col-sm-9 add-margin mb-3">
							<strong class="text-danger">
								<i class="fa fa-star"></i>
								&nbsp; BE: Budget Estimate, RE: Revised Estimate.
							</strong>
						</div>
					</div> <!-- /panel-body -->
				</div> <!-- /panel -->

			</div> <!-- /col-md-12 -->
		</div> <!-- /row -->
	</div> <!-- /main-content -->
</form:form>



<!-- JS -->
<script
	src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>">
</script>
<script src="<cdn:url value='/resources/app/js/budget/budgetItemFormHelper.js' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>">
</script>
<!-- <script>
$(document).ready(function () {
    var rowIndex = 2; // continue from existing rows
    var budgetHeadOptions = '<option value="">-- Select --</option>'; // placeholder

    // ✅ Load budget heads from server via AJAX (once)
    $.ajax({
        url: "/services/EGF/budgethead/ajaxBudgetHead",
        type: "GET",
        data: { query: "" }, // send empty query to get all, or change logic if needed
        success: function(data) {
            data.forEach(function(head) {
                budgetHeadOptions += `<option value="${head.id}">${head.name} - ${head.code}</option>`;
            });

            // Populate existing selects once data is ready
            $("#dynamicTable tbody select").each(function() {
                $(this).html(budgetHeadOptions);
            });
        },
        error: function() {
            alert("Failed to load budget heads.");
        }
    });

    // ✅ Add new row
    $(document).on("click", ".addRow", function () {
        let newRow = `<tr>
            <td>
                <select name="items[${rowIndex}].category" class="form-control">
                    ${budgetHeadOptions}
                </select>
            </td>
            <td><input type="text" name="items[${rowIndex}].value1" class="form-control"></td>
            <td><input type="text" name="items[${rowIndex}].value2" class="form-control"></td>
            <td><input type="text" name="items[${rowIndex}].value3" class="form-control"></td>
            <td><input type="text" name="items[${rowIndex}].value4" class="form-control"></td>
            <td class="text-center">
                <span style="cursor:pointer;" class="addRow" tabindex="0"
                    data-toggle="tooltip" title="Add new row" aria-hidden="true">
                    <i class="fa fa-plus text-success"></i>
                </span>
                <span class="add-padding removeRow" style="cursor:pointer;"
                    data-toggle="tooltip" title="Delete row" aria-hidden="true">
                    <i class="fa fa-trash text-danger"></i>
                </span>
            </td>
        </tr>`;

        // Insert new row before closing balance row
        $("#dynamicTable tbody tr:last").before(newRow);

        rowIndex++;
    });

    // ✅ Remove row (ensure at least one dynamic row remains)
    $(document).on("click", ".removeRow", function () {
        let totalDynamicRows = $("#dynamicTable tbody tr").length - 2;

        if (totalDynamicRows > 1) {
            $(this).closest("tr").remove();
        } else {
            alert("At least one row must be present.");
        }
    });

    // ✅ Activate tooltips
    $('[data-toggle="tooltip"]').tooltip();

    // ✅ Validate before submit
    $("#buttonSubmit").click(function (e) {
        if (!$('#budgetItemFunction').valid()) {
            e.preventDefault();
        }
    });
});
</script> -->