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
		log.info("Game Data Create Start  View the current game");
		String result;
		ApiRequester apiRequester = new ApiRequester();
		ResponseCodeChecker responseCodeChecker = new ResponseCodeChecker();
		apiRequester.setRegion(EnumConfiguration.regionTypeName.Korea.getValue());
		apiRequester.setApiUri("/lol/spectator/v4/featured-games");
		result = apiRequester.requesGet();
		int responseCode = apiRequester.getResponsCode();
		responseCodeChecker.setResponsCode(responseCode);

		if (!responseCodeChecker.checker()) {
			log.info("api call error");
			throw new GameListException(responseCodeChecker.getMessage());
		}

		FeaturedGames featuredGames = this.objectMapperConfiguration.objectMapper.readValue(result,
			FeaturedGames.class);

		log.info("Call Api Scheduled Number of data called: " + featuredGames.getGameList().size());
		rabbitMQSender.championMessageSender(featuredGames);
		log.info("Game Data Create end rabbitMQ Sendered");
	}
}
