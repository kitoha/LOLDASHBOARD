package com.example.webserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
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
		championDataService.scheduledData();
		log.info(" Redis Message GET : " + result);
	}
}
