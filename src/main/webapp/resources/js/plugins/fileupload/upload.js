$(document).ready(function() { 
	
	//$("#upload-form").attr("action", "/TurfClubOnline/rest/upload?" + $("#csrf").attr("name") + "= "+ $("#csrf").attr("value"));
	//console.log($("#upload-form").attr("action"));
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	console.log($("meta[name='_csrf']").attr("content") + " " + $("meta[name='_csrf_header']").attr("content"));
	var options = { 
			target: '#output',   // target element(s) to be updated with server response 
			beforeSubmit: beforeSubmit,  // pre-submit callback 
			uploadProgress: OnProgress,
			success: afterSuccess,  // post-submit callback 
			resetForm: true,
			beforeSend: function(jqXHR) {
                console.log(header + " " + token);
				jqXHR.setRequestHeader(header, token); 
            },// reset the form after successful submit 
		}; 
		
	 $('#upload-form').submit(function(e) { 
		 e.preventDefault();
		 console.log("prevent default");
		 $(this).ajaxSubmit(options);  			
			// always return false to prevent standard browser submit and page navigation 
			return false; 
		}); 
}); 

function afterSuccess()
{
	$('#submit-btn').show(); //hide submit button
	$('#loading-img').hide(); //hide submit button
	$('#progressbox').hide();
	$("#file-uploaded-div").show();
}

//function to check file size before uploading.
function beforeSubmit(){
    //check whether browser fully supports all File API
   if (window.File && window.FileReader && window.FileList && window.Blob)
	{
		
		if( !$('#imageInput').val()) //check empty input filed
		{
			$("#output").html("You must select a file!");
			return false
		}
		
		var fsize = $('#imageInput')[0].files[0].size; //get file size
		var ftype = $('#imageInput')[0].files[0].type; // get file type
		
		 console.log(ftype); 
		//allow only valid image file types 
		switch(ftype)
        {
          
        case 'image/png': case 'image/jpeg': case 'image/pjpeg': case 'application/pdf': case 'image/tiff':
                break;
            default:
                $("#output").html("<b>"+ftype+"</b> Unsupported file type!");
				return false;
        }
		
		//Allowed file size is less than 5 MB (5242880)
		if(fsize>5242880) 
		{
			$("#output").html("<b>"+bytesToSize(fsize) +"</b> File too big! <br />Please attach a file less than 5Mb in size, Alternatively post your P35 to the Turf Club.");
			return false
		}
				
		$('#submit-btn').hide(); //hide submit button
		$('#loading-img').show(); //hide submit button
		$('#progressbox').show();
		$("#output").html("");  
	}
	else
	{
		//Output error to older browsers that do not support HTML5 File API
		$("#output").html("Please upgrade your browser, because your current browser lacks some new features we need!");
		return false;
	}
}

//function to format bites bit.ly/19yoIPO
function bytesToSize(bytes) {
   var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
   if (bytes == 0) return '0 Bytes';
   var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
   return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
}

function OnProgress(event, position, total, percentComplete)
{
    //Progress bar
    $("#progressbar").width(percentComplete + '%') //update progressbar percent complete
    $("#statustxt").html(percentComplete + '%'); //update status text
    if(percentComplete>50)
        {
    	$("#statustxt").css('color','#fff'); //change status text to white after 50%
        }
}

$(document).on('change', '.btn-file :file', function() {
	  var input = $(this),
	      numFiles = input.get(0).files ? input.get(0).files.length : 1,
	      label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
	  input.trigger('fileselect', [numFiles, label]);
	});

	$(document).ready( function() {
	    $('.btn-file :file').on('fileselect', function(event, numFiles, label) {
	        
	        var input = $(this).parents('.input-group').find(':text'),
	            log = numFiles > 1 ? numFiles + ' files selected' : label;
	        
	        if( input.length ) {
	            input.val(log);
	        } else {
	            if( log ) alert(log);
	        }
	        
	    });
	});