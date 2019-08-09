package com.example.lolconsumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.example.lolconsumer.model.ChampionData;

@Component
public class RabbitMQConsumer {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@RabbitListener(queues = "${queuename}")
	public void consumer(ChampionData championData) {
		System.out.println("Received : " + championData.getParticipants().get(1).getChampionId());
		ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();

		valueOperations.set("test2",championData);
	}
}
