package com.example.webserver.controller;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.webserver.configuration.RegionName;
import com.example.webserver.model.ParameterPair;
import com.example.webserver.model.SummonerInfo;
import com.example.webserver.request.ApiRequester;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/search")
public class UserSearchController {

	@Autowired
	ApiRequester apiRequester;
	private String result;

	@GetMapping(value = "/summoner")
	public String searchUserName(@RequestParam("username") String userName) throws Exception {
		ArrayList<ParameterPair> params = new ArrayList<>();
		String encoderResult = URLEncoder.encode(userName, "UTF-8");
		apiRequester.setRegion(RegionName.Korea);
		apiRequester.setApiUri("/lol/summoner/v4/summoners/by-name/");
		apiRequester.setPathParmeters(encoderResult);
		this.result = apiRequester.requesGet();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SummonerInfo summonerInfo = objectMapper.readValue(this.result, SummonerInfo.class);

		params.add(new ParameterPair("beginIndex", "0"));
		params.add(new ParameterPair("endIndex", "10"));
		apiRequester.setPathParmeters(summonerInfo.getAccountId());
		apiRequester.setApiUri("/lol/match/v4/matchlists/by-account/");
		apiRequester.setParams(params);
		this.result = apiRequester.requesGet();

		return result;
	}

}
