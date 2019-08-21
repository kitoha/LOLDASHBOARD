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

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String result = (String)redisTemplate.getStringSerializer().deserialize(message.getBody());
		championDataService.toClientData();
		log.info(" Redis Message GET : " + result);
	}
}
