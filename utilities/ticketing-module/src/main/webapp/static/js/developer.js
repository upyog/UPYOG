/**
 * 
 */

var BUCKET_URL = "https://pmidc-ticket-images.s3.ap-south-1.amazonaws.com/ticket-images/";


/* Registration Form**/

function validateRegForm() {
	  var uName = document.registerForm.userName.value;
	  var uEmail = document.registerForm.userEmail.value;
	  var uMobile = document.registerForm.userMobileNo.value;
	  var uTown = document.registerForm.ulbId.value;
	  var uPass = document.registerForm.userPassword.value;
	  var uCPass = document.registerForm.userCPassword.value;
	  var regex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	  var MobileRegex = /^([0|\+[0-9]{1,5})?([7-9][0-9]{9})$/;
	  if(uName=="")
		{
		  alert('Please Enter Name!');
		document.registerForm.userName.focus();
		return false
		}
	  else if(uEmail=="" || !regex.test(uEmail))
		{
		  alert('Please Enter Valid Email!');
		document.registerForm.userEmail.focus();
		return false
		}
	  else if(uMobile=="" || !MobileRegex.test(uMobile))
		{
		  alert('Please Enter Valid Mobile No!');
		document.registerForm.userMobileNo.focus();
		return false
		}
	  else if(uTown=="0")
		{
		  alert('Please Select ULB First!');
		document.registerForm.ulbId.focus();
		return false
		}
	else if(uPass=="" || uPass.trim().length<3)
		{
		alert('Please Enter Valid Password!');
		document.registerForm.userPassword.focus();
		return false;
		}
	else if(uPass!=uCPass)
	{
	alert('Confirm Password mismatch!');
	document.registerForm.userPassword.focus();
	return false;
	}
	else
		{
	    return true;
	  }
	}

/* Login Form**/

function validateLoginForm() {
	  var uEmail = document.LoginForm.userEmail.value;
	  var uPass = document.LoginForm.userPassword.value;
	  var regex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	  if(uEmail=="" || !regex.test(uEmail))
		{
		  alert('Please Enter Valid Email!');
		document.LoginForm.userEmail.focus();
		return false
		}
	else if(uPass=="" || uPass.trim().length<3)
		{
		alert('Please Enter Valid Password!');
		document.LoginForm.userPassword.focus();
		return false;
		}
	else
		{
	    return true;
	  }
	}



/* Setting Form**/

function openSettingForm()
{

	$('#dsboard').hide();
	$('#genrateTicketForm').hide();
	$('#settingForm').show();
	$('#table-div').hide();
	/**/
}

function resetSettingForm()
{
	 $('#old-pass').val('');
	 $('#new-pass').val('');
	 $('#con-pass').val('');	
}

/*function validateSettingForm11() {
	
	  var oldPass = $('#old-pass').val();
	  var newPass = $('#new-pass').val();
	  var conPass = $('#con-pass').val();
	  if(oldPass=="" || oldPass.length<3)
		{
		  alert('Please Enter Valid Old Password!');
		  $('#old-pass').focus();
		}
	else if(newPass=="" || newPass.trim().length<3 || newPass!=conPass)
		{
		alert('New Password and Confirm Password Not Match!');
		$('#con-pass').focus();
		}
	else
		{
		$.ajax({
	        url: "update-setting",
	        data:{oldPassword:oldPass, newPassword:conPass},
	        type: "POST",
	        success: function (result)
	        {
	        	
//	        	alert(result);
	        
	        	
if(result==="login")
	{
	window.location.href = "login";
	}
else
	{
	$('#errorMsg').html(result);
	}
	            },
	            error: function (xhr, ajaxOptions, thrownError) {
	            	window.location.href = "login";
	            }
	        });
	  }
	}*/


//Multile Image show on pages

/* $(document).ready(function() {
	
	  if (window.File && window.FileList && window.FileReader) {
	
	    $("#files").on("change", function(e) {
	    
	      var files = e.target.files,
	        filesLength = files.length;
	      for (var i = 0; i < filesLength; i++) {
	        var f = files[i]
	        var fileReader = new FileReader();
	        fileReader.onload = (function(e) {
	          var file = e.target;
	          $("<span class=\"pip\">" +
	            "<img class=\"imageThumb\" src=\"" + e.target.result + "\" title=\"" + file.name + "\"/>" +
	            "<br/><span class=\"remove\">Remove image</span>" +
	            "</span>").insertAfter("#files");
	          $(".remove").click(function(){
	            $(this).parent(".pip").remove();
	          });
	          
	       
	        });
	        fileReader.readAsDataURL(f);
	      }
	    });
	  } else {
	    alert("Your browser doesn't support to File API")
	  }
	}); */


//Raised ticket list in table form

function raisedTicketList(status, heading){
	
	$('#loading').show();
	$.ajax({
        url: "raise-ticket",
        data:{status:status, heading:heading},
        type: 'GET',
        datatype:"JSON",
        contentType: "application/json",
        success: function (result)
        {
        	$('#loading').hide();
        	$('#dsboard').hide();
        	$('#genrateTicketForm').hide();
        	$('#settingForm').hide();
        	$('#table-div').show();
           $('#table-div').html(result);
            $(document).ready(function() {
//        	    $('.example').DataTable();
            	  $('.example').DataTable( {
            	        "order": [[ 0, "desc" ]]
            	    } );
        	} );

           
            },
            error: function (xhr, ajaxOptions, thrownError) {
           
            }
        });
    }

     
//Assigned ticket list in table form

function assignedTicketList(status, heading){
	
	$('#cmnTktSec').hide();
	$('#loading').show();
	$.ajax({
        url: "assign-ticket",
        data:{status:status, heading:heading},
        type: 'GET',
        datatype:"JSON",
        contentType: "application/json",
        success: function (result)
        {
        	//console.log(result);
        	$('#loading').hide();
        	$('#dsboard').hide();
        	$('#genrateTicketForm').hide();
        	$('#settingForm').hide();
        	$('#table-div').show();
           $('#table-div').html(result);
           
           $(document).ready(function() {
//        	    $('.example').DataTable();
        	   $('.example').DataTable( {
        	        "order": [[ 0, "desc" ]]
        	    } );
        	} );

           
            },
            error: function (xhr, ajaxOptions, thrownError) {
           
            }
        });
    }

//Open genrate ticket form

function genrateTicketForm() {
	$('#dsboard').hide();
	$('#table-div').hide();
	$('#settingForm').hide();
	$('#genrateTicketForm').show();
}

//View ticket details 

function viewAssignedTicket(tktId, userId, tyId, tyVal, userType, location, operation)
{
	debugger
	var htm1 = '';
	$('#loading').show();
	$("#comment").val('');
	$.ajax({
        url: "view-ticket",
        data:{tkt_id:tktId},
        type: 'GET',
        datatype:"json",
        contentType: "application/json; charset=utf-8",
        success: function (responce)
        {
        	var data = JSON.stringify(responce);
        	var obj = JSON.parse(data);
        	var ext = null;
        	htm1 = htm1+"<table class='det' >" +
			"<tr><td>PROJECT</td><td class='dtd'>"+obj.project+"</td></tr>" +
			"<tr><td>TICKET-NO</td><td class='dtd'>TKT-"+obj.tktId+"</td></tr>" +
			"<tr><td>ISSUE CATEGORY</td><td class='dtd'>"+obj.issueCategoryName+"</td></tr>" +
			"<tr><td>ISSUE TYPE</td><td class='dtd'>"+obj.tktType+"</td></tr>" +
			"<tr><td>SUMMARY</td><td class='dtd'>"+obj.tktSummary+"</td></tr>" +
			"<tr><td>RAISED BY</td><td class='dtd'>"+obj.raiserName +"</td></tr>" +
			"<tr><td>ENVIRONMENT</td><td class='dtd'>"+obj.environment+"</td></tr>" +
			"<tr><td>ULB NAME</td><td class='dtd'>"+obj.ulbName +"</td></tr>" +
			"<tr><td>ASSIGNED DATE</td><td class='dtd'>"+obj.raisedDate+"</td></tr>";
        	 if(obj.tktTypeId==5){
        		 htm1 = htm1+ "<tr><td>RESOLVED DATE</td><td class='dtd'>"+obj.updatedDate+"</td></tr>";
        		 $('#updateDiv').hide();
        	 }
					 htm1 = htm1+"<tr><td>FEEDBACK</td><td class='dtd'>"+obj.issueFeedbackName+"</td></tr>";
					
        	if(obj.attachment==null || obj.attachment=="null")
    		{
    		//htm1 = htm1+"<tr><td>IMAGES</td><td class='dtd'><a href='#'><img src='"+BUCKET_URL+""+obj.attachment+"' alt='image' width='80px' height='90px'/></a></td> ";
    		}
    	else
    		{
					obj.attachment=	(obj.attachment.includes(",")?obj.attachment:obj.attachment.concat(","));
						var array = obj.attachment.substring(0, (obj.attachment).length - 1).split(",");
				$.each(array,function(i){
    		ext = (array[i]).substr(((array[i]).lastIndexOf('.') + 1));
    		if(ext=="jpg" || ext=="JPG" || ext=="png" || ext=="PNG")
			{
			
			htm1 = htm1+"<tr><td>IMAGES</td><td class='dtd'><a href='"+obj.imageUrl+"' target='_blank'><img src='"+obj.imageUrl+"' alt='image' width='110px' height='120px'/></a></td> ";
    		
			}
		else
			{
			htm1 = htm1+"<tr><td>IMAGES</td><td class='dtd'><a href='"+obj.imageUrl+"' target='_blank'><img src='"+obj.imageUrl+"' alt='image' width='110px' height='120px'/></a></td> ";
    		
			}
			});
    		}
    	htm1 = htm1+"<tr><td>DISCRIPTION</td><td class='dtd'>"+obj.tktDescription+"</td></tr>" +
    	"</table>";
    	
    	if(operation=='push')
    		{
    	var type = '';
    	if(userType===0)
    		{
    		type = type+'<option value='+tyId+'>'+tyVal+'</option><option value=6>CLOSED</option><option value=8>RE-OPEN</option>';
    		}
    	else
    		{
    		type = type+'<option value='+tyId+'>'+tyVal+'</option><option value=3>IN-PROGRESS</option><option value=4>ON-HOLD</option><option value=5>RESOLVED</option><option value=9>CANCEL</option>';
    		}
    	
    	$('#tkt-id').val(tktId);
    	$('#location').val(location);
    	$('#page-name').val(tyVal);
    	$('#page-no').val(tyId);
    	$('#tkt-type-action').html(type);
    	getAllComments(tktId,userId);
    	 $('#updateDiv').show();
    	 
    	 if(obj.tktTypeId!=5 && obj.tktTypeId !=6)
    		 {
    		 
    	 $('#pushpop').html('<button class="btn btn-danger pushpop" onclick=pushPop('+tktId+','+userId+',"push",'+obj.projectId+')>Send To Common</button> <i class="fa fa-circle-o-notch fa-spin fa-3x fa-fw pushpoploader" style="display:none;"></i>');
    		}
    		}
    	else{
    		 $('#updateDiv').hide();
    		 $('#pushpop').html('<button class="btn btn-danger pushpop" onclick=pushPop('+tktId+','+userId+',"pop",'+obj.projectId+')>Assign To Me</button> <i class="fa fa-circle-o-notch fa-spin fa-3x fa-fw pushpoploader" style="display:none;"></i>');
    	}
    	
    	$('#loading').hide();
        	$('.detail-div').html(htm1);
        	
           $('#fsModal').modal('toggle');

           
            },
            error: function (xhr, ajaxOptions, thrownError) {
           
            }
        });


}


$(document).ready(function(){
	  $("#tkt-type-action").on('change',function(){
	    var issueNo = $(this).val();
	    var issueName = $(this).attr('name');
	    var htm = ' <div class="col-md-2"><label>Issue Type: </label></div><div class="col-md-3"><select class=" form-control" name="tktTypeId" id="tkt-issue-type">';
										 	
	if(issueNo==5)
		{
		 $.ajax({
             type: "GET",
             url: "getIssueType",
             datatype:"json",
             contentType: "application/json; charset=utf-8",
             success: function(responce){
                 var data = JSON.stringify(responce);
             	var obj = JSON.parse(data);
             //	console.log(data);
                 $.each(obj, function(index,item) {        
htm = htm +' <option value='+item.id+'>'+item.feedback+'</option>';
                 });
                 htm = htm+'</select></div>';
                 $('#tkt-issue-type').html(htm);  
                 },
               });
		}
	else
		{
		 $('#tkt-issue-type').html('');  
		}
	
	      });
	  
	  
	  //For issue category according to project
	  
	  
	  $("#projectId").on('change',function(){
		    var project_ID = $(this).val();
		    var htm = '';
			 $.ajax({
	             type: "GET",
	             url: "getIssueCategoryByProject",
	             data: {project_id:project_ID},
	             datatype:"json",
	             contentType: "application/json; charset=utf-8",
	             success: function(responce){
	                 var data = JSON.stringify(responce);
	             	var obj = JSON.parse(data);
	             	//console.log(data);
	                 $.each(obj, function(index,item) {        
	htm = htm +' <option value='+item.id+'>'+item.issue_category+'</option>';
	                 });
	                 htm = htm+'</select></div>';
	                 $('#issueCategoryId').html(htm);  
	                 },
	               });
		      });
	  
	  
	  //Get all common ticket by project
	  $("#projectIdForTicket").on('change',function(){
		    var project_ID = $(this).val();
		GetCommonTicketListByProjectID(project_ID);
		      });
	  });



function viewRaisdTicket(tktId, userId, tyId, tyVal, userType, location)
{
	$('#loading').show();
	$.ajax({
        url: "view-ticket",
        data:{tkt_id:tktId},
        type: 'GET',
        datatype:"json",
        contentType: "application/json; charset=utf-8",
        success: function (responce)
        {

        	var data = JSON.stringify(responce);
        	var obj = JSON.parse(data);
        	var htm1 = '';
        	var ext = null;
        	htm1 = htm1+"<table class='det' >" +
        	"<tr><td>PROJECT</td><td class='dtd'>"+obj.project+"</td></tr>" +
        	"<tr><td>TKET-NO</td><td class='dtd'>TKT-"+obj.tktId+"</td></tr>" +
			"<tr><td>ISSUE CATEGORY</td><td class='dtd'>"+obj.issueCategoryName+"</td></tr>" +
			"<tr><td>ISSUE TYPE</td><td class='dtd'>"+obj.tktType+"</td></tr>" +
			"<tr><td>SUMMARY</td><td class='dtd'>"+obj.tktSummary+"</td></tr>" +
			"<tr><td>ENVIRONMENT</td><td class='dtd'>"+obj.environment+"</td></tr>" +
			"<tr><td>ULB NAME</td><td class='dtd'>"+obj.ulbName +"</td></tr>" +
			"<tr><td>ASSIGNED DATE</td><td class='dtd'>"+obj.raisedDate+"</td></tr>" +
			"<tr><td>RESOLVED DATE</td><td class='dtd'>"+obj.updatedDate+"</td></tr>"+
			"<tr><td>FEEDBACK</td><td class='dtd'>"+obj.issueFeedbackName+"</td></tr>";
	
        	if(obj.attachment==null)
        		{
        		htm1 = htm1+"<tr><td>IMAGES</td><td class='dtd'><a href='#'><img src='"+BUCKET_URL+""+obj.attachment+"' alt='image' width='80px' height='90px'/></a></td> ";
        		}
        	else
        		{
								obj.attachment=	(obj.attachment.includes(",")?obj.attachment:obj.attachment.concat(","));
									var array = obj.attachment.substring(0, (obj.attachment).length - 1).split(",");
				$.each(array,function(i){
    		ext = (array[i]).substr(((array[i]).lastIndexOf('.') + 1));
        		//ext = (obj.attachment).substr(((obj.attachment).lastIndexOf('.') + 1));
        		if(ext=="jpg" || ext=="JPG" || ext=="png" || ext=="PNG")
        			{
        			htm1 = htm1+"<tr><td>IMAGES</td><td class='dtd'><a href='"+obj.imageUrl+"' target='_blank'><img src='"+obj.imageUrl+"' alt='image' width='110px' height='120px'/></a></td> ";
            		
        			}
        		else
        			{
        			htm1 = htm1+"<tr><td>IMAGES</td><td class='dtd'><a href='"+obj.imageUrl+"' target='_blank'><img src='"+obj.imageUrl+"' alt='image' width='110px' height='120px'/></a></td> ";
            		
							}
						});
        		
        		}
        	htm1 = htm1+"<tr><td>DISCRIPTION</td><td class='dtd'>"+obj.tktDescription+"</td></tr>" +
        	"</table>";
        	
        	var type = '';
        	if(userType===0)
        		{
        		type = type+'<option value='+tyId+'>'+tyVal+'</option><option value=6>CLOSED</option><option value=8>RE-OPEN</option>';
        		}
        	else
        		{
        		type = type+'<option value='+tyId+'>'+tyVal+'</option><option value=3>IN-PROGRESS</option><option value=4>ON-HOLD</option><option value=5>RESOLVED</option><option value=9>CANCEL</option>';
        		}
        	
        	$('#tkt-id').val(tktId);
        	$('#location').val(location);
        	$('#page-name').val(tyVal);
        	$('#page-no').val(tyId);
        	$('#tkt-type-action').html(type);
        	
        	$('#loading').hide();
            	$('.detail-div').html(htm1);
            	getAllComments(tktId,userId);
               $('#fsModal').modal('toggle');
           
           
            },
            error: function (xhr, ajaxOptions, thrownError) {
           
            }
        });

}

function viewTicketType(tktId, prId, prVal, tyId, tyVal, userType, location)
{
	
	var type = '';
	var priority = '';
	if(userType===0)
		{
		type = type+'<option value='+tyId+'>'+tyVal+'</option><option value=6>CLOSED</option><option value=8>RE-OPEN</option>';
		}
	else
		{
		type = type+'<option value='+tyId+'>'+tyVal+'</option><option value=3>IN-PROGRESS</option><option value=4>ON-HOLD</option><option value=5>RESOLVED</option>';
		}
	
	
	priority = priority+'<option value='+prId+'>'+prVal+'</option> <option value=1>HEIGHEST</option> <option value=3>LOWEST</option> <option value=2>NORMAL</option>';
	$('#tkt-id').val(tktId);
	$('#location').val(location);
	$('#page-name').val(tyVal);
	$('#page-no').val(tyId);
	$('#type-action').html(type);
	$('#priority-action').html(priority);
	$('#update-modal').modal('toggle');
    
}


function updateTicketType()
{
$("#tkt-issue-type").val($("#tkt-issue-type option:selected()").val());
	
	var tktId = $('#tkt-id').val();
	var typeId = $('#tkt-type-action').val();
	var tktIssueType=0;
	if(typeId==5)
	tktIssueType = $('#tkt-issue-type').val();
	else
	 tktIssueType=0;
	
	var location = $('#location').val();
	$('#fsModal').modal('toggle');
	$('#loading').show();
$.ajax({
    url: "update-ticket-type",
    type: 'GET',
    data:{tkt_id:tktId, type_id:typeId, tkt_issue_id:tktIssueType},
    datatype:"text",
    contentType: "application/json; charset=utf-8",
    success: function (responce)
    {
    	if(location=='assign')
    		{
    		assignedTicketList($('#page-no').val(), $("#page-name").val());

    		}
    	else
    		{
    		raisedTicketList($('#page-no').val(), $("#page-name").val());
    		
    		}
    	
//    		window.location =location;
    	
    },
    error: function (xhr, ajaxOptions, thrownError) {
   
    }
});
    
    
}


//============create ticket form validation===============


function validateTicketForm() {

	var project = ticketForm.projectId.value;
	var summary = ticketForm.tktSummary.value.trim();
	var userTypeId = ticketForm.usertypeid.value;
	var ulbID;
	  if(project==0)
		{
		  alert('Please Select Project First!');
		  ticketForm.projectId.focus();
		return false
		}
	  else if(summary=="" || summary.length<4)
		{
		  alert('Please Enter Summary!');
		  ticketForm.tktSummary.value='';
		  ticketForm.tktSummary.focus();
		return false
		}
		if(userTypeId=="3")
		{
			ulbID = ticketForm.ulbId.value;
			if(ulbID=="0")
			{
			  alert('Please Select ULB First!');
		  ticketForm.ulbId.focus();
		return false
			}
			else{
				return true;
			}
		}
	  
	else
		{
		$('#loading').show();
	    return true;
	  }
	}

function getAllComments(tktId,userID)
{
	
	$.ajax({
        url: "get-comments",
        data:{tkt_id:tktId},
        type: 'GET',
        datatype:"json",
        contentType: "application/json; charset=utf-8",
        success: function (responce)
        {
        	var data = JSON.stringify(responce);
        	var obj = JSON.parse(data);
        	var htm1 = '';
        	$("#addCmtBtn").attr("onclick", "return validateCommentForm("+tktId+","+userID+")");
        	   
        	$.each( obj, function( key, value ) {
        		  
        		  if(value.userId==userID)
        			  {
									if(value.attachment==null || value.attachment=='null')
									{
        			  htm1 = htm1+"<li class='left'><h4><i class='fa fa-user avatar' aria-hidden='true'></i>"+value.userName+"</h4><div class='commentText'><pre class='chat'>"+value.commentDesc+"<br><span class='spdate'>"+(value.dateTime).slice(0, -2)+"</span></pre></div></li><div class='clear'></div>";
									}
								else
								{
									htm1 = htm1+"<li class='left'><h4><i class='fa fa-user avatar' aria-hidden='true'></i>"+value.userName+"</h4><div class='commentText'><pre class='chat'>"+value.commentDesc+"<br><span class='cmtattachment'><a href='"+value.imageUrl+"' target='_blank'>"+value.attachment+"</a></span><br><span class='spdate'>"+(value.dateTime).slice(0, -2)+"</span></pre></div></li><div class='clear'></div>";
							}
						}
        		  else
        			  {
									if(value.attachment==null || value.attachment=='null')
									{
        			  htm1 = htm1+"<li class='right'><h4><i class='fa fa-user-plus' aria-hidden='true'></i>"+value.userName+"</h4><div class='commentText'><pre class='chat'>"+value.commentDesc+"<br><span class='spdate'>"+(value.dateTime).slice(0, -2)+"</span></pre></div></li><div class='clear'></div>";
									}
									else
									{
										htm1 = htm1+"<li class='right'><h4><i class='fa fa-user-plus' aria-hidden='true'></i>"+value.userName+"</h4><div class='commentText'><pre class='chat'>"+value.commentDesc+"<br><span class='cmtattachment'><a href='"+value.imageUrl+"' target='_blank'>"+value.attachment+"</a></span><br> <span class='spdate'>"+(value.dateTime).slice(0, -2)+"</span></pre></div></li><div class='clear'></div>";
									}
							}
        		});
        	$('.commentList').html(htm1);
           
            },
            error: function (xhr, ajaxOptions, thrownError) {
           
            }
        });
	
	}



function validateCommentForm(tktId,userId)
{
	
	var comment = document.getElementById('comment').value;
	if(comment.trim()=='')
		{
		alert('Please enter comment first!');
		document.getElementById('comment').value="";
		document.getElementById('comment').focus();
		return false;
		}
	else
		{
		$('#loading').show();
		var data = new FormData();
		data.append("tktId",tktId);
		data.append("userId",userId);
		data.append("commentDesc",comment);
		 var fi = document.getElementById('commentFile');
		if (fi.files.length > 0) {
			var	file =$("#commentFile").prop("files")[0]; 
		data.append("commentFile", file);
		data.append("attachment", "yes");
	}
	else{
	data.append("attachment", "no");
	}
	
		$.ajax({
	        url: "add-comment",
	        data:data,//{tktId:tktId, userId:userId, commentDesc:comment, commentFile:document.getElementById('commentFile').files[0]},
					type: 'POST',
					 processData: false,  // Important!
        contentType: false,
        cache: false,
	        success: function (responce)
	        {
	        	$('#loading').hide();
	        	if(responce==1)
	        		{
								 $('#commentFile').val("");
	        		getAllComments(tktId,userId);
	        		document.getElementById('comment').value="";
	        		}
	        	else
	        		{
	        		alert("comment didn't add, Please try again!");
	        		}
	        	   
	            },
	            error: function (xhr, ajaxOptions, thrownError) {
	           
	            }
	        });

		}
	
	}


function pushPop(tktId,userId,option,project_ID)
{
	var location = $('#location').val();
$('.pushpoploader').show();
$('.pushpop').hide();
	$.ajax({
        url: "push-pop",
        data:{ticketID:tktId, userID:userId, option:option},
        type: 'GET',
        datatype:"text",
        success: function (responce)
        {
        	if(responce==1)
        		{
        		$('.pushpop').show();
        		$('.pushpoploader').hide();
        		alert("You have assign this ticket!");
        		$('#fsModal').modal('toggle');
        		if(option=='push')
        			{
        			if(location=='assign')
            		{
            		assignedTicketList($('#page-no').val(), $("#page-name").val());

            		}
            	else
            		{
            		raisedTicketList($('#page-no').val(), $("#page-name").val());
            		
            		}
        			}
        		else
        			{
        			GetCommonTicketListByProjectID(project_ID);
        			}
        		
        		}
        	
            },
            error: function (xhr, ajaxOptions, thrownError) {
           
            }
        });
	
	}



function GetCommonTicketListByProjectID(project_ID)
{
    $('#loading').show();
	 $.ajax({
        type: "GET",
        url: "getCommontTicketByProject",
        data: {projectID:project_ID},
        datatype:"text",
        contentType: "application/json; charset=utf-8",
        success: function(responce){
          $('#table-div').html(responce); 
          $('#loading').hide();
          $(document).ready(function() {
//      	    $('.example').DataTable();
        	  $('.example').DataTable( {
        	        "order": [[ 0, "desc" ]]
        	    } );
      	} );
            },
          });	

}


function searchAllTkt() {
	var tktId = document.getElementById('search-input').value;
	if(tktId==='')
		{
		alert('Please enter TICKET NO first!');
		}
	else
		{
		$('#searchAll').hide();
		$('.searchAllLoadder').show();
		$.ajax({
	        url: "view-ticket",
	        data:{tkt_id:tktId.substring(4)},
	        type: 'GET',
	        datatype:"json",
	        contentType: "application/json; charset=utf-8",
	        success: function (responce)
	        {
	        	var data = JSON.stringify(responce);
	        	var obj = JSON.parse(data);
	        	var htm1 = '';
	        	var ext = null;
	        	if(obj.tktId==0)
	        		{
	        		htm1 = htm1+"<span style='color:red; font-size:25px; font-weight:800;'>No Record Found!</span>";
	        		}
	        	else
	        		{
	        	htm1 = htm1+"<table class='det' >" +
				"<tr><td>PROJECT</td><td class='dtd'>"+obj.project+"</td></tr>" +
				"<tr><td>TICKET-NO</td><td class='dtd'>TKT-"+obj.tktId+"</td></tr>" +
				"<tr><td>ISSUE CATEGORY</td><td class='dtd'>"+obj.issueCategoryName+"</td></tr>" +
				"<tr><td>STATUS</td><td class='dtd'>"+obj.tktType+"</td></tr>" +
				"<tr><td>SUMMARY</td><td class='dtd'>"+obj.tktSummary+"</td></tr>" +
				"<tr><td>RAISED BY</td><td class='dtd'>"+obj.raiserName +"</td></tr>" +
				"<tr><td>ASSIGNED TO</td><td class='dtd'>"+(obj.assignedToId==0 ? "COMMON POOL" : obj.assigneeName)+"</td></tr>" +
				"<tr><td>ENVIRONMENT</td><td class='dtd'>"+obj.environment+"</td></tr>" +
				"<tr><td>ULB NAME</td><td class='dtd'>"+obj.ulbName +"</td></tr>" +
				"<tr><td>LAST UPDATE</td><td class='dtd'>"+obj.updatedDate +"</td></tr>" +
				"<tr><td>ASSIGNED DATE</td><td class='dtd'>"+obj.raisedDate+"</td></tr>";
	        	 if(obj.tktTypeId==5){
	        		 htm1 = htm1+ "<tr><td>RESOLVED DATE</td><td class='dtd'>"+obj.updatedDate+"</td></tr>";
	        		 $('#updateDiv').hide();
	        	 }
	        	 htm1 = htm1+"<tr><td>FEEDBACK</td><td class='dtd'>"+obj.issueFeedbackName+"</td></tr>";
	        	 if(obj.attachment==null)
	    		{
	    		htm1 = htm1+"<tr><td>IMAGES</td><td class='dtd'><a href='#'><img src='"+BUCKET_URL+""+obj.attachment+"' alt='image' width='80px' height='90px'/></a></td> ";
	    		}
	    	else
	    		{
						obj.attachment=	(obj.attachment.includes(",")?obj.attachment:obj.attachment.concat(","));
	    		ext = (obj.attachment).substr(((obj.attachment).lastIndexOf('.') + 1));
	    		if(ext=="jpg" || ext=="JPG" || ext=="png" || ext=="PNG")
    			{
    			htm1 = htm1+"<tr><td>IMAGES</td><td class='dtd'><a href='download/"+obj.attachment+"'><img src='"+BUCKET_URL+""+obj.attachment+"' alt='image' width='110px' height='120px'/></a></td> ";
        		
    			}
    		else
    			{
    			htm1 = htm1+"<tr><td>IMAGES</td><td class='dtd'><a href='download/"+obj.attachment+"'><img src='"+BUCKET_URL+""+obj.attachment+"' alt='image' width='110px' height='120px'/></a></td> ";
        		
    			}
	    		}
	    	htm1 = htm1+"<tr><td>DISCRIPTION</td><td class='dtd'>"+obj.tktDescription+"</td></tr></table>";
	        		}
	        $('.alldetail-div').html(htm1);
	        $('#searchModal').modal('toggle');
	    	$('#searchAll').show();
			$('.searchAllLoadder').hide();
	        }
		          });
		
		}
	
}


//Get towns id and name
function getAllTowns() {
		$.ajax({
	        url: "get-towns",
	        type: 'GET',
	        datatype:"json",
	        contentType: "application/json; charset=utf-8",
	        success: function (responce)
	        {
	        	var htm = "<option value='0'> Select City</option>";
	        	//console.log(responce);
	        	 var data = JSON.stringify(responce);
	             	var obj = JSON.parse(data);
	                 $.each(obj, function(index,item) {        
	htm = htm +' <option value='+item.town_id+'>'+item.town_name+'</option>';
	                 });
	                 htm = htm;
	                 $('#select_twn').html(htm);  
	                 }      
		});
	
}


//Multiple file upload from genrate ticket screen
var count =0;
	$(document).ready(function() {
	  if (window.File && window.FileList && window.FileReader) {
	    $("#files").on("change", function(e) {
	    	var ext = $('#files').val().split('.').pop().toLowerCase();
	if($.inArray(ext, ['pdf','png','jpg','jpeg','csv','docx','xlsx']) == -1) {
    alert('Invalid File');
    return false;
	}
	else
	{
	      var files = e.target.files,
	        filesLength = files.length;
	       if(filesLength>0)
	       {
	       count++;
	       $("#attach").val(count);
	       }
	      for (var i = 0; i < filesLength; i++) {
	        var f = files[i]
	        var fileReader = new FileReader();
	        fileReader.onload = (function(e) {
	          var file = e.target;
	          $("<span class=\"pip pi"+count+"\">"+
	            "<img class=\"imageThumb\" src=\"" + e.target.result + "\" title=\"" + file.name + "\"/>" +
	            "</br><span class=\"remove rem"+count+"\" onclick=\"removeFunc("+count+")\">Remove image</span>" +
	            "</span>").insertAfter("#files");
 var x = $("#files"),
  y = x.clone();
  y.attr("name", "files");
  y.attr("style", "display:none")
  y.insertAfter(".rem"+count);
	        });
	        fileReader.readAsDataURL(files[0]);
	      }
	     }
	    });
	  } else {
	    alert("Your browser doesn't support to File API")
	  }
	}); 
	
	
	
	 function removeFunc(coun){
	            $('.pi'+coun).remove();
	            count--;
	            $("#attach").val(count);
	          }



























/*
 function uploadForm()
	 {
		 
		 alert('pppppppppppppppp');
	 	var frm = $('#createTicketForm');

	 	 frm.submit(function (e) {

	 	     e.preventDefault();

	 	     formData = $("#createTicketForm").serialize()
	 	      alert('formdata=== '+formData);
	 	     $.ajax({
	 	         type: 'POST',
	 	         url: 'raise-ticket-form',
	 	        contentType: 'multipart/form-data',
	 	          headers : {'Accept': 'application/json', 'content-Type':'multipart/form-data'},
	 	         data: formData,
	 	         success: function (data) {
	 	        	 alert(data);
	 	        	 if(data=="login"){
	 	        		 window.location.href = "login";
	 	        	 }
	 	        	 else
	 	        		 {
	 	        		 $('#errorMsg').html('Ticket Genrate Successfully.')
	 	        		 }
	 	         },
	 	         error: function (data) {
	 	        	 window.location.href = "login";
	 	         },
	 	     });
	 	 });	
	 }

*/

