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


<html>
<head>
<head>
   <!--  <meta charset="UTF-8"> -->
    <title >Cash Flow Statement</title>
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
            <!-- <tr> -->
                <!-- <td colspan="3"><b>(A) Cash Flows from Operating Activities</b></td> -->
				

				
            <!-- </tr> -->
			
			
			<tr style="color: #ff9933;">
           <td colspan="3"><b>(A) Cash Flows from Operating Activities</b></td>
             </tr>

			
            <tr>
                <td><b>Cash Receipt From</b></td>
            </tr>
            
			<tr>
                <td>Taxation</td>
				<!-- <td></td> -->
				<!-- <td></td> -->
		 <td>
        <input type="number" name="taxationCurrentYear" value="100" style="border: none; background-color: transparent; width: 100%; text-align: right;">

    </td> 
			 <td>
        <input type="number" name="taxationPreviousYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			
				<!-- <!-- <td input type="number" name="taxationCurrentYear" value="00"></td>  --> 
                <!-- <td input type="number" name="taxationPreviousYear" value="00"></td>  -->
				
            </tr>
			
            <tr>
                <td>Sales of Goods and Services</td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousyear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Grants related to Revenue/General Grants</td>
               <td>
        <input type="number" name="grantsrelatedtoRevenue/generalgrantscurrentyear" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="grantsrelatedtoRevenue/generalgrantspreviousyear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Interest Received </td>
                  <td>
        <input type="number" name="InterestReceivedcurrentyear " value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="interestreceivedpreviouspreviousyear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Other Receipts</td>
                  <td>
        <input type="number" name="otherreceiptcurrentyear" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="otherreceiptpreviousyear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td><b>Less: Cash Payments for:</b></td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Employee Costs</td>
                <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Superannuation</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Suppliers</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Interest Paid (Expense)</td>
  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Other Payments</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr  style="color: #ff9933;">
                <td><b>Net cash generated from/ (used in) operating activities (a)</b></td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
        
		
		
		
		
		<!-- Cash Flows from Investing Activities -->
            <tr style="color: #ff9933;">
                <td colspan="3"><b>(B) Cash Flows from Investing Activities</b></td>
            </tr>
            <tr>
                <td>(Purchase) of fixed assets & CWIP</td>
                <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>(Increase) / Decrease in Special funds/grants</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>(Increase) / Decrease in Earmarked funds</td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>(Purchase) of Investments</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Add:</td>
               <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Proceeds from disposal of assets</td>
               <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Proceeds from disposal of investments</td>
               <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Investment income received</td>
              <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Interest income received</td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr  style="color: #ff9933;">
                <td><b>Net cash generated from/ (used in) investing activities (b)</b></td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
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
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td><b>Less: </b></td>
				
                
            <tr>
                <td>Loans repaid during the period</td>
                <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            <tr>
                <td>Loans & advances to employees</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Loans to others</td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Finance expenses</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr style="color: #ff9933;">
                <td><b>Net cash generated from (used in) financing activities (c)</b></td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr style="color: #ff9933;">
                <td><b>Net increase/ (decrease) in cash and cash equivalents (a + b + c)</b></td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
        
		
		
		 <!-- Cash and cash equivalents -->
            <tr style="color: #ff9933;">
                <!-- <td colspan="3"><b>Cash and cash equivalents at beginning of period</b></td> -->
				<td><b>Cash and cash equivalents at beginning of period</b></td>
								
				
				<td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
				
				
				
				
            </tr>
            <tr>
                <td><b>Cash Balances</b></td>
                 <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td><b>Bank Balances</b></td>
                <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Scheduled co-operative banks</td>
               <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td> Balances with Post offices</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr>
                <td>Balances with other banks</td>
                  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
            </tr>
            <tr style="color: #ff9933;">
                <td><b>Cash and cash equivalents at end of period</b></td>
                
				  <td>
        <input type="number" name="salesofgoodsandservices" value="  " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
			 <td>
        <input type="number" name="taxationPreviousYearYear" value=" " style="border: none; background-color: transparent; width: 100%;text-align: right;">
    </td> 
				
            </tr>
        </table>
		
        
    </div>
</body>
</html>