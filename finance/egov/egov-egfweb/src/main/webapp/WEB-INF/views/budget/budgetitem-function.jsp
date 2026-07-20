<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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

</body>

</html>
