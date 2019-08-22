package com.example.webserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
		championDataService.setChampionData(getTimeFormatService.getDayPattern("soloRank"), "SoloRank-Day", 86400);
		championDataService.setChampionData(getTimeFormatService.getHourPattern("soloRank"), "SoloRank-Hour", 3600);
		championDataService.setChampionData(getTimeFormatService.getWeekPattern("soloRank"), "SoloRank-Week", 604800);
		championDataService.setChampionData(getTimeFormatService.getDayPattern("BANsoloRank"), "BAN-Day", 86400);
		championDataService.setChampionData(getTimeFormatService.getHourPattern("BANsoloRank"), "BAN-Hour", 3600);
		championDataService.setChampionData(getTimeFormatService.getWeekPattern("BANsoloRank"), "BAN-Week", 604800);
		log.info(" Redis Message GET : " + result);
	}
}
