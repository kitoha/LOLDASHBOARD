package com.example.loldashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.loldashboard.configuration.RegionName;
import com.example.loldashboard.exception.GameListException;
import com.example.loldashboard.exception.ResponseCodeChecker;
import com.example.loldashboard.model.FeaturedGames;
import com.example.loldashboard.request.ApiRequester;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GamesSender {
	@Autowired
	RabbitMQSender rabbitMQSender;
	@Autowired
	private ApiRequester apiRequester;

	@Autowired
	private ResponseCodeChecker responseCodeChecker;

	private String result;

	@Scheduled(fixedDelay = 1000)
	public void scheduleFixedRateTask() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		apiRequester.setRegion(RegionName.Korea);
		apiRequester.setApiUri("/lol/spectator/v4/featured-games");
		this.result = apiRequester.requesGet();
		int responseCode = apiRequester.getResponsCode();
		responseCodeChecker.setResponsCode(responseCode);

		if (!responseCodeChecker.checker()) {
			throw new GameListException(responseCodeChecker.getMessage());
		}

		System.out.println("result : " + this.result);

		FeaturedGames featuredGames = objectMapper.readValue(this.result, FeaturedGames.class);

		rabbitMQSender.send(featuredGames);
	}
}
