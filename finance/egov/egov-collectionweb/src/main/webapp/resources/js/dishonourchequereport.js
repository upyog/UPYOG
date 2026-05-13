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
		var heading= "Dishonoured Cheque Report \n";
		if($("#instrumentTypeId").val()){
			heading = heading  + ", For Payment Mode :" +  $("#instrumentTypeId option:selected").text();
		}
		if($("#bankAccountId").val() != 0 && $("#bankAccountId").val()){
			heading = heading  + " For Bank & Account Number:" +  $("#bankAccountId option:selected").text();
			
		}
		if($("#serviceId").val() != 0 && $("#serviceId").val()){
			heading = heading  + " For Service:" +  $("#serviceId option:selected").text();
			
		}
		if($("#instrumentNumberId").val()){
			heading = heading  + " For Cheque Number :" +  $("#instrumentNumberId").val();
		}
		if($("#fromDateId").val()){
			heading = heading  + " Cheque Dishonored From Date : " +  $("#fromDateId").val();
		}
		if($("#toDateId").val()){
			heading = heading  + " Cheque Dishonored To Date : " +  $("#toDateId").val();
		}
		return heading;

	}

	jQuery('#btnsearch').click(function(e) {
		var heading = prepareHeading().replace(/\n/g, "<br />");;
		$("#dishonouredReportHeading").html(heading);
		callAjaxSearch();
	});

	function callAjaxSearch() {
		var bankAccountId = $("#bankAccountId").val();
		var fromDate = $("#fromDateId").val();
		var toDate = $("#toDateId").val();
		if(fromDate==""){
			bootbox.alert(fromDateAlertMsg);
			return false;
		}else if(toDate==""){
			bootbox.alert(toDateAlertMsg);
			return false;
		}
		if(fromDate != "" && toDate != ""){
			fromDates = Date.parse(fromDate);
		    toDates = Date.parse(toDate);
			if(fromDates>toDates){
			bootbox.alert(fromDateToDateAlertMsg);
			return false;
		}
	}

		var fileName = 'Dishonoured Cheque Report';
		var drillDowntableContainer = $("#resultTable");
		$('.report-section').removeClass('display-hide');
		var data = getFormData(jQuery('form'));
		$('.error-section').addClass('display-hide');
		var columns = [
			{"data" : "id","sClass" : "text-center"},
			{"data" : "receiptNumber","sClass" : "text-left",
				fnCreatedCell : function(nTd,sData, oData, iRow,iCol) {				
					$(nTd).html('<a href="javascript:void(0);" onclick="viewReceipt(\''+ oData.receiptSourceUrl +'\',\''+ oData.service + '\')">' + oData.receiptNumber + '</a>');
				}
			},
			{"data" : "transactionDate","sClass" : "text-left",
				"type": "datetime",
				"render": function (value) {
					if (value === null) return "";
					return moment(value).format('DD/MM/YYYY');
				}
			},
			{"data" : "instrumentNumber","sClass" : "text-center"},

			{"data" : "bankName","sClass" : "text-left"},
			{"data" : "dishonorDate","sClass" : "text-left",
				"type" :   'datetime',
				"render": function (value) {
					if (value === null) return "";
					return moment(value).format('DD/MM/YYYY');
				}
			},
			{"data" : "instrumentAmount","sClass" : "text-left"},
			{"data" : "dishonorReason","sClass" : "text-left"}
			];
		$.fn.dataTable.ext.errMode = 'none';
		var heading = prepareHeading();
		reportdatatable = drillDowntableContainer.DataTable({
			ajax : {
				url : '/services/collection/report/dishonouredcheque/_search',
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
			buttons : [ 			{
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
																						}, {
				extend : 'pdfHtml5',
				title : heading,
				filename : fileName,
				orientation : 'landscape',
				pageSize : 'A4',
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

				    var header = {
				        columns: [
				            { text: '' },
				            titleContainer,
				            dateTimeContainer
				        ]
				    };


				    doc.content.splice(0, 0, header);

				    if (doc.content[1]) {
				        doc.content[1].margin = [20, 0, 20, 0];
				    }
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
					columns: [1, 2, 3, 4, 5,6]
				}
			}, {
				extend : 'excelHtml5',
				message : heading,
				filename : fileName,
				messageTop : heading,
				stripNewlines: false,
				exportOptions: {
					columns: [1, 2, 3, 4, 5,6]
				}
			} ],
			aaSorting : [],
			order: [[ 5, 'asc' ]],
			columns : columns,
			"fnInitComplete": function(oSettings, json) {
				toggleColumnBasedOnInstrumentType()
			}
		});
		console.log(reportdatatable);
		reportdatatable.on( 'order.dt search.dt', function () {
			reportdatatable.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
				cell.innerHTML = i+1;
			} );
		} ).draw();
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
		url : "/services/collection/report/dishonouredcheque/service/"+bankAccount,
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



function viewReceipt(receiptSourceUrl){
	event.preventDefault();
	window.open(receiptSourceUrl,'',' width=900, height=700');
}

