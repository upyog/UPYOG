<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<form:form role="form" action="../../update/${budgetRegister.id}" modelAttribute="budgetForm" id="budgetItemFunction"
    cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">

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
                                    <!-- Hidden fields -->
                                    <form:hidden path="opening.budgetGroup" id="opening.budgetGroup"
                                        name="opening.budgetGroup" value="Opening_Balance" />
                                    <form:hidden path="opening.function.id" id="opening.functionid"
                                        name="opening.functionid" value="${function.id}" />
                                    <form:hidden path="opening.id" />

                                    <td style="width: 40%;">
                                        Opening Balance as on
                                        <fmt:formatDate value="${currentFy.startingDate}" pattern="dd/MM/yyyy" />
                                    </td>

                                    <td style="width: 15%;">
                                        <form:input type="number" step="0.01" path="opening.currentEstimate"
                                            cssClass="form-control" maxlength="12" />
                                    </td>

                                    <td style="width: 15%;">
                                        <form:input type="number" step="0.01" path="opening.currentActual"
                                            cssClass="form-control" maxlength="12" />
                                    </td>

                                    <td style="width: 15%;">
                                        <form:input type="number" step="0.01" path="opening.currentRevisedEstimate"
                                            cssClass="form-control" maxlength="12" />
                                    </td>

                                    <td style="width: 15%;">
                                        <form:input type="number" step="0.01" path="opening.nextEstimate"
                                            cssClass="form-control" maxlength="12" />
                                    </td>
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
                                <c:forEach var="item" items="${all_budget_items}" varStatus="st">
                                    <tr id="budgetdetailsrow">
                                        <td style="width: 30%;">
                                            <div style="margin-bottom: 8px;">
                                                <input type="text" id="items[${st.index}].budgetcode"
                                                    name="items[${st.index}].budgetcode"
                                                    value="${item.budgetHead.code} - ${item.budgetHead.name}"
                                                    class="form-control table-input budgetHeadcode budgetcode"
                                                    placeholder="Type first 3 letters of Budget code"
                                                    data-errormsg="Budget Code is mandatory!" data-idx="0"
                                                    data-optional="0">
                                            </div>
                                            <c:choose>
                                                <c:when test="${item.scheme.id != null}">
                                                    <div class="scheme-container">
                                                        <!-- <input type="text" id="items[${st.index}].schemeCode"
                                                            name="items[${st.index}].schemeCode"
                                                            value="${item.scheme.code}"
                                                            class="scheme-input form-control table-input"
                                                            placeholder="Type Scheme code"> -->

                                                        <form:select path="items[${st.index}].scheme.id"
                                                            id="items[${st.index}].schemeId"
                                                            class="form-control scheme-input">

                                                            <form:option value="">
                                                                <spring:message code="lbl.select" />
                                                            </form:option>

                                                            <c:forEach items="${schemes}" var="scheme">
                                                                <option value="${scheme.id}"
                                                                    data-statecode="${scheme.stateCode}" 
                                                                    <c:if test="${scheme.id == item.scheme.id}">selected
                                                                    </c:if>>
                                                                    ${scheme.code} - ${scheme.name}
                                                                </option>
                                                            </c:forEach>
                                                        </form:select>
                                                    </div>
                                                </c:when>

                                                <c:otherwise>
                                                    <div class="scheme-container" style="display:none;">
                                                        <!-- <input type="text" id="items[${st.index}].schemeCode"
                                                            name="items[${st.index}].schemeCode"
                                                            class="scheme-input form-control table-input"
                                                            placeholder="Type Scheme code"> -->

                                                        <form:select path="items[${st.index}].scheme.id"
                                                            id="items[${st.index}].schemeId"
                                                            class="form-control scheme-input">

                                                            <form:option value="">
                                                                <spring:message code="lbl.select" />
                                                            </form:option>

                                                            <c:forEach items="${schemes}" var="scheme">
                                                                <option value="${scheme.id}"
                                                                    data-statecode="${scheme.stateCode}">
                                                                    ${scheme.code} - ${scheme.name}
                                                                </option>
                                                            </c:forEach>

                                                        </form:select>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <!-- <form:hidden path="items[${st.index}].scheme.id"
                                            name="items[${st.index}].scheme.id" value="${item.scheme.id}"
                                            id="items[${st.index}].scheme.id"
                                            class="form-control table-input hidden-input schemeId" /> -->

                                        <form:hidden path="" name="items[${st.index}].budgetheadcode"
                                            id="items[${st.index}].budgetheadcode" value="${item.budgetHead.code}"
                                            class="form-control table-input hidden-input budgetheadcode" />
                                        <form:hidden path="" name="items[${st.index}].budgetCode"
                                            id="items[${st.index}].genBudgetCode" value="${item.budgetCode}"
                                            class="form-control table-input hidden-input genBudgetCode" />
                                        <form:hidden path="" name="items[${st.index}].budgetGroup"
                                            id="items[${st.index}].budgetGroup" value="${item.budgetGroup}"
                                            class="form-control table-input hidden-input budgetGroup" />

                                        <form:hidden path="" name="items[${st.index}].stateCode"
                                            id="items[${st.index}].stateCode" value="${item.budgetHead.stateCode}"
                                            class="form-control table-input hidden-input stateCode" />

                                        <form:hidden path="" name="items[${st.index}].stateBudgetCode"
                                            id="items[${st.index}].stateBudgetCode" value="${item.stateBudgetCode}"
                                            class="form-control table-input hidden-input stateBudgetCode" />

                                        <form:hidden path="items[${st.index}].budgetHead.id"
                                            name="items[${st.index}].budgetHead.id" value="${item.budgetHead.id}"
                                            id="items[${st.index}].budgetHead.id"
                                            class="form-control table-input hidden-input budgetHeadId" />

                                        <form:hidden path="" name="items[${st.index}].budgetHeadId"
                                            id="items[${st.index}].budgetHeadId" value="${item.budgetHead.id}"
                                            class="form-control table-input hidden-input budgetHeadId" />

                                        <form:hidden path="" name="items[${st.index}].id" id="items[${st.index}].id"
                                            value="${item.id}" class="form-control table-input hidden-input id" />

                                        <td style="width: 15%;"><input type="number" step="0.01"
                                                name="items[${st.index}].currentEstimate"
                                                value="${item.currentEstimate}" class="form-control"
                                                data-errormsg="Current budget estimate is mandatory!" data-idx="0"
                                                data-optional="0" required="required"></td>
                                        <td style="width: 15%;"><input type="number" step="0.01"
                                                name="items[${st.index}].currentActual" value="${item.currentActual}"
                                                class="form-control" data-errormsg="Actuals is mandatory!" data-idx="0"
                                                data-optional="0" required="required"></td>
                                        <td style="width: 15%;"><input type="number" step="0.01"
                                                name="items[${st.index}].currentRevisedEstimate"
                                                value="${item.currentRevisedEstimate}" class="form-control"
                                                data-errormsg="Current revised estimate is mandatory!" data-idx="0"
                                                data-optional="0" required="required"></td>
                                        <td style="width: 15%;"><input type="number" step="0.01"
                                                name="items[${st.index}].nextEstimate" value="${item.nextEstimate}"
                                                class="form-control" data-errormsg="Next budget estimate is mandatory!"
                                                data-idx="0" data-optional="0" required="required"></td>

                                        <td class="text-center" style="width: 10%;">
                                            <span style="cursor:pointer;" onclick="addBudgetDetailsRow();" tabindex="0"
                                                id="items[${st.index}].addButton" data-toggle="tooltip" title=""
                                                data-original-title="press SPACE to Add!" aria-hidden="true"><i
                                                    class="fa fa-plus"></i></span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <!-- Submit Button -->
                        <div class="text-center mt-4">
                            <button type='submit' class='btn btn-primary' id="buttonSubmit">
                                <spring:message code='lbl.update' text="Update" />
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

<script>
    $(document).on('input', '.budgetcode', function () {
        let row = $(this).closest('tr');

        // Clear hidden scheme id
        row.find('.schemeId').val('');

        // Optional: clear scheme code text also
        row.find('.scheme-input').val('');

        // Optional: hide scheme container if you want
        // row.find('.scheme-container').hide();
    });
</script>