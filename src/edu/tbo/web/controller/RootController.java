package edu.tbo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RootController {
	
	@RequestMapping(path = "", method = RequestMethod.GET)
	public String index() {
		return "index";
	}
}
