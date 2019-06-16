package edu.tbo.web.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.tbo.data.UserDAO;
import edu.tbo.web.filters.MessageFilter;
import edu.tbo.web.models.UserModel;

@Controller
public class UserController {
	
	@Autowired
	UserDAO userDao;
	
	@RequestMapping(path = "login", method = RequestMethod.GET)
	public String doLogin() {
		return "login";
	}
	
	@RequestMapping(path = "logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/";
	}
	
	@RequestMapping(path = "login", method = RequestMethod.POST)
	public String login(@ModelAttribute UserModel user, HttpServletRequest request) {
		try {
			List<String> messages = new ArrayList<String>();
			
			UserModel dbUser = userDao.getUser(user.getUserId());//, user.getPassword());
			if(dbUser == null) {
				messages.add("Invalid Login");
			}
			
			// Add User
			if(messages.isEmpty()) {
				request.getSession().setAttribute("user", dbUser);
				messages.add("Succesful Login:" + dbUser.getUserId());
			}
			
			// TODO: support anon users
			MessageFilter.addUserMessage(request.getSession(), messages.toArray(new String[messages.size()]));
						
			return "redirect:/";	
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@RequestMapping(path = "register", method = RequestMethod.POST)
	public String register(@ModelAttribute UserModel user, HttpServletRequest request) {
		try {
			List<String> messages = new ArrayList<String>();
			String userId = user.getUserId();
			String password = user.getPassword();
			
			// Validate
			if(userId == null) {
				messages.add("User ID is required");
			}
			else if(userId.length() > 25 || userId.length() < 4) {
				messages.add("User ID must be between 4 and 25 characters");
			}
			if(password == null) {
				messages.add("Password is requried");
			}
			else if(password.length() > 35 || password.length() < 4) {
				messages.add("Password must be between 4 and 35 characters");
			}
			if(userDao.getUser(userId) != null) {
				messages.add("User account " + userId + " already exists, please try another name");
			}
			
			// Add User
			if(messages.isEmpty()) {
				try {
					userDao.addUser(userId, password, "webuser");
					messages.add("Account Created Succesfully");
				}
				catch(SQLException ex) {
					messages.add("An error occured while creating your account, username or password format my be unsupported");
				}
			}
			
			// TODO: support anon users
			MessageFilter.addUserMessage(request.getSession(), messages.toArray(new String[messages.size()]));
				
			return "redirect:/";	
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
