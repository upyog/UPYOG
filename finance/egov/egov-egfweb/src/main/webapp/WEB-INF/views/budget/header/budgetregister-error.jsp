<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <c:if test="${not empty errors}">
              <div class="alert alert-danger py-2 px-3 mb-0 text-center">
                <c:out value="${errors}" />
              </div>
            </c:if>
        </div>
    </div>
</div>