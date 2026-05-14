/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

function getFormData($form) {
	var unindexed_array = $form.serializeArray();
	var indexed_array = {};

	$.map(unindexed_array, function(n, i) {
		indexed_array[n['name']] = n['value'];
	});

	return indexed_array;
}

function callAjaxSearch() {
	drillDowntableContainer = jQuery("#resultTable");
	console.log($("#reBudget option:selected").text());
	console.log($("#referenceBudget").html());
	$('#REBudgetName1').html($("#reBudget option:selected").text());
	$('#REBudgetName2').html($("#reBudget option:selected").text());
	$('#BEBudgetName1').html($("#referenceBudget").html());
	$('#BEBudgetName2').html($("#referenceBudget").html());
	$('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer
			.dataTable({
				ajax : {
					url : "/services/EGF/budgetuploadreport/ajaxsearch",
					type : "POST",
					"data" : getFormData(jQuery('form'))
				},
				"bDestroy" : true,
				"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-xs-3'i><'col-xs-3 col-right'l><'col-xs-3 col-right'<'export-data'T>><'col-xs-3 text-right'p>>",
				"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
				"oTableTools" : {
					"sSwfPath" : "../../../../../../egi/resources/global/swf/copy_csv_xls_pdf.swf",
					"aButtons" : [ {
						"sExtends" : "pdf",
						"sTitle" : "Budget Upload Report Result",
						
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
						"sExtends" : "xls",
						"sTitle" : "Budget Upload Report Result"
					}, 					{
					    "sExtends" : "text",
					    "sButtonText" : "Print",
					    "fnClick" : function(nButton, oConfig) {
					        var reBudgetName = $("#reBudget option:selected").text();
					        var beBudgetName = $("#referenceBudget").text();
					        var currentDate = new Date().toLocaleDateString();
					        var currentTime = new Date().toLocaleTimeString();

					        var printContent = `
					            <html>
					            <head>
					                <title>Budget Upload Report Result</title>
					                <style>
					                    body { font-family: Arial, sans-serif; font-size: 11px; }
					                    .print-header { text-align: center; margin-bottom: 12px; }
					                    .print-header h2 { color: #1F4E79; margin: 3px 0; font-size: 15px; }
					                    .print-header h3 { color: #333; margin: 3px 0; font-size: 12px; }
					                    .print-meta { text-align: right; font-size: 11px; margin-bottom: 10px; }
					                    table { width: 100% !important; border-collapse: collapse; }
					                    th, td { border: 1px solid #aaa !important; padding: 5px 7px !important; }
					                    th { background-color: #dce6f1 !important; color: #1F4E79 !important; text-align: center !important; }
					                    td:nth-child(n+5) { text-align: right !important; }
					                </style>
					            </head>
					            <body>
					                <div class="print-header">
					                    <h2>Government of Jammu &amp; Kashmir</h2>
					                    <h2>Housing and Urban Development Department</h2>
					                    <h3>Budget Upload Report Result</h3>
					                </div>
					                <div class="print-meta">
					                    <strong>Date:</strong> ${currentDate} &nbsp;&nbsp;
					                    <strong>Time:</strong> ${currentTime}
					                </div>
					                <table>
					                    <thead>
					                        <tr>
					                            <th rowspan="2">Fund</th>
					                            <th rowspan="2">Department</th>
					                            <th rowspan="2">Function</th>
					                            <th rowspan="2">Budget Head</th>
					                            <th colspan="2">${reBudgetName}</th>
					                            <th colspan="2">${beBudgetName}</th>
					                        </tr>
					                        <tr>
					                            <th>Budgeted Amount</th>
					                            <th>Planning Amount</th>
					                            <th>Budgeted Amount</th>
					                            <th>Planning Amount</th>
					                        </tr>
					                    </thead>
					                    <tbody>`;

					        $('#resultTable tbody tr').each(function() {
					            printContent += '<tr>';
					            $(this).find('td').each(function() {
					                printContent += '<td>' + $(this).text() + '</td>';
					            });
					            printContent += '</tr>';
					        });

					        printContent += `
					                    </tbody>
					                </table>
					            </body>
					            </html>`;

								var printWindow = window.open('', '_blank');
								printWindow.document.write(printContent);
								printWindow.document.close();
								printWindow.focus();
								printWindow.print();
					    }
					} ]
				},
				aaSorting : [],
				columns : [ {
					"data" : "fundCode",
					"sClass" : "text-left"
				}, {
					"data" : "deptCode",
					"sClass" : "text-left"
				}, {
					"data" : "functionCode",
					"sClass" : "text-left"
				}, {
					"data" : "glCode",
					"sClass" : "text-left"
				}, {
					"data" : "approvedReAmount",
					"sClass" : "text-right"
				}, {
					"data" : "planningReAmount",
					"sClass" : "text-right"
				}, {
					"data" : "approvedBeAmount",
					"sClass" : "text-right"
				}, {
					"data" : "planningBeAmount",
					"sClass" : "text-right"
				} ]
			});
}

$('#reBudget').change(function() {
	$('#referenceBudget').html('');
	console.log($('#reBudget').val());
	if($('#reBudget').val()!=''){
		$.ajax({
			url : '/services/EGF/budgetuploadreport/ajax/getReferenceBudget',
			type : "get",
			data : {
				budgetId : $('#reBudget').val()
			},
			success : function(data, textStatus, jqXHR) {
				$('#referenceBudget').html(data)
			},
			error : function(jqXHR, textStatus, errorThrown) {
				bootbox.alert("Error while getting reference budget");
			}
		});
	}
});