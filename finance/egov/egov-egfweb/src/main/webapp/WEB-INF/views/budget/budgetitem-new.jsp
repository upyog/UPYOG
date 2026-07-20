<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/includes/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>






<form:form role="form" action="../form" modelAttribute="function" id="budgetItemFunction"
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

    <div class="row">
      <div class="col-md-12">
        <div class="panel panel-primary" data-collapsed="0">
          <div class="panel-heading">
            <div class="panel-title">
              <spring:message code="lbl.budget.input" text="Budget Input" />
            </div>
          </div>

          <div class="position_alert col-md-10 mx-auto">
            <c:if test="${not empty errors}">
              <div class="alert alert-danger py-2 px-3 mb-0 text-center">
                <c:out value="${errors}" />
              </div>
            </c:if>
          </div>

          <div class="panel-body">
            <div class="form-group">
              <label class="col-sm-3 control-label text-right">
                <spring:message code="lbl.function" text="Function" /> <span class="mandatory"></span>
              </label>

              <div class="col-sm-6 add-margin">
                <form:input path="name" name="function" id="function" class="form-control"
                  placeholder="Type first 3 letters of Function name" required="required" />

                <form:hidden path="id" name="id" id="functionId"
                  class="form-control table-input hidden-input cfunction" />
                <!--<form:hidden path="" name="budgetRegisterId" id="budgetRegisterId"
                             class="form-control table-input hidden-input" />-->
                <form:hidden path="" id="budgetRegisterId" name="budgetRegisterId" value="${budgetRegisterId}" />
                <form:errors path="id" cssClass="add-margin error-msg" />
              </div>
            </div>
          </div>
          
          <!-- <div class="form-group">
            <div class="text-center"> <button type='submit' class='btn btn-primary' id="buttonNext">
                <spring:message code='lbl.next' text="Next" /> </button> <a href='javascript:void(0)'
                class='btn btn-default' onclick="window.parent.postMessage('close','*');window.close();">
                <spring:message code='lbl.close' text="Close" /> </a> </div>
          </div> -->

          <div class="form-group text-center">
            <c:if test="${empty errors}">
              <button type='submit' class='btn btn-primary' id="buttonNext">
                <spring:message code='lbl.next' text="Next" />
              </button>
            </c:if>

            <a href='javascript:void(0)' class='btn btn-default'
              onclick="window.parent.postMessage('close','*');window.close();">
              <spring:message code='lbl.close' text="Close" />
            </a>
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
<script type="text/javascript" src="<cdn:url value='/resources/app/js/budgetGroupHelper.js?rnd=${app_release_no}'/>">
</script>
<script
  src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>">
</script>

<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>">
</script>

<script src="<cdn:url value='/resources/app/js/budget/budgetItemHelper.js' context='/services/EGF'/>"></script>