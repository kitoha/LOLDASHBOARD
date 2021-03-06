package com.example.loldashboard.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.loldashboard.dto.model.FeaturedGames;
import com.example.loldashboard.dto.model.MatchGameData;

@Service
public class RabbitMQSender {
	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Value("${exchange}")
	private String exchange;

	@Value("${champion_routingkey}")
	private String championRoutingKey;

	@Value("${user_routingkey}")
	private String userRountingKey;

	public void championMessageSender(FeaturedGames featuredGames) {
		rabbitTemplate.convertAndSend(exchange, championRoutingKey, featuredGames);
	}

	public void userMessageSender(MatchGameData matchGameData) {
		rabbitTemplate.convertAndSend(exchange, userRountingKey, matchGameData);
	}
}
