package com.example.webserver.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.webserver.service.ChampionDataService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	@Autowired
	ChampionDataService championDataService;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/websocket-endpoint").setAllowedOrigins("*").withSockJS();

	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/subscribe-server");
		registry.setApplicationDestinationPrefixes("/publish-server");
	}

	//	@EventListener
	//	public void test(SessionConnectedEvent event) throws  Exception{
	//		StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
	//		log.info("[Connected] {}", sha.getSessionId());
	//		championDataService.toClientData();
	//
	//	}

}
