
	$('#formsubmit').click(function(event){

 	   event.preventDefault();
 	

 		
 		$("#password").val($("#password").val().toUpperCase());     
 	    	
 		
 		$('#loginForm').submit();

 		
 	});
	
