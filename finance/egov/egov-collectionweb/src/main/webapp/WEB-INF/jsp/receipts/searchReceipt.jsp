
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
<%@ taglib prefix="egov-authz" uri="/WEB-INF/taglib/egov-authz.tld"%>
<link rel="stylesheet" type="text/css"
	href="<egov:url path='/yui/assets/skins/sam/autocomplete.css'/>" />
<head>
<title><s:text name="searchreceipts.title" /></title>
<style type="text/css">
table {
	width: 100%;
}

#fromDate, #toDate, #receiptNumber, textfield, textarea, select {
	width: 80% !important;
}
</style>
<script type="text/javascript">
	var serviceTypeMap = {};
	<s:iterator value="serviceTypeMap" var="entry">
	serviceTypeMap['<s:property value="#entry.key"/>'] = {};
	<s:iterator value="#entry.value" var="inner">
	serviceTypeMap['<s:property value="#entry.key"/>']['<s:property value="#inner.key"/>'] = '<s:property value="#inner.value" escapeJavaScript="true" escapeHtml="false"/>';
	</s:iterator>
	</s:iterator>

	function populateServiceType(selected) {
		var cell = document.getElementById('serviceTypeCell');
		var label = document.getElementById('serviceTypeLabel');

		cell.innerHTML = '';

		label.innerHTML = '<s:text name="searchreceipts.criteria.servicetype" /><span class="mandatory"></span>';

		/* if (selected == -1 || !serviceTypeMap[selected]) return; */

		var map = serviceTypeMap[selected];
		var keys = Object.keys(map);
		if (keys.length === 0)
			return;


		var sel = document.createElement('select');
		sel.name = 'serviceTypeId';
		sel.id = 'serviceType';
		sel.className = 'selectwk';
		sel.style.width = '100%';
		sel.options[0] = new Option(
				'<s:text name="searchreceipts.servicetype.select"/>', '-1');
		keys.forEach(function(k, i) {
			sel.options[i + 1] = new Option(map[k], k);
		});

		var prev = '<s:property value="serviceTypeId"/>';
		if (prev)
			sel.value = prev;

		cell.appendChild(sel);
	}

	// On page load, re-populate if category was already selected (e.g. after a search)
	jQuery(document).ready(function() {
		var prevCat = '<s:property value="serviceCategory"/>';
		if (prevCat && prevCat !== '-1') {
			populateServiceType(prevCat);
		}
	});

	function printResultTable() {
	    var tableContent = document.getElementById("resultTable").outerHTML;

	    /* var today = new Date();
	    var date = today.toLocaleDateString('en-GB'); // DD/MM/YYYY
	    var time = today.toLocaleTimeString(); */

	    var printWindow = window.open('', '', 'height=700,width=1000');

	    printWindow.document.write('<html><head><title>Print Receipts</title>');

	    // CSS
	    printWindow.document.write('<style>');
	    printWindow.document.write('body { font-family: Arial, sans-serif; }');

	    //Blue header styling
	    printWindow.document.write('.header { text-align: center; margin-bottom: 10px; color: #003366; }');
	    printWindow.document.write('.header h2 { margin: 0; font-size: 18px; font-weight: bold; color: #003366; }');
	    printWindow.document.write('.header h3 { margin: 0; font-size: 16px; color: #003366; }');
	    printWindow.document.write('.header h4 { margin: 0; font-size: 14px; color: #003366; }');

	    //printWindow.document.write('.datetime { position: absolute; right: 20px; top: 10px; font-size: 12px; }');
	    printWindow.document.write('hr { border: 1px solid black; }');

	    printWindow.document.write('table { width:100%; border-collapse: collapse; margin-top:10px;}');
	    printWindow.document.write('table, th, td { border: 1px solid black; }');
	    printWindow.document.write('th, td { padding: 6px; font-size: 12px; }');

	    // hide checkbox & hidden fields
	    printWindow.document.write('input { display:none; }');

	    printWindow.document.write('</style>');
	    printWindow.document.write('</head><body>');

	    // Header
	    printWindow.document.write(`
	       <!-- <div class="datetime">
	            Date: ${date}<br>
	            Time: ${time}
	        </div> -->

	        <div class="header">
	            <h2>Government of Jammu & Kashmir</h2>
	            <h3>Housing and Urban Development</h3>
	            <h4>Department</h4>
	        </div>

	        <hr/>
	    `);

	    // Table
	    printWindow.document.write(tableContent);

	    printWindow.document.write('</body></html>');

	    printWindow.document.close();
	    printWindow.focus();
	    printWindow.print();
	    printWindow.close();
	}

	function exportTableToExcel() {
	    var table = document.getElementById("resultTable");

	    if (!table) {
	        alert("Table not found!");
	        return;
	    }

	    var rows = table.rows;
	    var excel = "<table border='1'>";

	    // Header
	    var today = new Date();
	    var date = today.toLocaleDateString('en-GB');
	    var time = today.toLocaleTimeString();

	    excel += `
	        <tr>
	            <td colspan="10" style="text-align:center; font-weight:bold;">
	                Government of Jammu & Kashmir<br>
	                Housing and Urban Development<br>
	                Department
	            </td>
	            <td colspan="3" style="text-align:right;">
	                Date: ${date}<br>
	                Time: ${time}
	            </td>
	        </tr>
	        <tr><td colspan="13"></td></tr>
	    `;

	    // Loop table rows
	    for (var i = 0; i < rows.length; i++) {
	        excel += "<tr>";

	        var cols = rows[i].cells;

	        for (var j = 0; j < cols.length; j++) {
	            var data = cols[j].innerText;

	            // remove checkbox column (first column)
	            if (j === 0) continue;

	            excel += "<td>" + data + "</td>";
	        }

	        excel += "</tr>";
	    }

	    excel += "</table>";

	    var blob = new Blob([excel], { type: "application/vnd.ms-excel" });
	    var url = URL.createObjectURL(blob);

	    var a = document.createElement("a");
	    a.href = url;
	    a.download = "Receipt_Report.xls";
	    document.body.appendChild(a);
	    a.click();
	    document.body.removeChild(a);
	}
