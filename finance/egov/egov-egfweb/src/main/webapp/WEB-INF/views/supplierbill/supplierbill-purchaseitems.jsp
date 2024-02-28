
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<style>
.purchase-table {
	border-collapse: collapse;
	width: 100%;
}

.purchase-table th, .purchase-table td {
	border: 1px solid #ddd;
	padding: 8px;
	text-align: left;
}

.purchase-table th {
	background-color: #f2f2f2;
}
</style>

<div class="panel-heading">
	<div class="panel-title">Purchase-Items</div>
</div>
<div id="dynamicPurchaseItemList" class="panel-body"></div>

<script>

var purchaseList;

function getPurchaseItemsByOrderId() {

		var orderNumber = $("#purchaseOrder").val();
		

        $.ajax({
            url: '/services/EGF/supplierbill/get/purchaseItems?orderNumber='+orderNumber, // Replace with your actual API endpoint
            type: 'GET',
            contentType: 'application/json',
            //data: JSON.stringify({ orderNumber: orderNumber }),
            success: function (dataList) {
            
            		document.getElementById("purchaseObject").value = JSON.stringify(dataList);
                //	console.log(document.getElementById("purchaseObject").value);
                	var jsonObjectArray = [];
            
            	for (var i = 0; i < dataList.length; i++) {
            	
            	 
            	 var item = dataList[i];
            	 
            	 
					  var jsonObject = {
					    id:item.id,
					    itemCode: item.itemCode,
					    unit: item.unit,
					    unitRate: item.unitRate,
					    quantity: item.quantity,
					    amount: item.amount,
					    //purchaseOrder: item.purchaseOrder,
					    orderNumber: item.purchaseOrder.orderNumber,
						purchaseOrderId: item.purchaseOrder.id,
						orderValue: item.purchaseOrder.orderValue
    
  				};
  				
  				//console.log(item.purchaseOrder.orderNumber);
  
				  if (Array.isArray(item.purchaseOrder)) {
				        // Assuming purchaseOrderArray is an array of purchaseOrder objects
				        var purchaseOrders = item.purchaseOrder.map(function (po) {
				            return {
				                orderNumber: po.orderNumber,
				                purchaseOrderId: po.id
				            };
				            console.log(purchaseOrders);
				        });
			
			        // Add purchaseOrders array to the jsonObject
			        jsonObject.purchaseOrders = purchaseOrders;
			        
			        console.log(jsonObject.purchaseOrders);
			    }

			  // Push the jsonObject to the array
			  //console.log(jsonObject);
			  jsonObjectArray.push(jsonObject);
			  //console.log(jsonObjectArray);
            	 
            	 
                			var data = dataList[i];
            		
            		
            		//document.getElementById("purchaseorder_id").value = JSON.stringify(data.purchaseOrder.id);
            		
                	//console.log(document.getElementById("itemList").value);
                	//console.log(document.getElementById("itemList1").value);
                	//console.log(document.getElementById("itemList2").value);
                	//console.log(document.getElementById("itemList3").value);
                	
                	}
                	
                	document.getElementById("purchaseObject").value = JSON.stringify(jsonObjectArray);
                	
                    $('#dynamicPurchaseItemList').empty();
                    
                    
                    	var tableHTML = '<table id="tbldebitdetails1" border="1" class="purchase-table">';
            				tableHTML += '<tr>';
            				tableHTML += '<th>Item Code</th>';
            				tableHTML += '<th>Unit Rate</th>';
            				tableHTML += '<th contenteditable="true">Quantity</th>';
            				tableHTML += '<th contenteditable="true">Amount</th>';
            				tableHTML += '</tr>';
                    	
						//console.log("dataList"+JSON.stringify(dataList));
                    
                   		for (var i = 0; i < dataList.length; i++) {
                			var data = dataList[i];
                			
                		 	tableHTML += '<tr>';
						    tableHTML += '<td>' + data.itemCode + '</td>';
						    tableHTML += '<td id="unitRate_' + i + '" contenteditable="true" class="editable unitRate">' + data.unitRate + '</td>';
						    tableHTML += '<td id="quantity_' + i + '" contenteditable="true" class="editable quantity"></td>';
						    tableHTML += '<td id="amount_' + i + '" class="amount"></td>';
						    tableHTML += '</tr>';
                			
                		
            	}
            		
          
            	
            		//document.getElementById("itemList").value = JSON.stringify(data.itemCode);
            		//document.getElementById("itemList1").value = JSON.stringify(data.unitRate);
            		//document.getElementById("itemList2").value = JSON.stringify(data.quantity);
            		//document.getElementById("itemList3").value = JSON.stringify(data.amount);
            	
            	$('#dynamicPurchaseItemList').append(tableHTML);
            	$(document).on('input', '.editable.quantity', function () {
    var row = $(this).closest('tr');
    var unitRate = parseFloat(row.find('.unitRate').text()) || 0;
    var quantity = parseFloat($(this).text()) || 0;

    var amount = unitRate * quantity;

    // Update the amount field in the current row
    row.find('.amount').text(amount.toFixed(2));

    // Call the function to update the total amount
    updateTotalAmount();
});

function updateTotalAmount() {
    var totalAmount = 0;

    // Loop through each row and accumulate the amounts
    $('#tbldebitdetails1 tbody tr').each(function () {
        var rowAmount = parseFloat($(this).find('.amount').text()) || 0;
        totalAmount += rowAmount;
    });

    // Update the total amount in the tfoot
    $('#totalAmount').text(totalAmount.toFixed(2));
    
    // You can update other elements here if needed

    // Update the supplierNetPayableAmount using JavaScript
    var netPayableAmount = amountConverter(totalAmount);
    document.getElementById('billamount').value = netPayableAmount;
    $("#supplierNetPayableAmount").html(netPayableAmount);
}
    
    function amountConverter(amt) {
	var formattedAmt = amt.toFixed(2);
	return formattedAmt;
}
 
            	
            	
                    //$('#dynamicPurchaseItemList').append(JSON.stringify(dataList));
                    
              
         	$(document).on('input', '.editable', function () {
			    var editedCell = $(this);
			    var row = editedCell.closest('tr');
			    var itemCode = row.find('td:eq(0)').text(); // Assuming itemCode is in the first column
			    var quantity = editedCell.text();
			    
			   // console.log(item.purchaseOrder.orderNumber);
			
			    $.ajax({
			        type: 'POST',
			        url: '/services/EGF/supplierbill/checkQuantity',
			        data: { orderNumber: item.purchaseOrder.orderNumber
			        , quantity: quantity },
			        success: function (response) {
			            if (response === 'available') {
			            console.log('Quantity:', quantity);
			                // Quantity is available
			                // You may add additional styling or actions here
			                alert('Quantity is available.');
			            } else {
			            console.log('Quantity1:', quantity);
			                // Quantity is unavailable or exceeds available quantity
			                // You may add additional styling or actions here
			                alert('Quantity is unavailable or exceeds available quantity.');
			            }
			        },
			        error: function () {
			        console.log('Quantity2:', quantity);
			            alert('Error checking quantity.');
			        }
			    });
			});
           
            },
            error: function (error) {
                // Handle the error
                console.error('Error retrieving data:', error);
            }
        });
        
     
        
    }
    
  

    
   
    
</script>
