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


<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<link href="/services/EGF/resources/css/budget.css?rnd=${app_release_no}" rel="stylesheet"
	type="text/css" />
<style type="text/css">
@media print {
	#non-printable {
		display: none;
	}
}
</style>

<script>

function disableAsOnDate(){
	if(document.getElementById('period').value != "Date"){
		//document.getElementById('asOndate').disabled = true;
		document.getElementById('fromDate').disabled = true;
		document.getElementById('toDate').disabled = true;
		document.getElementById('financialYear').disabled = false;
       // document.getElementById('asOndate').value= '';
		document.getElementById('fromDate').value= '';
		document.getElementById('toDate').value= '';
		document.getElementById("dateinputs").style.display = 'none';
    }else{
		document.getElementById('financialYear').disabled = true;
		//document.getElementById('asOndate').disabled = false;
        document.getElementById("financialYear").selectedIndex = 0;
    	document.getElementById('fromDate').disabled = false;
		document.getElementById('toDate').disabled = false;
		document.getElementById("dateinputs").style.display = '';
    }
}


 function validateMandatoryFields(){
		if(document.getElementById('period').value=="Select")
		{
			bootbox.alert('<s:text name="msg.please.select.period"/>');
			return false;
		}
		if(document.getElementById('period').value!="Date"){
			if(document.getElementById('financialYear').value==0){
				bootbox.alert('<s:text name="msg.please.select.financial.year"/>');
				return false;
			}
		}

		/*if(document.getElementById('period').value=="Date" && document.getElementById('asOndate').value==""){
			bootbox.alert('<s:text name="msg.please.enter.as.onDate"/>');
			return false;
		}*/
		if( document.getElementById('period').value=="Date" && document.getElementById('fromDate').value==""){
			bootbox.alert('<s:text name="msg.please.select.from.date"/>');
			return false;
		}
		if( document.getElementById('period').value=="Date" && document.getElementById('toDate').value==""){
			bootbox.alert('<s:text name="msg.please.select.toDate"/>');
			return false;
		}
		return true;
	}
function balanceSheetReportSubmit()
	{
		if(validateMandatoryFields()){
			document.balanceSheetReport.action='/services/EGF/report/balanceSheetReport-printBalanceSheetReport.action';
			jQuery(balanceSheetReport).append(jQuery('<input>', {
		        type : 'hidden',
		        name : '${_csrf.parameterName}',
		        value : '${_csrf.token}'
		    }));
			document.balanceSheetReport.submit();
			return true;
		}
		return false;

}

function showAllSchedules(){
		if(validateMandatoryFields()){
	 		window.open(
	 			'/services/EGF/report/balanceSheetReport-generateScheduleReport.action'
	 			+ '?showDropDown=false'
	 			+ '&model.period=' + document.getElementById('period').value
	 			+ '&model.currency=' + document.getElementById('currency').value
	 			+ '&model.financialYear.id=' + document.getElementById('financialYear').value
	 			+ '&model.department.code=' + document.getElementById('department').value
	 			+ '&model.fund.id=' + document.getElementById('fund').value
	 			+ '&model.function.id=' + document.getElementById('function').value
	 			+ '&model.fromDate=' + document.getElementById('fromDate').value
	 			+ '&model.toDate=' + document.getElementById('toDate').value,
	 			'',
	 			'resizable=yes,height=650,width=900,scrollbars=yes,left=30,top=30,status=no'
	 		);

	 		//document.balanceSheetReport.action='/services/EGF/report/balanceSheetReport-generateScheduleReport.action?showDropDown=false&model.period='+document.getElementById('period').value+'&model.currency='+document.getElementById('currency').value+'&model.financialYear.id='+document.getElementById('financialYear').value+'&model.department.code='+document.getElementById('department').value+'&model.fund.id='+document.getElementById('fund').value+'&model.function.id='+document.getElementById('function').value+'&model.asOndate='+document.getElementById('asOndate').value,'','resizable=yes,height=650,width=900,scrollbars=yes,left=30,top=30,status=no';
	 		//document.balanceSheetReport.submit();
	 		return true;
	    }
		return false;
	}


