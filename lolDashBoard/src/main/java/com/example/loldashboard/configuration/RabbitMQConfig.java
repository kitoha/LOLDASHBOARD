package com.example.loldashboard.configuration;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	@Value("${champion_queuename}")
	private String championQueueName;

	@Value("${user_score_queuename}")
	private String userScroeQueueName;

	@Value("${exchange}")
	private String exchange;

	@Value("${champion_routingkey}")
	private String championRoutingKey;

	@Value("${user_routingkey}")
	private String userRountingKey;

	@Bean
	Queue championQueue() {
		return new Queue(championQueueName, true);
	}

	@Bean
	Queue userScoreQueue() {
		return new Queue(userScroeQueueName, true);
	}

	@Bean
	DirectExchange exchange() {
		return new DirectExchange(exchange);
	}

	@Bean
	Binding binding(Queue championQueue, DirectExchange exchange) {
		return BindingBuilder.bind(championQueue).to(exchange).with(championRoutingKey);
	}

	@Bean
	Binding userScorequeueBiding(Queue userScoreQueue, DirectExchange exchange) {
		return BindingBuilder.bind(userScoreQueue).to(exchange).with(userRountingKey);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}
}
