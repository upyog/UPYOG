<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div class="panel-heading">
	<div class="panel-title">
		<%-- <spring:message code="Purchase-Items" /> --%>Purchase-Items
	</div>
</div>
<div class="panel-body">
<input type="hidden" value="${purchaseOrder.purchaseItems.size()}" id="debitAmountrowcount"/>
	<table class="table table-bordered" id="tbldebitdetails">
	
	<thead>
			<tr>
				<th><%-- <spring:message code="lbl.account.code"/> --%>Items</th>
				<th><%-- <spring:message code="lbl.account.head"/> --%>Unit</th>
				<th><%-- <spring:message code="lbl.debit.amount"/> --%>UnitRate</th>
				<th><%-- <spring:message code="lbl.credit.amount"/> --%>Quantity</th>
				<th><%-- <spring:message code="lbl.action"/> --%>Amount</th>
				<th><spring:message code="lbl.action"/></th>  					
			</tr>
		</thead>
		<tbody>
		
		<%-- <c:choose> --%>
	<%-- 	<c:when test="${purchaseOrder.purchaseItems.size() == 0}">
	
	
			<tr id="debitdetailsrow">
			<td>
			<input type="text" id="purchaseItems[0].itemCode" name="purchaseItems[0].itemCode" class="form-control table-input debitDetailGlcode itemCode"  data-errormsg="Account Code is mandatory!" data-idx="0"  />
			<form:hidden path="purchaseItems[0].glcodeid" id="purchaseItems[0].glcodeid" class="form-control table-input hidden-input debitdetailid"/> 
				<form:hidden path="purchaseItems[0].glcode" name="purchaseItems[0].glcode" id="purchaseItems[0].glcode" class="form-control table-input hidden-input accountglcode"/> 
				<form:hidden path="purchaseItems[0].glcodeId.id" name="purchaseItems[0].glcodeId.id" id="purchaseItems[0].glcodeId.id" class="form-control table-input hidden-input accountglcodeid"/> 
				<form:hidden path="purchaseItems[0].isSubLedger" name="purchaseItems[0].isSubLedger" id="purchaseItems[0].isSubLedger" class="form-control table-input hidden-input accountglcodeissubledger"/>
			</td>
			<td>
				<select id="purchaseItems[0].unit" name="purchaseItems[0].unit" class="form-control table-input debitdetailname unit">
					<option value="No">No</option>
					<!-- <option value="No">No</option> -->
				</select>
			</td>
			<td>
				<form:input path="purchaseItems[0].unitRate" id="purchaseItems[0].unitRate" class="form-control table-input unitRate" data-errormsg="Unit Rate is mandatory!"  maxlength="12"  />
			</td> 
			<td>
				<form:input path="purchaseItems[0].quantity" id="purchaseItems[0].quantity" class="form-control table-input quantity" data-errormsg="Quantity is mandatory!"    maxlength="12" />
			</td> 
			<td>
				<form:input path="purchaseItems[0].amount" id="purchaseItems[0].amount" class="form-control table-input amount" onblur="calcualteNetpaybleAmount1();" data-errormsg="Amount is mandatory!" onkeyup="decimalvalue(this);" data-pattern="decimalvalue" data-idx="0" data-optional="0"   maxlength="12"  readonly="readonly"/>
			</td>
			<!-- <td class="text-center"><span style="cursor:pointer;" onclick="addDebitDetailsRow();"><i class="fa fa-plus" aria-hidden="true"></i></span>
						 <span class="add-padding purchaseitems-delete-row" onclick="deleteDebitDetailsRow(this);">
						 <i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td>  -->
			</tr>
			</c:when> --%>
			 <%-- <c:otherwise> --%>
			<c:forEach items="${purchaseOrder.purchaseItems}" var="billDeatils" varStatus="item">
			
			<input type="hidden" value="purchaseItems[${item.index}].id" id="id"/>
			
				<tr id="debitdetailsrow">
			<td>
			<input type="text" id="purchaseItems[${item.index}].itemCode" name="purchaseItems[${item.index}].itemCode" class="form-control table-input debitDetailGlcode itemCode"  data-errormsg="Account Code is mandatory!" data-idx="0"  value="${billDeatils.itemCode}"/>
				
				<form:hidden path="purchaseItems[${item.index }].glcodeid" name="purchaseItems[${item.index }].glcodeid" id="purchaseItems[${item.index }].glcodeid" class="form-control table-input hidden-input debitdetailid" value="${billDeatils.glcodeid}"/> 
				<%-- <form:hidden path="purchaseItems[${item.index }].glcodeId.id" name="purchaseItems[${item.index }].glcodeId.id" id="purchaseItems[${item.index }].glcodeId.id" class="form-control table-input hidden-input accountglcodeid"/> 
				<form:hidden path="purchaseItems[${item.index }].isSubLedger" name="purchaseItems[${item.index }].isSubLedger" id="purchaseItems[${item.index }].isSubLedger" class="form-control table-input hidden-input accountglcodeissubledger"/>
			 --%></td>
			<td>
				<form:select id="purchaseItems[${item.index}].unit" class="form-control table-input debitdetailname unit" name="purchaseItems[${item.index}].unit" path="purchaseItems[${item.index}].unit">
					<option value="No">No</option>
					<option value="No">No</option>
				</form:select>
			</td>
			<td>
				<form:input path="purchaseItems[${item.index}].unitRate" id="purchaseItems[${item.index}].unitRate" class="form-control table-input unitRate" data-errormsg="Unit Rate is mandatory!"  maxlength="12"  value="${billDeatils.unitRate}"/>
			</td> 
			<td>
				<form:input path="purchaseItems[${item.index}].quantity" id="purchaseItems[${item.index}].quantity" class="form-control table-input quantity" data-errormsg="Quantity is mandatory!"    maxlength="12"  value="${billDeatils.quantity}"/>
			</td> 
			<td>
				<form:input path="purchaseItems[${item.index}].amount" id="purchaseItems[${item.index}].amount" onblur="calcualteNetpaybleAmount1();" class="form-control table-input amount" data-errormsg="Amount is mandatory!" onkeyup="decimalvalue(this);" data-pattern="decimalvalue" data-idx="0" data-optional="0"   maxlength="12"  readonly="readonly" value="${billDeatils.amount}"/>
			</td>
			<td class="text-center"><span style="cursor:pointer;" onclick="addDebitDetailsRow();"><i class="fa fa-plus" aria-hidden="true"></i></span>
			<span class="add-padding purchaseitems-delete-row" onclick="deleteDebitDetailsRow(this);">
			<i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td> 
			</tr>
			
			</c:forEach>
			<%-- </c:otherwise>  --%>
		<%-- </c:choose> --%>
	
		</tbody>
		<!-- <tfoot>
		<tr>
		<td></td>
		<td></td>
		<td></td>
		<td>Total Amount</td>
		<td><span id="totalAmount" >0.00</span>
		<input type="text" name="totalAmount" id="totalAmount" readonly="readonly"/></td>
		</tr>
		</tfoot> -->
	</table>

