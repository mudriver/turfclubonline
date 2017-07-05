var currentInputFocus;
var lastYearEmployee = false;
var d = new Date();
var employmentEndYear;
var employmentStartYear = d.getFullYear();
var thisYear = d.getFullYear();
var earningsWarningShown = false;
var pensionWarningShown = false;
$(document).ready(function() {

	

	setupDocument();

});

function setupDocument() {

	console.log("Setup");
	loadExtraValuesOnSelect2();
	
	// setup date pickers
	$(".datepicker").datepicker({
		changeMonth : true,
		changeYear : true,
		maxDate : '+0m +0w +0d',
		minDate : '-100Y',
		dateFormat : 'dd/mm/yy',
		constrainInput : true,
		showOn: "button",
		buttonText : ''
	    
	});
	

	// setup date picker for date of birth with limit that only allows over 14
	// years old
	$("#dateOfBirth").datepicker({
		changeMonth : true,
		changeYear : true,
		minDate : '-100Y',
		maxDate : '-14Y',
		dateFormat : 'dd/mm/yy',
		constrainInput : true,
		yearRange: "-100:-14",
		showOn: "button",
		buttonText : ''
		
	  
	    
	});



    $('#date-of-birth-button').click(function() {
          $('#dateOfBirth').datepicker('show');
    });
    $('#date-to-button').click(function() {
        $('#dateTo').datepicker('show');
    });
    
	
	// setup select 2 on selects
	$("#marital").select2({
		placeholder : "You must select a civil status",
		allowClear : true
	});
	$("#hoursWorked").select2({
		placeholder : "Please Select",
		allowClear : true
	});
	
	$("#sex").select2({
		placeholder : "You must select a gender",
		allowClear : true
	});
	$("#cardType").select2({
		placeholder : "",
		allowClear : true
	});

	$("#nationality").select2({
		placeholder : "You must select a nationality",
		allowClear : true
	});
	// bring Irish and British nationalities to the top of the nationality
	// select list
	var select = $("#nationality");
	console.log(select.find('option:eq(1)').val() + " "
			+ select.find('option[value="Irish"]').val());
	select.find('option[value="Irish"]').insertBefore(
			select.find('option:eq(1)'));
	select.find('option[value="British"]').insertBefore(
			select.find('option:eq(2)'));

	$("#empCat").select2({
		placeholder : "You must select an employment category",
		allowClear : true
	});

	
	$("#title").select2({
		placeholder : "You must enter a title",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#titles").val())
	});

	
	$("#address4").select2({
		placeholder : "You must enter a county",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#counties").val())

	});

	var addressselect = $("#address5");
	addressselect.find('option[value="Ireland"]').insertBefore(
			addressselect.find('option:eq(1)'));

	$("#address5").select2({
		placeholder : "You must enter a country",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#countries").val())
	});

	
	
	// initialize validation checks for edit / new form.
	$('#employeeEditForm')
			.validator(
					{
						delay : '2000',
						disable : false,
						custom : {
							dateofbirth : function($el) {

								return checkDateOfBirth($el);
							},
							emailorphone : function($el) {

								return checkEmailOrPhone($el);
							},
							emailpattern : function($el) {

								if($('#email').val() != '') {
									return $el.val().match(/.*?@.*?\..{2,}/);
								}
								else {
									return true;
								}

							},
							datechecker : function($el){
								return checkDateStartedNotAfterEndDate($el);
							},
							minlength : function($el){
								if($el.val().length == 0 || $el.val().length >= 7){
									return true;
								}
								return false;
							},
							county : function($el){
								if($el.val().length > 0){
									return true;
								}
								return false;
							},
							country : function($el){
								
								if($el.val().length > 0){
									return true;
								}
								return false;
							},
							title : function($el){
								
								if($el.val().length > 0){
									return true;
								}
								return false;
							},
							taxable : function($el){
								console.log($("#has-taxable").val());
								if($("#has-taxable").val() == "true" || $("#has-taxable").val() == "false"){
									return true;
								}
								return false;
							}
							
						},
						errors : {
							dateofbirth : "You must enter a date of birth and employee must be at least 14 years old",
							emailorphone : "You must enter either a mobile ph no. or email address",
							emailpattern : "You must enter correct email eg. joe@bloggs.ie",
							datechecker : "Start date must be before end date and end date must not be in the future (End date must be this year or blank for existing employees)",
							minlength : "Phone number must be a minimum of 7 digits",
							county : "You must select or type a county",
							country : "You must select or type a country",
							title : "You must select or type a title",
							taxable : "You must select yes or no"
						}
					});

	$("#submitButton").click(
			function(e) {

				console.log("submit click");
				// validate the entire form
				$('#employeeEditForm').validator('validate');
				var submit = true;
				// check each error and if the error section is visible then do
				// not
				// submit form until error is fixed


				if($('.has-error:visible').length > 0) {

					submit = false;

				}
				if(validateDate($("#dateFrom").val())) {
					employmentStartYear = new Date($("#dateFrom").val()
							.replace(/(\d{2})\/(\d{2})\/(\d{4})/, "$2/$1/$3"))
							.getFullYear();
					// console.log("Date To" + employmentEndYear);
				}

				var earnings = (parseInt($('#earnings').val()) > 0);
				if(earnings && $("#employeeId").val() != null && (employmentStartYear == 2015)) {
					showDatePensionWarning();
					earningsWarningShown = true;
					submit = false;
				}
				
				//if the employee is new and they have no taxable earnings show the no save warning
				if($("#newHeader").length > 0 && $("#has-taxable").val() ==  "false") {
					
					showNoSaveWarning();
				}
				else if(submit) {
					// if the pension section is not visible remove that section
					// from
					//uppercase pps number
					$("#ppsNumber").val($("#ppsNumber").val().toUpperCase());
					$("#dateFrom").attr('disabled', false);
					// the form values to submit
					if($('.pension:visible').length == 0) {
						$('.pension').remove();
					}

					if($(".last-year-remove:visible").length == 0) {
						$(".last-year-remove:visible").remove();
					}

					$('#employeeEditForm').submit();
				}
			});

	// check if the person ended their employment last year
	if(validateDate($("#dateTo").val())) {
		employmentEndYear = new Date($("#dateTo").val().replace(
				/(\d{2})\/(\d{2})\/(\d{4})/, "$2/$1/$3")).getFullYear();
		// console.log("Date To" + employmentEndYear);
	}
	// check when the person started their employment
	if(validateDate($("#dateFrom").val())) {
		employmentStartYear = new Date($("#dateFrom").val().replace(
				/(\d{2})\/(\d{2})\/(\d{4})/, "$2/$1/$3")).getFullYear();
		// console.log("Date To" + employmentEndYear);
	}
	if(employmentEndYear != null && employmentEndYear <= thisYear) {
		lastYearEmployee = true;
	}

	setupCardSections();
	if($("#updateHeader").length > 0) {
		setupPensionDialogsInputAndSectionsUpdate();
	}
	else if($("#editHeader").length > 0) {
		setupPensionDialogsInputAndSectionsEdit();
	}
	else if($("#newHeader").length > 0) {
		setupPensionDialogsInputAndSectionsNew();

	}

	// initialize the dynamic year variable and apply to the sections which
	// indicate fields
	// for both the current and previous year.

	


		//CHANGE EARNINGS YEAR HERE
		//change -2 to -1
		var yearEnding = d.getFullYear() - 2;

		var earningsPlaceHolder = $('#earnings').attr("placeholder");

		earningsPlaceHolder =  earningsPlaceHolder.replace(/year/g, yearEnding);
		console.log(earningsPlaceHolder);

		$('#earnings').attr(
				"placeholder",earningsPlaceHolder);
				
		console.log("3");				
		$('#earnings-label').text(earningsPlaceHolder);
		console.log("4");

	

	$("#title").change(function(){
		console.log("title change");
		//if the title is mr change the sex to male
		if($("#title").val() == "Mr."){
			//console.log("title male"+ $("#sex").val());
			$("#sex").val("Male");
			$("#sex").trigger("change");
			//when the title is changed remove options from the gender selection depending on title selected
			$("#sex option").each(function(){
				if($(this).val() == "Male" ){
				$(this).removeClass( "hide_me" );
		        	
		        }
		        else{
		        	console.log("hide");
		        	$(this).addClass( "hide_me" );
		        }
			});
			
		}
		//if the title is mrs or ms change the sex to female
		else if($("#title").val() == "Ms." ||$("#title").val() == "Mrs."){
			$("#sex").val("Female");
			$("#sex").trigger("change");
			$("#sex option").each(function(){
				if($(this).val() == "Female" ){
				$(this).removeClass( "hide_me" );
		        	
		        }
		        else{
		        	console.log("hide");
		        	$(this).addClass( "hide_me" );
		        }
			});
			//console.log("title female" + $("#sex").val());
		}
		else{
			$("#sex option").each(function(){

				$(this).removeClass( "hide_me" );
		        	
			});
		}
		
		
		
	});
	$("#title").trigger("change");
	
	$("#earnings").change(function(){
		console.log("Val" + $(this).val()  + "|");
		if($(this).val() !== undefined && $(this).val().length > 0){
			var value= $(this).val().replace(/[^\d\.]/g, '');
			console.log(value);
			$(this).val(value);
			if(!$(this).val().indexOf(".") > -1){
				$(this).val(parseFloat($(this).val()).toFixed(2));
			}
		}
		else{
			$(this).val(0);
			if(!$(this).val().indexOf(".") > -1){
				$(this).val(parseFloat($(this).val()).toFixed(2));
			}
		}
		
		
		
	});
	$("#earnings").trigger("change");
	$("#dateFrom").change(
			function() {

				if(validateDate($("#dateFrom").val())) {
					employmentStartYear = new Date($("#dateFrom").val()
							.replace(/(\d{2})\/(\d{2})\/(\d{4})/, "$2/$1/$3"))
							.getFullYear();
					// console.log("Date To" + employmentEndYear);
				}
				var noearnings = (parseInt($('#earnings').val()) == 0);
				var earnings = (parseInt($('#earnings').val()) > 0);

				if(noearnings && (employmentStartYear == 2014)) {
					showNoPensionWarning();
				}
				else if(earnings && (employmentStartYear == 2015)) {
					showDatePensionWarning();
				}

	});


	/*
	if(lastYearEmployee){
		$("#taxable-label").text($("#taxable-label").text().replace("Is", "Was"));
	}*/

}


