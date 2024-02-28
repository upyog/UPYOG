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
				
				<th><%-- <spring:message code="lbl.debit.amount"/> --%>UnitRate</th>
				<th><%-- <spring:message code="lbl.credit.amount"/> --%>Quantity</th>
				<th><%-- <spring:message code="lbl.action"/> --%>Amount</th>
				 					
			</tr>
		</thead>
		<tbody>
		
	<%-- <c:out value="${purchaseItems}"></c:out> --%>
		 <c:forEach items="${purchaseItems}" var="item">
	
			<tr >
			<td> <input type="text" id="itemCode" name="itemCode" readonly="readonly" value="${item.itemCode}">
			<%-- <c:out value="${item.itemCode}"></c:out> --%>
			</td>
		
			<td><input type="text" id="unitRate" name="unitRate" readonly="readonly" value="${item.unitRate}">
			<%-- <c:out value="${item.unitRate}"></c:out> --%>
			</td>
			<td><input type="number" id= "quantity" name="quantity" value="${item.quantity}">
			<c:out value="${item.quantity}"></c:out>
				</td> 
			<td><input type="number" id= "amount" name="amount" value="${item.amount}">
			
			<c:out value="${item.amount}"></c:out>
				</td>
			</tr>
			</c:forEach>
			
		</tbody>
		<tfoot>
		<tr>
		<td></td>
		<td></td>
		
		<td>Total Amount</td>
		<td><span id="totalAmount" >0.00</span>
		<!-- <input type="text" name="totalAmount" id="totalAmount" readonly="readonly"/> --></td>
		</tr>
		</tfoot>
	</table>

</div>
<script>
    function getPurchaseItems(orderNumber) {
    
    var orderNumber = $("#purchaseOrder").val();
    
        $.ajax({
            url: '/services/EGF/supplierbill/get/purchaseItems?orderNumber='+orderNumber,
            type: 'GET',
            data: { orderNumber: orderNumber },
            success: function (data) {
                // Assuming data is an array with purchase items
                if (data.length > 0) {
                    // Update form input fields with the first purchase item
                    updateFormFields(data[0]);
                }
            },
            error: function (error) {
                console.error('Error retrieving purchase items:', error);
            }
        });
    }

    function updateFormFields(purchaseItem) {
        // Update form input fields with values from the purchase item
        $('#itemCode').val(purchaseItem.itemCode);
        $('#unitRate').val(purchaseItem.unitRate);

        // Update other fields as needed
    }
</script>
<script>
$(document).on('input', '.unitRate, .quantity', function () {
        // Get the values of unit rate and quantity from the current row
        var unitRate = parseFloat($(this).closest('tr').find('.unitRate').val()) || 0;
        var quantity = parseInt($(this).closest('tr').find('.quantity').val()) || 0;

        // Calculate the amount
        var amount = unitRate * quantity;

        // Update the amount field in the current row
        $(this).closest('tr').find('.amount').val(amount.toFixed(2));
        updateTotalAmount();
    });
    

    function updateTotalAmount(){
    
    	var totalAmount = 0;

    // Loop through each row and accumulate the amounts
    $('#tbldebitdetails tbody tr').each(function () {
        var rowAmount = parseFloat($(this).find('.amount').val()) || 0;
        totalAmount += rowAmount;
    });
	  // Update the total amount in the tfoot
    $('#totalAmount').text(totalAmount.toFixed(2));
    var netPayableAmount = amountConverter(totalAmount);

    // Update the supplierNetPayableAmount using JavaScript
    document.getElementById('orderValue').value = netPayableAmount;

    // Update the span with the corresponding ID
    $("#supplierNetPayableAmount").html(netPayableAmount);
    
    // Update the total amount in the tfoot
   // $('#totalAmount').text(totalAmount.toFixed(2));
   // netPayableAmount=amountConverter(totalAmount);
	//$("#supplier-netPayableAmount").val(netPayableAmount);
	//$("#supplierNetPayableAmount").html(netPayableAmount);
    }
</script>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>

