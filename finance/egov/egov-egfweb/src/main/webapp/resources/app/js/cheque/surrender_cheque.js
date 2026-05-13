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
		var heading= "Surrendered Cheque Report ";
		if($("#fund").val() != 0){
			heading = heading  + " For Fund:" +  $("#fund option:selected").text();
		}
		if($("#bankBranch").val()){
			heading = heading  + " For Bank-Branch :" +  $("#bankBranch option:selected").text();
		}
		if($("#bankAccountId").val() != 0 && $("#bankAccountId").val()){
			heading = heading  + " For Bank Account :" +  $("#bankAccountId option:selected").text();
			
		}
		if($("#surrenderReason").val()){
			heading = heading  + " For Surrender Reason :" +  $("#surrenderReason option:selected").text();
			
		}
		if($("#fromDate").val()){
			heading = heading  + " From Date :" +  $("#fromDate").val();
			
		}
		if($("#toDate").val()){
			heading = heading  + " To Date : " +  $("#toDate").val();
			
		}
	return heading;
			
	}
	
jQuery('#btnsearch').click(function(e) {
	var heading = prepareHeading();
	$("#surrenderChequeHeading").html(heading);
	callAjaxSearch();
});

function processDate(date){
	var parts = date.split("/");
	return new Date(parts[2], parts[1] - 1, parts[0]);
}

