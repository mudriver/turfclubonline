package ie.turfclub.controller;

import ie.turfclub.model.login.User;
import ie.turfclub.model.stableStaff.TeEmployees;
import ie.turfclub.model.stableStaff.TeEmployeesApproved;
import ie.turfclub.model.stableStaff.TeTrainers;
import ie.turfclub.service.stableStaff.StableStaffService;
import ie.turfclub.service.trainer.EmployeeService;
import ie.turfclub.service.trainer.TrainersService;
import ie.turfclub.utilities.EncryptDecryptUtils;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/employee")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private TrainersService trainersService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private StableStaffService stableStaffService;

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String getEmployeePage(HttpServletRequest request, ModelMap model) {
		
		model.addAttribute("emp", new TeEmployeesApproved());
		
		model.addAttribute("trainers", trainersService.getAllTrainers());
		model.addAttribute("sexEnum", employeeService.getSexEnum());
		model.addAttribute("maritalEnum",
				employeeService.getMaritalStatusEnum());
		model.addAttribute("employmentCatEnum",
				employeeService.getEmploymentCategoryEnum());
		model.addAttribute("titlesEnum", employeeService.getTitlesEnum());
		model.addAttribute("countiesEnum", employeeService.getCountiesEnum());
		model.addAttribute("countriesEnum",
				employeeService.getCountriesEnum());
		model.addAttribute("cardTypeEnum", employeeService.getAllCardType());
		model.addAttribute("pensionEnum", employeeService.getPension());
		model.addAttribute("nationalityEnum", employeeService.getNationalityEnum());
		model.addAttribute("year", trainersService.getYearForTrainerEmployeeOnline());
		model.addAttribute("prevYear", Integer.parseInt(trainersService.getYearForTrainerEmployeeOnline())-1);
		return "emp-add";
	}
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String handleEmployee(Authentication authentication, TeEmployeesApproved emp, HttpServletRequest request, ModelMap model) throws Exception {
		
		if(emp.getEmployeesEmployeeId() != null && emp.getEmployeesEmployeeId() > 0)
			model.addAttribute("success", messageSource.getMessage("success.updated.employee", new String[] {}, Locale.US));
		else
			model.addAttribute("success", messageSource.getMessage("success.added.employee", new String[] {}, Locale.US));
	
		Object principal = authentication.getPrincipal();
		User user = (User) principal;
		TeTrainers trainer = stableStaffService.getTrainer(user.getId());
		
		employeeService.handleSaveOrUpdateEmployeeApproved(emp, trainer);
		
		emp.setEmployeesPpsNumber(EncryptDecryptUtils.decrypt(emp.getEmployeesPpsNumber()));
		model.addAttribute("emp", new TeEmployeesApproved());
		model.addAttribute("trainers", trainersService.getAllTrainers());
		model.addAttribute("sexEnum", employeeService.getSexEnum());
		model.addAttribute("maritalEnum",
				employeeService.getMaritalStatusEnum());
		model.addAttribute("employmentCatEnum",
				employeeService.getEmploymentCategoryEnum());
		model.addAttribute("titlesEnum", employeeService.getTitlesEnum());
		model.addAttribute("countiesEnum", employeeService.getCountiesEnum());
		model.addAttribute("countriesEnum",
				employeeService.getCountriesEnum());
		model.addAttribute("cardTypeEnum", employeeService.getAllCardType());
		model.addAttribute("pensionEnum", employeeService.getPension());
		model.addAttribute("nationalityEnum", employeeService.getNationalityEnum());
		return "emp-add";
	}
}
