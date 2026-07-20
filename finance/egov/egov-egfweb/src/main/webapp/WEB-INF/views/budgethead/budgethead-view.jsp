<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <meta charset="UTF-8">
    <title>Budget Heads</title>
    <style>
        body { font-family: Arial, sans-serif; }
        h2 { color: #2c3e50; margin-top: 30px; }
        table { border-collapse: collapse; width: 95%; margin-bottom: 20px;  table-layout: fixed; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; word-wrap: break-word; }
        th { background-color: #f2f2f2; }

        /* ✅ Fixed column widths */
        th:nth-child(1), td:nth-child(1) { width: 30%; }  /* Name */
        th:nth-child(2), td:nth-child(2) { width: 24%; }  /* Category */
        th:nth-child(3), td:nth-child(3) { width: 18%; }  /* Code */
        th:nth-child(4), td:nth-child(4) { width: 15%; }  /* state code */
        th:nth-child(5), td:nth-child(5) { width: 13%; }  /* program applicable */
    </style>
</head>
<body>

<h1>Budget Heads Overview</h1>

<h2>Revenue Receipts (RR)</h2>
<table>
    <tr>
        <th>Budget Item</th>
        <th>Budget Head</th>
        <th>Code</th>
        <th>State Code</th>
        <th>Program / Scheme applicable</th>
    </tr>
    <c:forEach var="head" items="${rr}">
        <tr>
            <td>${head.name}</td>
            <td>${head.category}</td>
            <td>${head.code}</td>
            <td>${head.stateCode}</td>
            <td>${head.program}</td>
        </tr>
    </c:forEach>
</table>

<h2>Revenue Expenditure (RE)</h2>
<table>
    <tr>
        <th>Budget Item</th>
        <th>Budget Head</th>
        <th>Code</th>
        <th>State Code</th>
        <th>Program / Scheme applicable</th>
    </tr>
    <c:forEach var="head" items="${re}">
        <tr>
            <td>${head.name}</td>
            <td>${head.category}</td>
            <td>${head.code}</td>
            <td>${head.stateCode}</td>
            <td>${head.program}</td>
        </tr>
    </c:forEach>
</table>

<h2>Capital Receipts (CR)</h2>
<table>
    <tr>
        <th>Budget Item</th>
        <th>Budget Head</th>
        <th>Code</th>
        <th>State Code</th>
        <th>Program / Scheme applicable</th>
    </tr>
    <c:forEach var="head" items="${cr}">
        <tr>
            <td>${head.name}</td>
            <td>${head.category}</td>
            <td>${head.code}</td>
            <td>${head.stateCode}</td>
            <td>${head.program}</td>
        </tr>
    </c:forEach>
</table>

<h2>Capital Expenditure (CE)</h2>
<table>
    <tr>
        <th>Budget Item</th>
        <th>Budget Head</th>
        <th>Code</th>
        <th>State Code</th>
        <th>Program / Scheme applicable</th>
    </tr>
    <c:forEach var="head" items="${ce}">
        <tr>
            <td>${head.name}</td>
            <td>${head.category}</td>
            <td>${head.code}</td>
            <td>${head.stateCode}</td>
            <td>${head.program}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