</div>
<script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script
        src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>

<script
        src="<cdn:url value='/resources/app/js/common/voucherBillHelper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script
        src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script
        src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
        
        

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

var subLedgerDisplayName;
var detailTypeName;
var detailKeyName;
var debitCodes = new Array();
var debitAmountrowcount=0;
var creditAmoutrowcount=0;
var $purchaseOrderId = 0;
var $supplierId = 0;
var accountCodeTemplateMap = {};
$(document).ready(function(){
	console.log("Browser Language ",navigator.language);
	$.i18n.properties({ 
		name: 'message', 
		path: '/services/EGF/resources/app/messages/', 
		mode: 'both',
		async:true,
		cache:true,
		language: getLocale("locale"),
		callback: function() {
			console.log('File loaded successfully');
		}
	});
	//loadAccountCodeTemplate();

	$purchaseOrderId = $('#purchaseOrderId').val();
	$supplierId = $('#supplierId').val();
	patternvalidation(); 
	itemCode_initialize();
	creditGlcode_initialize();
	$('#fund').val($('#fund').val());
	if($supplierId){
		$('#supplier').val($supplierId);
		loadPurchaseOrder($supplierId);
	}
	if($purchaseOrderId){
		$('#purchaseOrder').val($purchaseOrderId);
		loadMisAttributes($purchaseOrderId);
	}
	creditAmoutrowcount=$("#creditAmoutrowcount").val() == undefined ? creditAmoutrowcount : $("#creditAmoutrowcount").val();
	debitAmountrowcount=$("#debitAmountrowcount").val() == undefined ? debitAmountrowcount : $("#debitAmountrowcount").val();
	calcualteNetpaybleAmount1();
});