function loadExtraValuesOnSelect2(){
	
	
	var list = $.parseJSON($("#titles").val());
	console.log(JSON.stringify(list ));
	var onList = false;
	$.each(list, function(index, value) {
	    if(list.id == $("#title").val()){
	    	onList = true;
	    }
		console.log(value.id);
	});
	
	if(!onList && $("#title").val() !== undefined){
		list.push( { "id":$("#title").val(), "text":$("#title").val() } );
		console.log("ADD TO LIST");
	}
	$("#titles").val(JSON.stringify(list ));
	list = $.parseJSON($("#counties").val());

	onList = false;
	$.each(list, function(index, value) {
	    if(list.id == $("#county").val()){
	    	onList = true;
	    }

	});
	
	if(!onList && $("#county").val() !== undefined){
		list.push( { "id":$("#county").val(), "text":$("#county").val() } );
		console.log("ADD TO LIST");
	}
	$("#counties").val(JSON.stringify(list ));
	list = $.parseJSON($("#countries").val());

	onList = false;
	$.each(list, function(index, value) {
	    if(list.id == $("#country").val()){
	    	onList = true;
	    }

	});
	
	if(!onList && $("#country").val() !== undefined){
		list.push( { "id":$("#country").val(), "text":$("#country").val() } );
		console.log("ADD TO LIST");
	}
	$("#countries").val(JSON.stringify(list ));
	
}


