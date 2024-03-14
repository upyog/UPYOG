<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div class="panel-heading">
	<div class="panel-title">
		<%-- <spring:message code="Purchase-Items" /> --%>Purchase-Items
	</div>
</div>
<div class="panel-body">
<input type="hidden" name="purchaseOrder" value="${purchaseOrder.id}" />
	<table class="table table-bordered" >
	
	<thead>
			<tr>
				<th><%-- <spring:message code="lbl.account.code"/> --%>Items</th>
				<th>Unit</th>
				<th><%-- <spring:message code="lbl.debit.amount"/> --%>UnitRate</th>
				<th><%-- <spring:message code="lbl.credit.amount"/> --%>Quantity</th>
				<th><%-- <spring:message code="lbl.action"/> --%>Amount</th>
				 					
			</tr>
		</thead>
		<tbody>
		
	<%-- <c:out value="${purchaseItems}"></c:out> --%>
		 <c:forEach items="${purchaseItems}" var="item">
	
			<tr >
			<td><c:out value="${item.itemCode}"></c:out>
			</td>
			
			<td><c:out value="${item.unit}"></c:out>
				
			</td> 
			<td><c:out value="${item.unitRate}"></c:out>
			</td>
			<td><c:out value="${item.quantity}"></c:out>
				</td> 
			<td><c:out value="${item.amount}"></c:out>
				</td>
			</tr>
			</c:forEach>
			
		</tbody>
		
	</table>

</div>


