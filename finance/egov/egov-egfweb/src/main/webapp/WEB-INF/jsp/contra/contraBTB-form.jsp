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


<tr>
	<td class="bluebox" colspan="12">
		<table width="100%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<th class="bluebgheadtd" width="100%" colspan="5"><strong
					style="font-size: 15px;"><s:text
							name="contra.fromBank.header" /></strong></th>
			</tr>
		</table>
	</td>
</tr>
<%@include file="../voucher/vouchertrans-filter-new.jsp"%>
<input type="hidden" id="csrfTokenValue" name="${_csrf.parameterName}" value="${_csrf.token}"/>
<tr>
	<td class="bluebox w5">&nbsp;</td>
	<egov:ajaxdropdown id="fromBankId" fields="['Text','Value']"
		dropdownId="fromBankId" url="/voucher/common-ajaxLoadBanks.action" />
	<td class="greybox w15"><s:text name="contra.fromBank" /> <span
		class="greybox"><span class="mandatory1">*</span></span></td>
	<s:hidden name="temp" value="contraBean.fromBankId" />
	<td class="greybox w25"><s:select name="contraBean.fromBankId"
			id="fromBankId" list="%{fromBankBranchMap}" headerKey="-1"
			headerValue="%{getText('lbl.choose.options')}" onChange="loadFromAccNum(this);" escapeHtml="false"/></td>
	<egov:ajaxdropdown id="fromAccountNumber" fields="['Text','Value']"
		dropdownId="fromAccountNumber"
		url="/voucher/common-ajaxLoadAccountNumbers.action" />
	<td class="bluebox w5">&nbsp;</td>
	<td class="greybox w15"><s:text name="contra.fromBankAccount" /> <span
		class="greybox"><span class="mandatory1">*</span></span></td>
	<td class="greybox w25"><s:select name="contraBean.fromBankAccountId"
			value="%{contraBean.fromBankAccountId}" id="fromAccountNumber"
			list="dropdownData.fromAccNumList" listKey="id"
			listValue="accountnumber" headerKey="-1" headerValue="%{getText('lbl.choose.options')}"
			onChange="populatefromNarration(this);loadFromBalance(this)" /> <s:textfield
			name="fromAccnumnar" id="fromAccnumnar" value="%{fromAccnumnar}"
			readonly="true" tabindex="-1"  escapeHtml="false" class="w100"/></td>
</tr>

<tr>
	<td class="bluebox w5">&nbsp;</td>
	<egov:updatevalues id="fromBankBalance" fields="['Text']"
		url="/payment/payment-ajaxGetAccountBalance.action" />
	<td class="bluebox w15"><s:text name="contra.fromBankBalance" />
		(Rs.) <span class="bluebox"><span class="mandatory1">*</span></span></td>
	<td class="bluebox w25"><s:textfield name="contraBean.fromBankBalance"
			id="fromBankBalance" readonly="true" tabindex="-1"
			cssStyle="text-align:right" class="w100" /></td>
	<td class="bluebox"></td>
	<td class="bluebox"></td>
</tr>
<tr>
	<td class="bluebox" colspan="12">
		<table width="100%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<th class="bluebgheadtd" width="100%" colspan="5"><strong
					style="font-size: 15px;"><s:text
							name="contra.toBank.header" /></strong></th>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td class="bluebox w5">&nbsp;</td>
	<s:if test="%{shouldShowHeaderField('fund')}">
		<td class="greybox w15"><s:text name="voucher.fund" /><span
			class="mandatory1">*</span></td>
		<td class="greybox w25"><s:select name="contraBean.toFundId"
				id="toFundId" list="dropdownData.fundList" listKey="id"
				listValue="name" onChange="loadToBank(this);checkInterFund();"
				headerKey="" headerValue="%{getText('lbl.choose.options')}" /></td>
	</s:if>
	<s:if test="%{shouldShowHeaderField('department')}">
		<td class="bluebox w5">&nbsp;</td>
		<td id="interFundRow1" style="visibility: hidden" class="greybox w15"><s:text
				name="voucher.department" /> <s:if
				test="%{isFieldMandatory('department')}">
				<span class="bluebox"><span class="mandatory1">*</span></span>
			</s:if></td>
		<td id="interFundRow2" style="visibility: hidden" class="greybox w25"><s:select
				name="contraBean.toDepartment" id="contraBean.toDepartment"
				list="dropdownData.departmentList" listKey="code" listValue="name"
				headerKey="" headerValue="%{getText('lbl.choose.options')}"
				value="voucherHeader.vouchermis.departmentcode"
				onChange="populateApproverDept(this);" class="w100" /></td>
	</s:if>
