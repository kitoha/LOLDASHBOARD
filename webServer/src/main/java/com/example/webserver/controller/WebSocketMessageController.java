package com.example.webserver.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.webserver.configuration.ObjectMapperConfiguration;
import com.example.webserver.dto.model.ClientUserData;
import com.example.webserver.dto.model.GameInfo;
import com.example.webserver.request.ElasticApi;
import com.example.webserver.service.ChampionDataService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin
public class WebSocketMessageController {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private ChampionDataService championDataService;

	@Autowired
	private ObjectMapperConfiguration objectMapperConfiguration;

	@Autowired
	private ElasticApi elasticApi;

	@MessageMapping("/to-client")
	public void fromClient(String content) throws Exception {
		log.info("Message from client: {}", content);
		ClientUserData clientUserData = objectMapperConfiguration.objectMapper
			.readValue(content, ClientUserData.class);

		String item = clientUserData.getGameMode() + "-" + clientUserData.getTimeMode();
		String data = championDataService.getChampionData(item);

		log.info("message get : " + item);
		Thread.sleep(1000);
	}

	@GetMapping("/api/getAllData")
	public List<String> getAllData() {
		List<String> result = new ArrayList<>();
		result.add(championDataService.getChampionData("SoloRank-Hour"));
		result.add(championDataService.getChampionData("SoloRank-Day"));
		result.add(championDataService.getChampionData("SoloRank-Week"));
		result.add(championDataService.getChampionData("BAN-Hour"));
		result.add(championDataService.getChampionData("BAN-Day"));
		result.add(championDataService.getChampionData("BAN-Week"));
		return result;
	}

	@GetMapping("/test/post/{type}/{typeid}/{name}/{id}")
	public void elasticPost(@PathVariable String type, @PathVariable String typeid, @PathVariable String name,
		@PathVariable String id) {
		String url = "lol" + "/" + type + "/" + typeid;
		System.out.println(url);
		GameInfo gameInfo = new GameInfo();
		gameInfo.setName(name);
		gameInfo.setId(id);

		Map<String, Object> result = elasticApi.callElasticApi("POST", url, gameInfo, null);
		System.out.println(result.get("resultCode"));
		System.out.println(result.get("resultBody"));
	}

	@GetMapping("/test/get/{id}")
	public void elasticGet(@PathVariable String id) {
		String url = "lol" + "/" + "classic" + "/" + id;
		Map<String, Object> result = elasticApi.callElasticApi("GET", url, null, null);
		System.out.println(result.get("resultCode"));
		System.out.println(result.get("resultBody"));
	}

	@GetMapping("/test/delete")
	public void elasticDelte() {
		String url = "lol" + "/" + "classic" + "/" + "-dhHzGwBsaWJL9GOFEis";
		Map<String, Object> result = elasticApi.callElasticApi("DELETE", url, null, null);
		System.out.println(result.get("resultCode"));
		System.out.println(result.get("resultBody"));

	}

}
