<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>

<html>

<head>
    <meta charset="UTF-8">
    <title>Functions</title>

    <style>
        body {
            font-family: Arial, sans-serif;
        }

        h1 {
            text-align: center;
        }

        table {
            border-collapse: collapse;
            width: 95%;
            margin: 0 auto 20px auto;
            table-layout: fixed;
        }

        th, td {
            border: 1px solid #ccc;
            padding: 8px;
            text-align: left;
            word-wrap: break-word;
        }

        th {
            background-color: #f2f2f2;
        }

        th:nth-child(1), td:nth-child(1) { width: 40%; }
        th:nth-child(2), td:nth-child(2) { width: 20%; }
        th:nth-child(3), td:nth-child(3) { width: 40%; }

        /* ---- Custom Button Styling ---- */
        .btn {
            padding: 6px 12px;
            border-radius: 4px;
            text-decoration: none;
            color: #fff;
            font-size: 14px;
            margin-right: 6px;
        }

        .btn-view {
            background-color: #3498db;
        }

        .btn-edit {
            background-color: #e67e22;
        }

        .btn-create {
            background-color: #2ecc71;
            padding: 8px 14px;
        }

        .btn-budget-back {
            background-color: #fe7a51 !important;
            border: 1px solid #fe7a51 !important;
            color: #ffffff !important;
            padding: 8px 22px !important;
            font-weight: 600 !important;
            font-size: 14px !important;
            border-radius: 4px !important;
            display: inline-block !important;
            text-decoration: none !important;
            cursor: pointer;
            transition: background-color 0.2s ease, border-color 0.2s ease;
        }

        .btn-budget-back:hover, .btn-budget-back:focus {
            background-color: #e5673e !important;
            border-color: #e5673e !important;
            color: #ffffff !important;
            text-decoration: none !important;
        }

        .top-create-btn {
            text-align: right;
            width: 95%;
            margin: 0 auto 20px auto;
        }
    </style>
</head>

<body>

    <h3>Function wise Budget Overview</h3>
    <br/>

    <h4>Budget Register: ${budgetRegister.budgetRegisterName} - ${budgetRegister.budgetRegisterNumber}</h4>

    <!-- CREATE BUTTON (Right Side) -->
    <div class="top-create-btn">
        <!--<a href="${pageContext.request.contextPath}/budget/new/${budgetRegister.id}" class="btn btn-primary btn-sm">
            + Create New
        </a>-->
    </div>

    <table>
        <tr>
            <th>Function Name</th>
            <th>Function Code</th>
            <th>Actions</th>
        </tr>

        <c:forEach var="item" items="${budgetFunction}">
            <tr>
                <td>${item.name}</td>
                <td>${item.code}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/budget/view/${item.id}/${budgetRegister.id}" class="btn btn-primary btn-sm">View</a>

                    <c:if test="${not empty allowCreate}">
                        <a href="${pageContext.request.contextPath}/budget/edit/${item.id}/${budgetRegister.id}" class="btn btn-secondary btn-sm">Edit</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>

    <div style="text-align: center; margin-top: 25px; margin-bottom: 25px;">
        <a href="${pageContext.request.contextPath}/budget/register/workflow/view/${budgetRegister.budgetRegisterNumber}" class="btn btn-budget-back">
            <i class="fa fa-arrow-left"></i> Back
        </a>
    </div>

</body>

</html>