</script>

<script>
	jQuery.noConflict();
	jQuery(document).ready(function() {

		jQuery(" form ").submit(function(event) {
			doLoadingMask();
		});
		doLoadingMask();
	});

	jQuery(window).load(function() {
		undoLoadingMask();
	});

	function isChecked(chk) {
		if (chk.length == undefined) {
			if (chk.checked == true)
				return true;
			else
				return false;
		} else {
			for (i = 0; i < chk.length; i++) {
				if (chk[i].checked == true)
					return true;
			}
			return false;
		}
	}

	function checkselectedreceiptcount(obj) {
		var cnt = document.getElementsByName('selectedReceipts');
		var receiptstatus = document.getElementsByName('receiptstatus');
		var j = 0;
		for (i = 0; i < cnt.length; i++) {
			if (cnt[i].checked == true) {
				j++;
				if (obj == 'cancel' && receiptstatus[i].value == "Cancelled") {
					dom.get("selectedcancelledreceiptserror").style.display = "block";
					return -1;
				} else {
					dom.get("selectedcancelledreceiptserror").style.display = "none";
				}
			} else {
				dom.get("selectedcancelledreceiptserror").style.display = "none";
			}
		}
		if (j == 0)
			return 0;
		else if (j > 1)
			return 2;
		else
			return 1;
	}

	function checkcancelforselectedrecord() {
		dom.get("pendingreceiptcancellationerror").style.display = "none";
		dom.get("selectcancelerror").style.display = "none";
		var check = checkselectedreceiptcount('cancel');
		// more than one receipt has been chosen. Should not allow cancellation
		if (check == 2) {
			dom.get("norecordselectederror").style.display = "none";
			dom.get("selectcancelerror").style.display = "block";
			dom.get("selectprinterror").style.display = "none";
			window.scroll(0, 0);
			return false;
		}
		// no receipts have been chosen. should not allow cancellation
		else if (check == 0) {
			dom.get("selectcancelerror").style.display = "none";
			dom.get("norecordselectederror").style.display = "block";
			dom.get("selectprinterror").style.display = "none";
			window.scroll(0, 0);
			return false;
		}
		// one or more cancelled receipts have been chosen. should not allow cancellation
		else if (check == -1) {
			dom.get("selectcancelerror").style.display = "none";
			dom.get("norecordselectederror").style.display = "none";
			dom.get("selectprinterror").style.display = "none";
			window.scroll(0, 0);
			return false;
		}
		//one receipt has been chosen. Cancellation is allowed
		else {
			var cnt = document.getElementsByName('selectedReceipts');
			var receiptstatus = document.getElementsByName('receiptstatus');
			var instrumenttype = document.getElementsByName('instrumenttype');
			var j = 0;
			for (m = 0; m < cnt.length; m++) {
				if (cnt[m].checked == true) {
					if (receiptstatus[m].value == "Pending") {
						dom.get("pendingreceiptcancellationerror").style.display = "block";
						window.scroll(0, 0);
						return false;
					}

					else if (receiptstatus[m].value == "Instrument Bounced") {
						dom.get("instrumentbouncedreceiptcancellationerror").style.display = "block";
						window.scroll(0, 0);
						return false;
					} else if (receiptstatus[m].value == "Remitted"
							|| receiptstatus[m].value == "Partial Remitted") {
						dom.get("remittedreceiptcancellationerror").style.display = "block";
						window.scroll(0, 0);
						return false;
					}

					if (instrumenttype[m].value == "online") {
						dom.get("onlinereceiptcancellationerror").style.display = "block";
						window.scroll(0, 0);
						return false;
					}

				}
			}
			dom.get("selectcancelerror").style.display = "none";
			var receipttype = document.getElementsByName('receipttype');
			var cnt = document.getElementsByName('selectedReceipts');

			for (m = 0; m < cnt.length; m++) {
				if (cnt[m].checked == true) {
					if (receipttype[m].value == "A"
							|| receipttype[m].value == "B") {
						document.searchReceiptForm.action = "receipt-cancel.action";
					}
					if (receipttype[m].value == 'C') {
						document.searchReceiptForm.action = "challan-cancelReceipt.action";
					}

				}
			}

			document.searchReceiptForm.submit();
		}
	}
	function checkprintforselectedrecord() {
		var check = checkselectedreceiptcount('print');
		// more than one receipts have been chosen. should not print
		if (check == 2) {
			dom.get("norecordselectederror").style.display = "none";
			dom.get("selectprinterror").style.display = "block";
			dom.get("selectcancelerror").style.display = "none";
			window.scroll(0, 0);
			return false;
		}
		// no receipts ahev been chosen for print
		else if (check == 0) {
			dom.get("selectprinterror").style.display = "none";
			dom.get("norecordselectederror").style.display = "block";
			dom.get("selectcancelerror").style.display = "none";
			window.scroll(0, 0);
			return false;
		}
		// single receipt has been chosen. Print is allowed
		else {
			dom.get("selectprinterror").style.display = "none";
			document.searchReceiptForm.action = "receipt-printReceipts.action";
			document.searchReceiptForm.submit();
		}
		//document.searchReceiptForm.action="receipt-printReceipts.action";
		//document.searchReceiptForm.submit();
	}

	function validate() {
		var fromdate = dom.get("fromDate").value;
		var todate = dom.get("toDate").value;
		var serviceType = dom.get("serviceType").value;
		console.log("serviceType : " + serviceType);
		var valSuccess = true;
		/* if(null!= document.getElementById('serviceClass') && document.getElementById('serviceClass').value == '-1'){
			dom.get("error_area").style.display="block";
			dom.get("error_area").innerHTML = '<s:text name="service.servictype.null" />' + '<br>';
			window.scroll(0,0);
			valSuccess=false;
			return false;
		} */

		if (serviceType == -1) {
			valSuccess = false;
			dom.get("error_area").style.display = "block";
			dom.get("error_area").innerHTML = '<s:text name="service.servictype.null" />'
					+ '<br>';
			window.scroll(0, 0);
			return false;
		}

		if (fromdate != "" && todate != "" && fromdate != todate) {
			if (!checkFdateTdate(fromdate, todate)) {
				dom.get("comparedatemessage").style.display = "block";
				window.scroll(0, 0);
				valSuccess = false;
				return false;
			}
		} else {
			dom.get("comparedatemessage").style.display = "none";
			doLoadingMask('#loadingMask');
			valSuccess = true;
			return true;
		}
		return valSuccess;

	}

	/* var receiptNumberSelectionEnforceHandler = function(sType, arguments) {
	 warn('improperreceiptNumberSelection');
	 }
	 var receiptNumberSearchSelectionHandler = function(sType, arguments) { 
	 var oData = arguments[2];
	 dom.get("receiptNumberSearch").value=oData[0];
	 }


	 var manualReceiptNumberSearchSelectionHandler = function(sType, arguments) { 
	 var oData = arguments[2];
	 dom.get("manualReceiptNumberSearch").value=oData[0];
	 }
	 var manualReceiptNumberSelectionEnforceHandler = function(sType, arguments) {
	 warn('impropermanualReceiptNumberSelectionWarning');
	 } */
	function checkviewforselectedrecord() {
		dom.get("norecordselectederror").style.display = "none";
		dom.get("selectprinterror").style.display = "none";
		dom.get("selectcancelerror").style.display = "none";
		var cnt = document.getElementsByName('selectedReceipts');
		var receiptstatus = document.getElementsByName('receiptstatus');
		var j = 0;
		for (i = 0; i < cnt.length; i++) {
			if (cnt[i].checked == true) {
				j++;
			}
		}
		//no records have been selected for view
		if (j == 0) {
			dom.get("norecordselectederror").style.display = "block";
			window.scroll(0, 0);
			return false;
		}
		// multiple records have been chosen . Viewing is allowed
		else {
			doLoadingMask('#loadingMask');
			document.searchReceiptForm.action = "receipt-viewReceipts.action";
			document.searchReceiptForm.submit();
		}

	}

	function onChangeServiceClass(obj) {
		if (obj != null && obj.value != null && obj.value != '-1') {
			populateserviceType({
				serviceClass : obj.value
			});
		}
	}
