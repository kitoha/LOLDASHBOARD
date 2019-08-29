package com.example.webserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import com.example.webserver.configuration.EnumConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisSubscriber implements MessageListener {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Autowired
	ChampionDataService championDataService;

	@Autowired
	GetTimeFormatService getTimeFormatService;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		log.info("Redis on Message : Redis subscribe start");
		String result = (String)redisTemplate.getStringSerializer().deserialize(message.getBody());
		championDataService.setChampionData(getTimeFormatService.getDayPattern("soloRank"), "SoloRank-Day",
			EnumConfiguration.dayTypeName.Day.getValue());
		championDataService.setChampionData(getTimeFormatService.getHourPattern("soloRank"), "SoloRank-Hour",
			EnumConfiguration.dayTypeName.Hour.getValue());
		championDataService.setChampionData(getTimeFormatService.getWeekPattern("soloRank"), "SoloRank-Week",
			EnumConfiguration.dayTypeName.Week.getValue());
		championDataService.setChampionData(getTimeFormatService.getDayPattern("BANsoloRank"), "BAN-Day",
			EnumConfiguration.dayTypeName.Day.getValue());
		championDataService.setChampionData(getTimeFormatService.getHourPattern("BANsoloRank"), "BAN-Hour",
			EnumConfiguration.dayTypeName.Hour.getValue());
		championDataService.setChampionData(getTimeFormatService.getWeekPattern("BANsoloRank"), "BAN-Week",
			EnumConfiguration.dayTypeName.Week.getValue());
		log.info("Redis on Message : finished champion all data ");
		SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
		String key = "sessionIds";

		long sessionSize = setOperations.size(key);
		log.info(Long.toString(sessionSize));

		if (sessionSize > 0) {
			log.info("RedisSubscriber : subscribe data broadcasting");
			championDataService.scheduledData();
		} else {
			log.info("There is no sessionId ");
		}

		log.info(" Redis Message GET : " + result);
		log.info("Redis on Message : Redis subscribe end");
	}
}
