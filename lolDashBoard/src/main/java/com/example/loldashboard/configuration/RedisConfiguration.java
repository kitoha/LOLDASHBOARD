package com.example.loldashboard.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedisConfiguration {

	@Value("${spring.redis.host}")
	private String host;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		log.info("RedisConnectionFactory start");
		RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
		redisConf.setHostName(host);
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConf);

		return lettuceConnectionFactory;
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate() {
		log.info("RedisTemplate start");
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
		return redisTemplate;
	}
}