function checkDateStartedNotAfterEndDate(ele){

	console.log($("#is-new").val() == "true");
	if(validateDate($("#dateFrom").val()) && validateDate($("#dateTo").val())) {

		var startDate = 	 new Date($("#dateFrom").val().replace(/(\d{2})\/(\d{2})\/(\d{4})/, "$2/$1/$3"));
		var endDate = 	 new Date($("#dateTo").val().replace(/(\d{2})\/(\d{2})\/(\d{4})/, "$2/$1/$3"));
		if($("#is-new").val() == "false"){
			console.log(startDate);
			var today = new Date();
			var year = (new Date).getFullYear();
			var newdate = new Date(year, 0, 1);
			if(endDate >= newdate && startDate <= today && startDate < endDate) {
				// console.log("valid");
				return true;
			}
			else {
				// console.log("not valid");
				return false;
			}
		}
		else if($("#is-new").val() == "true"){
		
			console.log(startDate);
			var today = new Date();
			if(startDate <= today && startDate < endDate) {
				// console.log("valid");
				return true;
			}
			else {
				// console.log("not valid");
				return false;
			}
		
		}
	
	}
	else if(validateDate($("#dateFrom").val()) && ($("#dateTo").val() == "" || $("#dateTo").val() == null)){
		return true;
	}
	else{
		return false;
	}
	

}

function checkDateOfBirth(date) {
	// console.log("Test" + date.val());
	var input = $.datepicker.parseDate('dd/mm/yy', date.val());
	// console.log(input);
	var newdate = new Date();
	newdate.setFullYear(newdate.getFullYear() - 14);

	// console.log(newdate);
	if(input < newdate) {
		// console.log("valid");
		return true;
	}
	else {
		// console.log("not valid");
		return false;
	}

}



function checkEmailOrPhone(ele) {
	// console.log("e or p !" + $('#email').val() + '!!' + $('#mobileNo').val()
	// + '!');
	if($('#email').val() == '' && $('#mobileNo').val() == ''
			&& $('#email').is(":visible") && $('#mobileNo').is(":visible")) {
		// console.log("neither");
		return false;
	}
	// console.log("one");
	return true;
}

function validateDate(dtValue) {
	var dtRegex = new RegExp(/\b\d{1,2}[\/-]\d{1,2}[\/-]\d{4}\b/);
	return dtRegex.test(dtValue);
}

