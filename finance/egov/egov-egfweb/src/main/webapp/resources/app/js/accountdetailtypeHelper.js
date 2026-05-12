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

jQuery('#btnsearch').click(function(e) {

	callAjaxSearch();
});

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
	jQuery('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer
	.dataTable({
		ajax : {
			url : "/services/EGF/accountdetailtype/ajaxsearch/"
				+ $('#mode').val(),
				type : "POST",
				"data" : getFormData(jQuery('form'))
		},
		"fnRowCallback" : function(row, data, index) {
		
			$(row).on('click', function() {
				console.log(data.id);
				window.open('/services/EGF/accountdetailtype/'+ $('#mode').val() +'/'+data.id,'','width=800, height=600');
			});
			

		},
		"bDestroy" : true,
		dom: "<'row'<'col-xs-12 pull-right'f>r>t<'row buttons-margin'<'col-md-3 col-xs-6'i><'col-md-3  col-xs-6'l><'col-md-3 col-xs-6'B><'col-md-3 col-xs-6 text-right'p>>",
		buttons: [
			{
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
								            '<title>Work Order Master</title>' +
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
								                            'Work Order Master' +
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
				    title: 'Subledger Category Master',
				    filename: 'Subledger Category Master',
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
				},{
				    extend: 'excel',
				    message : 'Subledger Category Master',
				    filename: 'Subledger Category Master'
				}
				],
		aaSorting : [],
		columns : [ {
			"data" : "name",
			"sClass" : "text-left"
		}, {
			"data" : "description",
			"sClass" : "text-left"
		}, {
			"data" : "isactive",
			"sClass" : "text-right"
		}, {
			"data" : "fullQualifiedName",
			"sClass" : "text-left"
		} ]
	});
}