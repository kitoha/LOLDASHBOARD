package com.example.webserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import com.example.webserver.configuration.ObjectMapperConfiguration;
import com.example.webserver.dto.model.BucketData;
import com.example.webserver.dto.model.ChampionWinRateData;
import com.example.webserver.dto.model.ChampionWinRates;
import com.example.webserver.request.ElasticApi;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChampionWinRateService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private ObjectMapperConfiguration objectMapperConfiguration;

	@Autowired
	private ElasticApi elasticApi;

	public List<ChampionWinRateData> getChampionWinRateData(String startTime, String endTime) throws Exception {
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		Set<String> values = setOperations.members("championId");
		List<ChampionWinRateData> championWinRateDataList = new ArrayList<>();
		for (String id : values) {
			log.info("championid test" + id);

			String jsondata = "{\n"
				+ "\t\"size\":0,\n"
				+ "\t\"query\":{\n"
				+ "\t\t\"bool\":{\n"
				+ "\t\t\t\"filter\":[\n"
				+ "\t\t\t\t{\n"
				+ "\t\t\t\t\t\"term\":{\"name\":" + id + "}\n"
				+ "\t\t\t\t},\n"
				+ "\t\t\t\t{\n"
				+ "\t\t\t\t\t\"range\":{\n"
				+ "\t\t\t\t\t\"date\":{\n"
				+ "\t\t\t\t\t\t\"gte\":\"" + startTime + "\",\n"
				+ "\t\t\t\t\t\t\"lte\":\"" + endTime + "\"\n"
				+ "\t\t\t\t\t}\n"
				+ "\t\t\t\t}\n"
				+ "\t\t\t\t}\n"
				+ "\t\t\t]\n"
				+ "\t\t}\n"
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
			Integer winCount = 0;
			Integer loseCount = 0;

			for (BucketData bucketData : championWinRates.getAggregations().getChampionData().getBuckets()) {
				if (bucketData.getKey_as_string().equals("true")) {
					winCount = bucketData.getDoc_count();
				} else {
					loseCount = bucketData.getDoc_count();
				}
			}

			Integer playCount = winCount + loseCount;
			double winRate = (winCount.doubleValue() / playCount.doubleValue()) * 100.0;
			winRate = Double.parseDouble(String.format("%.2f", winRate));
			ChampionWinRateData championWinRateData = new ChampionWinRateData(id, winRate, playCount);
			log.info("test data : " + championWinRateData);
			championWinRateDataList.add(championWinRateData);
		}

		return championWinRateDataList;
	}
}
