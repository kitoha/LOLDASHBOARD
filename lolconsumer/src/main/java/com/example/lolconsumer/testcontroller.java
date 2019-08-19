package com.example.lolconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testcontroller {
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@GetMapping("/test")
	public String test() {
		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
		//		List<String> keys= new ArrayList<>();
		//		keys.add("chmpion");
		//		zSetOperations.unionAndStore("chmpion3",keys,"out");
		return "sucess";
	}

}
