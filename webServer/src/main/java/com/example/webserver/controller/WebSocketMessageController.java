package com.example.webserver.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webserver.configuration.ObjectMapperConfiguration;
import com.example.webserver.dto.model.ChampionWinRates;
import com.example.webserver.dto.model.ClientUserData;
import com.example.webserver.request.ElasticApi;
import com.example.webserver.service.ChampionDataService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin
public class WebSocketMessageController {

	@Autowired
	RedisTemplate<String, String> redisTemplate;
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

	@GetMapping("/api/winrate")
	public String getAllWinrate() throws Exception {
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		Set<String> values = setOperations.members("championId");

		for (String id : values) {
			log.info("championid test" + id);
			String jsondata = "{\n"
				+ "\t\"size\":0,\n"
				+ "\t\"query\":{\n"
				+ "\t\t\"term\":{\"name\":" + id + "}\n"
				+ "\t},\n"
				+ "\t\"aggs\":{\n"
				+ "\t\t\"championData\":{\n"
				+ "\t\t\t\"terms\":{\"field\":\"win\"}\n"
				+ "\t\t}\n"
				+ "\t}\n"
				+ "}";

			Map<String, Object> result = elasticApi.callElasticApi("GET", "lol/classic/_search", null, jsondata);
			String championData = result.get("resultBody").toString();
			ChampionWinRates championWinRates = this.objectMapperConfiguration.objectMapper.readValue(championData,
				ChampionWinRates.class);

			log.info("result : " + championWinRates);
		}
		return "success";
	}

}
