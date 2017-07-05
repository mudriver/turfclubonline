package ie.turfclub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LinkController {


	@RequestMapping(value = "/logout")
	public String logout(Model model,
			@RequestParam(value = "logout") String logout) {

		// System.out.println(model.asMap());
		model.addAttribute("logout", logout);
		return "login";
	}

	@RequestMapping(value = "/notAuthorised")
	public String notAuthorised() {

		return "notAuthorised";
	}
	
	@RequestMapping(value = "/upload")
	public String upload() {

		return "upload";
	}

}