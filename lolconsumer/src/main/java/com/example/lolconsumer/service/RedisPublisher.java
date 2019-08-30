package com.example.lolconsumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisPublisher {
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	public void publish(ChannelTopic topic, String message) {
		redisTemplate.convertAndSend(topic.getTopic(), message);
	}
}
