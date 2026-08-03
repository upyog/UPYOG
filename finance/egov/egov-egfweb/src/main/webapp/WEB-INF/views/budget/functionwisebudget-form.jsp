<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<style>
    .acct-row {
        background-color: #2f4050;
        color: #ffffff;
        font-weight: 600;
        font-size: 15px;
    }

    .acct-row:hover,
    .acct-row:hover td {
        background-color: #2f4050 !important; /* same as normal */
    }

    .cat-row {
        background-color: #f5f7fa;
        font-weight: 600;
        color: #333;
        border-left: 4px solid #2f4050;
    }

    .indent2 {
        padding-left: 30px;
    }

    #budgetdetailsrow td {
        background-color: #ffffff;
        border-top: 1px solid #e0e0e0;
    }

    .table-text {
        font-weight: 500;
        color: #2c3e50;
        display: block;
        margin-bottom: 6px;
    }

    .scheme-input {
        margin-top: 6px;
    }


    table { border-collapse: collapse; width: 95%; margin-bottom: 20px;  table-layout: fixed; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: left; word-wrap: break-word; vertical-align: middle; }
    th { background-color: #f2f2f2; }

    th:nth-child(1), td:nth-child(1) { width: 32%; }
    th:nth-child(2), td:nth-child(2) { width: 8%; }
    th:nth-child(3), td:nth-child(3) { width: 15%; }
    th:nth-child(4), td:nth-child(4) { width: 15%; }
    th:nth-child(5), td:nth-child(5) { width: 15%; }
    th:nth-child(6), td:nth-child(6) { width: 15%; }

    .na-disabled {
        background-color: #f0f0f0;
        pointer-events: none;
    }

</style>



<form:form role="form" action="create" modelAttribute="budgetForm" id="budgetItemFunction"
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
                            <input type="hidden" name="budgetRegister.functionId"  id="budgetRegister.functionId" value="${function.id}" />
                            <form:hidden id="functionid" path="functionid" />
                            <form:hidden path="currentFinancialYear" id="currentFinancialYear" />
                            <form:hidden  id="financialYear" path="financialYear" />
                            <form:hidden path="" id="budgetRegisterId" name="budgetRegisterId"
                                         value="${budgetRegisterId}" />

                        </div>
                    </div>

                    <div class="panel-body">

                        <div class="table-responsive">

                        <table class="table table-bordered budget-table" id="dynamicTable">
                            <thead>
                            <tr>
                                <th>Budget Head</th>
                                <th>Not Applicable</th>
                                <th>BE <strong>${budgetRegister.currentFinancialYear.finYearRange}</strong></th>
                                <th>Actuals <strong>${budgetRegister.currentFinancialYear.finYearRange}</strong> (9 months)</th>
                                <th>RE <strong>${budgetRegister.currentFinancialYear.finYearRange}</strong></th>
                                <th>BE <strong>${budgetRegister.financialYear.finYearRange}</strong></th>
                            </tr>
                            </thead>
                            <tbody>

                            <tr>
                                <form:hidden path="opening.budgetGroup" id="opening.budgetGroup"
                                              />
                                <form:hidden path="opening.function.id" id="opening.functionid"
                                              />
                                <!--<form:hidden path="opening.id" />-->

                                <td colspan="2">
                                    Opening Balance as on
                                    <fmt:formatDate value="${budgetRegister.currentFinancialYear.startingDate}" pattern="dd/MM/yyyy" />
                                </td>

                                <td >
                                    <form:input type="number" step="0.01" path="opening.currentEstimate" cssClass="form-control"
                                                maxlength="12" cssErrorClass="form-control error" requiredtest="required"/>
                                    <form:errors path="opening.currentEstimate" cssClass="error-msg" />
                                </td>

                                <td >
                                    <form:input type="number" step="0.01" path="opening.currentActual" cssClass="form-control"
                                                maxlength="12" cssErrorClass="form-control error" requiredtest="required" />
                                    <form:errors path="opening.currentActual" cssClass="error-msg" />
                                </td>

                                <td >
                                    <form:input type="number" step="0.01" path="opening.currentRevisedEstimate"
                                                cssClass="form-control" maxlength="12" cssErrorClass="form-control error" requiredtest="required" />
                                    <form:errors path="opening.currentRevisedEstimate" cssClass="error-msg" />
                                </td>

                                <td >
                                    <form:input type="number" step="0.01" path="opening.nextEstimate" cssClass="form-control"
                                                maxlength="12" cssErrorClass="form-control error" requiredtest="required" />
                                    <form:errors path="opening.nextEstimate" cssClass="error-msg" />
                                </td>
                            </tr>

                            <c:forEach var="accountEntry" items="${groupedItems.entrySet()}">

                                <!-- Account Type Header -->
                                <tr class="acct-row">
                                    <td colspan="6">
                                        <strong>
                                            <c:out value="${accountEntry.key}" />
                                        </strong>
                                    </td>
                                </tr>

                                <c:forEach var="categoryEntry" items="${accountEntry.value.entrySet()}">

                                    <!-- Category Header -->
                                    <tr class="cat-row">
                                        <td colspan="6" style="padding-left:20px;">
                                            <strong>
                                                <c:out value="${categoryEntry.key}" />
                                            </strong>
                                        </td>
                                    </tr>

                                    <!-- Budget Items -->
                                    <c:forEach var="item" items="${categoryEntry.value}">
                                        <tr id="budgetdetailsrow">

                                            <td class="indent2">
                                                <span class="table-text">
                                                    ${item.budgetHead.code} - ${item.budgetHead.name}
                                                </span>

                                                <form:hidden path="items[${item.rowIndex}].budgetHead.id"/>
                                                <form:hidden path="items[${item.rowIndex}].rowIndex"/>
                                                <form:hidden path="items[${item.rowIndex}].budgetCode" /> <!-- normal budget code of budget item, eg: 91-RR-4027 -->
                                                <form:hidden path="items[${item.rowIndex}].stateBudgetCode" class="stateBudgetCode" /> <!-- state budget code to be generated, eg: 4217, 4217-60 -->
                                                <form:hidden path="items[${item.rowIndex}].budgetHead.stateCode" id="items[${item.rowIndex}].budgetHead.stateCode"
                                                             class="form-control table-input hidden-input stateCode" /> <!-- for future use to generate state budget code, check the js -->

                                                <c:if test="${item.budgetHead.schemeApplicable}">
                                                    <input type="text"
                                                           name="items[${item.rowIndex}].schemeCode"
                                                           value="${item.scheme.codeAndNameForShow}"
                                                           class="form-control scheme-input"
                                                           placeholder="Type Scheme code" />

                                                    <form:errors path="items[${item.rowIndex}].scheme.id" cssClass="error-msg" />

                                                    <form:hidden path="items[${item.rowIndex}].scheme.id" class="schemeId"  />





                                                </c:if>
                                            </td>


                                            <!--<form:hidden path="items[${item.rowIndex}].id"/>-->

                                            <td style="text-align:center;">
                                                <form:checkbox
                                                        path="items[${item.rowIndex}].notApplicable"
                                                        cssClass="na-checkbox"/>
                                            </td>

                                            <td>
                                                <form:input type="number" step="0.01"
                                                            path="items[${item.rowIndex}].currentEstimate"
                                                            class="form-control" cssErrorClass="form-control error" requiredtest="required"/>
                                                <form:errors path="items[${item.rowIndex}].currentEstimate" cssClass="error-msg" />
                                            </td>

                                            <td>
                                                <form:input type="number" step="0.01"
                                                            path="items[${item.rowIndex}].currentActual"
                                                            class="form-control" cssErrorClass="form-control error" requiredtest="required"/>
                                                <form:errors path="items[${item.rowIndex}].currentActual" cssClass="error-msg" />
                                            </td>

                                            <td>
                                                <form:input type="number" step="0.01"
                                                            path="items[${item.rowIndex}].currentRevisedEstimate"
                                                            class="form-control" cssErrorClass="form-control error" requiredtest="required"/>
                                                <form:errors path="items[${item.rowIndex}].currentRevisedEstimate" cssClass="error-msg" />
                                            </td>

                                            <td>
                                                <form:input type="number" step="0.01"
                                                            path="items[${item.rowIndex}].nextEstimate"
                                                            class="form-control" cssErrorClass="form-control error" requiredtest="required"/>
                                                <form:errors path="items[${item.rowIndex}].nextEstimate" cssClass="error-msg" />
                                            </td>

                                        </tr>
                                    </c:forEach>

                                </c:forEach>

                            </c:forEach>



                            </tbody>
                        </table>

                        </div>

                        <!-- Submit Button -->
                        <div class="text-center mt-4">
                            <button type='submit' class='btn btn-primary' id="buttonSubmit">
                                <spring:message code='lbl.submit' text="Submit" />
                            </button>
                        </div>

                        <div class="col-sm-9 add-margin mb-3">
                            <strong class="text-danger">
                                <i class="fa fa-star"></i>
                                &nbsp; BE: Budget Estimate, RE: Revised Estimate, BE: Budget Estimate.
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
<script src="<cdn:url value='/resources/app/js/budget/functionWiseBudgetInputHelper.js' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>">
</script>

