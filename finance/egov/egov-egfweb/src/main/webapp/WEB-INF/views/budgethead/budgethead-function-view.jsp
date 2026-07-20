<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>

<head>
    <meta charset="UTF-8">
    <title>Function wise Budget Head</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        h2 {
            color: #2c3e50;
            margin-top: 30px;
        }

        table {
            border-collapse: collapse;
            width: 95%;
            margin-bottom: 20px;
            table-layout: fixed;
        }

        th,
        td {
            border: 1px solid #ccc;
            padding: 8px;
            text-align: left;
            word-wrap: break-word;
        }

        th {
            background-color: #f2f2f2;
        }

        /* ✅ Fixed column widths */
        th:nth-child(1),
        td:nth-child(1) {
            width: 15%;
        }

        /* Name */
        th:nth-child(2),
        td:nth-child(2) {
            width: 35%;
        }

        /* Code */
        th:nth-child(3),
        td:nth-child(3) {
            width: 15%;
        }

        /* Code */
        th:nth-child(4),
        td:nth-child(4) {
            width: 35%;
        }

        /* function code */
    </style>
</head>

<body>

    <h1>Function wise Budget Head</h1>

    <table>
        <thead>
            <tr>
                <th>Function Code</th>
                <th>Function Name</th>
                <th>Budget Head Code</th>
                <th>Budget Head Name</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="functionBudget" items="${functionBudgetHead}">
                <tr>
                    <td>${functionBudget.function.code}</td>
                    <td>${functionBudget.function.name}</td>
                    <td>${functionBudget.budgetHead.code}</td>
                    <td>${functionBudget.budgetHead.name}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>


</body>

</html>