function showAllSchedulesDetailed(){
	if(validateMandatoryFields()){
		var period = document.getElementById('period').value;
		var currency=document.getElementById('currency').value;
		var financialYear=document.getElementById('financialYear').value;
		var department=document.getElementById('department').value;
		var fund=document.getElementById('fund').value;
		var functionId=document.getElementById('function').value;
/* 		var field=document.getElementById('field').value;
		var functionary=document.getElementById('functionary').value;
 */		
		var fromDate=document.getElementById('fromDate').value;
		var toDate=document.getElementById('toDate').value;


	 	window.open(
	 		"/services/EGF/report/balanceSheetReport-generateScheduleReportDetailed.action"
	 		+ "?showDropDown=false"
	 		+ "&model.period=" + period
	 		+ "&model.currency=" + currency
	 		+ "&model.financialYear.id=" + financialYear
	 		+ "&model.department.code=" + department
	 		+ "&model.fund.id=" + fund
	 		+ "&model.function.id=" + functionId
	 		+ "&model.fromDate=" + fromDate
	 		+ "&model.toDate=" + toDate,
	 		'',
	 		'resizable=yes,height=650,width=900,scrollbars=yes,left=30,top=30,status=no'
	 	);

	//document.balanceSheetReport.action="/services/EGF/report/balanceSheetReport-generateScheduleReportDetailed.action?showDropDown=false&model.period="+period+"&model.currency="+currency+"&model.financialYear.id="+financialYear+"&model.department.code="+department+"&model.fund.id="+fund+"&model.function.id="+functionId+"&model.asOndate="+asOndate;

	//document.balanceSheetReport.submit();
	return true;
    }
	return false;
}


function showSchedule(majorCode){
	window.open(
		'/services/EGF/report/balanceSheetReport-generateBalanceSheetSubReport.action'
		+ '?showDropDown=false'
		+ '&model.period=<s:property value="model.period"/>'
		+ '&model.currency=<s:property value="model.currency"/>'
		+ '&model.financialYear.id=<s:property value="model.financialYear.id"/>'
		+ '&model.department.code=<s:property value="model.department.code"/>'
		+ '&model.fund.id=<s:property value="model.fund.id"/>'
		+ '&model.fromdate=<s:property value="model.fromdate"/>'
		+ '&model.todate=<s:property value="model.todate"/>'
		+ '&model.function.id=<s:property value="model.function.id"/>'
		+ '&model.functionary.id=<s:property value="model.functionary.id"/>'
		+ '&model.field.id=<s:property value="model.field.id"/>'
		+ '&majorCode=' + majorCode,
		'',
		'height=650,width=900,scrollbars=yes,left=30,top=30,status=no'
	);
}

</script>
<style>
th.bluebgheadtd {
	padding: 0px;
	margin: 0px;
}