function setupCardSections() {
	// if new employee remove the card information
	// #### Card Section
	// if a new employee remove the card section
	if($("#newHeader").length == 1) {
		$(".card").remove();
	}
	else {
		// function if hours worked changes
		$('#hoursWorked').change(function() {

			if($('#hoursWorked').val() != "Greater than 8") {
				$("#cardType option").each(function() {

					if($(this).val() == "A") {
						// console.log("show");

						$(this).addClass("hide_me");
					}

				});
			}
			else {
				$("#cardType option").each(function() {
					$(this).removeClass("hide_me");
				});

			}

		});

		$('#hoursWorked').change();
	}



}

function setupPensionDialogsInputAndSectionsUpdate() {

	
	// setup date picker for date to 
	var year = (new Date).getFullYear();
	$("#dateTo").datepicker({
		changeMonth : true,
		changeYear : true,
		maxDate : '+0m +0w +0d',
		minDate: new Date(year, 0, 1),
		dateFormat : 'dd/mm/yy',
		constrainInput : true,
		showOn: "button",
		buttonText : ''
	});

	if(lastYearEmployee) {
		$("#detail-complete").hide();
		console.log("last year remove");
		$(".last-year-hide").hide();
		$(".last-year-remove").remove();
		$(".last-year-edit").hide();
		$(".last-year-no-edit").show();
		$(".last-year-no-edit").find(".form-control").attr('disabled',
				'disabled');

		$("#taxable").click(function(e) {
			console.log($("#has-taxable").val());
			if($(e.target).attr("id") == "yes-pension" && $("#has-taxable").val() != "true") {
				$("#has-taxable").val("true");
				$('#employeeEditForm').validator('validate');
				currentInputFocus = $("#taxable-focus");
				$('#no-pension').removeClass("active");
				$('#yes-pension').addClass("active");

				$(".last-year-edit").show();
				$("#detail-complete").hide();
				$("#submit-buttons").show();

			}
			else if($(e.target).attr("id") == "no-pension" && $("#has-taxable").val() != "0") {
				// if the employee is new
				$("#has-taxable").val("false");
				$('#employeeEditForm').validator('validate');
				currentInputFocus = $("#taxable-focus");
				$('#no-pension').addClass("active");
				$('#yes-pension').removeClass("active");
				$(".last-year-edit").hide();
				$("#detail-complete").show();
				$("#submit-buttons").show();
				showNoPensionWarning();
				
			}
		});
		
		
	
		// taxable earnings set the yes or no buttons to not clicked
		$('#no-pension').removeClass("active");
		$('#yes-pension').removeClass("active");


		if($("#has-taxable").val() == "false"){
			console.log("NO PENSION");
			$(".last-year-edit").hide();
			$('#no-pension').addClass("active");
			$('#yes-pension').removeClass("active");
		}
		else if($("#has-taxable").val() == "true"){
			console.log("PENSION");
			$(".last-year-edit").show();
			$('#no-pension').removeClass("active");
			$('#yes-pension').addClass("active");
		}

	}

	else {

		$("#date-from-section").hide();
		$("#detail-complete").remove();
		// function to keep track of the last input type form field that was in
		// focus
		$("input").focus(function() {
			// if the current item is the last input

			if($(this).closest(".row").find(".form-control").length > 2) {
				console.log("two");
				currentInputFocus = this;
			}
			else if($("#comments:visible").length > 0){
				currentInputFocus = $("#comments");
			}
			else {
				console.log("single");
				currentInputFocus = $("input:visible").last();
			}

		});

		$("textarea").focus(function() {
			
			currentInputFocus = $("#comments");
		});

		// focus on the first input
		$("#title").focus();
		showConfirmOKBox();
		// #Setup buttons
		// confirm detail button

		$('body').on('focus', '#confirmButton', function() {
			//console.log("confirm click focus");
			showConfirmOKBox();
			// showConfirmDivs($(this), 'CONFIRM');
		});

		// return key function
		$('body').keypress(function(event) {
			if(event.keyCode == 13) {
				console.log("key press");
				showConfirmOKBox();
			}
		});

		// select 2 return key function
		$('.select2-input').on("keydown", function(e) {
			if(e.keyCode == 13) {
				$("#select2-drop-mask").click();
				showConfirmOKBox();
				e.preventDefault();
			}
		});

		/*
		 * remove sections not required if(thisYearEmployee) {
		 * $("#date-to-section").remove(); } else {
		 * $("#date-from-section").remove(); }
		 */

		$("#taxable").click(function(e) {
			console.log("Taxable" + ($(e.target).attr("id") == "yes-pension" && $("#has-taxable").val() != "true") + " " + ($(e.target).attr("id") == "no-pension" && $("#has-taxable").val() != "false") + " " + $("#has-taxable").val());
			if($(e.target).attr("id") == "yes-pension" && $("#has-taxable").val() != "true") {
				$("#has-taxable").val("true");
				$('#employeeEditForm').validator('validate');
				$("#submit-buttons:visible").hide();
				currentInputFocus = $("#taxable-focus");
				console.log($(currentInputFocus).attr("id"));
				$('#no-pension').removeClass("active");
				$('#yes-pension').addClass("active");
				console.log("show confirm ok");
				showConfirmOKBox();

			}
			else if($(e.target).attr("id") == "no-pension" ) {
				// if the employee is new

				$("#has-taxable").val("false");
				$('#employeeEditForm').validator('validate');
				currentInputFocus = $("#taxable-focus");
				$('#no-pension').addClass("active");
				$('#yes-pension').removeClass("active");
				$("#confirmBox:visible").remove();
				$(".pension").hide();
				$("#date-to-section").hide();
				$("#submit-buttons").show();
				showNoPensionWarning();
				
			}
		});
		// taxable earnings set the yes or no buttons to not clicked
		$('#no-pension').removeClass("active");
		$('#yes-pension').removeClass("active");

		

	}

}




