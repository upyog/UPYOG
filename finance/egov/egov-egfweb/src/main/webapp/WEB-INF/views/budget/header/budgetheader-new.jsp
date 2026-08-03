<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<style>
    /* Fix label alignment */
    .control-label {
        font-weight: 600;
        padding-top: 8px;
    }

    /* Center submit button */
    .submit-btn-container {
        text-align: center;
        margin-top: 25px;
        margin-bottom: 15px;
    }

    /* Panel spacing fix */
    .panel-body {
        padding: 25px;
    }

    .mandatory {
        color: red;
    }
</style>

<form:form role="form" action="create" modelAttribute="budgetRegister" id="budgetRegisterForm" class="form-horizontal"
    enctype="multipart/form-data">

    <div class="main-content">

        <!-- Error Message -->
        <c:if test="${not empty error and empty hideError}">
            <div class="alert alert-danger alert-dismissible fade in" role="alert">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                ${error}
            </div>
        </c:if>

        <!-- Success Message -->
        <c:if test="${not empty message}">
            <div class="alert alert-success alert-dismissible fade in" role="alert">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                ${message}
            </div>
        </c:if>

        <div class="row">
            <div class="col-md-12">

                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            <spring:message code="lbl.budget.register" text="Budget Register" />
                        </h3>
                    </div>

                    <div class="panel-body">

                        <!-- FORM TITLE -->
                        <h4 style="margin-bottom:20px;">
                            <spring:message code="lbl.createBudgetRegister" text="Create Budget Register" />
                        </h4>

                        <!-- Register Name -->
                        <div class="form-group">
                            <label class="col-sm-3 control-label">
                                <spring:message code="lbl.budgetRegister" text="Name" />
                                <span class="mandatory">*</span>
                            </label>

                            <div class="col-sm-6">
                                <form:input path="budgetRegisterName" id="budgetRegisterName" class="form-control"
                                    required="required" />
                            </div>
                        </div>

                        <!-- Current & Next FY -->
                        <div class="form-group">
                            <label class="col-sm-3 control-label">Current Financial Year</label>
                            <div class="col-sm-3">
                                <input type="text" class="form-control"
                                    value="${budgetRegister.currentFinancialYear.finYearRange}" readonly />
                                <form:hidden path="currentFinancialYear.id" />
                            </div>

                            <label class="col-sm-3 control-label">Next Financial Year</label>
                            <div class="col-sm-3">
                                <input type="text" class="form-control"
                                    value="${budgetRegister.financialYear.finYearRange}" readonly />
                                <form:hidden path="financialYear.id" />
                            </div>
                        </div>

                        <!-- Submit Button -->
                        <div class="submit-btn-container">
                            <c:if test="${empty error}">
                                <button type="submit" class="btn btn-primary" id="buttonNext">
                                    <spring:message code="lbl.submit" text="Next" />
                                </button>
                            </c:if>
                        </div>

                    </div>
                </div>

            </div>
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