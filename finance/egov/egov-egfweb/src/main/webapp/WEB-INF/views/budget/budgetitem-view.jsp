

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

    </style>


    <div class="table-responsive">

        <h2 class="panel-title"
            style="font-weight: 700; font-size: 2rem; margin-bottom: 15px; position: relative; padding-left: 12px;">
            <span style="position: absolute; left: 0; top: 0; bottom: 0; width: 4px; background: #0d6efd; border-radius: 2px;"></span>
            Function: ${function.code} - ${function.name}
            <!--<span class="text-muted" style="font-size: 1.3rem;">(${function.code})</span>-->
        </h2>

        <table class="budget-table" id="budgetTable" >
            <thead>
            <tr>
                <th style="text-align:left;">Budget Head</th>
                <th>Budget Code</th>
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
            <c:if test="${not empty opening_balance}">
                <!--<tr class="section-row"><td colspan="6">Opening Balance</td></tr>-->

                <c:forEach var="item" items="${opening_balance}">
                    <tr>
                        <td>
                            <!--<c:choose>
                                <c:when test="${item.budgetHead != null}">
                                    ${item.budgetHead.name}
                                </c:when>
                                <c:otherwise><span class="muted">—</span></c:otherwise>
                            </c:choose>-->
                            Opening Balance as on <fmt:formatDate value="${currentFy.startingDate}" pattern = "dd/MM/yyyy" />
                        </td>

                        <td>${item.budgetCode}</td>
                        <td class="num"><fmt:formatNumber value="${item.currentEstimate}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.currentActual}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.currentRevisedEstimate}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.nextEstimate}" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
            </c:if>

            <!-- ========================== -->
            <!-- CLOSING BALANCE -->
            <!-- ========================== -->
            <!--<c:if test="${not empty closing_balance}">
                &lt;!&ndash;<tr class="section-row"><td colspan="6">Closing Balance</td></tr>&ndash;&gt;

                <c:forEach var="item" items="${closing_balance}">
                    <tr>
                        <td>
                            &lt;!&ndash;<c:choose>
                                <c:when test="${item.budgetHead != null}">
                                    ${item.budgetHead.name}
                                </c:when>
                                <c:otherwise><span class="muted">—</span></c:otherwise>
                            </c:choose>&ndash;&gt;
                            Closing Balance
                        </td>

                        <td>${item.budgetCode}</td>
                        <td class="num"><fmt:formatNumber value="${item.currentEstimate}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.currentActual}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.currentRevisedEstimate}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.nextEstimate}" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
            </c:if>-->

            <!-- ========================== -->
            <!-- PART A - REVENUE BUDGET -->
            <!-- ========================== -->
            <tr class="part-row"><td colspan="6">Part A - REVENUE BUDGET</td></tr>

            <c:forEach var="acctEntry" items="${grouped_rb.entrySet()}">

                <tr class="acct-row"><td colspan="6">${acctEntry.key}</td></tr>

                <c:forEach var="catEntry" items="${acctEntry.value.entrySet()}">

                    <tr class="cat-row">
                        <td colspan="6" class="indent1">${catEntry.key}</td>
                    </tr>

                    <%-- RESET TOTALS FOR THIS CATEGORY --%>
                    <%
                    java.math.BigDecimal totE = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal totA = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal totR = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal totN = java.math.BigDecimal.ZERO;
                    %>

                    <c:forEach var="item" items="${catEntry.value}">
                        <tr>
                            <td class="indent2">
                                <c:choose>
                                    <c:when test="${item.budgetHead != null}">${item.budgetHead.name}</c:when>
                                    <c:otherwise><span class="muted">—</span></c:otherwise>
                                </c:choose>
                            </td>

                            <td>${item.budgetCode}</td>

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

                        <%-- ACCUMULATE TOTALS --%>
                        <%
                        java.math.BigDecimal e = (java.math.BigDecimal) pageContext.findAttribute("item.currentEstimate");
                        java.math.BigDecimal a = (java.math.BigDecimal) pageContext.findAttribute("item.currentActual");
                        java.math.BigDecimal r = (java.math.BigDecimal) pageContext.findAttribute("item.currentRevisedEstimate");
                        java.math.BigDecimal n = (java.math.BigDecimal) pageContext.findAttribute("item.nextEstimate");

                        if (e != null) totE = totE.add(e);
                        if (a != null) totA = totA.add(a);
                        if (r != null) totR = totR.add(r);
                        if (n != null) totN = totN.add(n);
                        %>
                    </c:forEach>

                    <!-- TOTAL ROW FOR THIS CATEGORY -->
                    <!--<tr class="total-row">
                        <td class="indent2">Total</td>
                        <td></td>
                        <td class="num"><%= totE %></td>
                        <td class="num"><%= totA %></td>
                        <td class="num"><%= totR %></td>
                        <td class="num"><%= totN %></td>
                    </tr>-->

                    <!-- CATEGORY TOTAL ROW (Injected here inside loop) -->
                    <tr class="total-row">
                        <td class="indent2">Total</td>
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
            <tr class="part-row"><td colspan="6">Part B - CAPITAL BUDGET</td></tr>

            <c:forEach var="acctEntry" items="${grouped_cb.entrySet()}">

                <tr class="acct-row"><td colspan="6">${acctEntry.key}</td></tr>

                <c:forEach var="catEntry" items="${acctEntry.value.entrySet()}">

                    <tr class="cat-row">
                        <td colspan="6" class="indent1">${catEntry.key}</td>
                    </tr>

                    <%
                    java.math.BigDecimal totCE = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal totCA = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal totCR = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal totCN = java.math.BigDecimal.ZERO;
                    %>

                    <c:forEach var="item" items="${catEntry.value}">
                        <tr>
                            <td class="indent2">
                                <c:choose>
                                    <c:when test="${item.budgetHead != null}">${item.budgetHead.name}</c:when>
                                    <c:otherwise><span class="muted">—</span></c:otherwise>
                                </c:choose>
                            </td>

                            <td>${item.budgetCode}</td>

                            <td class="num"><fmt:formatNumber value="${item.currentEstimate}" maxFractionDigits="2"/></td>
                            <td class="num"><fmt:formatNumber value="${item.currentActual}" maxFractionDigits="2"/></td>
                            <td class="num"><fmt:formatNumber value="${item.currentRevisedEstimate}" maxFractionDigits="2"/></td>
                            <td class="num"><fmt:formatNumber value="${item.nextEstimate}" maxFractionDigits="2"/></td>
                        </tr>

                        <%
                        java.math.BigDecimal e2 = (java.math.BigDecimal) pageContext.findAttribute("item.currentEstimate");
                        java.math.BigDecimal a2 = (java.math.BigDecimal) pageContext.findAttribute("item.currentActual");
                        java.math.BigDecimal r2 = (java.math.BigDecimal) pageContext.findAttribute("item.currentRevisedEstimate");
                        java.math.BigDecimal n2 = (java.math.BigDecimal) pageContext.findAttribute("item.nextEstimate");

                        if (e2 != null) totCE = totCE.add(e2);
                        if (a2 != null) totCA = totCA.add(a2);
                        if (r2 != null) totCR = totCR.add(r2);
                        if (n2 != null) totCN = totCN.add(n2);
                        %>
                    </c:forEach>

                    <!--<tr class="total-row">
                        <td class="indent2">Total</td>
                        <td></td>
                        <td class="num"><%= totCE %></td>
                        <td class="num"><%= totCA %></td>
                        <td class="num"><%= totCR %></td>
                        <td class="num"><%= totCN %></td>
                    </tr>-->

                    <!-- total capital budgets -->
                    <tr class="total-row">
                        <td class="indent2">Total</td>
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
            <c:if test="${not empty closing_balance}">
                <!--<tr class="section-row"><td colspan="6">Closing Balance</td></tr>-->

                <c:forEach var="item" items="${closing_balance}">
                    <tr>
                        <td>
                            <!--<c:choose>
                                <c:when test="${item.budgetHead != null}">
                                    ${item.budgetHead.name}
                                </c:when>
                                <c:otherwise><span class="muted">—</span></c:otherwise>
                            </c:choose>-->
                            Closing Balance as on <fmt:formatDate value="${currentFy.endingDate}" pattern = "dd/MM/yyyy" />
                        </td>

                        <td>${item.budgetCode}</td>
                        <td class="num"><fmt:formatNumber value="${item.currentEstimate}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.currentActual}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.currentRevisedEstimate}" maxFractionDigits="2"/></td>
                        <td class="num"><fmt:formatNumber value="${item.nextEstimate}" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
            </c:if>

            </tbody>
        </table>


        <!--<div style="margin-top: 16px;">
            <button onclick="window.print()"
                    style="padding:6px 12px; background:#2c3e50; color:white; border:0; border-radius:4px;">
                Print
            </button>

            <a href="/services/EGF/budget/pdf"
               style="padding:6px 12px; background:#d35400; color:white; border-radius:4px; margin-left:5px; text-decoration:none;">
                Download PDF
            </a>

            <a href="/services/EGF/budget/excel"
               style="padding:6px 12px; background:#27ae60; color:white; border-radius:4px; margin-left:5px; text-decoration:none;">
                Download Excel
            </a>
        </div>-->



        <!--<c:if test="${not empty nestedGroup}">
            &lt;!&ndash; iterate over type -> (accountType -> (category -> list)) &ndash;&gt;
            <c:forEach var="typeEntry" items="${nestedGroup.entrySet()}">

                <c:set var="typeKey" value="${typeEntry.key}" />
                <c:if test="${not empty typeKey}">
                    <h2 style="margin-top:24px;">Type: ${typeKey}</h2>

                    <c:set var="accountMap" value="${typeEntry.value}" />
                    <c:if test="${not empty accountMap}">

                        <c:forEach var="acctEntry" items="${accountMap.entrySet()}">
                            <c:set var="acctType" value="${acctEntry.key}" />
                            <h3 style="margin-left:8px;">Account Type: ${acctType}</h3>

                            <c:set var="categoryMap" value="${acctEntry.value}" />
                            <c:if test="${not empty categoryMap}">

                                <c:forEach var="catEntry" items="${categoryMap.entrySet()}">
                                    <c:set var="category" value="${catEntry.key}" />
                                    <h4 style="margin-left:16px;">Category: ${category}</h4>

                                    <c:set var="items" value="${catEntry.value}" />
                                    <c:if test="${not empty items}">
                                        <table border="1" cellpadding="6" cellspacing="0" style="margin-left:20px; width:95%; border-collapse:collapse;">
                                            <thead>
                                            <tr>
                                                <th>Budget Code</th>
                                                <th>Budget Head</th>
                                                <th>Current Estimate</th>
                                                <th>Current Actual</th>
                                                <th>Revised Estimate</th>
                                                <th>Next Estimate</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="item" items="${items}">
                                                <c:if test="${not empty item}">
                                                    <tr>
                                                        <td>${item.budgetCode}</td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty item.budgetHead}">
                                                                    ${item.budgetHead.name}
                                                                </c:when>
                                                                <c:otherwise>—</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty item.currentEstimate}">
                                                                    <fmt:formatNumber value="${item.currentEstimate}" type="number" />
                                                                </c:when>
                                                                <c:otherwise>0</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty item.currentActual}">
                                                                    <fmt:formatNumber value="${item.currentActual}" type="number" />
                                                                </c:when>
                                                                <c:otherwise>0</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty item.currentRevisedEstimate}">
                                                                    <fmt:formatNumber value="${item.currentRevisedEstimate}" type="number" />
                                                                </c:when>
                                                                <c:otherwise>0</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty item.nextEstimate}">
                                                                    <fmt:formatNumber value="${item.nextEstimate}" type="number" />
                                                                </c:when>
                                                                <c:otherwise>0</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:if>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                        <br/>
                                    </c:if>
                                </c:forEach>

                            </c:if>
                        </c:forEach>

                    </c:if>
                </c:if>

            </c:forEach>
        </c:if>

        <c:if test="${empty nestedGroup}">
            <p>No data available.</p>
        </c:if>-->







                                <!--<table class="table table-bordered table-striped align-middle">
                                    <thead class="table-header">
                                        <tr>
                                            <th>Budget Head</th>
                                            <th>Budget Code</th>
                                            <th>Budget Estimate 2025-26</th>
                                            <th>Actuals 2025-26 (9 months)</th>
                                            <th>Revised Estimate 2025-26</th>
                                            <th>Budget Estimate 2026-27</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr class="section-header">
                                            <td colspan="6">3 - Finance, Accounts and Audits</td>
                                        </tr>
                                        <tr class="sub-header">
                                            <td colspan="6">Part A - REVENUE BUDGET</td>
                                        </tr>

                                        <tr>
                                            <td colspan="2"><strong>Opening balance as on 01.04.2025</strong></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>

                                        <tr class="category-header">
                                            <td colspan="6">Revenue Receipts</td>
                                        </tr>

                                        <tr>
                                            <td colspan="6"><strong>Rental Income from Municipal Properties</strong></td>
                                        </tr>
                                        <tr>
                                            <td>Rent from vehicles</td>
                                            <td>03-RR-040</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>Sale of scraps</td>
                                            <td>03-RR-041</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>

                                        <tr>
                                            <td colspan="6"><strong>Fees &amp; User Charges</strong></td>
                                        </tr>
                                        <tr>
                                            <td>Certificate fees</td>
                                            <td>03-RR-023</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>

                                        <tr class="category-header">
                                            <td colspan="6">Revenue Expenditure</td>
                                        </tr>

                                        <tr>
                                            <td colspan="6"><strong>Establishment Expenses</strong></td>
                                        </tr>
                                        <tr>
                                            <td>Salaries &amp; Allowances</td>
                                            <td>03-RE-001</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>Benefits &amp; Allowances</td>
                                            <td>03-RE-002</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>Pension</td>
                                            <td>03-RE-003</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr class="total-row">
                                            <td>Total</td>
                                            <td colspan="5"></td>
                                        </tr>

                                        <tr>
                                            <td colspan="6"><strong>Termination &amp; Retirement Benefits</strong></td>
                                        </tr>
                                        <tr>
                                            <td>Leave Encashment</td>
                                            <td>03-RE-004</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>Gratuity and retirement benefits</td>
                                            <td>03-RE-005</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr class="total-row">
                                            <td>Total</td>
                                            <td colspan="5"></td>
                                        </tr>

                                        <tr>
                                            <td colspan="6"><strong>Administrative Expenses</strong></td>
                                        </tr>
                                        <tr>
                                            <td>Office contingencies</td>
                                            <td>03-RE-006</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>Printing and stationery</td>
                                            <td>03-RE-007</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>Professional fees</td>
                                            <td>03-RE-008</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>Electricity charges</td>
                                            <td>03-RE-009</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                    </tbody>
                                </table>-->
    </div>