function setupPensionDialogsInputAndSectionsNew() {

	$("#detail-complete").remove();
	$('#date-from-button').click(function() {
        $('#dateFrom').datepicker('show');
    });
	// setup date picker for date to 
	$("#dateTo").datepicker({
		changeMonth : true,
		changeYear : true,
		maxDate : '+0m +0w +0d',
		dateFormat : 'dd/mm/yy',
		constrainInput : true,
		showOn: "button",
		buttonText : ''
	});
	// function to keep track of the last input type form field that was in
	// focus
	$("input").focus(function() {
		// if the current item is the last input

		if($(this).closest(".row").find(".form-control").length > 2 && $("#row:visible").length <= 1) {
			console.log("two");
			currentInputFocus = this;
		}
		else if($("#comments:visible").length > 0){
			currentInputFocus = $("#comments");
		}
		else {
			console.log("single");
			currentInputFocus = $("input:visible").last();
		}

	});

	$("textarea").focus(function() {
		
		currentInputFocus = $("#comments");
	});
	
	// focus on the first input
	$("#title").focus();
	showConfirmOKBox();
	// #Setup buttons
	// confirm detail button


	
	$('body').on('focus', '#confirmButton', function() {
		console.log("confirm click focus");
		showConfirmOKBox();
		// showConfirmDivs($(this), 'CONFIRM');
	});

	// return key function
	$('body').keypress(function(event) {
		if(event.keyCode == 13) {
			console.log("key press");
			showConfirmOKBox();
		}
	});

	// select 2 return key function
	$('.select2-input').on("keydown", function(e) {
		if(e.keyCode == 13) {
			$("#select2-drop-mask").click();
			showConfirmOKBox();
			e.preventDefault();
		}
	});


	$("#taxable").click(function(e) {
		console.log($("#has-taxable").val());
		if($(e.target).attr("id") == "yes-pension" && $("#has-taxable").val() != "true") {
			$("#has-taxable").val("true");
			$('#employeeEditForm').validator('validate');
			$("#submit-buttons:visible").hide();
			currentInputFocus = $("#taxable-focus");
			$('#no-pension').removeClass("active");
			$('#yes-pension').addClass("active");

			showConfirmOKBox();

		}
		else if($(e.target).attr("id") == "no-pension" && $("#has-taxable").val() != "false"){
			// if the employee is new

			$("#has-taxable").val("false");
			$('#employeeEditForm').validator('validate');
			currentInputFocus = $("#taxable-focus");
			$('#no-pension').addClass("active");
			$('#yes-pension').removeClass("active");
			$("#confirmBox:visible").remove();
			$(".pension").hide();
			$("#date-to-section").hide();
			$("#date-from-section").hide();
			$("#submit-buttons").show();
			showNoPensionWarning();
			
		}
	});
	// taxable earnings set the yes or no buttons to not clicked
	$('#no-pension').removeClass("active");
	$('#yes-pension').removeClass("active");

	
	// setup the pension warning buttons
	$('#nosave').click(function() {

		$('#cancel-button').click();
		
	});


	

}

