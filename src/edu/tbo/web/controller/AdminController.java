package edu.tbo.web.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.tbo.logic.CandidateMigrator;
import edu.tbo.web.models.UserModel;

@Controller
public class AdminController {
	
	@Autowired
	CandidateMigrator migrator;
	
	@RequestMapping(path = "/migrator", method = RequestMethod.GET)
	public ModelAndView migrator(HttpServletRequest request) throws Exception {
		UserModel user = (UserModel) request.getSession().getAttribute("user");
		
		if(user == null || !user.getRole().equalsIgnoreCase("admin")) {
			throw new ServletException("Not Authorized");
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setStatus(HttpStatus.OK);
		mav.setViewName("migrator");
		mav.addObject("migrator", migrator);
		return mav;
	}
	
	@RequestMapping(value = "/migrator/{action}", method = RequestMethod.POST)
	public String migratorStart(@PathVariable("action") String action, HttpServletRequest request) throws Exception {
		UserModel user = (UserModel) request.getSession().getAttribute("user");
		
		if(user == null || !user.getRole().equalsIgnoreCase("admin")) {
			throw new ServletException("Not Authorized");
		}
		
		switch(action.toLowerCase()) {
		case "start":
			migrator.start();
			break;
		case "stop":
			migrator.stop();
			break;
		}
		
		return "redirect:/migrator";
	}
}
