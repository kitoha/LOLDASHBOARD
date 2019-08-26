package com.example.webserver.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChampionDataService {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public void setChampionData(String pattern, String resultKeyName, long seconds) {
		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
		Set<String> keys = redisTemplate.keys(pattern);
		zSetOperations.unionAndStore("", keys, resultKeyName);
		redisTemplate.expire(resultKeyName, seconds, TimeUnit.SECONDS);
	}

	public String getChampionData(String resultKeyName) {
		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
		Object object = zSetOperations.rangeWithScores(resultKeyName, 0, -1);
		Gson gson = new Gson();
		String data = gson.toJson(object);
		log.info(" Message send to client: {}", data);
		return data;
	}

	public void scheduledData() {
		String data = getChampionData("SoloRank-Hour");
		template.convertAndSend("/subscribe-server/ChampionData/SoloRank/Hour", data);
		data = getChampionData("SoloRank-Day");
		template.convertAndSend("/subscribe-server/ChampionData/SoloRank/Day", data);
		data = getChampionData("SoloRank-Week");
		template.convertAndSend("/subscribe-server/ChampionData/SoloRank/Week", data);
		data = getChampionData("BAN-Hour");
		template.convertAndSend("/subscribe-server/ChampionData/BAN/Hour", data);
		data = getChampionData("BAN-Day");
		template.convertAndSend("/subscribe-server/ChampionData/BAN/Day", data);
		data = getChampionData("BAN-Week");
		template.convertAndSend("/subscribe-server/ChampionData/BAN/Week", data);
	}

}