$('#supplier').change(function () {
	$purchaseOrderId = "";
	$('#purchaseOrder').empty();
	$('#purchaseOrder').append($('<option>').text('Select from below').attr('value', ''));
	loadPurchaseOrder($('#supplier').val());
});

$('#purchaseOrder').change(function () {
	$('#fundId').val("");
	$('#fundName').val("");
	$('#departmentCode').val("");
	$('#departmentName').val("");
	$('#schemeId').val("");
	$('#schemeName').val("");
	$('#subSchemeId').val("");
	$('#subSchemeName').val("");
	loadMisAttributes($('#purchaseOrder').val());
});

$('.btn-wf-primary').click(function(){
	var button = $(this).attr('id');
	if (button != null && (button == 'Forward')) {
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			return true;
		}else
			return false;
		
	}else if (button != null && (button == 'Create And Approve')) {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').removeAttr('required');
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			return true;
		}else
			return false;
	} else{
		if(!validateWorkFlowApprover(button))
			return false;
		if($("form").valid()){
			return true;
		}else
			return false;
	}
	return false;
});

function getCookie(name){
	let cookies = document.cookie;
	if(cookies.search(name) != -1){
		var keyValue = cookies.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	    return keyValue ? keyValue[2] : null;
	}
}

function getLocale(paramName){
	return getCookie(paramName) ? getCookie(paramName) : navigator.language;
}

function itemCode_initialize() {
	 var custom = new Bloodhound({
	    datumTokenizer: function(d) { return d.tokens; },
	    queryTokenizer: Bloodhound.tokenizers.whitespace,
		   remote: {
	            url: '/services/EGF/common/getsupplierdebitcodes?glcode=',
	            dataType: "json",
	            replace: function (url, query) {
					return url + query ;
				},
	            filter: function (data) {
	            	var responseObj = JSON.parse(data);
	                return $.map(responseObj, function (ct) {
	                    return {
	                        id: ct.id,
	                        name: ct.name,
	                        glcode: ct.glcode,
	                        issubledger: ct.isSubLedger,
	                        glcodesearch: ct.glcode+' ~ '+ct.name
	                    };
	                });
	            }
	        }
  });

  custom.initialize();
  var dt = $('.itemCode').typeahead({
  	hint : true,
		highlight : true,
		minLength : 3
		
	}, {
        displayKey: 'glcodesearch',
        source: custom.ttAdapter()
  }).on('typeahead:selected typeahead:autocompleted', function (event, data) {
	   
	   var originalglcodeid = data.id;
	   var flag = false;
	   $('#tbldebitdetails  > tbody > tr:visible[id="debitdetailsrow"]').each(function(index) {
		   var glcodeid =document.getElementById('purchaseItems['+index+'].glcodeid').value;
			if( glcodeid!= "" && originalglcodeid == glcodeid) {
				flag = true;
			}
	   });
	  if(flag){
		  
			bootbox.alert($.i18n.prop('msg.debit.code.already.added'), function() {
				var index= dt.length - 1;
				if(document.getElementById('purchaseItems['+index+'].itemCode'))
					document.getElementById('purchaseItems['+index+'].itemCode').value = "";
			});
		}else{
		   	$(this).parents("tr:first").find('.debitdetailid').val(data.id);
		   	$(this).parents("tr:first").find('.debitdetailname').val(data.name);
		}
  });
}

