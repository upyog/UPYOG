var $parentId = 0;
var tableContainer;
var reportdatatable;
$(document).ready(function(){
	$('#coaReportResult-table').hide();
	$('#reportgeneration-header').hide();
	
	function getFormData($form) {
		var unindexed_array = $form.serializeArray();
		var indexed_array = {};
		$.map(unindexed_array, function(n, i) {
			indexed_array[n['name']] = n['value'];
		});
		return indexed_array;
	}
	
	function prepareHeading(){
		var heading= "COA Report Result ";
		if($("#type").val()){
			heading = heading  + " For Type:" +  $("#type option:selected").text();
		}
		if($("#accountCodeId").val()){
			heading = heading  + " For Account Code :" +  $("#accountCodeId").val();
		}
		if($("#majorCode").val()){
			heading = heading  + " For Major Code :" +  $("#majorCode option:selected").text();
			
		}
		if($("#minorCode").val()){
			heading = heading  + " For Minor Code :" +  $("#minorCode option:selected").text();
			
		}
		if($("#purposeId").val()){
			heading = heading  + " For Purpose :" +  $("#purposeId option:selected").text();
			
		}
		if($("#detailTypeId").val()){
			heading = heading  + " For Account detail type : " +  $("#detailTypeId option:selected").text();
			
		}
		if($("#active").val()){
			heading = heading  + " For Active for Posting : " +  $("#active option:selected").text();
			
		}
		if($("#functionReqd").val()){
			heading = heading  + " For Function Required : " +  $("#functionReqd option:selected").text();
			
		}
		if($("#budgetCheckReq").val()){
			heading = heading  + " For Budget Required : " +  $("#budgetCheckReq option:selected").text();
			
		}
			
		$("#coareportheading").html(heading);

	return heading;
			
	}
	
jQuery('#btnsearch').click(function(e) {
	prepareHeading();
	callAjaxSearch();
});

function callAjaxSearch() {
	drillDowntableContainer = $("#resultTable");
	$('.report-section').removeClass('display-hide');
	var heading1 = prepareHeading();
	
	reportdatatable = drillDowntableContainer
			.dataTable({
				ajax : {
					url : '/services/EGF/masters/coareport/coareportResult/',
					type : "get",
					"data" : getFormData(jQuery('form'))         
				},
				"bDestroy" : true,
				dom: "<'row'<'col-xs-12 pull-right'f>r>t<'row buttons-margin'<'col-md-3 col-xs-6'i><'col-md-3  col-xs-6'l><'col-md-3 col-xs-6'B><'col-md-3 col-xs-6 text-right'p>>",
				buttons: [
						  {
						    extend: 'print',
						    title: ""+heading1+"",
						    filename: 'COA Report'
						},{
						    extend: 'pdf',
						    title: ""+heading1+"",
						    filename: 'COA Report',
							
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
						},{
						    extend: 'excel',
						    message : ""+heading1+"",
						    filename: 'COA Report'
						}
						],
				aaSorting : [],
				columns : [ 
					{
						"data" : "majorcode",
						"sClass" : "text-left"
					},
					{
						"data" : "minorcode",
						"sClass" : "text-left"
					},{
					"data" : "accountcode",
					"sClass" : "text-left"
				},
				{
					"data" : "accountname",
					"sClass" : "text-left"
				},
				{
					"data" : "type",
					"sClass" : "text-left"
				}, {
					"data" : "purpose",
					"sClass" : "text-left"
				}, {
					"data" : "accountdetailtype",
					"sClass" : "text-left"
				}, {
					"data" : "isActiveForPosting",
					"sClass" : "text-left"
				} ]
			});
}




var accountCodeName = new Bloodhound({
	datumTokenizer : function(datum) {
		return Bloodhound.tokenizers.whitespace(datum.value);
	},
	queryTokenizer : Bloodhound.tokenizers.whitespace,
	remote : {
		url : '/services/EGF/masters/coareport/ajax/getAccountCodeAndName',
		replace : function(url, uriEncodedQuery) {
			return url + '?accountCode=' + uriEncodedQuery;

		},
		filter: function (data) {
            return $.map(data, function (ct) {
                return {
                    id: ct.id,
                    name: ct.name,
                    glcode: ct.glcode,
                    glcodesearch: ct.glcode+' - '+ct.name
                };
            });
        }
	}
});


accountCodeName.initialize();
$('#accountCode').typeahead({
	hint : true,
	highlight : true,
	minLength : 3
}, {
	displayKey : 'glcodesearch',
	source : accountCodeName.ttAdapter()
}).on('typeahead:selected', function (event, data) {
	$("#accountCode").val(data.glcode);
	$("#accountCodeId").val(data.glcode);
});

});

$('#majorCode').change(function () {
	$('#minorCode').append($('<option>').text('Select from below').attr('value', ''));
	loadMinorCode($('#majorCode').val());
});

function loadMinorCode(parentId){
	if (!parentId) {
		$('#minorCode').empty();
		$('#minorCode').append($('<option>').text('Select from below').attr('value', ''));
		return;
	} else {
		
		$.ajax({
			method : "GET",
			url : "/services/EGF/masters/coareport/ajax/getMinorCode",
			data : {
				parentId : parentId
			},
			async : true
		}).done(
				function(response) {
					$('#minorCode').empty();
					var output = '<option value="">Select</option>';
					$.each(response, function(index, value) {
						output += '<option value='
								+ value.id + '>'
								+ value.glcode + ' - '
								+ value.name + '</option>';
					});
					$('#minorCode').append(output);
				});

		
	}
}



