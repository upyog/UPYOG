<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			Purchase Itmes Details
		</div>
	</div>
	
	<div style="padding: 0 15px;">
		<table class="table table-bordered" id="tblsubledgerdetails">
			<thead>
				<tr>
					<th>Item Name</th>
					<th>Unit Rate</th>
<<<<<<< HEAD
					<th>Billed-Quantity</th>
					<th>UnitValueWithGST</th>
=======
>>>>>>> 8d2ef484acce46e0f39d80188c2b43ccca9e9508
					<th>Quantity</th>
					<th>Amount</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${egBillregister.egBillPurchaseItemsDetails.size() > 0}">
					
						<c:forEach items="${egBillregister.egBillPurchaseItemsDetails}" var="egBillPurchaseItemsDetails" varStatus="item">
							<tr id="subledhgerrow">
								<td>
									<span class="itemCode_${item.index }">${egBillPurchaseItemsDetails.itemCode}</span>
								</td>
								<td>
									<span class="unitRate_${item.index }">${egBillPurchaseItemsDetails.unitRate }</span>
								</td>
								<td>
<<<<<<< HEAD
									<span class="billed-Quantity${item.index }">${egBillPurchaseItemsDetails.quantity }</span>
								</td>
								<td>
									<span class="unitValueWithGst${item.index }">${egBillPurchaseItemsDetails.unitValueWithGst}</span>
								</td>
								
								<td>
=======
>>>>>>> 8d2ef484acce46e0f39d80188c2b43ccca9e9508
									<span class="quantity${item.index }">${egBillPurchaseItemsDetails.quantity }</span>
								</td>
								
									<td >
										<span class="amount_${item.index }" >${egBillPurchaseItemsDetails.amount}</span>
									</td>
								
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
	</div>
</div>