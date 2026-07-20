<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="/WEB-INF/tags/fmt.tld" prefix="fmt" %>


<div class="main-content">
    <div class="row">
        <div class="col-md-12">

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
                    <h4 class="panel-title pull-left" style="padding-top:6px;">Budget Registers</h4>
                    <!--<div class="pull-right">
                        <a href="${pageContext.request.contextPath}/budget/register/new" class="btn btn-primary btn-sm">
                            Create New
                        </a>
                    </div>-->
                </div>

                <div class="panel-body">


                    <hr/>

                    <div class="table-responsive">
                        <table class="table table-striped table-bordered">
                            <thead>
                            <tr>
                                <th>Register No.</th>
                                <th>Name</th>
                                <th>Financial Year</th>
                                <th>Status</th>
                                <th>Created Date</th>
                                <th class="text-center">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="budgetRegister" items="${budgetRegisters}">
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
                                    <td>
                                        <a href="${pageContext.request.contextPath}/budget/register/workflow/view/${budgetRegister.budgetRegisterNumber}" class="btn btn-primary btn-sm">
                                            View
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>


                            </tbody>
                        </table>
                    </div>


                </div> <!-- panel-body -->
            </div> <!-- panel -->

        </div>
    </div>
</div>
