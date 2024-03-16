<!-- 
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  -->

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>



<head>
    <title>Budget Details</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>

<div class="panel-heading">
    <div class="panel-title">
        Budget Details
    </div>
</div>
<div class="panel-body">
    <label for="glCodeInput">Enter GlCode:</label>
    <input type="text" id="glCodeInput">
    <button type="button" onclick="fetchBudgetDetails()">Fetch Details</button>

    <table class="table table-bordered">
        <thead>
            <tr>
                <th>Approved Amount</th>
                <th>Available Amount</th>
                <th>Expense Amount</th>
            </tr>
        </thead>
        <tbody id="budgetDetailsBody">
            <!-- Budget details will be dynamically added here -->
        </tbody>
    </table>
</div>

<script>
    function fetchBudgetDetails() {
        var glCode = $('#glCodeInput').val();
        $.ajax({
            url: '/services/EGF/supplierbill/code',
            type: 'GET',
            contentType: 'application/json',
            data: { glCode: glCode },
            success: function(data) {
                console.log(data);
                displayBudgetDetails(data);
            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }

    function displayBudgetDetails(data) {
       var lines = data.split('\n');
    var budgetDetails = {};
    var uniqueDebitAmounts = new Set();

    for (var i = 0; i < lines.length; i++) {
        var parts = lines[i].split(':');
        if (parts.length >= 2) {
            var key = parts[0].trim();
            var value = parts.slice(1).join(':').trim();
            budgetDetails[key] = value;
            if (key === 'debitamount') {
                uniqueDebitAmounts.add(parseFloat(value));
            }
        } else {
            console.error("Malformed line:", lines[i]);
        }
    }

    var totalDebitAmount = 0;
    uniqueDebitAmounts.forEach(amount => totalDebitAmount += amount);

    var approvedAmount = parseFloat(budgetDetails['approvedamount']);
    var budgetAvailable = approvedAmount - totalDebitAmount;

    $('#budgetDetailsBody').empty();

    var row = $('<tr>');
    row.append($('<td>').text(approvedAmount));
    row.append($('<td>').text(budgetAvailable.toFixed(2)));
    row.append($('<td>').html('<a href="#" id="expenseAmountLink">' + totalDebitAmount.toFixed(2) + '</a>'));
    $('#budgetDetailsBody').append(row);

    // Open popup when the Expense Amount link is clicked
    $('#expenseAmountLink').click(function(event) {
        event.preventDefault();
        openPopup(budgetDetails);
    });
}

function openPopup(budgetDetails) {
    // Customize your popup window here
    var popupWindow = window.open('', '_blank', 'width=900,height=600');
    popupWindow.document.write("<h2 style='text-align: center;'>Budget Expense Details</h2>");
    popupWindow.document.write("<table border='1'>");
    popupWindow.document.write('<table border="1" style="margin:auto; text-align: center; width: 80%; border-collapse: collapse;">');
// First row for column names

popupWindow.document.write('<tr>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Bill Resister ID</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Bill Number</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Account Head</td>');

popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Bill Date</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Bill ID</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Debit Amount</td>');

popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Bill Type</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Expenditure Type</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Bill Details ID</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Financial Year</td>');

popupWindow.document.write('</tr>');


// Second row for values
popupWindow.document.write('<tr>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['Bill Resister ID'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['Bill Number'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['accounthead'] || '') + '</td>');

popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['billdate'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['billid'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['debitamount'] || '') + '</td>');

popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['billtype'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['expendituretype'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['egbilldetailsId'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['financialyear'] || '') + '</td>');


popupWindow.document.write('</tr>');

popupWindow.document.write('</table>');





popupWindow.document.write("<h2 style='text-align: center;'>Voucher Details</h2>");
    popupWindow.document.write("<table border='1'>");
    popupWindow.document.write('<table border="1" style="margin:auto; text-align: center; width: 80%; border-collapse: collapse;">');
// First row for column names

popupWindow.document.write('<tr>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Vourcher Name</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Vourcher Type</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Vourcher Number</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Vourcher Date</td>');
popupWindow.document.write('<td style="padding: 10px; background-color: #139B9C; color: white;">Cgvn No</td>');

popupWindow.document.write('</tr>');


// Second row for values
popupWindow.document.write('<tr>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['name'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['type'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['vouchernumber'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['voucherdate'] || '') + '</td>');
popupWindow.document.write('<td style="padding: 10px;">' + (budgetDetails['cgvn'] || '') + '</td>');

popupWindow.document.write('</tr>');

popupWindow.document.write('</table>');




    //popupWindow.document.write("<table>");
    //for (var key in budgetDetails) {
       // popupWindow.document.write("<tr>");
        //popupWindow.document.write("<td>" + key + "</td>");
        //popupWindow.document.write("<td>" + budgetDetails[key] + "</td>");
        //popupWindow.document.write("</tr>");
    //}
    
    //popupWindow.document.write("</table>");
    popupWindow.document.close();
}
</script>

</body>