function creditGlcode_initialize() {
	 var custom = new Bloodhound({
	    datumTokenizer: function(d) { return d.tokens; },
	    queryTokenizer: Bloodhound.tokenizers.whitespace,
		   remote: {
	            url: '/services/EGF/common/getsuppliercreditcodes?glcode=',
	            dataType: "json",
	            replace: function (url, query) {
					return url + query  ;
				},
	            filter: function (data) {
	            	var responseObj = JSON.parse(data);
	                return $.map(responseObj, function (ct) {
	                    return {
	                        id: ct.id,
	                        name: ct.name,
	                        glcode: ct.glcode,
	                        issubledger: ct.isSubLedger,
	                        glcodesearch: ct.glcode+' ~ '+ct.name
	                    };
	                });
	            }
	        }
  });

  custom.initialize();

  $('.creditGlcode').typeahead({
  	hint : true,
		highlight : true,
		minLength : 3
		
	}, {		    
        displayKey: 'glcodesearch',
        source: custom.ttAdapter()
  }).on('typeahead:selected typeahead:autocompleted', function (event, data) {
	  
	  var originalglcodeid = data.id;
	   var flag = false;
	   $('#tblcreditdetails  > tbody > tr:visible[id="creditdetailsrow"]').each(function(index) {
		   var glcodeid =document.getElementById('creditDetails['+index+'].glcodeid').value;
			if( glcodeid!= "" && originalglcodeid == glcodeid) {
				flag = true;
			}
	   });
	   if(flag){
			bootbox.alert($.i18n.prop('msg.credit.code.already.added'), function() {
				var index= dt.length - 1;
				document.getElementById('creditDetails['+index+'].creditGlcode').value = "";
			});
		}else{
		  	$(this).parents("tr:first").find('.creditdetailid').val(data.id);
		  	$(this).parents("tr:first").find('.creditdetailname').val(data.name);
		}
  });
}

function addDebitDetailsRow() { 
	
	$('.itemCode').typeahead('destroy');
	$('.itemCode').unbind();
	var rowcount = $("#tbldebitdetails tbody tr").length;
	if (rowcount < 40) {
		if (document.getElementById('debitdetailsrow') != null) {
			addRow('tbldebitdetails','debitdetailsrow');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.debitDetailGlcode').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.debitdetailname').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.unitRate').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.quantity').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.amount').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').blur(calcualteNetpaybleAmount1);
			itemCode_initialize();
			++debitAmountrowcount;
		}
	} else {
		  bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
}

function deleteDebitDetailsRow(obj) {
	var rowcount=$("#tbldebitdetails tbody tr").length;
    if(rowcount<=1) {
		bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
		return false;
    } else if (confirm("Are you sure you want to Delete")) {
    	$(document).ready(function() {
    $(".delete-button").click(function() {
        var itemId = $(this).data("id");

        $.ajax({
            type: "DELETE",
            url: "/services/EGF/purchaseorder/" + itemId,
            success: function(response) {
                // Handle success response
                console.log(response);
            },
            error: function(error) {
                // Handle error response
                console.error(error);
            }
        });
    });
});
		deleteRow(obj,'tbldebitdetails');
		--debitAmountrowcount;
		calcualteNetpaybleAmount1();
		return true;
	}else{
		return false
	}
}

 function addPurchaseItemsRow() {
    // Get a reference to the table body
    var tableBody = $("#tbldebitdetails tbody");

    // Clone the first row (assuming it's your template row)
    var newRow = $("#debitdetailsrow").clone();

    // Clear input values in the new row
    newRow.find('input').val('');

    // Append the new row to the table body
    tableBody.append(newRow.show()); // Show the cloned row before appending
}

function reset(){
var debitDetailsCount = $("#tbldebitdetails > tbody > tr:visible[id='debitdetailsrow']").length;
for (var i = debitDetailsCount; i >= 1; i--) {
	if(1 == i){
		document.getElementById('purchaseItems[0].itemCode').value = "";
		document.getElementById('purchaseItems[0].unit').value = "";
		document.getElementById('purchaseItems[0].unitRate').value = "";
		document.getElementById('purchaseItems[0].quantity').value = "";
		document.getElementById('purchaseItems[0].amount').value = "";
	}else{
		var objects = $('.debit-delete-row');
		deleteRow(objects[i-1],'tbldebitdetails');
	}
}



}

function loadMisAttributes(orderNumber){
	if (!orderNumber) {
		$('#fundId').val("");
		$('#fundName').val("");
		$('#departmentCode').val("");
		$('#departmentName').val("");
		$('#schemeId').val("");
		$('#schemeName').val("");
		$('#subSchemeId').val("");
		$('#subSchemeName').val("");
		return;
	} else {
		
		$.ajax({
			method : "GET",
			url : "/services/EGF/common/getpurchaseoderbyordernumber",
			data : {
				orderNumber : orderNumber
			},
			async : true
		}).done(
				function(response) {
					$.each(response, function(index, value) {
						$('#fundId').val(value.fund.id);
						$('#fundName').val(value.fund.name);
						$('#departmentCode').val(value.department);
						$('#departmentName').val(value.description);
						$('#schemeId').val(value.scheme.id);
						$('#schemeName').val(value.scheme.name);
						$('#subSchemeId').val(value.subScheme.id);
						$('#subSchemeName').val(value.subScheme.name);
					});
				});

	}
}

