package com.example.webserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.webserver.service.ChampionDataService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WebSocketMessageController {

	@Autowired
	ChampionDataService championDataService;

	@MessageMapping("/to-client")
	public String fromClient(String content) throws Exception {
		log.info("Message from client: {}", content);
		String data = championDataService.getChampionData();
		Thread.sleep(1000);
		return data;
	}

	@SendTo("/subscribe-server/ChampionData")
	public String DataToClient(String content) throws Exception {
		log.info("cheking message", content);
		Thread.sleep(1000);
		return content;
	}

}
