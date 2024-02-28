$(document).ready(function(){
	accountDetailGlcode_initialize();
	entityName_initialize(0);
});

$('#voucherSubType').change(function () {
	makePartyNameMandatory($('#voucherSubType').val());
});

function makePartyNameMandatory(voucherSubType){
	
	if(voucherSubType){
		if(voucherSubType != 'JVGeneral'){
			$('#partyNameLabelId').removeClass('hide');
			$('#partyName').prop("required","required");
			$('#partyName').removeAttr("disabled");
			$('#partyBillNumber').removeAttr("disabled");
			$('#partyBillDate').removeAttr("disabled");
			$('#billNumber').removeAttr("disabled");
			$('#billDate').removeAttr("disabled");
		}else{
			$('#partyNameLabelId').addClass('hide');
			$('#partyName').removeAttr("required");
			$('#partyName').prop("disabled","disabled");
			$('#partyBillNumber').prop("disabled","disabled");
			$('#partyBillDate').prop("disabled","disabled");
			$('#billNumber').prop("disabled","disabled");
			$('#billDate').prop("disabled","disabled");
		}
	}else{
		$('#partyNameLabelId').addClass('hide');
		$('#partyName').removeAttr("required");
		$('#partyName').removeAttr("disabled");
		$('#partyBillNumber').removeAttr("disabled");
		$('#partyBillDate').removeAttr("disabled");
		$('#billNumber').removeAttr("disabled");
		$('#billDate').removeAttr("disabled");
	}
}

function accountDetailGlcode_initialize() {
	 var custom = new Bloodhound({
	    datumTokenizer: function(d) { return d.tokens; },
	    queryTokenizer: Bloodhound.tokenizers.whitespace,
		   remote: {
	            url: '/EGF/common/getallaccountcodes?glcode=',
	            replace: function (url, query) {
					return url + query ;
				},
	            dataType: "json",
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
 $('.accountDetailGlcode').typeahead({
  	hint : true,
		highlight : true,
		minLength : 3
		
	}, {		    
        displayKey: 'glcodesearch',
        source: custom.ttAdapter()
  }).on('typeahead:selected typeahead:autocompleted', function (event, data) {
			$(this).parents("tr:first").find('.accountglname').val(data.name);
		   	$(this).parents("tr:first").find('.accountglcode').val(data.glcode);
		   	$(this).parents("tr:first").find('.accountglcodeid').val(data.id);
		   	$(this).parents("tr:first").find('.accountglcodeissubledger').val(data.issubledger);
		   	loadSubLedgerAccountCodes();
  });
}

function deletePurchaseItemsRow(obj) {
	var rowcount=$("#tblpurchaseitems tbody tr").length;
    if(rowcount<=1) {
		bootbox.alert("This row can not be deleted");
		return false;
	} else {
		deleteRow(obj,'tblpurchaseitems');
		return true;
	}
}

function addPurchaseItemsRow() { 
	
	$('.accountDetailGlcode').typeahead('destroy');
	$('.accountDetailGlcode').unbind();
	var rowcount = $("#tblpurchaseitems tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById('purchaseitemsrow') != null) {
			addRow('tblpurchaseitems','tblpurchaseitems');
			$('#tblpurchaseitems tbody tr:eq('+rowcount+')').find('.accountDetailGlcode').val('');
			$('#tblpurchaseitems tbody tr:eq('+rowcount+')').find('.accountglname').val('');
			$('#tblpurchaseitems tbody tr:eq('+rowcount+')').find('.accountglcode').val('');
			$('#tblpurchaseitems tbody tr:eq('+rowcount+')').find('.accountglcodeid').val('');
			$('#tblpurchaseitems tbody tr:eq('+rowcount+')').find('.accountglcodeissubledger').val('');
			$('#tblpurchaseitems tbody tr:eq('+rowcount+')').find('.unit').val('');
			$('#tblpurchaseitems tbody tr:eq('+rowcount+')').find('.unitRate').val('');
			$('#tblpurchaseitems tbody tr:eq('+rowcount+')').find('.quantity').val('');
			$('#tblpurchaseitems tbody tr:eq('+rowcount+')').find('.amount').val('');
			accountDetailGlcode_initialize();
		}
	} else {
		  bootbox.alert('limit reached!');
	}
}