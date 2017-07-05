package ie.turfclub.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import ie.turfclub.model.login.User;
import ie.turfclub.model.stableStaff.TeChangesLog;
import ie.turfclub.model.stableStaff.TeEmployees;
import ie.turfclub.model.stableStaff.TeEmployentHistory;
import ie.turfclub.model.stableStaff.TeTrainers;
import ie.turfclub.pojos.StatusResponse;
import ie.turfclub.service.downloads.DownloadService;
import ie.turfclub.service.downloads.TokenService;
import ie.turfclub.service.stableStaff.FileService;
import ie.turfclub.service.stableStaff.StableStaffService;
import ie.turfclub.utilities.EmployeeHistoryUtils;
import ie.turfclub.utilities.FTPUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping(value = "/trainersEmployeesOnline")
public class StableStaffController {

	static Logger logger = LoggerFactory.getLogger(StableStaffController.class);

	@Autowired
	StableStaffService stableStaffService;
	@Autowired
	EmployeeHistoryUtils employeeUtils;
	@Autowired
	private DownloadService downloadService;
	@Autowired
	private FileService fileService;
	
	@Autowired
	private TokenService tokenService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listStaffMembers(
			Model model,
			Authentication authentication,
			@RequestParam(value = "saveMessage", required = false) String saveMessage) {
		logger.info("IN: Staff Members for :" + authentication.getName());

		Object principal = authentication.getPrincipal();
		User user = (User) principal;
		TeTrainers trainer = stableStaffService.getTrainer(user.getId());

		Integer id = user.getId();
		System.out.println(id);
		List<TeEmployees> employees = stableStaffService.getEmployees(id);
		System.out.println("Sort empl");
		employeeUtils.sortEmployees(employees, id);
		if (saveMessage != null) {
			// logger.info("Save message" + saveMessage);
			model.addAttribute("saveMessage", saveMessage);
		}

		model.addAttribute("viewOnly", trainer.isTrainerReturnComplete());
		model.addAttribute("employees", employeeUtils.getCurrentEmployees());
		model.addAttribute("leftEmployees", employeeUtils.getEmployeesLeft());
		model.addAttribute("newEmployees", employeeUtils.getNewEmployees());
		model.addAttribute("over65Employees",
				employeeUtils.getEmployeesOver65());
		model.addAttribute("trainerName", user.getTrainerFirstName() + " "
				+ user.getTrainerSurname());
		System.out.println("P35" + trainer.isTrainerP35Attached());
		model.addAttribute("pdfUploaded", fileService.hasFiles(trainer.getTrainerId()));
		model.addAttribute("trainer", trainer);

		return "stablestaff-list";
	}

	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String menu(Model model, Authentication authentication) {
		Object principal = authentication.getPrincipal();
		User user = (User) principal;
		TeTrainers trainer = stableStaffService.getTrainer(user.getId());
		model.addAttribute("returnComplete", trainer.isTrainerReturnComplete());
		model.addAttribute("returnDate", "18/11/2015");
		//model.addAttribute("p35Returned", fileService.hasFiles(trainer.getTrainerAccountNo()));
		return "stablestaff-mainmenu";
	}

	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public String complete(Model model,@ModelAttribute TeTrainers trainerDetail, Authentication authentication) {

		System.out.println("Trainer Contact:" + trainerDetail.getTrainerContactName());
		
		Object principal = authentication.getPrincipal();
		User user = (User) principal;
		TeTrainers trainer = stableStaffService.getTrainer(user.getId());
		trainer.setTrainerContactName(trainerDetail.getTrainerContactName());
		trainer.setTrainerContactPhone(trainerDetail.getTrainerContactPhone());
		trainer.setTrainerReturnComplete(true);
		trainer.setTrainerDateCompleted(new Date());
		stableStaffService.saveTrainer(trainer);
		model.addAttribute("ref", user.getUsername());
		model.addAttribute("p35Returned", fileService.hasFiles(trainer.getTrainerId()));
		return "stablestaff-complete";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editStaffMember(Model model, Authentication authentication,
			@RequestParam(value = "type", required = true) String type,
			@RequestParam(value = "id", required = false) Integer id) {

		Object principal = authentication.getPrincipal();
		User user = (User) principal;
		if (type.equals("edit") || type.equals("update")) {
			logger.info("EDIT" + type + id);

			TeEmployees employee = stableStaffService.getEmployee(id);

			model.addAttribute("employee", stableStaffService.getEmployee(id));

			List<TeEmployentHistory> histories = stableStaffService
					.getEmploymentHistories(id, user.getId());
			if (histories.isEmpty()) {
				return "redirect:/logout?logout=You are not authorised to edit this employee";
			}
			TeEmployentHistory history = employeeUtils
					.createSingleEmploymentHistoryFromList(histories);

			model.addAttribute("employeeHistory", history);
		} else {
			logger.info("EDIT" + type);
			TeEmployees newEmployee = new TeEmployees();
			newEmployee.setEmployeesIsNew(true);
			model.addAttribute("employee", newEmployee);
			model.addAttribute("employeeHistory", new TeEmployentHistory());
		}
		model.addAttribute("sexEnum", stableStaffService.getSexEnum());
		model.addAttribute("maritalEnum",
				stableStaffService.getMaritalStatusEnum());
		model.addAttribute("employmentCatEnum",
				stableStaffService.getEmploymentCategoryEnum());
		model.addAttribute("cardTypeEnum", stableStaffService.getCardTypeEnum());
		model.addAttribute("nationalityEnum",
				stableStaffService.getNationalityEnum());
		model.addAttribute("hoursEnum", stableStaffService.getHoursWorkedEnum());
		model.addAttribute("titlesEnum", stableStaffService.getTitlesEnum());
		model.addAttribute("countiesEnum", stableStaffService.getCountiesEnum());
		model.addAttribute("countriesEnum",
				stableStaffService.getCountriesEnum());
		model.addAttribute("update", type.equals("update"));
		model.addAttribute("edit", type.equals("edit"));
		Calendar now = Calendar.getInstance();
		//CHANGE EARNINGS YEAR HERE
		//Change here to -1 when earnings for 2015 should be entered  
		now.add(Calendar.YEAR, -2);
		model.addAttribute("currentYear", now.get(Calendar.YEAR));

		return "stablestaff-edit";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveStaffMember(
			Model model,
			Authentication auth,
			@ModelAttribute TeEmployees employeeUpdated,
			@ModelAttribute TeEmployentHistory history,
			@RequestParam(value = "saveMessage", required = true) String saveMessage) {

		Object principal = auth.getPrincipal();
		User user = (User) principal;
		TeEmployees originalEmployee = null;
		TeChangesLog changesLog = new TeChangesLog();
		StringBuilder sb = new StringBuilder("");
		logger.info("Employee Save" + employeeUpdated.getEmployeesEmployeeId());
		TeTrainers trainer = stableStaffService.getTrainer(((User) auth
				.getPrincipal()).getId());
		changesLog.setLogTrainerId(user.getId());
		// check if an employee is being edited and if so get the original
		// employee object and save to log
		if (employeeUpdated.getEmployeesEmployeeId() != null) {

			originalEmployee = stableStaffService.getEmployee(employeeUpdated
					.getEmployeesEmployeeId());

			changesLog.setLogEmployeeId(originalEmployee);
		}
		System.out.println(employeeUpdated.getEmployeesTitle());
		System.out.println(employeeUpdated.getEmployeesSex());
		System.out.println(employeeUpdated.getEmployeesFirstname());
		System.out.println(employeeUpdated.getEmployeesSurname());
		System.out.println(employeeUpdated.getEmployeesDateOfBirth());

		System.out.println(employeeUpdated.getEmployeesHasTaxableEarnings());
		System.out.println(history.getEhEarnings());
		System.out.println(employeeUpdated.getEmployeesPpsNumber());
		System.out.println(history.getEhDateFrom() + " "
				+ history.getEhDateTo());

		System.out.println(employeeUpdated.getEmployeesAddress1());
		System.out.println(employeeUpdated.getEmployeesAddress2());
		System.out.println(employeeUpdated.getEmployeesAddress3());
		System.out.println(employeeUpdated.getEmployeesAddress4());
		System.out.println(employeeUpdated.getEmployeesPostCode());
		System.out.println(employeeUpdated.getEmployeesAddress5());
		System.out.println(employeeUpdated.getEmployeesNationality());
		System.out.println(employeeUpdated.getEmployeesPhoneNo());
		System.out.println(employeeUpdated.getEmployeesMobileNo());
		System.out.println(employeeUpdated.getEmployeesEmail());
		System.out.println(employeeUpdated.getEmployeesMaritalStatus());

		if (originalEmployee != null) {
			// check if the title was changed
			logger.info("Employee Edits to:"
					+ employeeUpdated.getEmployeesEmployeeId());
			if (employeeUpdated.getEmployeesTitle() != null
					&& !employeeUpdated.getEmployeesTitle().isEmpty()) {
				if (originalEmployee.getEmployeesTitle() != null
						&& !originalEmployee.getEmployeesTitle().equals(
								employeeUpdated.getEmployeesTitle())) {
					sb.append("Title changed from: '"
							+ originalEmployee.getEmployeesTitle() + "' to: '"
							+ employeeUpdated.getEmployeesTitle() + "'. ");
					originalEmployee.setEmployeesTitle(employeeUpdated
							.getEmployeesTitle());
				} else if (originalEmployee.getEmployeesTitle() == null) {
					sb.append("Title set to: '"
							+ employeeUpdated.getEmployeesTitle() + "'. ");
					originalEmployee.setEmployeesTitle(employeeUpdated
							.getEmployeesTitle());
				}
			}
			// check if the sex was changed
			if (employeeUpdated.getEmployeesSex() != null
					&& !employeeUpdated.getEmployeesTitle().isEmpty()) {
				if (originalEmployee.getEmployeesSex() != null
						&& !originalEmployee.getEmployeesSex().equals(
								employeeUpdated.getEmployeesSex())) {
					sb.append("Gender changed from: "
							+ originalEmployee.getEmployeesSex() + " to: "
							+ employeeUpdated.getEmployeesSex() + ". ");
					originalEmployee.setEmployeesSex(employeeUpdated
							.getEmployeesSex());
				} else if (originalEmployee.getEmployeesSex() == null) {
					sb.append("Gender set to: '"
							+ employeeUpdated.getEmployeesSex() + "'. ");
					originalEmployee.setEmployeesSex(employeeUpdated
							.getEmployeesSex());
				}
			}

			
			// check if the first name was changed
			if (employeeUpdated.getEmployeesFirstname() != null
					&& !employeeUpdated.getEmployeesTitle().isEmpty()) {
				if (originalEmployee.getEmployeesFirstname() != null
						&& !originalEmployee.getEmployeesFirstname().equals(
								employeeUpdated.getEmployeesFirstname())) {
					sb.append("First Name changed from: "
							+ originalEmployee.getEmployeesFirstname()
							+ " to: " + employeeUpdated.getEmployeesFirstname()
							+ ". ");
					originalEmployee.setEmployeesFirstname(employeeUpdated
							.getEmployeesFirstname());
				} else if (originalEmployee.getEmployeesFirstname() == null) {
					sb.append("First Name set to: "
							+ employeeUpdated.getEmployeesFirstname() + ". ");
					originalEmployee.setEmployeesFirstname(employeeUpdated
							.getEmployeesFirstname());
				}

			}

			// check if the surname was changed
			if (employeeUpdated.getEmployeesSurname() != null
					&& !employeeUpdated.getEmployeesTitle().isEmpty()) {
				if (originalEmployee.getEmployeesSurname() != null
						&& !originalEmployee.getEmployeesSurname().equals(
								employeeUpdated.getEmployeesSurname())) {
					sb.append("Surname changed from: "
							+ originalEmployee.getEmployeesSurname() + " to: "
							+ employeeUpdated.getEmployeesSurname() + ". ");
					originalEmployee.setEmployeesSurname(employeeUpdated
							.getEmployeesSurname());
				} else if (originalEmployee.getEmployeesSurname() == null) {
					sb.append("Surname set to: "
							+ employeeUpdated.getEmployeesSurname() + ". ");
					originalEmployee.setEmployeesSurname(employeeUpdated
							.getEmployeesSurname());
				}
			}

			// check if the dob was changed
			if (employeeUpdated.getEmployeesDateOfBirth() != null
					&& !employeeUpdated.getEmployeesTitle().isEmpty()) {
				if (originalEmployee.getEmployeesDateOfBirth() != null
						&& !originalEmployee.getEmployeesDateOfBirth().equals(
								employeeUpdated.getEmployeesDateOfBirth())) {
					sb.append("Date of birth changed from: "
							+ originalEmployee.getEmployeesDateOfBirth()
							+ " to: "
							+ employeeUpdated.getEmployeesDateOfBirth() + ". ");
					originalEmployee.setEmployeesDateOfBirth(employeeUpdated
							.getEmployeesDateOfBirth());
				} else if (originalEmployee.getEmployeesDateOfBirth() == null) {
					sb.append("Date of birth set to: "
							+ employeeUpdated.getEmployeesDateOfBirth() + ". ");
					originalEmployee.setEmployeesDateOfBirth(employeeUpdated
							.getEmployeesDateOfBirth());
				}
			}

			// check if the marital status was changed
			if (employeeUpdated.getEmployeesMaritalStatus() != null
					&& !employeeUpdated.getEmployeesTitle().isEmpty()) {
				if (originalEmployee.getEmployeesMaritalStatus() != null
						&& !originalEmployee.getEmployeesMaritalStatus()
								.equals(employeeUpdated
										.getEmployeesMaritalStatus())) {
					System.out.println("Martial Status Edit"
							+ employeeUpdated.getEmployeesMaritalStatus());
					sb.append("Marital status changed from: "
							+ originalEmployee.getEmployeesMaritalStatus()
							+ " to: "
							+ employeeUpdated.getEmployeesMaritalStatus()
							+ ". ");
					originalEmployee.setEmployeesMaritalStatus(employeeUpdated
							.getEmployeesMaritalStatus());
				} else if (originalEmployee.getEmployeesMaritalStatus() == null) {
					System.out.println("Martial Status set"
							+ employeeUpdated.getEmployeesMaritalStatus());
					sb.append("Marital set to: "
							+ employeeUpdated.getEmployeesMaritalStatus()
							+ ". ");
					originalEmployee.setEmployeesMaritalStatus(employeeUpdated
							.getEmployeesMaritalStatus());
				}
			} else {
				originalEmployee.setEmployeesMaritalStatus(null);
			}
			/*
			 * check if the spouse was changed if
			 * (employeeUpdated.getEmployeesSpouseName() != null &&
			 * !employeeUpdated.getEmployeesTitle().isEmpty()) { if
			 * (originalEmployee.getEmployeesSpouseName() != null &&
			 * !originalEmployee.getEmployeesSpouseName().equals(
			 * employeeUpdated.getEmployeesSpouseName())) {
			 * sb.append("Marital status changed from: " +
			 * originalEmployee.getEmployeesSpouseName() + " to: " +
			 * employeeUpdated.getEmployeesSpouseName() + ". ");
			 * originalEmployee.setEmployeesSpouseName(employeeUpdated
			 * .getEmployeesSpouseName()); } }
			 */
			// check if the address1 was changed
			if (employeeUpdated.getEmployeesAddress1() != null
					&& !employeeUpdated.getEmployeesAddress1().isEmpty()) {
				if (originalEmployee.getEmployeesAddress1() != null
						&& !originalEmployee.getEmployeesAddress1().equals(
								employeeUpdated.getEmployeesAddress1())) {
					sb.append("Address 1 changed from: "
							+ originalEmployee.getEmployeesAddress1() + " to: "
							+ employeeUpdated.getEmployeesAddress1() + ". ");
					originalEmployee.setEmployeesAddress1(employeeUpdated
							.getEmployeesAddress1());
				} else if (originalEmployee.getEmployeesAddress1() == null) {
					sb.append("Address 1 set to: "
							+ employeeUpdated.getEmployeesAddress1() + ". ");
					originalEmployee.setEmployeesAddress1(employeeUpdated
							.getEmployeesAddress1());
				}
			}
			// check if the address2 was changed
			if (employeeUpdated.getEmployeesAddress2() != null
					&& !employeeUpdated.getEmployeesAddress2().isEmpty()) {
				if (originalEmployee.getEmployeesAddress2() != null
						&& !originalEmployee.getEmployeesAddress2().equals(
								employeeUpdated.getEmployeesAddress2())) {
					sb.append("Address 2 changed from: "
							+ originalEmployee.getEmployeesAddress2() + " to: "
							+ employeeUpdated.getEmployeesAddress2() + ". ");
					originalEmployee.setEmployeesAddress2(employeeUpdated
							.getEmployeesAddress2());
				} else if (originalEmployee.getEmployeesAddress2() == null) {
					sb.append("Address 2 set to: "
							+ employeeUpdated.getEmployeesAddress2() + ". ");
					originalEmployee.setEmployeesAddress2(employeeUpdated
							.getEmployeesAddress2());
				}
			}
			// check if the address3 was changed
			if (employeeUpdated.getEmployeesAddress3() != null
					&& !employeeUpdated.getEmployeesAddress3().isEmpty()) {
				if (originalEmployee.getEmployeesAddress3() != null
						&& !originalEmployee.getEmployeesAddress3().equals(
								employeeUpdated.getEmployeesAddress3())) {
					sb.append("Address 3 changed from: "
							+ originalEmployee.getEmployeesAddress3() + " to: "
							+ employeeUpdated.getEmployeesAddress3() + ". ");
					originalEmployee.setEmployeesAddress3(employeeUpdated
							.getEmployeesAddress3());
				} else if (originalEmployee.getEmployeesAddress3() == null) {
					sb.append("Address 3 set to: "
							+ employeeUpdated.getEmployeesAddress3() + ". ");
					originalEmployee.setEmployeesAddress3(employeeUpdated
							.getEmployeesAddress3());
				}
			}
			// check if the address4 was changed

			if (employeeUpdated.getEmployeesAddress4() != null
					&& !employeeUpdated.getEmployeesAddress4().isEmpty()) {
				if (originalEmployee.getEmployeesAddress4() != null
						&& !originalEmployee.getEmployeesAddress4().equals(
								employeeUpdated.getEmployeesAddress4())) {
					sb.append("Address 4 changed from: "
							+ originalEmployee.getEmployeesAddress4() + " to: "
							+ employeeUpdated.getEmployeesAddress4() + ". ");
					originalEmployee.setEmployeesAddress4(employeeUpdated
							.getEmployeesAddress4());
				} else if (originalEmployee.getEmployeesAddress4() == null) {
					sb.append("Address 4 set to: "
							+ employeeUpdated.getEmployeesAddress4() + ". ");
					originalEmployee.setEmployeesAddress4(employeeUpdated
							.getEmployeesAddress4());
				}
			}
			// check if the address5 was changed
			if (employeeUpdated.getEmployeesAddress5() != null
					&& !employeeUpdated.getEmployeesAddress5().isEmpty()) {
				if (originalEmployee.getEmployeesAddress5() != null
						&& !originalEmployee.getEmployeesAddress5().equals(
								employeeUpdated.getEmployeesAddress5())) {
					sb.append("Address 5 changed from: "
							+ originalEmployee.getEmployeesAddress5() + " to: "
							+ employeeUpdated.getEmployeesAddress5() + ". ");
					originalEmployee.setEmployeesAddress5(employeeUpdated
							.getEmployeesAddress5());
				} else if (originalEmployee.getEmployeesAddress5() == null) {
					sb.append("Address 5 set to: "
							+ employeeUpdated.getEmployeesAddress5() + ". ");
					originalEmployee.setEmployeesAddress5(employeeUpdated
							.getEmployeesAddress5());
				}
			}
			// check if the postcode was changed
			if (employeeUpdated.getEmployeesPostCode() != null) {
				if (originalEmployee.getEmployeesPostCode() != null
						&& !originalEmployee.getEmployeesPostCode().equals(
								employeeUpdated.getEmployeesPostCode())) {
					sb.append("Postcode changed from: "
							+ originalEmployee.getEmployeesPostCode() + " to: "
							+ employeeUpdated.getEmployeesPostCode() + ". ");
					originalEmployee.setEmployeesPostCode(employeeUpdated
							.getEmployeesPostCode());
				} else if (originalEmployee.getEmployeesPostCode() == null) {
					sb.append("Postcode set to: "
							+ employeeUpdated.getEmployeesPostCode() + ". ");
					originalEmployee.setEmployeesPostCode(employeeUpdated
							.getEmployeesPostCode());
				}
			}
			// check if the phone was changed
			if (employeeUpdated.getEmployeesPhoneNo() != null) {
				if (originalEmployee.getEmployeesPhoneNo() != null
						&& !originalEmployee.getEmployeesPhoneNo().equals(
								employeeUpdated.getEmployeesPhoneNo())) {
					sb.append("Phone changed from: "
							+ originalEmployee.getEmployeesPhoneNo() + " to: "
							+ employeeUpdated.getEmployeesPhoneNo() + ". ");
					originalEmployee.setEmployeesPhoneNo(employeeUpdated
							.getEmployeesPhoneNo());
				} else if (originalEmployee.getEmployeesPhoneNo() == null) {
					sb.append("Phone set to: "
							+ employeeUpdated.getEmployeesPhoneNo() + ". ");
					originalEmployee.setEmployeesPhoneNo(employeeUpdated
							.getEmployeesPhoneNo());
				}
			}
			// check if the Mobile was changed
			if (employeeUpdated.getEmployeesMobileNo() != null) {
				if (originalEmployee.getEmployeesMobileNo() != null
						&& !originalEmployee.getEmployeesMobileNo().equals(
								employeeUpdated.getEmployeesMobileNo())) {
					System.out.println("Mobile Changed");
					sb.append("Mobile changed from: "
							+ originalEmployee.getEmployeesMobileNo() + " to: "
							+ employeeUpdated.getEmployeesMobileNo() + ". ");
					originalEmployee.setEmployeesMobileNo(employeeUpdated
							.getEmployeesMobileNo());
				} else if (originalEmployee.getEmployeesMobileNo() == null) {
					sb.append("Mobile changed set to: "
							+ employeeUpdated.getEmployeesMobileNo() + ". ");
					originalEmployee.setEmployeesMobileNo(employeeUpdated
							.getEmployeesMobileNo());
				}
			}
			// check if the Email was changed
			if (employeeUpdated.getEmployeesEmail() != null) {
				if (originalEmployee.getEmployeesEmail() != null
						&& !originalEmployee.getEmployeesEmail().equals(
								employeeUpdated.getEmployeesEmail())) {
					sb.append("Email changed from: "
							+ originalEmployee.getEmployeesEmail() + " to: "
							+ employeeUpdated.getEmployeesEmail() + ". ");
					originalEmployee.setEmployeesEmail(employeeUpdated
							.getEmployeesEmail());
				} else if (originalEmployee.getEmployeesEmail() == null) {
					sb.append("Email set to: "
							+ employeeUpdated.getEmployeesEmail() + ". ");
					originalEmployee.setEmployeesEmail(employeeUpdated
							.getEmployeesEmail());
				}
			}
			/*
			 * check if the HRI Account No. was changed if
			 * (employeeUpdated.getEmployeesHriAccountNo() != null) { if
			 * (originalEmployee.getEmployeesHriAccountNo() != null &&
			 * !originalEmployee.getEmployeesHriAccountNo().equals(
			 * employeeUpdated.getEmployeesHriAccountNo())) {
			 * sb.append("HRI acc no. changed from: " +
			 * originalEmployee.getEmployeesHriAccountNo() + " to: " +
			 * employeeUpdated.getEmployeesHriAccountNo() + ". ");
			 * originalEmployee.setEmployeesHriAccountNo(employeeUpdated
			 * .getEmployeesHriAccountNo()); } }
			 */
			// check if the PPS No. was changed only if country is selected
			// which is a pension field
			// this way the pps is not over written by 2014 employees left
			// employer
			/*
			 * if (employeeUpdated.getEmployeesPpsNumber() != null &&
			 * employeeUpdated.getEmployeesAddress5() != null) { if
			 * (originalEmployee.getEmployeesPpsNumber() != null &&
			 * !originalEmployee.getEmployeesPpsNumber().equals(
			 * employeeUpdated.getEmployeesPpsNumber())) {
			 * sb.append("PPS no. changed from: " +
			 * originalEmployee.getEmployeesPpsNumber() + " to: " +
			 * employeeUpdated.getEmployeesPpsNumber() + ". ");
			 * originalEmployee.setEmployeesPpsNumber(employeeUpdated
			 * .getEmployeesPpsNumber()); } else if
			 * (originalEmployee.getEmployeesPpsNumber() == null) {
			 * sb.append("PPS no. set to: " +
			 * employeeUpdated.getEmployeesPpsNumber() + ". ");
			 * originalEmployee.setEmployeesPpsNumber(employeeUpdated
			 * .getEmployeesPpsNumber()); } }
			 */
			// check if the Nationality. was changed
			if (employeeUpdated.getEmployeesNationality() != null) {
				if (originalEmployee.getEmployeesNationality() != null
						&& !originalEmployee.getEmployeesNationality().equals(
								employeeUpdated.getEmployeesNationality())) {
					sb.append("Nationality changed from: "
							+ originalEmployee.getEmployeesNationality()
							+ " to: "
							+ employeeUpdated.getEmployeesNationality() + ". ");
					originalEmployee.setEmployeesNationality(employeeUpdated
							.getEmployeesNationality());
				} else if (originalEmployee.getEmployeesNationality() == null) {
					sb.append("Nationality set to: "
							+ employeeUpdated.getEmployeesNationality() + ". ");
					originalEmployee.setEmployeesNationality(employeeUpdated
							.getEmployeesNationality());
				}
			}

			// check if the Taxable Earnings was changed
			if (employeeUpdated.getEmployeesHasTaxableEarnings() != null) {
				if (originalEmployee.getEmployeesHasTaxableEarnings() != null
						&& !originalEmployee.getEmployeesHasTaxableEarnings()
								.equals(employeeUpdated
										.getEmployeesHasTaxableEarnings())) {
					sb.append("Taxable earnings changed from: "
							+ originalEmployee.getEmployeesHasTaxableEarnings()
							+ " to: "
							+ employeeUpdated.getEmployeesHasTaxableEarnings()
							+ ". ");
					originalEmployee
							.setEmployeesHasTaxableEarnings(employeeUpdated
									.getEmployeesHasTaxableEarnings());
				} else if (originalEmployee.getEmployeesHasTaxableEarnings() == null) {
					sb.append("Taxable earnings set to: "
							+ employeeUpdated.getEmployeesHasTaxableEarnings()
							+ ". ");
					originalEmployee
							.setEmployeesHasTaxableEarnings(employeeUpdated
									.getEmployeesHasTaxableEarnings());
				}
			}

			// check if the comments field was changed
			if (employeeUpdated.getEmployeesComments() != null
					&& !employeeUpdated.getEmployeesComments().isEmpty()) {
				if (originalEmployee.getEmployeesComments() != null
						&& !originalEmployee.getEmployeesComments().equals(
								employeeUpdated.getEmployeesAddress3())) {
					sb.append("Comments changed from: "
							+ originalEmployee.getEmployeesComments() + " to: "
							+ employeeUpdated.getEmployeesComments() + ". ");
					originalEmployee.setEmployeesComments(employeeUpdated
							.getEmployeesComments());
				} else if (originalEmployee.getEmployeesComments() == null) {
					sb.append("Comments set to: "
							+ employeeUpdated.getEmployeesComments() + ". ");
					originalEmployee.setEmployeesComments(employeeUpdated
							.getEmployeesComments());
				}
			}

			originalEmployee.setEmployeesLastUpdated(new Date());

			List<TeEmployentHistory> histories = null;
			// if new employee delete existing history and create new history to
			// save
			if (originalEmployee.getEmployeesIsNew()) {

				stableStaffService
						.deleteEmployementHistories(stableStaffService
								.getEmploymentHistories(originalEmployee
										.getEmployeesEmployeeId(), trainer
										.getTrainerId()));
				System.out.println("DELETE FINSIHED");
				histories = employeeUtils
						.createListOfEmploymentHistoriesFromSingleForNewEmployee(history);
			} else {
				histories = employeeUtils.updateExistingEmploymentHistory(
						stableStaffService.getEmploymentHistories(
								originalEmployee.getEmployeesEmployeeId(),
								trainer.getTrainerId()), history);

			}

			System.out.println("SAVING HISTORY");
			// save history record for employee
			for (TeEmployentHistory newHistory : histories) {

				if (originalEmployee.getEmployeesIsNew()) {
					newHistory.setTeTrainers(trainer);
					newHistory.setTeEmployees(originalEmployee);
					// System.out.println();
					// System.out.println("save NEW History");
					
					//Set Employee Last Updated
					newHistory.setEmployeeLastUpdated(new Date());
					stableStaffService.saveNewHistory(newHistory);
				} else {
					newHistory.setTeTrainers(trainer);
					newHistory.setTeEmployees(originalEmployee);
					// System.out.println();
					// System.out.println("save NEW History");
					
					//Set Employee Last Updated
					newHistory.setEmployeeLastUpdated(new Date());
					stableStaffService.saveHistory(newHistory);
				}

			}

			changesLog.setLogDetail(sb.toString());

			stableStaffService.saveEmployee(originalEmployee);
			stableStaffService.saveLog(changesLog);
			logger.info("Employee updated: " + sb.toString());

		} else {
			// log the details of the new employee added

			logger.info("Employee Save new");

			employeeUpdated.setEmployeesDateEntered(new Date());
			employeeUpdated.setEmployeesLastUpdated(new Date());
			employeeUpdated.setEmployeesIsNew(true);
			TeEmployees savedEmployee = stableStaffService
					.getEmployee(stableStaffService
							.saveNewEmployee(employeeUpdated));
			changesLog.setLogEmployeeId(savedEmployee);

			// save history record for employee
			List<TeEmployentHistory> histories = employeeUtils
					.createListOfEmploymentHistoriesFromSingleForNewEmployee(history);
			for (TeEmployentHistory newHistory : histories) {

				newHistory.setTeTrainers(trainer);
				newHistory.setTeEmployees(savedEmployee);
				//Set Employee Last Updated
				newHistory.setEmployeeLastUpdated(new Date());
				stableStaffService.saveNewHistory(newHistory);
			}

			changesLog.setLogDetail("New Employee Entered");

			stableStaffService.saveLog(changesLog);
			saveMessage = "You have successfully updated employee: "
					+ savedEmployee.getEmployeesFirstname() + ' '
					+ savedEmployee.getEmployeesSurname();

		}

		return "redirect:/trainersEmployeesOnline/list?saveMessage="
				+ saveMessage;

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String mainRedirect(Model model) {

		return "redirect:/trainersEmployeesOnline/list";
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> singleSave(@RequestParam("file") MultipartFile[] files,
			Authentication authentication, MultipartHttpServletRequest request,
			HttpServletResponse response) {

		Object principal = authentication.getPrincipal();
		User user = (User) principal;
		System.out.println(user.getUsername());
		System.out.println(files.length);
		String fileNames = "";
		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf = null;
		int count = 0;
		FTPUtils ftpUtils = null;
		try {
			ftpUtils = new FTPUtils("appsrv2.turfclub.ie", "stablestaff",
					"upload@1790");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		while (itr.hasNext()) {

			count++;
			String fileName = null;
			mpf = request.getFile(itr.next());
			fileName = mpf.getOriginalFilename();
			String[] fileNameSplit = fileName.split("\\.");
			System.out.println(fileName);
			System.out.println(fileNameSplit.length);
			System.out.println(fileNameSplit[fileNameSplit.length - 1]);

			fileName = user.getUsername() + "-" + count + "."
					+ fileNameSplit[fileNameSplit.length - 1];
			System.out.println(mpf.getOriginalFilename() + " " + fileName);
			InputStream inputStream = null;
			try {
				inputStream = new ByteArrayInputStream(mpf.getBytes());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Start");

			// FTP server path is relative. So if FTP account HOME directory is
			// "/home/pankaj/public_html/" and you need to upload
			// files to "/home/pankaj/public_html/wp-content/uploads/image2/",
			// you should pass directory parameter as
			// "/wp-content/uploads/image2/"
			try {
				ftpUtils.uploadFile(inputStream, fileName, "/");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ftpUtils.disconnect();
			System.out.println("Done");

			// BufferedOutputStream buffStream = new BufferedOutputStream(new
			// FileOutputStream(new File("C:/test/" + fileName)));
			// buffStream.write(bytes);
			// buffStream.close();

			TeTrainers trainer = stableStaffService.getTrainer(user.getId());
			trainer.setTrainerP35Attached(true);
			stableStaffService.saveTrainer(trainer);
			if (fileNames.length() > 0) {
				fileNames += ", " + mpf.getOriginalFilename();
			} else {
				fileNames += mpf.getOriginalFilename();
			}
		}

		count = 0;
		for (MultipartFile file : files) {
			String fileName = null;
			count++;
			if (!file.isEmpty()) {
				try {
					System.out.println("Start");
					fileName = file.getOriginalFilename();
					byte[] bytes = file.getBytes();
					System.out.println("1");
					String[] fileNameSplit = fileName.split("\\.");
					System.out.println(fileName);
					System.out.println(fileNameSplit.length);
					System.out.println(fileNameSplit[fileNameSplit.length - 1]);

					fileName = user.getUsername() + "-" + count + "."
							+ fileNameSplit[fileNameSplit.length - 1];

					InputStream inputStream = new ByteArrayInputStream(bytes);
					System.out.println("Start");
					FTPUtils ftpUploader = new FTPUtils("appsrv2.turfclub.ie",
							"stablestaff", "upload@1790");
					// FTP server path is relative. So if FTP account HOME
					// directory is "/home/pankaj/public_html/" and you need to
					// upload
					// files to
					// "/home/pankaj/public_html/wp-content/uploads/image2/",
					// you should pass directory parameter as
					// "/wp-content/uploads/image2/"
					ftpUploader.uploadFile(inputStream, fileName, "/");
					ftpUploader.disconnect();
					System.out.println("Done");

					// BufferedOutputStream buffStream = new
					// BufferedOutputStream(new FileOutputStream(new
					// File("C:/test/" + fileName)));
					// buffStream.write(bytes);
					// buffStream.close();

					TeTrainers trainer = stableStaffService.getTrainer(user
							.getId());
					trainer.setTrainerP35Attached(true);
					stableStaffService.saveTrainer(trainer);
					if (fileNames.length() > 0) {
						fileNames += ", " + file.getOriginalFilename();
					} else {
						fileNames += file.getOriginalFilename();
					}

				} catch (Exception e) {
					 
				}
			} else {
				 
			}
		}
		
		 Map<String, Object> filesList = new HashMap<>();
		 filesList.put("files", null);
	        return filesList;
		

	}

	@RequestMapping(value = "/download/progress")
	public @ResponseBody
	StatusResponse checkDownloadProgress(@RequestParam String token) {
		return new StatusResponse(true, tokenService.check(token));
	}

	@RequestMapping(value = "/download/token")
	public @ResponseBody
	StatusResponse getDownloadToken() {
		return new StatusResponse(true, tokenService.generate());
	}

	@RequestMapping(value = "/employeeReportDownload", method = RequestMethod.GET)
	public void generatePdfDownload(@RequestParam String token,
			HttpServletResponse response, Authentication authentication) {
		Object principal = authentication.getPrincipal();
		User user = (User) principal;
		System.out.println("MAKE REPORT FOR:" + user.getUsername());
		TeTrainers trainer = stableStaffService.getTrainer(user.getId());

		// Setup my data connection
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.sql.Connection conn = null;
		try {
			conn = DriverManager
					.getConnection("jdbc:mysql://127.0.0.1:3000/trainers?user=root&password=password");
			BufferedImage image = null;
			try {
				image = ImageIO.read(this.getClass().getResourceAsStream(
						"/images/tclogo.jpg"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("image error");
				e.printStackTrace();
			}
			HashMap<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("trainerId", trainer.getTrainerId());
			parameterMap.put("trainer", trainer.getTrainerFirstName() + " "
					+ trainer.getTrainerSurname());
			parameterMap.put("tclogo", image);
			Calendar now = Calendar.getInstance();
		
			//CHANGE EARNINGS YEAR HERE
			//comment out this line to change to current year 
			now.add(Calendar.YEAR, -1);
			System.out.println("startDate " +  now.get(Calendar.YEAR) + "-01-01");
			parameterMap.put("startDate", now.get(Calendar.YEAR) + "-01-01");
			//CHANGE EARNINGS YEAR HERE
			//change this line to -2  if the above line is commented out
			now.add(Calendar.YEAR, -1);
			
			System.out.println("trainerId " +  trainer.getTrainerId());
			System.out.println("employmentEarningStartDate " +  now.get(Calendar.YEAR) + "-01-01");
			System.out.println("earningEndDate " +  now.get(Calendar.YEAR) + "-12-31");
			parameterMap.put("earningsDateFrom", now.get(Calendar.YEAR) + "-01-01");
			parameterMap.put("earningsDateTo", now.get(Calendar.YEAR) + "-12-31");
			parameterMap.put("REPORT_CONNECTION", conn);
			parameterMap.put("previousYear", Calendar.getInstance().get(Calendar.YEAR)-1);
			parameterMap.put("currYear", Calendar.getInstance().get(Calendar.YEAR));
			logger.info("rendering the pdf view");
			downloadService.download("/jasper/trainersReturn.jasper",
					parameterMap, null, conn, token, response);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
