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
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/javascript/voucherHelper.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/javascript/contraBTBHelper.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/ajaxCommonFunctions.js?rnd=${app_release_no}"></script>

<style>
/* Hide header normally */
.print-header {
    display: none;
}

/* PRINT MODE */
@media print {

    /* FORCE show all form content */
    form,
    .formmainbox,
    table,
    tbody,
    tr,
    td,
    th,
    div,
    span {
        display: block !important;
        visibility: visible !important;
    }

    table {
        display: table !important;
    }

    tr {
        display: table-row !important;
    }

    td, th {
        display: table-cell !important;
    }

    .print-header {
        display: block;
        text-align: center;
        font-size: 20px;
        font-weight: bold;
        color: #1F4E79;
        margin-bottom: 5px;
        margin-top:-5px
    }

    /* Hide buttons */
    .no-print,
    #printButton,
    #closeButton,
    input[type="button"],
    button {
        display: none !important;
    }

    /* Hide mandatory stars */
    .mandatory1,
    span.mandatory1 {
        display: none !important;
    }

    /* Clean form look */
    input[type="text"],
    textarea,
    select {
        border: none !important;
        background: transparent !important;
        box-shadow: none !important;
    }
    
    /* Remove dropdown arrow */
    select {
        -webkit-appearance: none !important;
        -moz-appearance: none !important;
        appearance: none !important;
        background: none !important;
        padding-right: 0 !important;
        border: none !important;
    }

    /* Show ---Choose--- when unselected */
    select:has(option:selected[value='-1'])::after,
    select:has(option:selected[value=''])::after {
        content: attr(data-print-default);
        display: inline-block;
        color: #000;
    }

    select:has(option:selected[value='-1']),
    select:has(option:selected[value='']) {
        color: transparent;
    }
    
    /* Remove extra narration box height */
    textarea {
        height: auto !important;
        min-height: auto !important;
        max-height: none !important;
        resize: none !important;
        border: none !important;
        overflow: visible !important;
        background: transparent !important;
        padding: 0 !important;
        line-height: 1.2 !important;
    }
    
    textarea {
        font-size: 13px;
        font-weight: normal;
    }

    td.greybox textarea {
        display: inline-block;
        vertical-align: middle;
    }
    .datepicker,
    .ui-datepicker-trigger {
        display: none !important;
    }
     /* Hide calendar icon (date picker image & link) */
    a[href*="show_calendar"],
    img[src*="calendar"] {
        display: none !important;
    }
}
</style>
</head>
<body onload="onloadTask_view()">
    <div class="print-header">
    Government of Jammu &amp; Kashmir<br/>
    <strong>Housing and Urban Development Department</strong>
   </div>
	<s:form action="contraBTB" theme="simple" name="cbtbform">
		<s:push value="model">
			<jsp:include page="../budget/budgetHeader.jsp">
				<jsp:param value="Bank to Bank Transfer" name="heading" />
			</jsp:include>
			<div class="formmainbox">
				<div class="formheading" />
				<div class="subheadnew">Create Bank to Bank Transfer</div>
				<div id="listid" style="display: block">
					<br />
				</div>

				<div align="center">
					<font style='color: red;'>
						<p class="error-block" id="lblError"></p>
					</font> <span class="mandatory1"> <s:actionerror /> <s:fielderror />
						<s:actionmessage />
					</span>
					<table border="0" width="100%" cellspacing="0" cellpadding="0">
						<tr>
							<td class="bluebox" width="10%"></td>
							<td class="bluebox" width="22%"><s:text
									name="voucher.number" /></td>
							<td class="bluebox" width="22%"><s:textfield
									name="voucherNumber" id="voucherNumber" /></td>
							<s:hidden name="id" />
							<td class="bluebox" width="18%"><s:text name="voucher.date" /><span
								class="mandatory1">*</span></td>
							<td class="bluebox" width="38%"><input type="text"
								name="voucherDate"
								onkeyup="DateFormat(this,this.value,event,false,'3')"
								value='<s:date name="voucherDate" format="dd/MM/yyyy"/>' /> <a
								href="javascript:show_calendar('cbtbform.voucherDate');"
								style="text-decoration: none">&nbsp;<img tabIndex="-1"
									src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></A>(dd/mm/yyyy)</td>
						</tr>
						<%@include file="contraBTB-form.jsp"%>

					</table>
				</div>
				<!-- <div class="buttonbottom">
					<input type="button" id="closeButton" value="Close"
						onclick="javascript:window.close()" class="button" />
				</div> -->
				
				<div class="buttonbottom no-print">
    <input type="button"
           id="printButton"
           value="Print"
           class="button"
           onclick="window.print();" />

    <input type="button"
           id="closeButton"
           value="Close"
           onclick="javascript:window.close()"
           class="button" />
</div>

				<input type="hidden" id="voucherTypeBean.voucherName"
					name="voucherTypeBean.voucherName" value="BankToBank" /> <input
					type="hidden" id="voucherTypeBean.voucherType"
					name="voucherTypeBean.voucherType" value="Contra" /> <input
					type="hidden" id="voucherTypeBean.voucherNumType"
					name="voucherTypeBean.voucherNumType" value="Contra" /> <input
					type="hidden" id="voucherTypeBean.cgnType"
					name="voucherTypeBean.cgnType" value="BTB" />
			</div>
		</s:push>
	</s:form>
	<script type="text/javascript">
function onloadTask_view() {

    // Detect print mode
    if (window.matchMedia && window.matchMedia('print').matches) {
        return; // DO NOTHING during print
    }

    var srcFund = '<s:property value="contraBean.fromFundId"/>';
    var desFund = '<s:property value="contraBean.toFundId"/>';

    if (srcFund == desFund) {
        document.getElementById("interFundRow").style.visibility = "hidden";
    } else {
        document.getElementById("interFundRow").style.visibility = "visible";
    }

    // ⚠️ THIS causes blank print
    /* disableControls(0, true); */
    document.getElementById("closeButton").disabled = false;
}
</script>
</body>
</html>
