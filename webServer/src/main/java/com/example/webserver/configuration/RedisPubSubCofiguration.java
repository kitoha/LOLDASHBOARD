package com.example.webserver.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.example.webserver.service.RedisSubscriber;

@Configuration
@EnableCaching
public class RedisPubSubCofiguration {
	@Autowired
	RedisSubscriber redisSubscriber;

	@Bean
	MessageListenerAdapter messageListenerAdapter() {
		return new MessageListenerAdapter(redisSubscriber);
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(messageListenerAdapter(), topic());
		return container;
	}

	@Bean
	ChannelTopic topic() {
		return new ChannelTopic("champion");
	}

}
