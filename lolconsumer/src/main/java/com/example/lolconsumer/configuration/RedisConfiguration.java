package com.example.lolconsumer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.lolconsumer.dto.model.FeaturedGames;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedisConfiguration {

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		log.info("RedisConnectionFactory start");
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
		return lettuceConnectionFactory;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		log.info("RedisTemplate start");
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(FeaturedGames.class));
		return redisTemplate;
	}
}
