package com.tsh.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class SampleController {

	@RequestMapping("/")
	public String index() {
		return "home.jsp";
	}
}
