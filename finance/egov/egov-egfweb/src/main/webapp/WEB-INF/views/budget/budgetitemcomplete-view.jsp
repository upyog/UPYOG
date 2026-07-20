

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>

<style>
    /* Stronger selector and !important to override Bootstrap defaults */
    <!--thead.table-header th {
        background-color: #003366 !important;
        color: #fff !important;
        text-align: center;
        vertical-align: middle;
        font-weight: 700;
        font-size:medium;
    }

    .section-header {
        background-color: #d9edf7;
        font-weight: bold;
        text-align: center;
    }

    .sub-header {
        background-color: #fce4d6;
        font-weight: bold;
    }

    .category-header {
        background-color: #f2f2f2;
        font-weight: bold;
    }

    .total-row {
        background-color: #eeeeee;
        font-weight: bold;
    }-->

   body { font-family: Arial, Helvetica, sans-serif; margin: 12px; }

    table.budget-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 13px;
    }
    table.budget-table th, table.budget-table td {
        border: 1px solid #333;
        padding: 6px 8px;
    }

    /* header */
    table.budget-table thead tr {
        background: #12324a;
        color: #fff;
        font-weight: bold;
        text-align: center;
    }

    .section-row { background: #cfe9f0; font-weight: bold; }
    .part-row    { background: #f7e6d9; font-weight: bold; }
    .acct-row    { background: #e8f5ff; font-weight: bold; }
    .cat-row     { background: #f4f4f4; font-weight: bold; }

    .total-row {
        background: #e9e2ee;
        font-weight: bold;
        border-top: 2px solid #000;
    }

    .num     { text-align: right; }
    .indent1 { padding-left: 12px; }
    .indent2 { padding-left: 28px; }
    .muted   { color: #777; }

    th:nth-child(1), td:nth-child(1) { width: 32%; }
    th:nth-child(2), td:nth-child(2) { width: 10%; }
    th:nth-child(3), td:nth-child(3) { width: 10%; }
    th:nth-child(4), td:nth-child(4) { width: 12%; }
    th:nth-child(5), td:nth-child(5) { width: 12%; }
    th:nth-child(6), td:nth-child(6) { width: 12%; }
    th:nth-child(7), td:nth-child(7) { width: 12%; }

</style>


<div class="table-responsive">

    <h2 class="panel-title"
        style="font-weight: 700; font-size: 2rem; margin-bottom: 15px; position: relative; padding-left: 12px;">
        <span style="position: absolute; left: 0; top: 0; bottom: 0; width: 4px; background: #0d6efd; border-radius: 2px;"></span>
        Budget Register : ${budgetRegister.budgetRegisterName} - ${budgetRegister.budgetRegisterNumber}
        <!--<span class="text-muted" style="font-size: 1.3rem;">(${function.code})</span>-->
    </h2>

    <table class="budget-table" id="budgetTable" >
        <thead>
        <tr>
            <th style="text-align:left;">Budget Head</th>
            <th>Budget Code</th>
            <th>State Budget Code</th>
            <th>Budget Estimate ${currentFy.finYearRange}</th>
            <th>Actuals ${currentFy.finYearRange}<br/>(9 months)</th>
            <th>Revised Estimate ${currentFy.finYearRange}</th>
            <th>Budget Estimate ${nextFy.finYearRange}</th>
        </tr>
        </thead>

        <tbody>

        <!-- ========================== -->
        <!-- OPENING BALANCE -->
        <!-- ========================== -->
        <c:if test="${opening_balance != null}">
            <tr>
                <td>
                    Opening Balance as on
                    <fmt:formatDate value="${currentFy.startingDate}" pattern="dd/MM/yyyy"/>
                </td>
                <td></td>
                <td></td>
                <td class="num">
                    <fmt:formatNumber value="${opening_balance.currentEstimate}" maxFractionDigits="2"/>
                </td>
                <td class="num">
                    <fmt:formatNumber value="${opening_balance.currentActual}" maxFractionDigits="2"/>
                </td>
                <td class="num">
                    <fmt:formatNumber value="${opening_balance.currentRevisedEstimate}" maxFractionDigits="2"/>
                </td>
                <td class="num">
                    <fmt:formatNumber value="${opening_balance.nextEstimate}" maxFractionDigits="2"/>
                </td>
            </tr>
        </c:if>



        <!-- ========================== -->
        <!-- PART A - REVENUE BUDGET -->
        <!-- ========================== -->
        <tr class="part-row"><td colspan="7">Part A - REVENUE BUDGET</td></tr>

        <c:forEach var="acctEntry" items="${grouped_rb.entrySet()}">

            <tr class="acct-row"><td colspan="7">${acctEntry.key}</td></tr>

            <c:forEach var="catEntry" items="${acctEntry.value.entrySet()}">

                <tr class="cat-row">
                    <td colspan="7" class="indent1">${catEntry.key}</td>
                </tr>


                <c:forEach var="item" items="${catEntry.value}">
                    <tr>
                        <td class="indent2">
                            <c:choose>
                                <c:when test="${item.budgetHead != null}">${item.budgetHead.name}</c:when>
                                <c:otherwise><span class="muted">—</span></c:otherwise>
                            </c:choose>
                        </td>

                        <td>${item.budgetCode}</td>

                        <td>${item.stateBudgetCode}</td>

                        <td class="num">
                            <fmt:formatNumber value="${item.currentEstimate}" maxFractionDigits="2"/>
                        </td>

                        <td class="num">
                            <fmt:formatNumber value="${item.currentActual}" maxFractionDigits="2"/>
                        </td>

                        <td class="num">
                            <fmt:formatNumber value="${item.currentRevisedEstimate}" maxFractionDigits="2"/>
                        </td>

                        <td class="num">
                            <fmt:formatNumber value="${item.nextEstimate}" maxFractionDigits="2"/>
                        </td>
                    </tr>

                </c:forEach>


                <!-- CATEGORY TOTAL ROW (Injected here inside loop) -->
                <tr class="total-row">
                    <td class="indent2">Total</td>
                    <td></td>

                    <td></td>

                    <td class="num">
                        <fmt:formatNumber value="${rbTotals[catEntry.key].estimate}" maxFractionDigits="2"/>
                    </td>

                    <td class="num">
                        <fmt:formatNumber value="${rbTotals[catEntry.key].actual}" maxFractionDigits="2"/>
                    </td>

                    <td class="num">
                        <fmt:formatNumber value="${rbTotals[catEntry.key].revised}" maxFractionDigits="2"/>
                    </td>

                    <td class="num">
                        <fmt:formatNumber value="${rbTotals[catEntry.key].next}" maxFractionDigits="2"/>
                    </td>
                </tr>

            </c:forEach>
        </c:forEach>


        <!-- ========================== -->
        <!-- PART B - CAPITAL BUDGET -->
        <!-- ========================== -->
        <tr class="part-row"><td colspan="7">Part B - CAPITAL BUDGET</td></tr>

        <c:forEach var="acctEntry" items="${grouped_cb.entrySet()}">

            <tr class="acct-row"><td colspan="7">${acctEntry.key}</td></tr>

            <c:forEach var="catEntry" items="${acctEntry.value.entrySet()}">

                <tr class="cat-row">
                    <td colspan="7" class="indent1">${catEntry.key}</td>
                </tr>

                <c:forEach var="item" items="${catEntry.value}">
                    <tr>
                        <td class="indent2">
                            <c:choose>
                                <c:when test="${item.budgetHead != null}">${item.budgetHead.name}</c:when>
                                <c:otherwise><span class="muted">—</span></c:otherwise>
                            </c:choose>
                        </td>

                        <td>${item.budgetCode}</td>

                        <td>${item.stateBudgetCode}</td>

                        <td class="num"><fmt:formatNumber value="${item.currentEstimate}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.currentActual}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.currentRevisedEstimate}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.nextEstimate}" maxFractionDigits="2"/></td>
                    </tr>

                </c:forEach>

                <!-- total capital budgets -->
                <tr class="total-row">
                    <td class="indent2">Total</td>
                    <td></td>

                    <td></td>

                    <td class="num">
                        <fmt:formatNumber value="${cbTotals[catEntry.key].estimate}" maxFractionDigits="2"/>
                    </td>

                    <td class="num">
                        <fmt:formatNumber value="${cbTotals[catEntry.key].actual}" maxFractionDigits="2"/>
                    </td>

                    <td class="num">
                        <fmt:formatNumber value="${cbTotals[catEntry.key].revised}" maxFractionDigits="2"/>
                    </td>

                    <td class="num">
                        <fmt:formatNumber value="${cbTotals[catEntry.key].next}" maxFractionDigits="2"/>
                    </td>
                </tr>

            </c:forEach>
        </c:forEach>



        <!-- ========================== -->
        <!-- CLOSING BALANCE -->
        <!-- ========================== -->
        <c:if test="${closing_balance != null}">
            <tr>
                <td>
                    Closing Balance as on
                    <fmt:formatDate value="${currentFy.endingDate}" pattern="dd/MM/yyyy"/>
                </td>
                <td></td>
                <td></td>
                <td class="num">
                    <fmt:formatNumber value="${closing_balance.currentEstimate}" maxFractionDigits="2"/>
                </td>
                <td class="num">
                    <fmt:formatNumber value="${closing_balance.currentActual}" maxFractionDigits="2"/>
                </td>
                <td class="num">
                    <fmt:formatNumber value="${closing_balance.currentRevisedEstimate}" maxFractionDigits="2"/>
                </td>
                <td class="num">
                    <fmt:formatNumber value="${closing_balance.nextEstimate}" maxFractionDigits="2"/>
                </td>
            </tr>
        </c:if>

        </tbody>
    </table>

</div>

