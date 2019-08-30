package com.example.webserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebSocketEventListener {
	@Autowired
	ChampionDataService championDataService;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@EventListener
	public void connected(SessionConnectedEvent event) throws Exception {
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
		String key = "sessionIds";
		String sessionId = sha.getSessionId();
		setOperations.add(key, sessionId);
		log.info("[Connected] {} ", sessionId);
	}

	@EventListener
	public void subsribed(SessionSubscribeEvent event) {
		log.info("[Subscribed] hi ");
	}

	@EventListener
	public void disConnected(SessionDisconnectEvent event) {
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
		String key = "sessionIds";
		String sessionId = sha.getSessionId();
		setOperations.remove(key, sessionId);
		log.info("[DisConnected] {}", sessionId);
	}

}
