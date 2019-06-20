package edu.tbo.web.controller;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.tbo.data.CandidateDAO;
import edu.tbo.web.models.UserModel;

@Controller
public class ListingController {
	
	@Autowired
	CandidateDAO dao;
	
	@RequestMapping(path = "listing", method = RequestMethod.GET)
	public ModelAndView listing() throws SQLException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("listing");
		mav.addObject("models", dao.listFreeCandidates(20));
		return mav;
	}
	
	@RequestMapping(path = "saved", method = RequestMethod.GET)
	public ModelAndView saved(HttpServletRequest request) throws Exception {
		UserModel user = (UserModel) request.getSession().getAttribute("user");
		
		if(user == null) {
			throw new ServletException("Not Authorized");
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("saved");
		mav.addObject("models", dao.listUserCandidates(user.getUserId(), 20));
		return mav;
	}
	
	@RequestMapping(value = "/listing/checkout/{systemId}/{bodyId}", method = RequestMethod.POST)
	public String checkout(@PathVariable int systemId, @PathVariable int bodyId, HttpServletRequest request) throws Exception {
		UserModel user = (UserModel) request.getSession().getAttribute("user");
		
		if(user == null) {
			throw new ServletException("Not Authorized");
		}
		
		dao.addUserCandidate(systemId, bodyId, user.getUserId());
		
		return "redirect:/saved";
	}
	
	@RequestMapping(value = "/listing/release/{systemId}/{bodyId}", method = RequestMethod.POST)
	public String release(@PathVariable int systemId, @PathVariable int bodyId, HttpServletRequest request) throws Exception {
		UserModel user = (UserModel) request.getSession().getAttribute("user");
		
		if(user == null ) {
			throw new ServletException("Not Authorized");
		}
		
		dao.removeUserCandidate(systemId, bodyId, user.getUserId());
		
		return "redirect:/saved";
	}
}
