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

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<style type="text/css">
.print-header {
    display: none;
}
.position_alert {
	position: fixed;
	z-index: 9999;
	top: 25px;
	right: 20px;
	background: #F2DEDE;
	padding: 10px 20px;
	border-radius: 5px;
}

.position_alert1 {
	position: fixed;
	z-index: 9999;
	top: 25px;
	right: 520px;
	background: #F2DEDE;
	padding: 10px 20px;
	border-radius: 5px;
}

.position_alert2 {
	position: fixed;
	z-index: 9999;
	top: 25px;
	right: 270px;
	background: #F2DEDE;
	padding: 10px 20px;
	border-radius: 5px;
}
@media print {
	@page {
		margin: 0;
	}
	.print-header {
		display: block;
		text-align: center;
		border-bottom: 2px solid #000;
		padding: 5px 0;
		position: relative; 
		margin-bottom: 5px;
		font-family: Arial, sans-serif;
	}
.header-center h2 {
        margin: 0;
        font-size: 16px;
        font-weight: bold;
       color: #003366 !important;
    }

    .header-center h1 {
        margin: 2px 0;
        font-size: 18px;
        font-weight: bold;
        color: #003366 !important;
    }
	.header-right {
		position: absolute;
		right: 20px;
		top: 10px;
		text-align: right;
		font-size: 14px;
	}
	.header-right p {
		margin: 0;
		padding: 0;
	}
	#printButton {
		display: none !important;
	}
	#closeButton {
		display: none !important;
	}
	header {
		display: none !important;
	}
	footer {
		display: none !important;
	}
	nav {
		display: none !important;
	}
	.navbar {
		display: none !important;
	}
	.navbar-header {
		display: none !important;
	}
	.sidebar {
		display: none !important;
	}
	.left-panel {
		display: none !important;
	}
	.commontopyellowbg {
		display: none !important;
	}
	.commontopbluebg {
		display: none !important;
	}
	.commontopbg {
		display: none !important;
	}
	.commonbottombg {
		display: none !important;
	}
	.footerfix {
		display: none !important;
	}
	.nav-tabs {
		display: none !important;
	}
	.position_alert {
		display: none !important;
	}
	.position_alert1 {
		display: none !important;
	}
	.position_alert2 {
		display: none !important;
	}
	@page {
		margin: 0;
	}
	body {
        margin: 0;
        padding: 0;
    }
}
</style>
<script type="text/javascript">
document.addEventListener("DOMContentLoaded", function () {
    var now = new Date();

    var date = now.toLocaleDateString('en-GB');
    var time = now.toLocaleTimeString();

    var d = document.getElementById("printDate");
    var t = document.getElementById("printTime");

    if (d) d.innerText = date;
    if (t) t.innerText = time;
});
</script>

<form:form name="contractorBillForm" role="form" action=""
	modelAttribute="egBillregister" id="egBillregister"
	class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">

	<div class="print-header">

		<div class="header-right">
			<p>
				Date: <span id="printDate"></span>
			</p>
			<p>
				Time: <span id="printTime"></span>
			</p>
		</div>

		<div class="header-center">
			<h1>Government of Jammu &amp; Kashmir</p>
			<h2>
				Housing and Urban Development<br />Department
			</h2>
			<h2>${ulbName}</h2>
		</div>

	</div>
	
	<div class="position_alert">
		<spring:message code="lbl.netpayable.amount" text="Net Payable Amount"/>
		: &#8377 <span id="contractorNetPayableAmount"><c:out
				value="${contractorNetPayableAmount}" default="0.0"></c:out></span>
	</div>
	<div class="position_alert1">
		<spring:message code="lbl.total.debit.amount" text="Total Debit Amount"/>
		: &#8377 <span id="contractorBillTotalDebitAmount"> <c:out
				value="${contractorBillTotalDebitAmount}" default="0.0"></c:out></span>
	</div>
	<div class="position_alert2">
		<spring:message code="lbl.total.deduction.amount" text="Total Deduction Amount"/>
		: &#8377 <span id="contractorBillTotalCreditAmount"> <c:out
				value="${contractorBillTotalCreditAmount}" default="0.0"></c:out></span>
	</div>
	
	<div>
		<spring:hasBindErrors name="egBillregister">
			<div class="alert alert-danger col-md-10 col-md-offset-1">
				<form:errors path="*" />
				<br />
			</div>
		</spring:hasBindErrors>
	</div>
	<input type="hidden" id="id" value="${egBillregister.id }" />
	<input type="hidden" name="mode" id="mode" value="${mode }" />
	<input type="hidden" name="budgetDetails" id="budgetDetails"
		value="${budgetDetails}" />
	<%--<form:hidden path="budgetDetails" id="budgetDetails" class="budgetDetail" value="${budgetDetails}"/>--%>
	<form:hidden path="billamount" id="billamount" class="billamount"
		value="${egBillregister.billamount }" />
	<form:hidden path="" name="netPayableAmount" id="netPayableAmount"
		value="${netPayableAmount}" />
	<div class="panel-title text-center" style="color: green;">
		<c:out value="${message}" />
		<br />
	</div>

	<jsp:include page="contractorbill-view-header.jsp" />
	<jsp:include page="contractorbill-view-accountdetails.jsp" />
	<jsp:include page="contractorbill-view-subledgeraccountdetails.jsp" />
	<c:if
		test="${egBillregister.documentDetail != null &&  !egBillregister.documentDetail.isEmpty()}">
		<jsp:include page="billdocument-upload.jsp" />
	</c:if>
	<%-- <jsp:include page="contractorbill-budgetdetails.jsp"/> --%>
	<c:if test="${!workflowHistory.isEmpty() && mode != 'readOnly'}">
		<jsp:include page="../common/commonworkflowhistory-view.jsp"></jsp:include>
	</c:if>
	<c:if test="${mode != 'readOnly'}">
		<jsp:include page="../common/commonworkflowmatrix.jsp" />
		<div class="buttonbottom" align="center">
			<jsp:include page="../common/commonworkflowmatrix-button.jsp" />
		</div>
	</c:if>
	<c:if test="${mode == 'readOnly'}">
		<div class="row">
			<div class="col-sm-12 text-center">
				<input type="button" name="printButton" id="printButton"
					value='<spring:message code="lbl.print" text="Print"/>'
					class="btn btn-primary" style="margin-right: 10px;"
					onclick="window.print();" /> <input type="submit"
					name="closeButton" id="closeButton"
					value='<spring:message code="lbl.close" text="Close"/>'
					class="btn btn-default" onclick="window.close();" />
			</div>
		</div>
	</c:if>

</form:form>
<script
	src="<cdn:url value='/resources/app/js/contractorbill/viewcontractorbill.js?rnd=${app_release_no}'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