function setupPensionDialogsInputAndSectionsEdit() {
	console.log("Edit Setup");

	$("#detail-complete").remove();
	//only allow this year to be selected if the employee is not new
	if($("#is-new").val() == "false"){
		// setup date picker for date to 
		var year = (new Date).getFullYear();
		$("#dateTo").datepicker({
			changeMonth : true,
			changeYear : true,
			maxDate : '+0m +0w +0d',
			minDate: new Date(year, 0, 1),
			dateFormat : 'dd/mm/yy',
			constrainInput : true,
			showOn: "button",
			buttonText : ''
		});
	}
	else if($("#is-new").val() == "true"){
		// setup date picker for date to 

		$("#dateTo").datepicker({
			changeMonth : true,
			changeYear : true,
			maxDate : '+0m +0w +0d',

			dateFormat : 'dd/mm/yy',
			constrainInput : true,
			showOn: "button",
			buttonText : ''
		});
		$('#date-from-button').click(function() {
	        $('#dateFrom').datepicker('show');
	    });
	}
	
	
	//remove the card fields
	$(".card").remove();
	
	
	//hide the fields not required for last year employees
	if(lastYearEmployee) {
		
		$("#detail-complete").hide();
		console.log("last year employee");
		$(".last-year-hide").hide();
		$(".last-year-remove").remove();
		$(".last-year-edit").hide();
		$(".last-year-no-edit").show();
		$(".last-year-no-edit").find(".form-control").attr('disabled',
				'disabled');

		$("#taxable").click(function(e) {
			console.log($("#has-taxable").val());
			if($(e.target).attr("id") == "yes-pension" && $("#has-taxable").val() != "true") {
				$("#has-taxable").val("true");
				$('#employeeEditForm').validator('validate');
				currentInputFocus = $("#taxable-focus");
				$('#no-pension').removeClass("active");
				$('#yes-pension').addClass("active");

				$(".last-year-edit").show();

				$("#submit-buttons").show();

			}
			else if($(e.target).attr("id") == "no-pension" && $("#has-taxable").val() != "false") {
				// if the employee is new
				$("#has-taxable").val("false");
				$('#employeeEditForm').validator('validate');
				currentInputFocus = $("#taxable-focus");
				$('#no-pension').addClass("active");
				$('#yes-pension').removeClass("active");
				$(".last-year-edit").hide();

				$("#submit-buttons").show();
				showNoPensionWarning();
				
			}
		});
		
		
	
		// taxable earnings set the yes or no buttons to not clicked
		$('#no-pension').removeClass("active");
		$('#yes-pension').removeClass("active");


		if($("#has-taxable").val() == "false"){
			console.log("NO PENSION");
			$(".last-year-edit").hide();
			$('#no-pension').addClass("active");
			$('#yes-pension').removeClass("active");
		}
		else if($("#has-taxable").val() == "true"){
			console.log("PENSION");
			$(".last-year-edit").show();
			$('#no-pension').removeClass("active");
			$('#yes-pension').addClass("active");
		}
		
	}
	else{
		console.log("thisYEAR");
		$("#date-from-section").hide();
		$("#taxable").click(function(e) {
			console.log($("#has-taxable").val());
			if($(e.target).attr("id") == "yes-pension" && $("#has-taxable").val() != "true") {
				$("#has-taxable").val("true");
				$('#employeeEditForm').validator('validate');
				$(".pension").show();
				$('#no-pension').removeClass("active");
				$('#yes-pension').addClass("active");

			}
			else if($(e.target).attr("id") == "no-pension" && $("#has-taxable").val() != "false") {
				// if the employee is new
				
				$("#has-taxable").val("false");
				$('#employeeEditForm').validator('validate');
				$(".pension").hide();
				$("#date-to-section").hide();
				$('#no-pension').addClass("active");
				$('#yes-pension').removeClass("active");
				showNoPensionWarning();

			}
		});
		
		// taxable earnings set the yes or no buttons to not clicked
		$('#no-pension').removeClass("active");
		$('#yes-pension').removeClass("active");
		
		if($("#has-taxable").val() == "false"){
			console.log("NO PENSION");

			$(".pension").hide();
			$("#date-to-section").hide();
			$('#no-pension').addClass("active");
			$('#yes-pension').removeClass("active");
		}
		else if($("#has-taxable").val() == "true"){
			console.log("PENSION");
			
			$(".pension").show();
			$("#date-to-section").show();
			$('#no-pension').removeClass("active");
			$('#yes-pension').addClass("active");
		}
	}
		
		



		

	

}


(function($) {
	$.fn.setCursorToTextEnd = function() {
		var $initialVal = this.val();
		this.val($initialVal);
	};
})(jQuery);

function showNoPensionWarning() {
	pensionWarningShown = true;
	$("#pensionWarningOK").modal('show');
}

function showDatePensionWarning() {

	$("#pensionDateWarning").modal('show');
}

function showNoSaveWarning() {

	$("#noSaveWarning").modal('show');
}

