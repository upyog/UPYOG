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


  <html>
  <head>
  <%@ include file="/includes/taglibs.jsp"%>
  <%@ page language="java"%>

  <script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
  <script type="text/javascript" src="/services/EGF/resources/javascript/voucherHelper.js?rnd=${app_release_no}"></script>
  <script type="text/javascript" src="/services/EGF/resources/javascript/contraBTBHelper.js?rnd=${app_release_no}"></script>
  <script type="text/javascript" src="/services/EGF/resources/javascript/calendar.js?rnd=${app_release_no}"></script>
  <script language="javascript" src="../resources/javascript/jsCommonMethods.js?rnd=${app_release_no}"></script>
  <script type="text/javascript" src="/services/EGF/resources/javascript/dateValidation.js?rnd=${app_release_no}"></script>
  <script type="text/javascript" src="/services/EGF/resources/javascript/ajaxCommonFunctions.js?rnd=${app_release_no}"></script>
  
  <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
  
  <script type="text/javascript">
  $(document).ready(function() {
  
	  // Initialize voucher date
	  $("#voucherDate").datepicker().datepicker("setDate", new Date());
  
	  // Show interfund rows if funds differ
	  var srcFund = '<s:property value="contraBean.fromFundId"/>';
	  var desFund = '<s:property value="contraBean.toFundId"/>';
	  if(srcFund && desFund && srcFund !== desFund) {
		  $("#interFundRow1, #interFundRow2, #interFundRow3").css("visibility", "visible");
	  }
  
	  // Handle transaction messages
	  var button = '<s:property value="button"/>';
	  if(button && $("#Errors").html() === '') {
		  bootbox.alert('<s:text name="contra.transaction.succcess"/>', function() {
			  if(button === "Save_Close") window.close();
			  else if(button === "Save_View") {
				  var vhId = '<s:property value="vhId"/>';
				  document.forms[0].action = "${pageContext.request.contextPath}/voucher/preApprovedVoucher-loadvoucherview.action?vhid=" + vhId;
				  document.forms[0].submit();
			  }
			  else if(button === "Save_New") {
				  document.forms[0].button.value = '';
				  document.forms[0].action = "contraBTB-newform.action";
				  document.forms[0].submit();
			  }
		  });
	  }
  
	  // Cheque grid visibility
	  var modeOfCollection = '<s:property value="contraBean.modeOfCollection"/>';
	  var showChequeNumber = '<s:property value="egovCommon.isShowChequeNumber()"/>' === 'true';
	  if(showChequeNumber || modeOfCollection !== 'cheque') $("#chequeGrid").css("visibility", "visible");
	  else $("#chequeGrid").css("visibility", "hidden");
  
	  // Bank balance mandatory check
	  if(!$('#bankBalanceMandatory').val()) {
		  $('#lblError').html('<s:text name="msg.bank.bal.mendatory.param.not.defined" />');
	  }
  
	  <s:if test="%{getUserDepartment()!=null}">
    <script type="text/javascript">
        $("#vouchermis\\\.departmentid").val('<s:property value="getUserDepartment()"/>');
    </script>
</s:if>

  });
  
  // Toggle cheque/RTGS fields
  function toggleChequeAndRefNumber(obj) {
	  $('#chequeNum').val('');
	  $('#chequeNumberlblError').html('');
	  if(obj.value === "RTGS/NEFT") {
		  $("#chequeGrid").css("visibility", "visible");
		  $("#mdcNumber").html('<s:text name="contra.refNumber" />');
		  $("#mdcDate").html('<s:text name="contra.refDate" />');
	  } else {
		  var showChequeNumber = '<s:property value="egovCommon.isShowChequeNumber()"/>' === 'true';
		  $("#chequeGrid").css("visibility", showChequeNumber ? "visible" : "hidden");
		  $("#mdcNumber").html('<s:text name="contra.chequeNumber" />');
		  $("#mdcDate").html('<s:text name="contra.chequeDate" />');
	  }
  }
  
  // Decimal input validation
  function decimalvalue(obj){
	  var regexp_decimalvalue = /[^0-9.]/g;
	  if($('#modeOfCollectioncheque').is(':checked')){
		  $(obj).val($(obj).val().replace(regexp_decimalvalue,''));
		  if($(obj).val().length >= 7) $(obj).val($(obj).val().substring(0,6));
	  }
  }
  
  // Cheque number validation
  function validateChequeNumber(obj){
	  if($('#modeOfCollectioncheque').is(':checked')){
		  $('#chequeNumberlblError').html('');
		  if(obj.value.length != 6){
			  $('#chequeNumberlblError').html('<s:text name="msg.cheque.number.must.be.six.digits" />');
		  }
	  }
  }
  </script>
  
  </head>
  <body>
  <s:form action="contraBTB" theme="simple" name="cbtbform">
  <s:push value="model">
  
  <jsp:include page="../budget/budgetHeader.jsp">
	  <jsp:param value="Bank to Bank Transfer" name="heading" />
  </jsp:include>
  
  <div class="formmainbox">
	  <div class="formheading"></div>
	  <div class="subheadnew"><s:text name="lbl.create.bank.to.bank.transfer"/> </div>
	  <div id="listid" style="display: block"><br/></div>
	  <div align="center"><font style='color: red;'><p class="error-block" id="lblError"></p></font></div>
	  <span class="mandatory1">
		  <div id="Errors">
			  <s:actionerror />
			  <s:fielderror />
		  </div>
		  <s:actionmessage />
	  </span>
  
	  <table border="0" width="100%" cellspacing="0" cellpadding="0">
		  <tr>
			  <td class="bluebox"></td>
			  <s:if test="%{shouldShowHeaderField('vouchernumber')}">
				  <td class="bluebox" width="22%"><s:text name="voucher.number" /><span class="mandatory1">*</span></td>
				  <td class="bluebox" width="22%"><s:textfield name="voucherNumber" id="voucherNumber" /></td>
			  </s:if>
			  <s:hidden name="id" />
			  <td class="bluebox" width="18%"><s:text name="voucher.date" /><span class="mandatory1">*</span></td>
			  <td class="bluebox" width="34%">
				  <s:textfield id="voucherDate" name="voucherDate" data-date-end-date="0d" value="{voucherDate}" onkeyup="DateFormat(this,this.value,event,false,'3')" placeholder="DD/MM/YYYY" class="form-control datepicker" data-inputmask="'mask': 'd/m/y'" />
			  </td>
			  <td class="bluebox"></td>
			  <td class="bluebox"></td>
		  </tr>
		  <%@include file="contraBTB-form.jsp"%>
	  </table>
  </div>
  
  <div class="mandatory1" align="left">* <s:text name="lbl.mendatory.field"/> </div>
  <br/><br/>
  <%@include file="../voucher/SaveButtons.jsp"%>
  
  <input type="hidden" id="name" name="name" value="BankToBank" />
  <input type="hidden" id="type" name="type" value="Contra" />
  <s:hidden id="bankBalanceMandatory" name="bankBalanceMandatory" value="%{isBankBalanceMandatory()}" />
  <s:hidden id="startDateForBalanceCheckStr" name="startDateForBalanceCheckStr" value="%{startDateForBalanceCheckStr}" />
  
  </s:push>
  </s:form>
  </body>
  </html>
  