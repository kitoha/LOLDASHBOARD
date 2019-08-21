package com.example.loldashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.loldashboard.configuration.EnumConfiguration;
import com.example.loldashboard.configuration.ObjectMapperConfiguration;
import com.example.loldashboard.dto.model.FeaturedGames;
import com.example.loldashboard.exception.GameListException;
import com.example.loldashboard.exception.ResponseCodeChecker;
import com.example.loldashboard.request.ApiRequester;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GamesSender {
	@Autowired
	RabbitMQSender rabbitMQSender;

	@Autowired
	ObjectMapperConfiguration objectMapperConfiguration;

	@Scheduled(fixedDelay = 30000)
	public void scheduleFixedRateTask() throws Exception {
		String result;
		ApiRequester apiRequester = new ApiRequester();
		ResponseCodeChecker responseCodeChecker = new ResponseCodeChecker();
		apiRequester.setRegion(EnumConfiguration.regionTypeName.Korea.getValue());
		apiRequester.setApiUri("/lol/spectator/v4/featured-games");
		result = apiRequester.requesGet();
		int responseCode = apiRequester.getResponsCode();
		responseCodeChecker.setResponsCode(responseCode);

		if (!responseCodeChecker.checker()) {
			throw new GameListException(responseCodeChecker.getMessage());
		}

		FeaturedGames featuredGames = this.objectMapperConfiguration.objectMapper.readValue(result,
			FeaturedGames.class);
		log.info("result : " + featuredGames);
		rabbitMQSender.championMessageSender(featuredGames);
	}
}
