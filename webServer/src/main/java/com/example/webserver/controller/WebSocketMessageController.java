package com.example.webserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.webserver.configuration.ObjectMapperConfiguration;
import com.example.webserver.dto.model.ClientUserData;
import com.example.webserver.service.ChampionDataService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WebSocketMessageController {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private ChampionDataService championDataService;

	@Autowired
	private ObjectMapperConfiguration objectMapperConfiguration;

	@MessageMapping("/to-client")
	public void fromClient(String content) throws Exception {
		log.info("Message from client: {}", content);
		ClientUserData clientUserData = objectMapperConfiguration.objectMapper
			.readValue(content, ClientUserData.class);

		String item = clientUserData.getGameMode() + "-" + clientUserData.getTimeMode();
		String data = championDataService.getChampionData(item);

		log.info("message get : " + item);
		Thread.sleep(1000);

		//template.convertAndSend("/subscribe-server/ChampionData/SoloRank/Hour", data);
		//		template.convertAndSendToUser(headerAccessor.getSessionId(),"/subscribe-server/ChampionData/"+
		//			clientUserData.getGameMode() + "/" +clientUserData.getTimeMode(), data);

		//		template.convertAndSend("/subscribe-server/ChampionData/"+
		//			clientUserData.getGameMode() + "/" +clientUserData.getTimeMode(), data);
	}

}
