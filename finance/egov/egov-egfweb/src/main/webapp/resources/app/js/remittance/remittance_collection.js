var $parentId = 0;
var tableContainer;
var reportdatatable;
$(document).ready(function(){
	
	function getFormData($form) {
		var unindexed_array = $form.serializeArray();
		var indexed_array = {};
		$.map(unindexed_array, function(n, i) {
			indexed_array[n['name']] = n['value'];
		});
		return indexed_array;
	}
	
	function prepareHeading(){
		var heading= "Remittance Collections Report \n";
		if($("#financialYearId").val()){
			heading = heading  + " For Financial Year :" +  $("#financialYearId option:selected").text();
		}
		if($("#fundId").val() != 0 && $("#fundId").val()){
			heading = heading  + ", For Fund :" +  $("#fundId option:selected").text();
		}
		if($("#instrumentTypeId").val()){
			heading = heading  + ", For Instrument Type :" +  $("#instrumentTypeId option:selected").text();
		}
		if($("#fromDate").val()){
			heading = heading  + " From Date : " +  $("#fromDate").val();
		}
		if($("#toDate").val()){
			heading = heading  + " Till Date : " +  $("#toDate").val();
		}
	return heading;
			
	}
	
jQuery('#btnsearch').click(function(e) {
	var heading = prepareHeading().replace(/\n/g, "<br />");;
	$("#remittanceReportHeading").html(heading);
	callAjaxSearch();
});

function processDate(date){
	var parts = date.split("/");
	return new Date(parts[2], parts[1] - 1, parts[0]);
}

function callAjaxSearch() {
	var bankAccountId = $("#bankAccountId").val();
	var financialYearId = $("#financialYearId").val();
	var fund = $("#fund").val();
	if($("#bankAccountId").val() == 0){
		bootbox.alert(bankAccountAlertMsg);
		return false;
	}else if($("#financialYearId").val() == 0){
		bootbox.alert(finYearAlertMsg);
		return false;
	}else if($("#instrumentTypeId").val() == 0){
		bootbox.alert(paymntTypeAlertMsg);
		return false;
	}else{
		var fromDate = $("#fromDate").val();
		var toDate = $("#toDate").val();
		fromDate = processDate(fromDate);
		toDate = processDate(toDate);
		if(fromDate != "" && toDate != "" && fromDate.getTime()> toDate.getTime()){
			bootbox.alert(fromDateToDateAlertMsg);
			return false;
		}
	}

	var fileName = 'Remittance Collections Report';
	var drillDowntableContainer = $("#resultTable");
	$('.report-section').removeClass('display-hide');
	var data = getFormData(jQuery('form'));
	$('.error-section').addClass('display-hide');
	var columns = [{"data" : "srNo","sClass" : "text-center"},{"data" : "remittedOn","sClass" : "text-left"},{"data" : "serviceName","sClass" : "text-center"},
	               {"data" : "departmentName","sClass" : "text-center"},{"data" : "instrumentAmount","sClass" : "text-center"},{"data" : "bank","sClass" : "text-left"},
	               {"data" : "bankBranch","sClass" : "text-left"},{"data" : "bankAccount","sClass" : "text-left"}, {"data" : "transactionNumber","sClass" : "text-left"},
	               {"data" : "payee","sClass" : "text-left"},{"data" : "remittedBy","sClass" : "text-left"}];
	
	$.fn.dataTable.ext.errMode = 'none';
	var heading = prepareHeading();
							reportdatatable = drillDowntableContainer.dataTable({
									ajax : {
										url : '/services/EGF/report/remittance/collection/_search',
										type : "get",
										"data" : data,
										"dataSrc" : "",
										error: function (jqXHR, textStatus, errorThrown) {
											bootbox.alert(jqXHR.responseText);
											$('.report-section').addClass('display-hide');
											$('.error-section').removeClass('display-hide');
							            }
									},
									"bDestroy" : true,
									dom : "<'row'<'col-xs-12 pull-right'f>r>t<'row buttons-margin'<'col-md-3 col-xs-6'i><'col-md-3  col-xs-6'l><'col-md-3 col-xs-6'B><'col-md-3 col-xs-6 text-right'p>>",
									buttons : [ {
										extend : 'print',
										title : heading,
										filename : fileName
									}, 																		{
									    extend : 'pdfHtml5',
									    title : heading,
									    filename : fileName,
									    orientation : 'landscape',
									    pageSize : 'A4',

									    customize: function(doc) {

									        // your existing line (kept same)
									        doc.content[1].margin = [20, 0, 20, 0];

									        //  your added block (clean merge) 
									        doc.defaultStyle.fontSize = 8;
									        doc.pageMargins = [10, 10, 10, 10];
											var ulbName = $('#ulbName').val() || '';
									        var titleContainer = {
									            stack: [
									                {
									                    text: 'Government of Jammu & Kashmir',
									                    fontSize: 12,
									                    bold: true,
									                    alignment: 'center',
									                    noWrap: true,
									                    color: '#1F4E79'
									                },
									                {
									                    text: 'Housing and Urban Development Department',
									                    fontSize: 12,
									                    bold: true,
									                    alignment: 'center',
									                    noWrap: true,
									                    margin: [0, 2, 0, 10],
									                    color: '#1F4E79'
									                },
													{
													    text: ulbName,
													    fontSize: 11,
													    bold: true,
													    alignment: 'center',
													    noWrap: true,
													    margin: [0, 2, 0, 10],
													    color: '#1F4E79'
													}
									            ]
									        };

									        var currentDate = new Date().toLocaleDateString();
									        var currentTime = new Date().toLocaleTimeString();

									        var dateTimeContainer = {
									            text: 'Date: ' + currentDate + '\nTime: ' + currentTime,
									            fontSize: 10,
									            bold: true,
									            alignment: 'right',
									            margin: [0, 5, 10, 10]
									        };

									        var logoBase64 = window.logoBase64 || null;

									        var header;
									        if (logoBase64) {
									            header = {
									                columns: [
									                    { image: 'data:image/png;base64,' + logoBase64, width: 50 },
									                    titleContainer,
									                    dateTimeContainer
									                ]
									            };
									        } else {
									            header = {
									                columns: [
									                    { text: '' },
									                    titleContainer,
									                    dateTimeContainer
									                ]
									            };
									        }

									        doc.content.splice(0, 0, header);

									        var tableNode;
									        for (var i = 0; i < doc.content.length; i++) {
									            if (doc.content[i].table) {
									                tableNode = doc.content[i];
									                break;
									            }
									        }

									        if (tableNode && tableNode.table && tableNode.table.body) {
									            var colCount = tableNode.table.body[0].length;
									            tableNode.table.widths = Array(colCount).fill('*');
									        }
									    },

									    exportOptions: {
									        columns: ':visible'
									    }
									}, 
																		{
										extend : 'excelHtml5',
										message : heading,
										filename : fileName,
										messageTop : heading,
										stripNewlines: false,
								        exportOptions: {
							                  columns: ':visible'
							            }
									} ],
									aaSorting : [],
									columns : columns,
									"fnInitComplete": function(oSettings, json) {
									      toggleColumnBasedOnInstrumentType()
									 }
								});
}

function toggleColumnBasedOnInstrumentType(){
	var drillDowntableContainer = $("#resultTable");
	var resultDataTable = drillDowntableContainer.DataTable();
	if($("#instrumentTypeId").val() == 'Cash'){
		resultDataTable.columns([8,9]).visible(false);
	}else if($("#instrumentTypeId").val() == 'Cheque'){
		resultDataTable.columns([8,9]).visible(true);
	}
}
});

function loadMappedService(){
	var bankAccount = $("#bankAccountId").val();
		$.ajax({
			method : "GET",
			url : "/services/EGF/report/remittance/service/"+bankAccount,
			async : true
		}).done(function(response) {
			$('#serviceId').empty();
			var output = '<option value>Select</option>';
			console.log("response : ",response);
			$.each(response, function(idx,data) {
				output += '<option value=' + data.businessDetails + '>' + data.businessDetails + '</option>';
			});
			$('#serviceId').append(output);
		});
}