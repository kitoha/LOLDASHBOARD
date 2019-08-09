package com.example.loldashboard.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.loldashboard.model.ChampionData;

@Service
public class RabbitMQSender {
	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Value("${exchange}")
	private String exchange;

	@Value("${routingkey}")
	private String routingkey;

	public void send(ChampionData championData) {
		rabbitTemplate.convertAndSend(exchange, routingkey, championData);
		System.out.println("Send msg = " + championData);

	}
}