function loadAccountCodeTemplate(){
	$.ajax({
		method : "GET",
		url : "/services/EGF/accountCodeTemplate/supplierlist",
		data : {
					module: 'SupplierBill',
		},
		async : true
	}).done(
			function(response) {
				accountCodeTemplateMap = {}
				var output = '<option value>Select</option>';
				$('#accountCodeTemplateId').empty();
				$.each(response, function(index, value) {
					accountCodeTemplateMap[value.code] = value; 
					output = output + '<option value="'+value.code+'">'+value.code +' - '+value.name+'</option>'
			});
				$('#accountCodeTemplateId').append(output);
	});
}

$('#accountCodeTemplateId').change(function () {
var selectedTemp = $(this).val();
console.log("current1 : ",$.data(this, 'current'));
if($(this).val()){
	populateAccountCodeTemplateDetails(selectedTemp);
}
});

function populateAccountCodeTemplateDetails(selectedTemp){
if(clearAllDetails()){
  var accTempDet = accountCodeTemplateMap[selectedTemp];
	$.each(accTempDet.debitCodeDetails, function(index, value) {
		$('.debitGlcode').typeahead('destroy');
		$('.debitGlcode').unbind();
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitDetailGlcode').val(value.glcode+' ~ '+value.name);
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitdetailname').val(value.name);
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitaccountcode').val(value.glcode);
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitdetailid').val(value.id);
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitAmount').val("0");
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitDetailTypeName').val(detailTypeName);
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitDetailKeyName').val(detailKeyName);
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitIsSubLedger').val(value.isSubledger ? true : false);
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitDetailTypeId').val($('#subLedgerType').val());
		$('#tbldebitdetails tbody tr:eq('+index+')').find('.debitDetailKeyId').val($('#detailkeyId').val());
		debitGlcode_initialize();
		if(++index < accTempDet.debitCodeDetails.length)
			addDebitDetailsRow();
	});
	$.each(accTempDet.creditCodeDetails, function(index, value) {
		$('.creditGlcode').typeahead('destroy');
		$('.creditGlcode').unbind();
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditDetailGlcode').val(value.glcode+' ~ '+value.name);
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditdetailname').val(value.name);
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditaccountcode').val(value.glcode);
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditdetailid').val(value.id);
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditAmount').val("0");
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditDetailTypeName').val(detailTypeName);
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditDetailKeyName').val(detailKeyName);
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditIsSubLedger').val(value.isSubLedger);
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditDetailTypeId').val($('#subLedgerType').val());
		$('#tblcreditdetails tbody tr:eq('+index+')').find('.creditDetailKeyId').val($('#detailkeyId').val());
		creditGlcode_initialize();
		if(++index < accTempDet.creditCodeDetails.length)
			addCreditDetailsRow();
	});
	if($("#netPayableAccountCode option[value="+accTempDet.netPayable.id+"]").length==1){
		$('#netPayableAccountCode').val(accTempDet.netPayable.id);
		$('#netPayableDetailTypeId').val($('#subLedgerType').val());
		$('#netPayableIsSubLedger').val(accTempDet.netPayable.isSubLedger);
		$('#netPayableDetailKeyId').val($('#detailkeyId').val());
		$('#netPayableDetailTypeName').val(detailTypeName);
		$('#netPayableDetailKeyName').val(detailKeyName);
		$('#netPayableGlcode').val(accTempDet.netPayable.glcode);
		$('#netPayableAccountHead').val(accTempDet.netPayable.name+'~'+(accTempDet.netPayable.isSubledger? 'true':'false'));
	}
}

}

