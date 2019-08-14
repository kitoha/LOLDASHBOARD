package com.example.webserver.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class DataRequestService {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Scheduled(fixedDelay = 30000)
	public void getDataMethod() {
		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
		Set<Object> data = zSetOperations.range("420", 0, -1);
		System.out.println("test 입니다.");
		for (Object object : data) {
			System.out.println(object);
		}

		System.out.println("test 종료");
	}
}