</tr>
<tr>
	<td class="bluebox w5">&nbsp;</td>
	<egov:ajaxdropdown id="toBankId" fields="['Text','Value']"
		dropdownId="toBankId" url="/voucher/common-ajaxLoadBanks.action" />

	<td class="bluebox w15"><s:text name="contra.toBank" /> <span
		class="bluebox"><span class="mandatory1">*</span></span></td>
	<td class="bluebox w25"><s:select name="contraBean.toBankId"
			id="toBankId" list="%{toBankBranchMap}" headerKey="-1"
			headerValue="%{getText('lbl.choose.options')}" onChange="loadToAccNum(this);" escapeHtml="false"/></td>
	<egov:ajaxdropdown id="toAccountNumber" fields="['Text','Value']"
		dropdownId="toAccountNumber"
		url="/voucher/common-ajaxLoadAccountNumbers.action" />
	<td class="bluebox w5">&nbsp;</td>
	<td class="bluebox w15"><s:text name="contra.toBankAccount" /> <span
		class="bluebox"><span class="mandatory1">*</span></span></td>
	<td class="bluebox w25"><s:select name="contraBean.toBankAccountId"
			id="toAccountNumber" list="dropdownData.toAccNumList" listKey="id"
			listValue="accountnumber" headerKey="-1" headerValue="%{getText('lbl.choose.options')}"
			onChange="populatetoNarration(this);loadToBalance(this)" escapeHtml="false" width="50%"/> <s:textfield
			name="toAccnumnar" id="toAccnumnar" value="%{toAccnumnar}"
			readonly="true" tabindex="-1" escapeHtml="false" class="w100" /></td>
</tr>

<tr>
	<td class="bluebox w5">&nbsp;</td>
	<egov:updatevalues id="toBankBalance" fields="['Text']"
		url="/payment/payment-ajaxGetAccountBalance.action" />
	<td class="greybox w15"><s:text name="contra.toBankBalance" /> (Rs.)
		<span class="greybox"><span class="mandatory1">*</span></span></td>
	<td class="greybox 25"><s:textfield name="contraBean.toBankBalance"
			id="toBankBalance" readonly="true" tabindex="-1"
			cssStyle="text-align:right" class="w100"/></td>
	<td class="greybox"></td>
	<td class="greybox"></td>
</tr>
<tr id="interFundRow3" style="display: none">
	<td class="bluebox w5">&nbsp;</td>
	<td class="greybox w15"><s:text name="lbl.source.inter.fund.code" /><span class="mandatory1">*</span></td>
	<td class="greybox w25"><s:select
			name="contraBean.sourceGlcode" id="sourceGlcode"
			list="dropdownData.interFundList" listKey="glcode"
			listValue="glcode+'-'+name" headerKey="-1"
			headerValue="%{getText('lbl.choose.options')}" /></td>
	<td class="bluebox w5">&nbsp;</td>
	<td class="greybox w15"><s:text name="lbl.destination.inter.fund.code" /><span class="mandatory1">*</span></td>
	<td class="greybox w25"><s:select
			name="contraBean.destinationGlcode" id="destinationGlcode"
			list="dropdownData.interFundList" listKey="glcode"
			listValue="glcode+'-'+name" headerKey="-1"
			headerValue="%{getText('lbl.choose.options')}" /></td>
</tr>


<tr>
	<td class="bluebox w5">&nbsp;</td>
	<td class="bluebox w15"><s:text name="contra.modeOfCollection" /> <span
		class="bluebox"><span class="mandatory1">*</span></span></td>
	<td class="bluebox w25"><s:radio name="contraBean.modeOfCollection"
			id="modeOfCollection" list="%{modeOfCollectionMap}"
			onclick="toggleChequeAndRefNumber(this)"/></td>
	<td class="bluebox"></td>
	<td class="bluebox"></td>
</tr>

<tr id="chequeGrid">
	<td class="bluebox w5">&nbsp;</td>
	<td class="greybox w15"><span id="mdcNumber"><s:text
				name="contra.refNumber" /></span> <span class="greybox"><span
			class="mandatory1">*</span></span></td>
	<td class="greybox w25"><s:textfield name="contraBean.chequeNumber"
			id="chequeNum" value="%{contraBean.chequeNumber}" onblur="validateChequeNumber(this)" onkeyup="decimalvalue(this)" class="w100"/>
				<span>
					<font style='color: red;'>
						<p class="error-block" id="chequeNumberlblError"></p>
					</font>
				</span>		
	</td>
	<td class="bluebox w5">&nbsp;</td>
	<td class="greybox w15"><span id="mdcDate"><s:text
				name="contra.refDate" /></span></td>
	<td class="greybox w25"><s:textfield id="chequeDate"
			name="contraBean.chequeDate" data-date-end-date="0d"
			onkeyup="DateFormat(this,this.value,event,false,'3')"
			placeholder="DD/MM/YYYY" class="form-control datepicker w100"
			data-inputmask="'mask': 'd/m/y'" /></td>

</tr>

<tr>
	<td class="bluebox w5">&nbsp;</td>
	<td class="bluebox w15"><s:text name="contra.amount" /> (Rs.) <span
		class="bluebox"><span class="mandatory1">*</span></span></td>
	<td class="bluebox w25"><s:textfield name="amount" id="amount"
			cssStyle="text-align:right" class="w100"/></td>
	<td class="bluebox"></td>
	<td class="bluebox"></td>
</tr>

<tr>
	<td class="bluebox w5">&nbsp;</td>
	<td class="greybox w15"><s:text name="voucher.narration" /></td>
	<td class="greybox w25" colspan="3"><s:textarea name="description"
			id="description" class="w100"/></td>
	<td class="greybox"></td>
	<td class="greybox"></td>
</tr>

<script>
	var fund_map = new Array();
	var i=0;
	<s:iterator var="f" value="%{dropdownData.fundList}" status="stat">
		fund_map[i++]= '<s:property value="%{id}"/>'+"_"+'<s:property value="%{chartofaccountsByPayglcodeid.glcode}"/>';
	</s:iterator>	
	
	
</script>