function loadPurchaseOrder(supplierId){
	if (!supplierId) {
		$('#purchaseOrder').empty();
		$('#purchaseOrder').append($('<option>').text('Select from below').attr('value', ''));
		$('#purchaseOrder').empty();
		$('#purchaseOrder').append($('<option>').text('Select from below').attr('value', ''));
		return;
	} else {
		
		$.ajax({
			method : "GET",
			url : "/services/EGF/common/getpurchaseodersbysupplierid",
			data : {
				supplierId : supplierId
			},
			async : true
		}).done(
				function(response) {
					$('#purchaseOrder').empty();
					$('#purchaseOrder').append($("<option value=''>Select from below</option>"));
					$.each(response, function(index, value) {
						var selected="";
						if($purchaseOrderId && $purchaseOrderId==value.orderNumber)
						{
								selected="selected";
						}
						$('#purchaseOrder').append($('<option '+ selected +'>').text(value.name).attr('value', value.orderNumber));
					});
				});

	}
}
function amountConverter(amt) {
	var formattedAmt = amt.toFixed(2);
	return formattedAmt;
}
function calcualteNetpaybleAmount1(){
	
	
	var debitamt = 0;
	//var creditamt = 0;
	for (var count = 0; count <=debitAmountrowcount; ++count) {
		//console.log("hi");

		if (null != document.getElementById("purchaseItems[" + count
				+ "].amount")) 
				{
		//		console.log("hi1");
			var val = document.getElementById("purchaseItems[" + count + "].amount").value;
			console.log(document.getElementById("purchaseItems[" + count + "].amount").value);
			if (val != "" && !isNaN(val)) {
		//	console.log("hi2");
//				debitamt = debitamt + parseFloat(val);
				debitamt = parseFloat(Number(debitamt) + Number(val)).toFixed(2);
				document.getElementById("purchaseItems[" + count + "].amount").value = Number(val).toFixed(2);
			//	console.log(debitamt);
			}
		}
	}
	
	//alert("hi1");

//	
	netPayableAmount=amountConverter(debitamt);
	$("#supplier-netPayableAmount").val(netPayableAmount);
	$("#supplierNetPayableAmount").html(netPayableAmount);
	//$("#supplierBillTotalDebitAmount").html(debitamt);
	//$("#supplierBillTotalCreditAmount").html(creditamt);
}

function validateCutOff()
{
	var cutofdate = $("#cutOffDate").val();
	var billdate = $("#billdate").val();
	var cutOffDateArray=cutofdate.split("/");
	var billDateArray=billdate.split("/");
	var cutOffDate = new Date(cutOffDateArray[1] + "/" + cutOffDateArray[0] + "/"
			+ cutOffDateArray[2]);
	var billDate = new Date(billDateArray[1] + "/" + billDateArray[0] + "/"
			+ billDateArray[2]);
	if(billDate<=cutOffDate)
	{
		return true;
	}
	else
	{
		bootbox.alert($.i18n.prop('msg.cutoff.warnig.message',cutofdate));
		return false;
	}
	return false;
}

function validateWorkFlowApprover(name) {
	document.getElementById("workFlowAction").value = name;
	var button = document.getElementById("workFlowAction").value;
	if (button != null && button == 'Submit') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Reject') {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Cancel') {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Forward') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Approve') {
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Create And Approve') {
		return validateCutOff();
	}else
		return true;
	
	return true;
}

function validate(){
var billamount = $("#billamount").val();
var netpayableamount = $("#supplierNetPayableAmount")["0"].innerHTML;
var debitamount = $("#supplierBillTotalDebitAmount")["0"].innerHTML;
var creditamount = $("#supplierBillTotalCreditAmount")["0"].innerHTML;
	
	$("#passedamount").val(debitamount);

	if(debitamount != Number(Number(creditamount) + Number(netpayableamount)).toFixed(2)){
		bootbox.alert($.i18n.prop('msg.debit.and.credit.amount.is.not.matching'));
		return false;
	}
	
	if(debitamount == 0){
		bootbox.alert($.i18n.prop('msg.please.select.atleast.one.debit.details'));
		return false;
	}
	
	if(!$("#supplier-netPayableAmount").val())
	{
		bootbox.alert($.i18n.prop('msg.please.select.one.net.payable.account.detail'));
		return false;
	}
	
	if(parseFloat(billamount) < parseFloat(debitamount)){
		bootbox.alert($.i18n.prop('msg.bill.amount.should.not.greater.than.passed.amount'));
		return false;
	}
	
	if(parseFloat(debitamount) > parseFloat(billamount)){
		bootbox.alert($.i18n.prop('msg.passed.amount.should.not.be.greater.than.bill.amount'));
		return false;
	}
	
	return true;
}

</script>
