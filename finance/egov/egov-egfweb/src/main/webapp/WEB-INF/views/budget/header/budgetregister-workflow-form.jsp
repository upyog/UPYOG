<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="/WEB-INF/tags/fmt.tld" prefix="fmt" %>


<form:form role="form" action="../update" modelAttribute="budgetRegister" id="budgetRegisterForm"
           cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">


    <div class="main-content">

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                ${error}
            </div>
        </c:if>

        <c:if test="${not empty message}">
            <div class="alert alert-success alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                ${message}
            </div>
        </c:if>


        <div class="panel panel-default">

            <div class="panel-heading clearfix">
                <h4 class="panel-title pull-left" style="padding-top:6px;">Budget Register</h4>

            </div>

            <div class="panel-body">

                <form:hidden path="budgetRegisterNumber" value="${budgetRegister.budgetRegisterNumber}" />

                <div class="table-responsive">
                    <table class="table table-striped table-bordered">

                        <thead>
                        <tr>
                            <th>Register No.</th>
                            <th>Name</th>
                            <th>Financial Year</th>
                            <th>Status</th>
                            <th>Created Date</th>
                        </tr>
                        </thead>
                        <tr>
                            <td><c:out value="${budgetRegister.budgetRegisterNumber}" /></td>
                            <td><c:out value="${budgetRegister.budgetRegisterName}" /></td>
                            <td><c:out value="${budgetRegister.financialYear.finYearRange}" /></td>
                            <td>
                                <c:out value="${budgetRegister.status.description}" />
                            </td>
                            <td>
                                <fmt:formatDate value="${budgetRegister.createdDate}" pattern="dd-MMM-yyyy hh:mm a" />
                            </td>
                        </tr>

                    </table>

                </div>

                <div class="panel-body">

                    <div class="pull-right" style="margin-right: 15px;" >
                        <a href="${pageContext.request.contextPath}/budget/complete/${budgetRegister.id}/view"  class="btn btn-primary btn-sm" onclick="window.open(this.href, '_blank', 'width=1080,height=700'); return false;" >
                            View Complete Budget
                        </a>
                    </div>

                </div>

                <c:if test="${not empty showWorkflow}">
                    <jsp:include page="../../common/commonworkflowmatrix-expensebill.jsp"/>
                    <div class="buttonbottom" align="center">
                        <jsp:include page="../../common/commonworkflowmatrix-button.jsp"/>
                    </div>
                </c:if>


            </div>

        </div>

</form:form>

<script>
    $('#buttonNext').click(function (e) {
      if ($('form').valid()) {} else {
        e.preventDefault();
      }
    });
</script>

<script
        src="<cdn:url value='/resources/app/js/budget/register/budgetregister-workflow.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
