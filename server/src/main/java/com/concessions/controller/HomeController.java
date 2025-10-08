package com.concessions.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.model.User;
import com.concessions.spring.SessionContext;

@Controller
@RequestMapping("/home")
public class HomeController extends AbstractController {

	public HomeController() {
		// TODO Auto-generated constructor stub
	}

	@GetMapping(value ={"/", ""})
	public String home (Model model)
	{
		if (SessionContext.getCurrentOrganization() == null) {
			User user = SessionContext.getCurrentUser();
			if (user != null) {
				
			}
			
			return "redirect:/organization/select";
		}
		
		return "index.html";
	}
	
	public String getModule ()
	{
		return "home";
	}
}
