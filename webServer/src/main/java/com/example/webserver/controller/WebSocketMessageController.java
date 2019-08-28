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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webserver.configuration.ObjectMapperConfiguration;
import com.example.webserver.dto.model.BucketData;
import com.example.webserver.dto.model.ChampionData;
import com.example.webserver.dto.model.ChampionWinRateData;
import com.example.webserver.dto.model.ChampionWinRates;
import com.example.webserver.dto.model.ClientUserData;
import com.example.webserver.request.ElasticApi;
import com.example.webserver.service.ChampionDataService;
import com.example.webserver.service.ChampionWinRateService;
import com.example.webserver.service.GetTimeFormatService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin
public class WebSocketMessageController {

	@Autowired
	private ChampionDataService championDataService;

	@Autowired
	private ChampionWinRateService championWinRateService;

	@Autowired
	private GetTimeFormatService getTimeFormatService;

	@GetMapping("/getAllData")
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

	@GetMapping("/winrate/{period}")
	public List<ChampionWinRateData> getAllWinrate(@PathVariable String period) throws Exception {
		String startTime = "";
		String endTime = "";
		endTime = getTimeFormatService.getDayString();
		if (period.equals("Day")) {
			startTime = getTimeFormatService.getDayString();
		} else if (period.equals("Week")) {
			startTime = getTimeFormatService.getFromWeekString();
		}

		return championWinRateService.getChampionWinRateData(startTime, endTime);
	}

}
