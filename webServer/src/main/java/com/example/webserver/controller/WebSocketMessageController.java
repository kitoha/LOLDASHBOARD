package com.example.webserver.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webserver.dto.model.ChampionWinRateData;
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
		log.info("WebSocketMessageController : get all champion data start");
		List<String> result = new ArrayList<>();
		result.add(championDataService.getChampionData("SoloRank-Hour"));
		result.add(championDataService.getChampionData("SoloRank-Day"));
		result.add(championDataService.getChampionData("SoloRank-Week"));
		result.add(championDataService.getChampionData("BAN-Hour"));
		result.add(championDataService.getChampionData("BAN-Day"));
		result.add(championDataService.getChampionData("BAN-Week"));
		log.info("WebSocketMessageController : get all champion data end");
		return result;
	}

	@GetMapping("/winrate/{period}")
	public List<ChampionWinRateData> getAllWinrate(@PathVariable String period) throws Exception {
		log.info("WebSocketMessageController : getAllWinrate start");
		String startTime = "";
		String endTime = "";
		endTime = getTimeFormatService.getDayString();
		if (period.equals("Day")) {
			log.info("Today's champion data is taken from Elastic Search.");
			startTime = getTimeFormatService.getDayString();
		} else if (period.equals("Week")) {
			log.info("Get champion data for one week from Elastic Search.");
			startTime = getTimeFormatService.getFromWeekString();
		} else {
			log.info("One hour not supported.");
			return null;
		}

		log.info("WebSocketMessageController : getAllWinrate end");
		return championWinRateService.getChampionWinRateData(startTime, endTime);
	}

}
