$(function() {
	
	setTimeout(function() {
		$('#successMsg').hide();
	}, 5000);
	
	$('#employeesIsNew1').trigger('click');
	
	$("#employeesTitle").select2({
		placeholder : "You must enter a title",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if ($(data).filter(function() {
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

	$("#teCard\\.cardsCardType").select2({
		placeholder : "You must select a Card Type",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#cardTypeEnum").val())
	});

	$("#employeesSex").select2({
		placeholder : "You must select a gender",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#sexEnum").val())
	});
	$('#employeesMaritalStatus').select2({
		placeholder : "You must select a marital status",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#maritalEnum").val())
	});
	$('#employeesAddress4').select2({
		placeholder : "You must select a county",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#countiesEnum").val())
	});

	var addressselect = $("#employeesAddress5");
	addressselect.find('option[value="Ireland"]').insertBefore(
			addressselect.find('option:eq(1)'));
	$("#employeesAddress5").val('Ireland').trigger('change');

	$('#employeesAddress5').select2({
		placeholder : "You must select a country",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#countriesEnum").val())
	});

	$('#pensions0\\.employmentCategory').select2({
		placeholder : "You must select an Employment Category",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#employmentCatEnum").val())
	});
	$('#employeeCategoryOfEmployment').select2({
		placeholder : "You must select an Employment Category",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#employmentCatEnum").val())
	});

	var select = $("#employeesNationality");
	select.find('option[value="Irish"]').insertBefore(
			select.find('option:eq(1)'));
	select.find('option[value="British"]').insertBefore(
			select.find('option:eq(2)'));

	$('#employeesNationality').select2({
		placeholder : "You must select an Employee Nationality",
		allowClear : true,
		createSearchChoice : function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {
					id : term,
					text : term
				};
			}
		},
		multiple : false,
		data : $.parseJSON($("#nationalityEnum").val())
	});

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

								if ($('#email').val() != '') {
									return $el.val().match(/.*?@.*?\..{2,}/);
								} else {
									return true;
								}

							},
							minlength : function($el) {
								if ($el.val().length == 0
										|| $el.val().length >= 7) {
									return true;
								}
								return false;
							},
							county : function($el) {
								if ($el.val().length > 0) {
									return true;
								}
								return false;
							},
							country : function($el) {

								if ($el.val().length > 0) {
									return true;
								}
								return false;
							},
							title : function($el) {

								if ($el.val().length > 0) {
									return true;
								}
								return false;
							},
							taxable : function($el) {
								/*
								 * console.log($("#has-taxable").is(':checked'));
								 * if($("#has-taxable").val() == "true" ||
								 * $("#has-taxable").val() == "false"){
								 */
								return true;
								/*
								 * } return false;
								 */
							}

						},
						errors : {
							dateofbirth : "You must enter a date of birth and employee must be at least 14 years old",
							emailorphone : "You must enter either a mobile ph no. or email address",
							emailpattern : "You must enter correct email eg. joe@bloggs.ie",
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

				if ($('.has-error:visible').length > 0) {

					submit = false;

				}
				if (validateDate($("#dateFrom").val())) {
					employmentStartYear = new Date(
							$("#histories0\\.ehDateFrom").val().replace(
									/(\d{2})\/(\d{2})\/(\d{4})/, "$2/$1/$3"))
							.getFullYear();
					// console.log("Date To" + employmentEndYear);
				}

				// if the employee is new and they have no taxable earnings show
				// the no save warning
				if ($("#newHeader").length > 0
						&& $("#has-taxable").val() == "false") {

					showNoSaveWarning();
				} else if (submit) {
					// if the pension section is not visible remove that section
					// from
					// uppercase pps number
					$("#employeesPpsNumber").val(
							$("#employeesPpsNumber").val().toUpperCase());

					$('#employeeEditForm').submit();
				}
			});

});

function checkDateOfBirth(date) {
	// console.log("Test" + date.val());
	if(date.val() != '') {
		var pattern = /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/;
		var arrayDate = date.val().match(pattern);
		var dt = new Date(arrayDate[3], arrayDate[2] - 1, arrayDate[1]);
		var input = dt;//$.datepicker.parseDate('dd/mm/yyyy', );
		// console.log(input);
		var newdate = new Date();
		newdate.setFullYear(newdate.getFullYear() - 14);
	
		// console.log(newdate);
		if (input < newdate) {
			// console.log("valid");
			return true;
		} else {
			// console.log("not valid");
			return false;
		}
	} else
		return false;
}

function checkEmailOrPhone(ele) {
	// console.log("e or p !" + $('#email').val() + '!!' + $('#mobileNo').val()
	// + '!');
	if ($('#employeesMobileNo').val() == '' && $('#employeesEmail').val() == ''
			&& $('#employeesMobileNo').is(":visible")
			&& $('#employeesEmail').is(":visible")) {
		// console.log("neither");
		return false;
	}
	// console.log("one");
	return true;
}

function showNoPensionWarning() {

	$("#pensionWarningOK").modal('show');
}

function showDatePensionWarning() {

	$("#pensionDateWarning").modal('show');
}

function showNoSaveWarning() {

	$("#noSaveWarning").modal('show');
}

function validateDate(dtValue) {
	var dtRegex = new RegExp(/\b\d{1,2}[\/-]\d{1,2}[\/-]\d{4}\b/);
	return dtRegex.test(dtValue);
}