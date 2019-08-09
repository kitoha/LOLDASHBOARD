package com.example.loldashboard.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.loldashboard.model.FeaturedGames;

@Service
public class RabbitMQSender {
	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Value("${exchange}")
	private String exchange;

	@Value("${routingkey}")
	private String routingkey;

	public void send(FeaturedGames featuredGames) {
		rabbitTemplate.convertAndSend(exchange, routingkey, featuredGames);
		System.out.println("Send msg = " + featuredGames);

	}
}
