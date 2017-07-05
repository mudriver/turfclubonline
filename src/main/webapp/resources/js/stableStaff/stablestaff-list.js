var p35WarningShown = false;

$(document).ready(function() {

	setupDocument();

	
	
});

function setupDocument() {
	pdfPreviewSectionSetup();
	

	
 $('#listEmployeeForm')
	.validator(
			{
				delay : '900000',
				disable : false
			});
	
	$("#listEmployeeForm").submit(function(e){
	    
		
		e.preventDefault();
	    //console.log("submit" + $( ".notComplete" ).length );
	    if (! $( ".notComplete" ).length ){
	    	console.log($(".new-employee-section").length);
	    	
	    	if ($( "#noNew" ).val() == 'YES' || $(".new-employee-section").length > 0 ){
	    		
	    		//warn no p35 loaded
	
	    		if($("#documents-uploaded:visible").length > 0 || p35WarningShown){
	    			console.log("submit");
	    			$('#listEmployeeForm').validator('validate');
	    			if($('.has-error:visible').length == 0) {
	    			
	    			$('#listEmployeeForm').unbind('submit');
		    		$('#listEmployeeForm').submit();
	    			}
	    		}
	    		else{
	    			p35WarningShown = true;
	    			$("#no-p35-warning").modal('show');
	    		}
	    		
	    	}
	    	else{
	    		
	    		$("#noNewWarning").modal('show');
	    	}
	    	
	    }
	    else{
	    	$("#notCompleteWarning").modal('show');
	    }
	  });
	$('#noNewEmployee').click(function() {
		$( "#noNew" ).val("YES");
	});
	console.log($('#saveMessage').length);
	if($('#saveMessage').length != 0){
		$("#saveMessage").modal('show');
	}
	
	var dateToday = new Date();
	//CHANGE EARNINGS YEAR HERE
	var year = (dateToday.getFullYear());
	$('#leftHeader').text($('#leftHeader').text().replace("year" , year-2));
}




function pdfPreviewSectionSetup(){
	
	
	
	
	
	$("#main-page").click(function(){
		
		 $("#pdf-section").hide();
		
	});	
	//set the width of the pdf section dynamically
	 $("#pdf").width($("#pdf-section").width());
	//hide the pdf section
	 $("#pdf-section").hide();
	 $("#pdf-show").click(function() {
		 
		 

		 if (! $( ".notComplete" ).length ){
		    	
		    	if ($( "#noNew" ).val() == 'YES' || $(".new-employee-section").length > 0 ){
		    		
		    		
		    		download();
		    		
		    	}
		    	else{
		    		
		    		$("#noNewWarning").modal('show');
		    	}
		    	
		    }
		    else{
		    	$("#notCompleteWarning").modal('show');
		    }
	 });
	 $("#pdf-show-view").click(function() {
		 
		
		    	
	
		    		download();
		    		
		    
	 });
	 $("#pdf-close").click(function() {
		 $("#pdf-section").hide();
	 });


}







function download() {
	// Retrieve download token
	// When token is received, proceed with download
	$.get('/TurfClubOnline/trainersEmployeesOnline/download/token', function(response) {
		// Store token
		var token = response.message[0];
		
	
		$("#please-wait").modal('show');
		// Start download
		$("#pdf-section").show();
		$('#pdf-preview').html('<object id="pdf" standby="please wait for pdf to load" type="application/pdf" data="/TurfClubOnline/trainersEmployeesOnline/employeeReportDownload?'+'token='+token +'" width="800" height="1000"></object>');


		// Check periodically if download has started
		var frequency = 1000;
		var timer = setInterval(function() {
			$.get('/TurfClubOnline/trainersEmployeesOnline/download/progress', {token: token}, 
					function(response) {
						// If token is not returned, download has started
						// Close progress dialog if started
						if (response.message[0] != token) {
							$("#please-wait").modal('hide');
							$('html, body').animate({
							    scrollTop: $("#listEmployeeForm").offset().top
							}, 500);
							
							clearInterval(timer);
						}
				});
		}, frequency);
		
	});
}
