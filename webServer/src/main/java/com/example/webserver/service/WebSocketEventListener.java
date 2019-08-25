package com.example.webserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebSocketEventListener {
	@Autowired
	ChampionDataService championDataService;
	@Autowired
	private SimpMessagingTemplate template;

	@EventListener
	public void test(SessionConnectedEvent event) throws Exception {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
		log.info("[Connected] {}", sha.getSessionId());
	}

	@EventListener
	public void test2(SessionSubscribeEvent event) {
		log.info("[Subscribed] hi");
		championDataService.toClientData("SoloRank-Hour");
	}

}