</script>
</head>
<body>
	<div class="errorstyle" id="error_area" style="display: none;"></div>
	<span align="center" style="display: none"
		id="pendingreceiptcancellationerror">
		<li><font size="2" color="red"><b><s:text
						name="error.pendingreceipt.cancellation" /></b></font></li>
	</span>
	<span align="center" style="display: none"
		id="instrumentbouncedreceiptcancellationerror">
		<li><font size="2" color="red"><b><s:text
						name="error.instrumentbouncedreceipt.cancellation" /></b></font></li>
	</span>
	<span align="center" style="display: none"
		id="remittedreceiptcancellationerror">
		<li><font size="2" color="red"><b><s:text
						name="error.remittedreceipt.cancellation" /></b></font></li>
	</span>
	<span align="center" style="display: none"
		id="onlinereceiptcancellationerror">
		<li><font size="2" color="red"><b><s:text
						name="error.onlinereceipt.cancellation" /></b></font></li>
	</span>
	<span align="center" style="display: none" id="selectprinterror">
		<li><font size="2" color="red"><b><s:text
						name="error.print.nomultipleprintreceipts" /> </b></font></li>
	</span>
	<span align="center" style="display: none" id="selectcancelerror">
		<li><font size="2" color="red"><b><s:text
						name="error.print.nomultiplecancelreceipts" /> </b></font></li>
	</span>
	<span align="center" style="display: none" id="norecordselectederror">
		<li><font size="2" color="red"><b><s:text
						name="error.norecordselected" /></b></font></li>
	</span>
	<span align="center" style="display: none"
		id="selectedcancelledreceiptserror">
		<li><font size="2" color="red"><b><s:text
						name="error.selectedcancelledreceiptserror" /></b></font></li>
	</span>
	<span align="center" style="display: none" id="invaliddateformat">
		<li><font size="2" color="red"><b> <s:text
						name="common.dateformat.errormessage" />
			</b></font></li>
	</span>
	<span align="center" style="display: none" id="comparedatemessage">
		<li><font size="2" color="red"><b> <s:text
						name="common.comparedate.errormessage" />
			</b></font></li>
	</span>
	<s:if test="%{hasErrors()}">
		<div align="center">
			<div id="actionErrorMessages" class="alert alert-danger">
				<s:actionerror />
				<s:fielderror />
			</div>
		</div>
	</s:if>
	<s:form theme="simple" name="searchReceiptForm"
		action="searchReceipt-search.action">
		<div class="formmainbox">
			<div class="subheadnew">
				<s:text name="searchreceipts.title" />
			</div>
			<div class="subheadsmallnew">
				<span class="subheadnew"><s:text
						name="searchreceipts.criteria" /></span>
			</div>
			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">

				<tr>
					<td width="2%" class="bluebox">&nbsp;</td>
					<td width="15%" class="bluebox"><s:text
							name="service.master.classification" /> <span class="mandatory"></td>
					<td width="30%" class="bluebox">
						<%-- <s:select list="serviceClassMap" headerKey="-1" headerValue="%{getText('miscreceipt.select')}"
				name="serviceClass" id="serviceClass" onchange="onChangeServiceClass(this);"></s:select> --%>
						<s:select name='type' list="#{'type':'MISCELLANEOUS' }"></s:select>
					</td>
					<td width="15%" class="bluebox"><s:text
							name="searchreceipts.criteria.receiptno" /></td>
					<td width="30%" class="bluebox">
						<div class="yui-skin-sam">
							<s:textfield id="receiptNumber" type="text" name="receiptNumber" />
					</td>
					<%--  <egov:ajaxdropdown id="serviceTypeDropdown" fields="['Text','Value']" dropdownId='serviceType'
                url='receipts/ajaxReceiptCreate-ajaxLoadServiceByClassification.action' /> --%>
					<!--   <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.servicetype"/> <span class="mandatory"></td>
	        <td width="24%" class="bluebox"><s:select headerKey="-1"  headerValue="%{getText('searchreceipts.servicetype.select')}"  name="serviceTypeId" id="serviceType" cssClass="selectwk" list="dropdownData.serviceTypeList" listKey="code" listValue="businessService" value="%{serviceTypeId}" /> </td>-->

					<%-- <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.counter"/></td>
	      <td width="30%" class="bluebox"><s:select headerKey="-1" headerValue="%{getText('searchreceipts.counter.select')}" name="counterId" id="counter" cssClass="selectwk" list="dropdownData.counterList" listKey="id" listValue="name" value="%{counterId}" /> </td> --%>
				</tr>
				<tr>
					<td width="2%" class="bluebox">&nbsp;</td>
					<td width="15%" class="bluebox"><s:text
							name="searchreceipts.criteria.fromdate" /></td>
					<s:date name="fromDate" var="cdFormat" format="dd/MM/yyyy" />
					<td width="30%" class="bluebox"><s:textfield id="fromDate"
							name="fromDate" value="%{cdFormat}"
							onfocus="javascript:vDateType='3';"
							onkeyup="DateFormat(this,this.value,event,false,'3')" /><a
						href="javascript:show_calendar('forms[0].fromDate');"
						onmouseover="window.status='Date Picker';return true;"
						onmouseout="window.status='';return true;"><img
							src="/services/egi/resources/erp2/images/calendaricon.gif"
							alt="Date" width="18" height="18" border="0" align="absmiddle" /></a>
					<div class="highlight2" style="width: 80px">DD/MM/YYYY</div></td>
					<td width="15%" class="bluebox"><s:text
							name="searchreceipts.criteria.todate" /></td>
					<s:date name="toDate" var="cdFormat1" format="dd/MM/yyyy" />
					<td width="30%" class="bluebox"><s:textfield id="toDate"
							name="toDate" value="%{cdFormat1}"
							onfocus="javascript:vDateType='3';"
							onkeyup="DateFormat(this,this.value,event,false,'3')" /><a
						href="javascript:show_calendar('forms[0].toDate');"
						onmouseover="window.status='Date Picker';return true;"
						onmouseout="window.status='';return true;"><img
							src="/services/egi/resources/erp2/images/calendaricon.gif"
							alt="Date" width="18" height="18" border="0" align="absmiddle" /></a>
					<div class="highlight2" style="width: 80px">DD/MM/YYYY</div></td>
				</tr>
				<tr>
					<td width="2%" class="bluebox">&nbsp;</td>
					<td width="15%" class="bluebox"><s:text
							name="searchreceipts.criteria.servicecategory" /> <span
						class="mandatory"></td>
					<td width="30%" class="bluebox"><s:select headerKey="-1"
							headerValue="%{getText('miscreceipt.select')}"
							name="serviceCategory" id="serviceCategoryid" cssClass="selectwk"
							list="serviceCategoryNames" value="%{serviceCategory}"
							onChange="populateServiceType(this.value);" /></td>
					<td width="15%" class="bluebox" id="serviceTypeLabel"></td>
					<td width="30%" class="bluebox" id="serviceTypeCell"></td>
				</tr>



				<!--  <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.receiptno"/></td>
	      <td width="24%" class="bluebox">
	      <div class="yui-skin-sam"><s:textfield id="receiptNumber" type="text" name="receiptNumber"/></td>

	     <%--  <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.user"/></td>
	      <td width="30%" class="bluebox"><s:select headerKey="-1" headerValue="%{getText('searchreceipts.user.select')}" name="userId" id="user" cssClass="selectwk" list="dropdownData.userList" listKey="id" listValue="name" value="%{userId}" /> </td>
	    --%>
	    </tr>-->
				<%--    <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.status"/></td>
	      <td width="24%" class="bluebox"><s:select id="searchStatus" name="searchStatus" headerKey="-1" headerValue="%{getText('searchreceipts.status.select')}" cssClass="selectwk" list="%{receiptStatuses}" value="%{searchStatus}" listKey="id" listValue="description" /> </td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.paymenttype"/></td>
	      <td width="30%" class="bluebox"><s:select headerKey="" headerValue="%{getText('searchreceipts.paymenttype.select')}" name="instrumentType" id="instrumentType" cssClass="selectwk" list="dropdownData.instrumentTypeList" listKey="type" listValue="type" value="%{instrumentType}" /> </td>	
	    </tr>
	    <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.manual.receiptno"/></td>
	      <td width="24%" class="bluebox"><s:textfield id="manualReceiptNumber" type="text" name="manualReceiptNumber"/></td>
	      <td width="21%" class="bluebox"> &nbsp; </td>
	      <td width="30%" class="bluebox"> &nbsp; </td>   
	    </tr> --%>
				<%--  
	    <tr>
					<td>
						<div class="subheadsmallnew"><span class="subheadnew">
											<s:text name="bankcollection.title" />
						</span>		
						</div>
					</td>
		</tr>
	     <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.bankbranch"/></td>
	      <td width="24%" class="bluebox"><s:select headerKey="-1"
								headerValue="Select Bank Branch" name="branchId" id="branchId"
								cssClass="selectwk" list="dropdownData.bankBranchList"
								listKey="id" listValue="branchname"
								value="%{branchId}" /> </td>
	      <td width="21%" class="bluebox">&nbsp;</td>
	      <td width="30%" class="bluebox">&nbsp;</td>
	    </tr> --%>
			</table>
			<%-- <div align="left" class="mandatory1">
		              <s:text name="report.bankbranch.note"/>
		</div> --%>
		</div>
		<div id="loadingMask"
			style="display: none; overflow: hidden; text-align: center">
			<img src="/services/collection/resources/images/bar_loader.gif" /> <span
				style="color: red">Please wait....</span>
		</div>
		<div class="buttonbottom">
			<label><s:submit type="submit" cssClass="buttonsubmit"
					id="button" key="lbl.search" onclick="return validate();" /></label> <label><s:submit
					type="submit" cssClass="button" key="lbl.reset"
					onclick="document.searchReceiptForm.action='searchReceipt-reset.action'" /></label>
			<s:if test="%{results.isEmpty()}">
				<input name="closebutton" type="button" class="button"
					id="closebutton" value="<s:text name='lbl.close'/>"
					onclick="window.close();" />
			</s:if>

		</div>
		<s:if test='%{resultList.isEmpty()}'>
			<table width="90%" border="0" align="center" cellpadding="0"
				cellspacing="0" class="tablebottom">
				<tr>
					<div>&nbsp;</div>
					<div class="subheadnew">
						<s:text name="searchresult.norecord" />
					</div>
				</tr>
			</table>
		</s:if>
		<s:if test='%{!resultList.isEmpty()}'>

			<div align="center">
				<display:table name="resultList" uid="currentRow" id="resultTable" htmlId="resultTable"
                  style="width:100%;border-left: 1px solid #DFDFDF;" cellpadding="0"
                     cellspacing="0" export="false" requestURI="">
					<display:caption media="pdf">&nbsp;</display:caption>
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						style="width:3%">
						<s:if test='%{collectionVersion eq "V2"}'>
							<input name="selectedReceipts" type="checkbox"
								id="selectedReceipts" value="${currentRow.paymentId}" />
						</s:if>
						<s:else>
							<input name="selectedReceipts" type="checkbox"
								id="selectedReceipts" value="${currentRow.receiptnumber}" />
						</s:else>
						<input type="hidden" name="receiptstatus" id="receiptstatus"
							value="${currentRow.curretnStatus}" />
						<input type="hidden" name="receipttype" id="receipttype"
							value="${currentreceipttype}" />
					</display:column>
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Receipt No." style="width:8%;text-align:right"
						property="receiptnumber" />
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						property="receiptdate" title="Receipt Date"
						format="{0,date,dd/MM/yyyy}" style="width:8%;text-align: center" />
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="G8 Receipt number/Date" style="width:8%;text-align:right"
						property="g8data" />
					<%-- <display:column headerClass="bluebgheadtd" class="blueborderfortd" property="manualreceiptdate" title="G8 Receipt Date" format="{0,date,dd/MM/yyyy}" style="width:8%;text-align: center" /> --%>
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Category" style="width:12%;text-align:left"
						property="serviceCat" />
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Service" style="width:12%;text-align:left"
						property="service" />
					<%-- <display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Bill Number" style="width:8%;text-align:right" property="referencenumber" /> --%>
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Narration" style="width:27%;text-align:left"
						property="referenceDesc" />
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Paid By" style="width:27%;text-align:left"
						property="paidBy" />
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Amount (Rs.)" property="totalAmount"
						style="width:8%; text-align: right" format="{0, number, #,##0.00}" />
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Mode of Payment" style="width:8%" property="modOfPayment" />
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Fund Name" style="width:10%;text-align:left"
						property="fund" />

					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Ward No" style="width:10%;text-align:left"
						property="wardNo" />
					<%-- <div align="center">
