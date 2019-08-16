package com.example.webserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class UserInfoApiController {

	@GetMapping("/summoner")
	public String getUserMatchlist(@RequestParam("username") String userName) {
		String result = "";
		return result;
	}

}