function showConfirmOKBox() {
	console.log("show confirm" + $(currentInputFocus).attr("id") + " - " + $(currentInputFocus).closest(".form-group").next().attr("id"));
	// setup the document if there is no confirm ok box showing on screen and
	// the skip pension parameter is not defined
	if($('#confirmBox').length == 0 && $("#editHeader").length == 0
			&& $("#submit-buttons:visible").length > 0
			&& $(currentInputFocus).attr("id") != "taxable-focus") {
		console.log("SETUP");
		// hide all rows
		$(".row").hide();
		// show the first row of inputs
		$(".row:eq(0)").show();
		// set the first input item as focus
		if($(".row:eq(0)").find('.form-control:eq(0)').hasClass(
				"select2-container")) {

			$(".row:eq(0)").find('.form-control:eq(0)').select2('focus');
		}
		else {
			$(".row:eq(0)").find('.form-control:eq(0)').focus();
		}


	
		$(".row:eq(0)")
					.after(
							"<div id='confirmBox' class=' row form-group'><input type='hidden' id='lastValue'></input><div class='col-xs-3 text-right'></div><div class='col-xs-6'><span id='confirm-transparent' class='col-xs-12 semi-transparent'><div class='col-xs-12 text-center has-error' id='error-text'></div><div class='col-xs-12 text-center' ><button id='confirmButton' type='button' class='btn btn-primary'>This is correct</button></div></span></div><div class='col-xs-3'></div>");

		
	}

	
	// if the current item in focus is the earnings section
	else if($(currentInputFocus).attr("id") == "earnings") {
		console.log("earnings");
		// if the current section has errors focus on that form control element
		$('#employeeEditForm').validator('validate');

		// if the current section has errors focus on that form control element
		if($('#employeeEditForm').find(".with-errors ul:visible").length > 0) {
			console.log("errors");
			$('.with-errors ul:visible').each(function(index) {
				$(this).html();
			});
			$("#confirm-transparent").addClass("semi-transparent-message-box-error");
			$("#confirm-transparent").removeClass("semi-transparent");
			$("#error-text").html("You must correct errors in the boxes marked with red above before continuing");
			if($(currentInputFocus).find('.form-control').first().hasClass(
					"select2-container")) {

				$(currentInputFocus).select2('focus');
			}
			else {
				$(currentInputFocus).focus();
			}
		}
		// show the pension warning
		else {
			$("#confirm-transparent").removeClass("semi-transparent-message-box-error");
			$("#confirm-transparent").addClass("semi-transparent");
			$("#error-text").html("");
			console.log($('#earnings').val());
			console.log(employmentStartYear);
			var noearnings = (parseInt($('#earnings').val()) == 0);

			if(noearnings && !pensionWarningShown && (employmentStartYear == 2014)) {
				pensionWarningShown = true;
				showNoPensionWarning();
			}
			else {
				if($(currentInputFocus).closest(".row").next().attr("id") == "confirmBox") {
					$(currentInputFocus).closest(".row").next().remove();
				}
				// show the next section
				// find all form-group elements with the parent form of the
				// current
				// element
				var elements = $(currentInputFocus).closest('form').find(
						'.form-group');
				// get the index of the current form group item
				var index = elements.index($(currentInputFocus).closest(
						".form-group"));
				// get the next form group item within the list using the index
				// of
				// the
				// previous item
				var nextFormGroup = elements.slice(index + 1, index + 2);
				$(nextFormGroup).closest('.row').show();

				// get the title of the next form group item from its label
	

				// focus on the next input item depending on whether it is an
				// input
				// or
				// select2
				if($(nextFormGroup).find('.form-control').first().hasClass(
						"select2-container")) {

					$(nextFormGroup).find('.form-control').first().select2(
							'focus');
				}
				else {
					$(nextFormGroup).find('.form-control').focus();
				}

				// if the current section does not contain buttons add the
				// confirm
				// ok dialog
				if(!$(nextFormGroup).find('.btn-group').length > 0) {


						$(nextFormGroup)
								.closest(".row")
								.after(
										"<div id='confirmBox' class=' row form-group'><input type='hidden' id='lastValue'></input><div class='col-xs-3 text-right'></div><div class='col-xs-6'><span id='confirm-transparent' class='col-xs-12 semi-transparent'><div class='col-xs-12 text-center has-error' id='error-text'></div><div class='col-xs-12 text-center' ><button id='confirmButton' type='button' class='btn btn-primary'>This is correct</button></div></span></div><div class='col-xs-3'></div>");

					
				}
			}

		}
	}
	// if the current item in focus is the taxable section
	
	/*
	 * if the current item in focus is the held-card section else
	 * if($(currentInputFocus).closest(".form-group").next().attr("id") ==
	 * "previously-held-card-section") {
	 * 
	 * var elements = $(currentInputFocus).closest('form').find('.form-group'); //
	 * get the index of the current form group item var index =
	 * elements.index($(currentInputFocus).closest(".form-group")); // get the
	 * next form group item within the list using the index of // the //
	 * previous item var nextFormGroup = elements.slice(index + 1, index + 2);
	 * $(nextFormGroup).show();
	 *  }
	 */

	// not the first item continue to show the next form control item and
	// confirm/ok box
	else {

		// check that the current input item is valid if it doesn't have errors
		// already

		

		$('#employeeEditForm').validator('validate');

		// if the current section has errors focus on that form control element
		if($('#employeeEditForm').find(".with-errors ul:visible").length > 0) {
			console.log("errors");
			$('.with-errors ul:visible').each(function(index) {
				$(this).html();
			});
			$("#confirm-transparent").addClass("semi-transparent-message-box-error");
			$("#confirm-transparent").removeClass("semi-transparent");
			$("#error-text").html("You must correct errors in the boxes marked with red above before continuing");
			if($(currentInputFocus).find('.form-control').first().hasClass(
					"select2-container")) {

				$(currentInputFocus).select2('focus');
			}
			else {
				$(currentInputFocus).focus();
			}
		}
		// if valid add confirm/ok box for the next input item
		else {

			$("#confirm-transparent").removeClass("semi-transparent-message-box-error");
			$("#confirm-transparent").addClass("semi-transparent");
			$("#error-text").html("");
			// remove the confirm box after the current row if its id is
			// #confirmBox

			if($(currentInputFocus).closest(".row").next().attr("id") == "confirmBox") {
				$(currentInputFocus).closest(".row").next().remove();
			}
			// console.log($(currentInputFocus).html());
			// find all form-group elements with the parent form of the current
			// element
			var elements = $(currentInputFocus).closest('form').find(
					'.form-group');
			// get the index of the current form group item
			var index = elements.index($(currentInputFocus).closest(
					".form-group"));
			// get the next form group item within the list using the index of
			// the
			// previous item
			var nextFormGroup = elements.slice(index + 1, index + 2);
			// console.log($(nextFormGroup).html());

			// if the current section is a card section
			if($(nextFormGroup).closest('.row').attr("class").indexOf("card") > -1) {
				while ($(nextFormGroup).closest('.row').attr("class").indexOf(
						"card") > -1) {

					if($(nextFormGroup).find('.form-control').first().hasClass(
							"select2-container")) {
						console.log("disable sel");
						$(nextFormGroup).find('.form-control').first().select2(
								"disable", true);
					}
					else {
						console.log("disable input");
						$(nextFormGroup).find('.form-control').attr('disabled',
								'disabled');
					}

					console.log("card skip");
					$(nextFormGroup).closest('.row').show();
					index = index + 1;
					nextFormGroup = elements.slice(index + 1, index + 2);

				}
				console.log("no card show");
				$(nextFormGroup).closest('.row').show();
			}

			// if the current section is a card section
			if($(nextFormGroup).closest('.row').attr("class")
					.indexOf("address") > -1) {
				while ($(nextFormGroup).closest('.row').attr("class").indexOf(
						"address") > -1) {

					console.log("address skip");
					$(nextFormGroup).closest('.row').show();
					index = index + 1;
					nextFormGroup = elements.slice(index + 1, index + 2);

				}
				$(nextFormGroup).closest('.row').show();
			}

			// if the current section is a date from section
			else if($(nextFormGroup).closest('.row').attr("class")
					.indexOf("date-from-section") > -1  && $("#newHeader").length == 0) {
				while ($(nextFormGroup).closest('.row').attr("id")
						.indexOf("date-from-section") > -1) {

					console.log("date from skip");
					//$(nextFormGroup).closest('.row').show();
					index = index + 1;
					nextFormGroup = elements.slice(index + 1, index + 2);

				}
				$(nextFormGroup).closest('.row').show();
			}
			
			else {
				// if the next formGroupItem's parent row is not visible make it
				// show
				$(nextFormGroup).closest('.row').show();
			}

	

			// console.log($(nextFormGroup).html());
			// focus on the next input item depending on whether it is an input
			// or
			// select2
			if($(nextFormGroup).find('.form-control').first().hasClass(
					"select2-container")) {
			
				if($(nextFormGroup).find('#address5').html() !== undefined ){
					
					$("#address1").focus();
				}
				else{
					console.log("ID " + $(nextFormGroup).find('.form-control').attr("id"));
					$(nextFormGroup).find('.form-control').first().select2('focus');
				}
			}
			else {
				

					
					$(nextFormGroup).find('.form-control').focus();

				
			}

			// if the current section does not contain buttons add the confirm
			// ok dialog
			if(!$(nextFormGroup).find('.btn-group').length > 0) {


						$(nextFormGroup)
								.closest(".row")
								.after(
										"<div id='confirmBox' class=' row form-group'><input type='hidden' id='lastValue'></input><div class='col-xs-3 text-right'></div><div class='col-xs-6'><span id='confirm-transparent' class='col-xs-12 semi-transparent'><div class='col-xs-12 text-center has-error' id='error-text'></div><div class='col-xs-12 text-center' ><button id='confirmButton' type='button' class='btn btn-primary'>This is correct</button></div></span></div><div class='col-xs-3'></div>");

					
				
				
			}

		}
	}

	$('html, body').scrollTop($(document).height());
}


