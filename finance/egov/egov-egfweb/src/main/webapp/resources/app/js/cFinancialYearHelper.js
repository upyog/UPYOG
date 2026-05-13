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




$(document).ready(function(){
	console.log("Browser Language ",navigator.language);
	$.i18n.properties({ 
		name: 'message', 
		path: '/services/EGF/resources/app/messages/', 
		mode: 'both',
		async: true,
	    cache: true,
		language: getLocale("locale"),
		callback: function() {
			console.log('File loaded successfully');
		}
	});
	
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
jQuery('#btnsearch').click(function(e) {

	callAjaxSearch();
});



function getFormData($form){
	var unindexed_array = $form.serializeArray();
	var indexed_array = {};

	$.map(unindexed_array, function(n, i){
		indexed_array[n['name']] = n['value'];
	});

	return indexed_array;
}

function validateFields(){
		var tbl=document.getElementById("fiscalPeriodTable");
		var lastRow = (tbl.rows.length)-1;
		var startingDate=getControlInBranch(tbl.rows[1],'startDate').value;
		var finYearStartDate=document.getElementById("startingDate").value;
		var lastRowFiscalName = getControlInBranch(tbl.rows[lastRow],'name').value;
		var lastRowEndDate=getControlInBranch(tbl.rows[lastRow],'endDate').value;
		var finYearEndDate=document.getElementById("endingDate").value;
		
		if(startingDate!=finYearStartDate){
			//bootbox.alert('Enter valid Start date');
			bootbox.alert($.i18n.prop('msg.enter.valid.startdate'));
			getControlInBranch(tbl.rows[1],'startDate').value='';
			getControlInBranch(tbl.rows[1],'startDate').focus();
			return false;
		}
		if(lastRowEndDate!=finYearEndDate)
		{
			bootbox.alert('Enter valid End date');
			bootbox.alert($.i18n.prop('msg.enter.valid.enddate'));
			getControlInBranch(tbl.rows[lastRow],'endDate').value='';
			getControlInBranch(tbl.rows[lastRow],'endDate').focus();
			return false;
		}
		if(lastRowFiscalName==""){
			//bootbox.alert('Enter Fiscal Period Name');
			bootbox.alert($.i18n.prop('msg.enter.fiscal.period.name'));
			getControlInBranch(tbl.rows[1],'name').value='';
			getControlInBranch(tbl.rows[1],'name').focus();
			return false;
		}
	    var previousRow = lastRow - 1;
	    if(previousRow>0){
			var lastRowStartDate=getControlInBranch(tbl.rows[lastRow],'startDate').value;
		    var previousEndDate=getControlInBranch(tbl.rows[previousRow],'endDate').value;
		    if( compareDate(formatDate6(previousEndDate),formatDate6(lastRowStartDate)) == -1 )
			{
			    // bootbox.alert('Enter valid Start Date');
			     bootbox.alert($.i18n.prop('msg.enter.valid.startdate'));
				 getControlInBranch(tbl.rows[lastRow],'startDate').value='';
				 getControlInBranch(tbl.rows[lastRow],'startDate').focus();
				 return false;
			}
	
	    }
	
	 return true;

}



function addRow1() 
{
	var table = document.getElementById('fiscalPeriodTable');

	if(!checkforNonEmptyPrevRow())
		return false;


	var rowCount = table.rows.length;
	var row = table.insertRow(rowCount);
	var counts = rowCount - 1;
	var newRow = document.createElement("tr");

	var newCol = document.createElement("td");
	newRow.appendChild(newCol);

	var cell1 = row.insertCell(0);
	var fiscalName = document.createElement("input");
	var att = document.createAttribute("class");
	fiscalName.setAttributeNode(att); 
	fiscalName.setAttribute("class","form-control text-right patternvalidation");
	fiscalName.type = "text";
	fiscalName.setAttribute("required", "required");
	fiscalName.setAttribute("id","name");
	fiscalName.setAttribute("maxlength", "10");
	fiscalName.name = "cFiscalPeriod[" + counts + "].name";
	cell1.appendChild(fiscalName);

	var newCol = document.createElement("td");
	newRow.appendChild(newCol);
	var cell2 = row.insertCell(1);
	var fiscalDate = document.createElement("input");
	fiscalDate.setAttribute("class","form-control datepicker");
	fiscalDate.setAttribute("id","startDate");
	fiscalDate.type = "text";
	fiscalDate.setAttribute("required", "required");
	fiscalDate.className = "form-control datepicker";
	fiscalDate.setAttribute("maxlength", "10");
	fiscalDate.setAttribute("data-inputmask","'mask': 'd/M/y'");
	fiscalDate.name = "cFiscalPeriod[" + counts + "].startingDate";

	
	cell2.appendChild(fiscalDate);

	var newCol = document.createElement("td");
	newRow.appendChild(newCol);
	var cell3 = row.insertCell(2);
	var att = document.createAttribute("class");
	att.value="form-control datepicker";
	var fiscalDate = document.createElement("input");
	fiscalDate.setAttribute("class","form-control datepicker");
	fiscalDate.setAttribute("id","endDate");
	fiscalDate.type = "text";
	fiscalDate.setAttribute("required", "required");
	
	fiscalDate.setAttribute("maxlength", "10");
	fiscalDate.setAttribute("data-inputmask","'mask': 'd/M/y'");
	
	fiscalDate.name = "cFiscalPeriod[" + counts + "].endingDate";
	cell3.appendChild(fiscalDate);

	var newCol = document.createElement("td");
	newRow.appendChild(newCol);
	var cell4 = row.insertCell(3);

	var addButton = document.createElement("input");
	addButton.type = "button";
	addButton.setAttribute("class", "btn btn-primary");
	addButton.setAttribute("onclick", "return addRow1();");
	addButton.setAttribute("value", "Add");
	cell4.appendChild(addButton);

	var x = document.createElement("LABEL");
	var t = document.createTextNode(" ");
	cell4.appendChild(t);

	var hiddenId = document.createElement("input");
	hiddenId.type = "hidden";
	hiddenId.id = "cFiscalPeriod[" + counts + "].id";
	hiddenId.setAttribute("value", "${cFiscalPeriod[" + counts + "].id}");
	cell4.appendChild(hiddenId);
	
	jQuery(".datepicker").datepicker({
		format: "dd/mm/yyyy",
		autoclose:true
	}); 

}

function compareDate(dt1, dt2){			
	/*******		Return Values [0 if dt1=dt2], [1 if dt1<dt2],  [-1 if dt1>dt2]     *******/
	var d1, m1, y1, d2, m2, y2, ret;
	dt1 = dt1.split('/');
	dt2 = dt2.split('/');
	ret = (dt2[2]>dt1[2]) ? 1 : (dt2[2]<dt1[2]) ? -1 : (dt2[1]>dt1[1]) ? 1 : (dt2[1]<dt1[1]) ? -1 : (dt2[0]>dt1[0]) ? 1 : (dt2[0]<dt1[0]) ? -1 : 0 ;										
	return ret;
}

function validateStartDate() {
	var startDate = document.getElementById('startingDate').value;
	var finYearStartDate = document.getElementById('finYearStartDate').value;
	var currDate = new Date();
	var currentDate = currDate.getDate() + "/" + (currDate.getMonth()+1) + "/" + currDate.getFullYear() ;
	/*To check whether Start Date is Greater than End Date*/
	if(startDate!=finYearStartDate){
		if( compareDate(formatDate6(finYearStartDate),formatDate6(startDate)) == -1 )
		{
			//bootbox.alert('Enter valid Start Date');
			bootbox.alert($.i18n.prop('msg.enter.valid.startdate'));
			document.getElementById('endingDate').value='';
			document.getElementById('endingDate').focus();
			return false;
		}
	}
	return true;
}

function validateEndDate() {
	var strtDate = document.getElementById('startingDate').value;
	var endDate = document.getElementById('endingDate').value;
	var currDate = new Date();
	var currentDate = currDate.getDate() + "/" + (currDate.getMonth()+1) + "/" + currDate.getFullYear() ;
	/*To check whether Start Date is Greater than End Date*/
	if( compareDate(formatDate6(strtDate),formatDate6(endDate)) == -1 )
	{
		//bootbox.alert('Start Date cannot be greater than End Date');
		bootbox.alert($.i18n.prop('msg.startdate.enddate.greater'));
		document.getElementById('endingDate').value='';
		document.getElementById('endingDate').focus();
		return false;
	}
}

function formatDate6(dt){
	if(dt==null || dt==''  || dt=="" )return '';
	var array = dt.split("/");
	var mon=array[1];
	var day=array[0];
	var year=array[2].substring(0,4);			
	dt = day+"/"+mon+"/"+year;			
	return dt;	
}

function getControlInBranch(tableobj, columnName) {
	if (!tableobj || !(tableobj.getAttribute)) {
		return null;
	}
	// check if the object itself has the name
	if (tableobj.getAttribute("id") == columnName) {
		return tableobj;
	}

	// try its children
	var children = tableobj.childNodes;
	var child;
	if (children && children.length > 0) {
		for (var i = 0; i < children.length; i++) {
			child = this.getControlInBranch(children[i], columnName);
			if (child) {
				return child;
			}
		}
	}
	return null;
}

function validateFiscalEndDate() {

	var endDate = document.getElementById('endingDate').value;

	if( endDate == '' )
	{
		//bootbox.alert('Enter Ending Date');
		bootbox.alert($.i18n.prop('msg.enter.endingdate'));
		document.getElementById('endDate').value='';
		document.getElementById('endDate').focus();
		return false;
	}

	//var fiscalEndDate= document.getElementById('endingDate').value
}

function callAjaxSearch() {
	drillDowntableContainer = jQuery("#resultTable");		
	jQuery('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer
	.dataTable({
		ajax : {
			url : "/services/EGF/cfinancialyear/ajaxsearch/"+$('#mode').val(),      
			type: "POST",
			"data":  getFormData(jQuery('form'))
		},
		"fnRowCallback": function (row, data, index) {
			$(row).on('click', function() {
				console.log(data.id);
				window.open('/services/EGF/cfinancialyear/'+ $('#mode').val() +'/'+data.id,'','width=800, height=600');
			});
		},
		"bDestroy" : true,
		dom: "<'row'<'col-xs-12 pull-right'f>r>t<'row buttons-margin'<'col-md-3 col-xs-6'i><'col-md-3  col-xs-6'l><'col-md-3 col-xs-6'B><'col-md-3 col-xs-6 text-right'p>>",
		buttons: [
				  {
				    extend: 'print',
				    title: 'Financial Year',
				    filename: 'Financial Year',
					 customize: function(win) {

					        var ulbName = $('#ulbName').val() || '';

					        var titleContainer =
					            '<div style="text-align:center; color:#1F4E79;">' +
					                '<div style="font-size:16px; font-weight:bold; color:#1F4E79;">Government of Jammu & Kashmir</div>' +
					                '<div style="font-size:14px; font-weight:bold; margin-top:2px; color:#1F4E79;">Housing and Urban Development Department</div>' +
					                '<div style="font-size:13px; font-weight:bold; margin-top:2px; color:#1F4E79;">' + ulbName + '</div>' +
					            '</div>';

					        var currentDate = new Date().toLocaleDateString();
					        var currentTime = new Date().toLocaleTimeString();

					        var dateTimeContainer =
					            '<div style="font-size:10px; font-weight:bold; text-align:right;">' +
					                'Date: ' + currentDate + '<br>' +
					                'Time: ' + currentTime +
					            '</div>';

					        var header =
					            '<div style="width:100%; margin-bottom:10px;">' +

					                '<table style="width:100%; border:none !important;">' +
					                    '<tr>' +

					                        '<td style="width:20%; border:none !important;"></td>' +

					                        '<td style="width:60%; border:none !important;">' +
					                            titleContainer +
					                        '</td>' +

					                        '<td style="width:20%; border:none !important;">' +
					                            dateTimeContainer +
					                        '</td>' +

					                    '</tr>' +
					                '</table>' +

					                '<hr style="border:1px solid black;">' +

					            '</div>';

					        $(win.document.body).prepend(header);

					        $(win.document.body).find('h1').remove();

					        $(win.document.body).css('font-size', '8pt');

					        $(win.document.body).find('table')
					            .css('width', '100%')
					            .css('border-collapse', 'collapse');

					        $(win.document.body).find('table th, table td')
					            .css('padding', '5px');

					        $(win.document.body).find('table:first td')
					            .css('border', 'none');
					    }
				},{
				    extend: 'pdf',
				    title: 'Financial Year',
				    filename: 'Financial Year',
					customize: function(doc) {


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
				    message : 'Financial Year',
				    filename: 'Financial Year'
				}
				],
		aaSorting : [],				
		columns : [ { 
			"data" : "finYearRange", "sClass" : "text-left"} ,{ 
				"data" : "startingDate", "sClass" : "text-left"} ,{ 
					"data" : "endingDate", "sClass" : "text-left"} ,{ 
							"data" : "isActiveForPosting", "sClass" : "text-left"} ,{ 
								"data" : "isClosed", "sClass" : "text-left"} ,{ 
									"data" : "transferClosingBalance", "sClass" : "text-left"}]				
	});
}

$('#buttonSubmit').click(function(e){
	if ($("#isFinYrCloses").is(":checked")) {
	e.preventDefault();
	var id=$("#finYearRange option:selected").attr('value');
	if ($('form').valid()) {
	
		$.ajax({
			
			url : '/services/EGF/cfinancialyear/validatedIsClosed/'+id,
			type : "get",
			async : false,
			success: function (res) {
				if (res == "true")
				{
					bootbox.confirm({
						message: 'Once a year is closed there cannot be any posting made for the selected financial year. Are you sure all the accounts are verified and you are ready to proceed?. ',
						buttons: {
							'cancel': {
								label: 'No',
								className: 'btn-default pull-right'
							},
							'confirm': {
								label: 'Yes',
								className: 'btn-danger pull-right'
							}
						},
						callback: function(result) {
							if (result) {
								$("#cFinancialYearform").submit();
							}
						}
					});
					
				} else if (res == "false") {
					bootbox.alert($.i18n.prop('msg.transfer.closing.balance.this.year'));
				}
			},
			error : function (res){
				bootbox.alert(res);
			}
		})
		}
	}
	
});