function callAjaxSearch() {
	var fromDate = $("#fromDate").val();
	var toDate = $("#toDate").val();
	if(!$("#fromDate").val()){
		bootbox.alert('please select from Date!');
		return false;
	}else if(!$("#toDate").val()){
		bootbox.alert('please select to Date!');
		return false;
	}else{
		fromDate = processDate(fromDate);
		toDate = processDate(toDate);
		if(fromDate.getTime()>toDate.getTime()){
			bootbox.alert('FromDate must be lower than ToDate!');
			return false;
		}
	}

	var fileName = 'Surrendered Cheque Report';
	drillDowntableContainer = $("#resultTable");
	$('.report-section').removeClass('display-hide');
	$('.error-section').addClass('display-hide');
	$.fn.dataTable.ext.errMode = 'none';
	var heading = prepareHeading();
							reportdatatable = drillDowntableContainer.dataTable({
									ajax : {
										url : '/services/EGF/report/cheque/surredered/_search',
										type : "get",
										"data" : getFormData(jQuery('form')),
										error: function (jqXHR, textStatus, errorThrown) {
											$('.report-section').addClass('display-hide');
											$('.error-section').removeClass('display-hide');
							            }
									},
									"bDestroy" : true,
									dom : "<'row'<'col-xs-12 pull-right'f>r>t<'row buttons-margin'<'col-md-3 col-xs-6'i><'col-md-3  col-xs-6'l><'col-md-3 col-xs-6'B><'col-md-3 col-xs-6 text-right'p>>",
									buttons : [ 									{
																			    text: 'Print',
																			    action: function(e, dt, node, config) {
																			        var currentDate = new Date().toLocaleDateString();
																			        var currentTime = new Date().toLocaleTimeString();
																			        var ulb = (typeof ulbName !== 'undefined' && ulbName) ? ulbName : '';

																			        // Build table rows from DataTables data
																			        var tableHTML = '<table style="width:100%; border-collapse:collapse; font-size:11px;">';
																			        
																			        // Header row
																			        tableHTML += '<thead><tr style="background-color:#f2f2f2;">';
																			        tableHTML += '<th style="border:1px solid #ddd; padding:6px;">Order No.</th>';
																			        tableHTML += '<th style="border:1px solid #ddd; padding:6px;">Name</th>';
																			        tableHTML += '<th style="border:1px solid #ddd; padding:6px;">Total/Order Value</th>';
																			        tableHTML += '<th style="border:1px solid #ddd; padding:6px;">Contractor Name</th>';
																			        tableHTML += '<th style="border:1px solid #ddd; padding:6px;">Active Y/N</th>';
																			        tableHTML += '</tr></thead><tbody>';

																					$('#resultTable tbody tr').each(function() {
																					    tableHTML += '<tr>';
																					    $(this).find('td').each(function() {
																					        tableHTML += '<td style="border:1px solid #ddd; padding:5px;">' + $(this).text() + '</td>';
																					    });
																					    tableHTML += '</tr>';
																					});

																			        tableHTML += '</tbody></table>';

																			        var fullHTML = '<!DOCTYPE html><html><head>' +
																			            '<title>Surrendered Cheque Report</title>' +
																			            '<style>' +
																			                'body { font-family: Arial, sans-serif; margin: 20px; }' +
																			                '@media print { body { margin: 10px; } }' +
																			            '</style>' +
																			            '</head><body>' +

																			            // ---- HEADER ----
																			            '<table style="width:100%; border:none; margin-bottom:8px;">' +
																			                '<tr>' +
																			                    '<td style="width:15%; border:none;"></td>' +
																			                    '<td style="width:70%; text-align:center; border:none;">' +
																			                        '<p style="font-size:15px; font-weight:bold; color:#1F4E79; margin:0;">' +
																			                            'Government of Jammu &amp; Kashmir' +
																			                        '</p>' +
																			                        '<p style="font-size:14px; font-weight:bold; color:#1F4E79; margin:4px 0;">' +
																			                            'Housing and Urban Development Department' +
																			                        '</p>' +
																			                        '<p style="font-size:13px; font-weight:bold; color:#1F4E79; margin:4px 0;">' +
																			                            ulb +
																			                        '</p>' +
																			                        '<p style="font-size:13px; font-weight:bold; color:#333; margin:6px 0;">' +
																			                            'Surrendered Cheque Report' +
																			                        '</p>' +
																			                    '</td>' +
																			                    '<td style="width:15%; text-align:right; vertical-align:top; border:none; font-size:10px; color:#333;">' +
																			                        'Date: ' + currentDate + '<br/>Time: ' + currentTime +
																			                    '</td>' +
																			                '</tr>' +
																			            '</table>' +
																			            '<hr style="border:1px solid #1F4E79; margin-bottom:12px;"/>' +
																			            // ---- TABLE ----
																			            tableHTML +
																			            // ---- AUTO PRINT ----
																			            '<script>window.onload = function(){ window.print(); }<\/script>' +
																			            '</body></html>';

																			        var printWin = window.open('', '_blank', 'width=900,height=600');
																			        printWin.document.open();
																			        printWin.document.write(fullHTML);
																			        printWin.document.close();
																			    }
																			},{
															    extend: 'pdf',
															    title: heading,
															    filename: heading,
																
																customize: function(doc) {

																   
																    doc.defaultStyle.fontSize = 8;
																    doc.pageMargins = [10, 10, 10, 10];

																 
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

																    // Optional Logo (safe)
																    var logoBase64 = window.logoBase64 || null;

																    var header;
																    if (logoBase64) {
																        header = {
																            columns: [
																                { image: 'data:image/png;base64,' + logo, width: 50 },
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
																}
															}, {
										extend : 'excelHtml5',
										message : heading,
										filename : fileName
									} ],
									aaSorting : [],
									columns : [
											{
												"data" : "id",
												"sClass" : "text-center"
											},
											{
												"data" : "bankBranch",
												"sClass" : "text-left"
											},
											{
												"data" : "bankAccountNumber",
												"sClass" : "text-center"
											},
											{
												"data" : "chequeNumber",
												"sClass" : "text-center"
											},
											{
												"data" : "chequeDate",
												"sClass" : "text-center"
											},
											{
												"data" : "payTo",
												"sClass" : "text-left"
											},
											{
												"data" : "voucherNumber",
												"sClass" : "text-left",
												fnCreatedCell : function(nTd,sData, oData, iRow,iCol) {
													$(nTd).html("<a href='' onclick='viewVoucher(event,"+ oData.voucherHeaderId+ ")'>"+ oData.voucherNumber + "</a>");
												}
											},
											{
												"data" : "voucherDate",
												"sClass" : "text-center"
											}, 
											{
												"data" : "surrenderReason",
												"sClass" : "text-left"
											} ]
								});
}

});


function loadBankBranch() {
	var fundId = $("#fund").val();
	$.ajax({
		method : "GET",
		url : "/services/EGF/report/cheque/bankBranch/_search",
		data : {
			fundId : fundId
		},
		async : true
	}).done(function(response) {
		$('#bankBranch').empty();
		$('#bankAccountId').empty();
		var output = '<option value="">Select</option>';
		$.each(response, function(index, value) {
			console.log("index: ", index);
			console.log("value: ", value);
			output += '<option value=' + index + '>' + value + '</option>';
		});
		$('#bankBranch').append(output);
	});
}

function loadBankAccount() {
	var branchId = $("#bankBranch").val().split("-")[1];
	var fundId = $("#fund").val();
	console.log();
	fundId = fundId == 0 || fundId == "" || fundId == undefined ? 0 : fundId;
	$.ajax({
		method : "GET",
		url : "/services/EGF/report/cheque/bankAccount/_search",
		data : {
			fundId : fundId,
			branchId : branchId
		},
		async : true
	}).done(function(response) {
		$('#bankAccountId').empty();
		var output = '<option value="0">Select</option>';
		$.each(response, function(index, value) {
			console.log("index: ", index);
			console.log("value: ", value);
			output += '<option value=' + index + '>' + value + '</option>';
		});
		$('#bankAccountId').append(output);
	});
}

function viewVoucher(event,vid){
	event.preventDefault();
	var url = '/services/EGF/voucher/preApprovedVoucher-loadvoucherview.action?vhid='+vid;
	window.open(url,'',' width=900, height=700');
}