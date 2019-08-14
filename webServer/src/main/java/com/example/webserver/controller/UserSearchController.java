package com.example.webserver.controller;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.webserver.configuration.RegionName;
import com.example.webserver.request.ApiRequester;

@RestController
@RequestMapping(value = "/search")
public class UserSearchController {

	@Autowired
	ApiRequester apiRequester;
	private String result;

	@GetMapping(value = "/summoner")
	public String searchUserName(@RequestParam("username") String userName) throws Exception {
		ArrayList<String> params = new ArrayList<>();
		String encoderResult = URLEncoder.encode(userName, "UTF-8");
		params.add(encoderResult);
		apiRequester.setRegion(RegionName.Korea);
		apiRequester.setApiUri("/lol/summoner/v4/summoners/by-name");
		apiRequester.setParams(params);
		this.result = apiRequester.requesGet();

		return result;
	}

}
