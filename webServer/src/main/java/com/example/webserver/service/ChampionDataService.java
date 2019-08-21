package com.example.webserver.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
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

	public String getChampionData(String pattern) {
		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
		List<String> keys = new ArrayList<>();
		RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
		ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
		Cursor<byte[]> cursor = redisConnection.scan(options);

		while (cursor.hasNext()) {
			keys.add(new String(cursor.next()));
		}

		zSetOperations.unionAndStore("", keys, "out");
		Object object = zSetOperations.rangeWithScores("out", 0, -1);
		Gson gson = new Gson();
		String data = gson.toJson(object);
		log.info(" Message send to client: {}", data);
		return data;
	}

	public void toClientData() {
		String data = getChampionData("soloRank*:*-21/08/2019");
		template.convertAndSend("/subscribe-server/ChampionData", data);
	}

	public String fromClientData() {
		String data = getChampionData("soloRank*:*-21/08/2019");
		return data;
	}

	public String fromClientBannedData() {
		String data = getChampionData("BANsoloRank*:*-21/08/2019");
		return data;
	}

}