.extracontent {
	font-weight: bold;
	font-size: xx-small;
	color: #CC0000;
}
</style>
<div id="non-printable">
	<s:if test="%{hasErrors()}">
		<div id="actionErrorMessages" class="errorstyle">
			<s:actionerror />
			<s:fielderror />
		</div>
	</s:if>
	<s:form name="balanceSheetReport" action="balanceSheetReport"
		theme="simple">
		<div class="formmainbox">
			<div class="formheading"></div>
			<div class="subheadnew">
				<s:text name="report.balancesheet" />
			</div>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="10%" class="bluebox">&nbsp;</td>
					<td width="15%" class="bluebox"><s:text name="report.period" />:<span
						class="mandatory1">*</span></td>
					<td width="22%" class="bluebox"><s:select name="period"
							id="period"
							list="#{'Select':'---Choose---','Date':'Date','Yearly':'Yearly','Half Yearly':'Half Yearly'}"
							onclick="disableAsOnDate()" value="%{model.period}" /></td>
					<td class="bluebox" width="12%"><s:text
							name="report.financialYear" />:<span class="mandatory1">*</span></td>
					<td width="41%" class="bluebox"><s:select name="financialYear"
							id="financialYear" list="dropdownData.financialYearList"
							listKey="id" listValue="finYearRange" headerKey="0"
							headerValue="%{getText('lbl.choose.options')}" value="%{model.financialYear.id}" />
					</td>
				</tr>
				<tr id="dateRow">
					<td class="greybox">&nbsp;</td>
					<td class="greybox"><s:text name="report.fromDate"/>:</td>
					<td class="greybox">
						<s:textfield name="fromDate" id="fromDate" cssStyle="width:100px"/>
						<a href="javascript:show_calendar('balanceSheetReport.fromDate');"
						   style="text-decoration:none">
							<img src="/services/egi/resources/erp2/images/calendaricon.gif" border="0"/>
						</a>(dd/mm/yyyy)
					</td>
					<td class="greybox"><s:text name="report.toDate"/>:</td>
					<td class="greybox">
						<s:textfield name="toDate" id="toDate" cssStyle="width:100px"/>
						<a href="javascript:show_calendar('balanceSheetReport.toDate');"
						   style="text-decoration:none">
							<img src="/services/egi/resources/erp2/images/calendaricon.gif" border="0"/>
						</a>(dd/mm/yyyy)
					</td>
				</tr>
				<tr>
					<td class="bluebox">&nbsp;</td>
					<td class="bluebox"><s:text name="report.department" />:</td>
					<td class="bluebox"><s:select name="department"
							id="department" list="dropdownData.departmentList" listKey="code"
							listValue="name" headerKey="null" headerValue="%{getText('lbl.choose.options')}"
							value="model.department.code" /></td>
					<td class="bluebox"><s:text name="report.fund" />:</td>
					<td class="bluebox"><s:select name="fund" id="fund"
							list="dropdownData.fundList" listKey="id" listValue="name"
							headerKey="0" headerValue="%{getText('lbl.choose.options')}" value="model.fund.id" />
					</td>
				</tr>
				<tr>
					<td class="greybox">&nbsp;</td>

					<td class="greybox"><s:text name="report.function" />:</td>
					<td class="greybox"><s:select name="function" id="function"
							list="dropdownData.functionList" listKey="id" listValue="name"
							headerKey="0" headerValue="%{getText('lbl.choose.options')}"
							value="model.function.id" /></td>
							
							<td class="greybox"><s:text name="report.rupees" />:<span
						class="mandatory1">*</span></td>
					<td class="greybox"><s:select name="currency" id="currency"
							list="#{'Rupees':'Rupees','Thousands':'Thousands','Lakhs':'Lakhs'}"
							value="%{model.currency}" /></td>

							<%-- <td class="greybox"><s:text name="report.functionary" />:</td>
					<td class="greybox"><s:select name="functionary"
							id="functionary" list="dropdownData.functionaryList" listKey="id"
							listValue="name" headerKey="0" headerValue="%{getText('lbl.choose.options')}"
							value="model.functionary.id" /></td> --%>
							<td class="bluebox"></td>
					<td class="bluebox"></td>
				</tr>
				<%-- <tr>
					<td class="bluebox">&nbsp;</td>
					<td class="bluebox"><s:text name="report.field" />:</td>
					<td class="bluebox"><s:select name="field" id="field"
							list="dropdownData.fieldList" listKey="id" listValue="name"
							headerKey="0" headerValue="%{getText('lbl.choose.options')}" value="model.field.id" />
					</td>
					<td class="bluebox"></td>
					<td class="bluebox"></td>
				</tr> --%>
				<tr>
					<td></td>
				</tr>
			</table>
			</br>
			</br>
			<div align="left" class="mandatory1">
				*
				<s:text name="report.mandatory.fields" />
			</div>
			<div class="buttonbottom" style="padding-bottom: 10px;">
				<s:submit key="<s:text name='lbl.submit'/>"  method="printBalanceSheetReport"
					cssClass="buttonsubmit" onclick="return balanceSheetReportSubmit()" />
				<input name="button" type="button" class="buttonsubmit" id="button3"
					value="<s:text name='lbl.print'/>" onclick="window.print()" />&nbsp;&nbsp;  <input
					type="button" value="<s:text name='lbl.view.all.minor.schedules'/>" class="buttonsubmit" method="generateScheduleReport"
					onclick="return showAllSchedules()" /> &nbsp;&nbsp; <input
					type="button" value="<s:text name='lbl.view.all.schedules'/>" class="buttonsubmit" method="generateScheduleReportDetailed"
					onclick="return showAllSchedulesDetailed()" /> &nbsp;&nbsp;


			</div>
			<div align="left" class="extracontent">
				To print the report, please ensure the following settings:<br /> 1.
				Paper size: A4<br /> 2. Paper Orientation: Landscape <br />
			</div>
		</div>
	</s:form>
</div>
<script>
disableAsOnDate();
</script>
<%@ include file='balanceSheetReport-results.jsp'%>
