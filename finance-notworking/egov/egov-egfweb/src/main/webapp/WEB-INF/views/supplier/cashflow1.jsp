<%--
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
  --%>


<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>


<head>
   <!--  <meta charset="UTF-8"> -->
    <title >Cash Flow Statement</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <style>
          body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #FFFFFF;
        }
		
		h2 {
			color:#ff9933;
        }
		
		h3{
			color:#ff9933;
		
		}
        .container {
            max-width: 800px;
            margin: 20px auto;
            background-color: #FFFFFF;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #ff9933;
        }
		
		
		
    </style>
</head>
    
<body>

 <button id="getDataButton">Get Data</button>



    <div class="container">
        <h2 style="text-align:center">Cash-Flow-Statement</h2>
        <h2 style="text-align: center; ">Nagar Nigam Dehradun</h2>
        <!-- <h3 style="text-align: center;  ">As on period end</h3> -->

        <table>
            <tr>
                <th>Particulars</th>
                <th>Amount (CurrentYear)</th>
                <th>Amount (PreviousYear)</th>
            </tr>
            
			
			<tr style="color: #ff9933;">
           <td colspan="3"><b>(A) Cash Flows from Operating Activities</b></td>
             </tr>

			
            <tr>
                <td><b>Cash Receipt From</b></td>
            </tr>
            
			<tr>
                <td>Taxation</td>
				
		 <td>
        <input type="number" name="taxationCurrentYear"  value="" style="border: none; background-color: transparent; width: 100%; text-align: right;">

    </td> 
			 <td>
        <input type="number" name="taxationPreviousYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			
				<!-- <!-- <td input type="number" name="taxationCurrentYear" value="00"></td>  --> 
                <!-- <td input type="number" name="taxationPreviousYear" value="00"></td>  -->
				
            </tr>
			
            <tr>
                <td>Sales of Goods and Services</td>
                 <td>
        <input type="number" name="salesofgoodsandservicesCurrentYear" value="" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="salesofgoodsandservicesPreviousYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Grants related to Revenue/General Grants</td>
               <td>
        <input type="number" name="grantsrelatedtoRevenue/generalgrantscurrentyear" value="" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="grantsrelatedtoRevenue/generalgrantspreviousyear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Interest Received </td>
       <td>
    <input type="number" name="InterestReceivedcurrentyear" value="" style="border: none; background-color: transparent; width: 100%;text-align: right;">
          </td>

			 <td>
        <input type="number" name="interestreceivedpreviouspreviousyear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Other Receipts</td>
                  <td>
        <input type="number" name="otherreceiptcurrentyear" value="1000.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="otherreceiptpreviousyear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td><b>Less: Cash Payments for:</b></td>
                  <td>
     <!--    <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            --> 
            </tr>
            <tr>
                <td>Employee Costs</td>
                <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
	    <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Superannuation</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Suppliers</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="1200.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Interest Paid (Expense)</td>
  <td>
        <input type="number" name="salesofgoodsandservices" value="1468.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Other Payments</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="10000.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr  style="color: #ff9933;">
                <td><b>Net cash generated from/ (used in) operating activities (a)</b></td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="103816.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
        
		
		
		
		
		<!-- Cash Flows from Investing Activities -->
            <tr style="color: #ff9933;">
                <td colspan="3"><b>(B) Cash Flows from Investing Activities</b></td>
            </tr>
            <tr>
                <td>(Purchase) of fixed assets and CWIP</td>
                <td>
        <input type="number" name="salesofgoodsandservices" value="1000.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>(Increase) / Decrease in Special funds/grants</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="1231.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>(Increase) / Decrease in Earmarked funds</td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>(Purchase) of Investments</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Add:</td>
             <!--   <td>
        <input type="number" name="salesofgoodsandservices" value="" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td>  -->
            </tr>
            <tr>
                <td>Proceeds from disposal of assets</td>
               <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Proceeds from disposal of investments</td>
               <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Investment income received</td>
              <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Interest income received</td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr  style="color: #ff9933;">
                <td><b>Net cash generated from/ (used in) investing activities (b)</b></td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="2231" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
			
            <!-- Add Cash Flows from Financing Activities section -->
        
		
		
		
		<tr  style="color: #ff9933;">
                <td colspan="3"><b>('C) Cash Flows from Financing Activities</b></td>
            </tr>
            <tr>
                <td><b>Add:</b></td>
                
            </tr>
            <tr>
                <td>Loans from banks/others received</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td><b>Less: </b></td>
				
                
            <tr>
                <td>Loans repaid during the period</td>
                <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            <tr>
                <td>Loans and advances to employees</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Loans to others</td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Finance expenses</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="1000.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr style="color: #ff9933;">
                <td><b>Net cash generated from (used in) financing activities (c)</b></td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="1000" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr style="color: #ff9933;">
                <td><b>Net increase/ (decrease) in cash and cash equivalents (a + b + c)</b></td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="107047" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
        
		
		
		 <!-- Cash and cash equivalents -->
            <tr style="color: #ff9933;">
                <!-- <td colspan="3"><b>Cash and cash equivalents at beginning of period</b></td> -->
				<td><b>Cash and cash equivalents at beginning of period</b></td>
								
				
				<td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
				
            </tr>
            <tr>
                <td><b>Cash Balances</b></td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td><b>Bank Balances</b></td>
                <td>
        <input type="number" name="salesofgoodsandservices" value="" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Scheduled co-operative banks</td>
               <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td> Balances with Post offices</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Balances with other banks</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr style="color: #ff9933;">
                <td><b>Cash and cash equivalents at end of period</b></td>
                
				  <td>
        <input type="number" name="salesofgoodsandservices" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value="00.00" style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
				
            </tr>
        </table>
		        
    </div>
    <script>
    
//1.for the first taxisation values    
$(document).ready(function() {
    $("#getDataButton").click(function() {
        // Make AJAX request to fetch data between GL codes
        $.ajax({
            url: '/services/EGF/supplier/generalLedgerDataBetweenGLCodes',
            type: 'GET',
            contentType: 'application/json',
            success: function(response)  {
                console.log("Response data:", response);
              
            },
            error: function(xhr, status, error) {
                console.error("Error fetching data:", error);
                console.error("Status:", status);
                console.error("Response:", xhr.responseText);
            }
        });
    });
});


$(document).ready(function() {
    $("#getDataButton").click(function() {
        // Make AJAX request to fetch data between GL codes
        $.ajax({
            url: '/services/EGF/supplier/generalLedgerDataBetweenGLCodes',
            type: 'GET',
            contentType: 'application/json',
            success: function(response) {
                console.log("Response data:", response);
                
                // Extract the value from the response
                var taxationValue = response;        // Adjust this based on your response structure
                
                // Add two decimal places
                taxationValue = parseFloat(taxationValue).toFixed(2);
                
                // Set the value in the taxationCurrentYear input field
                $("input[name='taxationCurrentYear']").val(taxationValue);
            },
            error: function(xhr, status, error) {
                console.error("Error fetching data:", error);
            }
        });
    });
});




 //2.for for the salesand good services

$(document).ready(function() {
    $("#getDataButton").on("click", function() {
        // Make AJAX request to fetch data between GL codes
        $.ajax({
            url: '/services/EGF/supplier/generalLedgerDataBetweenGLCode', // Update URL
            type: 'GET',
            contentType: 'application/json',
            success: function(response) {
                console.log("Response data:", response);
            }, // <-- Corrected the comma to a semicolon
            error: function(xhr, status, error) {
                console.error("Error fetching data:", error);
                console.error("Status:", status);
                console.error("Response:", xhr.responseText);
            }
        });
    });
});

$(document).ready(function() {
    $("#getDataButton").on("click", function() {
        // Make AJAX request to fetch data between GL codes
        $.ajax({
            url: '/services/EGF/supplier/generalLedgerDataBetweenGLCode', // Update URL
            type: 'GET',
            contentType: 'application/json',
            success: function(response) {
                // Assuming response contains the value you want to display
                var salesValue = response;
            
              // Add two decimal places
                salesValue = parseFloat(salesValue).toFixed(2);
                

                // Set the value of the input field
                $("input[name='salesofgoodsandservicesCurrentYear']").val(salesValue);
            },
            error: function(xhr, status, error) {
                console.error("Error fetching data:", error);
                console.error("Status:", status);
                console.error("Response:", xhr.responseText);
            }
        });
    });
});


// for the 3 rd grantsrelatedtoRevenuecurrentyear

$(document).ready(function() {
    $("#getDataButton").on("click", function() {
        // Make AJAX request to fetch data between GL codes
        $.ajax({
            url: '/services/EGF/supplier/generalLedgerDataBetweenGLCod',                         // Update URL
            type: 'GET',
            contentType: 'application/json',
            success: function(response) {
                console.log("Response data:", response);
            }, // <-- Corrected the comma to a semicolon
            error: function(xhr, status, error) {
                console.error("Error fetching data:", error);
                console.error("Status:", status);
                console.error("Response:", xhr.responseText);
            }
        });
    });
});


$(document).ready(function() {
    $("#getDataButton").on("click", function() {
        // Make AJAX request to fetch data between GL codes
        $.ajax({
            url: '/services/EGF/supplier/generalLedgerDataBetweenGLCod',
            type: 'GET',
            contentType: 'application/json',
            success: function(response) {
                // Assuming response contains the total debit amount
                var grantsrelatedtoRevenue = response;

                // Add two decimal places
                grantsrelatedtoRevenue = parseFloat(grantsrelatedtoRevenue).toFixed(2);

                // Set the value of the input field
                $("input[name='grantsrelatedtoRevenue/generalgrantscurrentyear']").val(grantsrelatedtoRevenue);
            },
            error: function(xhr, status, error) {
                console.error("Error fetching data:", error);
                console.error("Status:", status);
                console.error("Response:", xhr.responseText);
            }
        });
    });
});




//for  the 4rd query ..interestValuecurrentyear
$(document).ready(function() {
    $("#getDataButton").on("click", function() {
        // Make AJAX request to fetch data between GL codes
        $.ajax({
            url: '/services/EGF/supplier/generalLedgerDataBetweenGL', // Update URL
            type: 'GET',
            contentType: 'application/json',
            success: function(response) {
                console.log("Response data:", response);
            }, // <-- Corrected the comma to a semicolon
            error: function(xhr, status, error) {
                console.error("Error fetching data:", error);
                console.error("Status:", status);
                console.error("Response:", xhr.responseText);
            }
        });
    });
});

$(document).ready(function() {
    $("#getDataButton").on("click", function() {
        // Make AJAX request to fetch data between GL codes
        $.ajax({
            url: '/services/EGF/supplier/generalLedgerDataBetweenGL',
            type: 'GET',
            contentType: 'application/json',
            success: function(response) {
                // Assuming response contains the total debit amount
                var interestValue = response;

                // Add two decimal places
                interestValue = parseFloat(interestValue).toFixed(2);

                // Set the value of the input field
                $("input[name='InterestReceivedcurrentyear']").val(interestValue);
            },
            error: function(xhr, status, error) {
                console.error("Error fetching data:", error);
                console.error("Status:", status);
                console.error("Response:", xhr.responseText);
            }
        });
    });
});



//for the 5th Other Receipts   

















</script>   
</body>