<s:set var="instrtype" value="" />
<s:iterator status="stat1" value="#attr.currentRow.receiptInstrument">
<s:if test="instrumentType.type!=null">
<s:property value="instrumentType.type"/>
<s:set var="instrtype" value="%{instrumentType.type}" />
</s:if>
<s:if test="!#stat1.last">, </s:if>
</s:iterator>&nbsp;
</div>
<input type="hidden" name="instrumenttype" id="instrumenttype" value="${instrtype}" />
</display:column> --%>
					<display:column headerClass="bluebgheadtd" class="blueborderfortd"
						title="Status" style="width:8%;text-align:center"
						property="curretnStatus"></display:column>
					<%-- <display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Owner" style="width:8%;text-align:center" property="workflowUserName"></display:column> --%>
				</display:table>
			</div>
			<br />
			<div class="buttonbottom">
				<input name="button32" type="button" class="buttonsubmit"
					id="button32" value="View Receipt"
					onclick="return checkviewforselectedrecord()" /> 
					<input name="button32" type="button" class="buttonsubmit" id="button32"
					value="Print Receipt" onclick="return checkprintforselectedrecord()" />
					<input type="button" class="buttonsubmit"
                         value="Print PDF"
                         onclick="printResultTable()" />
                     <input type="button" class="buttonsubmit"
                       value="Export to Excel"
                       onclick="exportTableToExcel()" />    
				<%-- <egov-authz:authorize actionName="CancelReceipt">
  <input name="button32" type="button" class="buttonsubmit" id="button32" value="Cancel Receipt" onclick="return checkcancelforselectedrecord()"/>
  </egov-authz:authorize> --%>
				<input name="button32" type="button" class="button" id="button32"
					value="<s:text name='lbl.close'/>"
					onclick="window.parent.postMessage('close','*');window.close();" />
			</div>
		</s:if>
	</s:form>
</body>


