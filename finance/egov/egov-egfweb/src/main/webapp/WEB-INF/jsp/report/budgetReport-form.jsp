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
  <%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
  
  <body>
  <div class="formmainbox">
	  <div class="formheading"></div>
	  <div class="subheadnew">Working Budget Report</div>
  
	  <s:form action="budgetReport" theme="simple" name="budgetReport">
		  <table width="100%" cellpadding="0" cellspacing="0" border="0">
			  <tr>
				  <td class="greybox" width="10%">Financial Year:<span class="bluebox"><span class="mandatory">*</span></span></td>
				  <td class="greybox">
					  <s:select name="budgetDetail.budget.financialYear.id" 
								id="financialYear"
								list="dropdownData.financialYearList" listKey="id"
								listValue="finYearRange" headerKey="-1"
								headerValue="----Choose----"
								value="%{budgetDetail.budget.financialYear.id}" />
				  </td>
				  <td class="greybox" width="10%">Type:<span class="bluebox"><span class="mandatory">*</span></span></td>
				  <td class="greybox">
					  <s:radio name="budgetDetail.budget.isbere"
							   list="dropdownData.isbereList"
							   value="%{budgetDetail.budget.isbere}" />
				  </td>
			  </tr>
			  <tr>
				  <td class="bluebox" width="10%">Budget:<span class="bluebox"><span class="mandatory">*</span></span></td>
				  <td class="bluebox">
					  <div id="budgetData">
						  <s:select name="budgetDetail.budget.id" id="budget"
									list="budgetList" listKey="id" listValue="name" 
									headerKey="-1" headerValue="----Choose----" 
									value="%{budget.id}" />
					  </div>
				  </td>
				  <td class="bluebox" width="10%">Department:</td>
				  <td class="bluebox">
					  <s:select name="budgetDetail.executingDepartment.id"
								id="executingDepartment"
								list="dropdownData.executingDepartmentList" listKey="id"
								listValue="name" headerKey="-1" headerValue="----Choose----"
								value="%{executingDepartment.id}" />
				  </td>
			  </tr>
			  <tr>
				  <td class="greybox" width="10%">Budget Head:</td>
				  <td class="greybox">
					  <s:select name="budgetDetail.budgetGroup.id" id="budgetGroup"
								list="dropdownData.budgetGroupList" listKey="id"
								listValue="name" headerKey="-1" headerValue="----Choose----"
								value="%{budgetGroup.id}" />
				  </td>
				  <td class="greybox" width="10%">&nbsp;</td>
				  <td class="greybox">&nbsp;</td>
			  </tr>
		  </table>
		  <br /><br />
		  <div class="buttonbottom">
			  <s:submit value="Submit" method="generateReport"
						onclick="return validateData();" cssClass="buttonsubmit" />
			  <s:reset name="button" type="submit" cssClass="button" id="button" value="Cancel" />
			  <input type="button" value="Close" onclick="window.close()" class="button" />
		  </div>
	  </s:form>
  </div>
  
  <!-- JS at the end so DOM is ready -->
  <script>
  window.onload = function() {
	  var callback = {
		  success: function(o){
			  if(o.responseText != '')
				  document.getElementById('budgetData').innerHTML = o.responseText;
		  },
		  failure: function(o) {}
	  };
  
	  function getBereValue() {
		  var radios = document.getElementsByName('budgetDetail.budget.isbere');
		  for(var i=0; i<radios.length; i++){
			  if(radios[i].checked) return radios[i].value;
		  }
		  return '';
	  }
  
	  window.populateBudgets = function() {
		  var finYear = document.getElementById('financialYear');
		  if(!finYear || finYear.value == -1) return;
  
		  var bereValue = getBereValue();
		  if(!bereValue) return;
  
		  var url = '/EGF/report/budgetReport!ajaxLoadBudgets.action?budgetDetail.budget.financialYear.id='
					+ finYear.value + '&budgetDetail.budget.isbere=' + bereValue;
  
		  YAHOO.util.Connect.asyncRequest('POST', url, callback, null);
	  };
  
	  window.validateData = function(){
		  var finYear = document.getElementById('financialYear');
		  if(!finYear || finYear.value == -1){
			  bootbox.alert("Please select a Financial Year");
			  return false;
		  }
		  var budget = document.getElementById('budget');
		  if(!budget || budget.value == -1){
			  bootbox.alert("Please select a Budget");
			  return false;
		  }
		  return true; 
	  };
  
	  window.exportXls = function(){
		  var finYear =  document.getElementById('financialYear').value;
		  var budget =  document.getElementById('budget').value;
		  var department =  document.getElementById('executingDepartment').value;
		  var budgetGroup =  document.getElementById('budgetGroup').value;
		  window.open('/EGF/report/budgetReport!exportXls.action?budgetDetail.budget.financialYear.id='
					  +finYear+'&budgetDetail.budget.id='+budget+'&budgetDetail.budgetGroup.id='
					  +budgetGroup+'&budgetDetail.executingDepartment.id='+department,'','resizable=yes,height=650,width=900,scrollbars=yes,left=30,top=30,status=no');
	  };
  
	  window.exportPdf = function(){
		  var finYear =  document.getElementById('financialYear').value;
		  var budget =  document.getElementById('budget').value;
		  var department =  document.getElementById('executingDepartment').value;
		  var budgetGroup =  document.getElementById('budgetGroup').value;
		  window.open('/EGF/report/budgetReport!exportPdf.action?budgetDetail.budget.financialYear.id='
					  +finYear+'&budgetDetail.budget.id='+budget+'&budgetDetail.budgetGroup.id='
					  +budgetGroup+'&budgetDetail.executingDepartment.id='+department,'','resizable=yes,height=650,width=900,scrollbars=yes,left=30,top=30,status=no');
	  };
  
	  // Attach onchange events after DOM ready
	  var finYearSelect = document.getElementById('financialYear');
	  if(finYearSelect) finYearSelect.onchange = populateBudgets;
  
	  var radios = document.getElementsByName('budgetDetail.budget.isbere');
	  for(var i=0; i<radios.length; i++) radios[i].onchange = populateBudgets;
  };
  </script>
  