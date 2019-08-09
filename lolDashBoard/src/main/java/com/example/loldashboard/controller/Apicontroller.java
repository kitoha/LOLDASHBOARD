package com.example.loldashboard.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loldashboard.model.ChampionData;
import com.example.loldashboard.model.FeaturedGames;
import com.example.loldashboard.request.ApiRequester;
import com.example.loldashboard.service.RabbitMQSender;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api")
public class Apicontroller {
	private String result;
	@Autowired
	RabbitMQSender rabbitMQSender;

	private ApiRequester apiRequester = new ApiRequester();

	@GetMapping("/champion")
	public String sender() throws Exception {
		ArrayList<String> params = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		apiRequester.setRegion("kr");
		apiRequester.setApiUri("/lol/spectator/v4/featured-games");
		this.result = apiRequester.requesGet();
		FeaturedGames featuredGames = objectMapper.readValue(result,FeaturedGames.class);

//		apiRequester.setRegion("euw1");
//		apiRequester.setApiUri("/lol/spectator/v4/active-games/by-summoner");
//		params.add("RGAPI-828f4999-366b-4c78-9a79-a7ac18b0d265");
//		apiRequester.setParams(params);
//
//		String result = apiRequester.requesGet();
//		ChampionData championData = objectMapper.readValue(result, ChampionData.class);
//
//		rabbitMQSender.send(championData);

		return result;
	}
}
