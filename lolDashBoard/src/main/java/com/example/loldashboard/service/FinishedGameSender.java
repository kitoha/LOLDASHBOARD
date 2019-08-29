package com.example.loldashboard.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.loldashboard.configuration.EnumConfiguration;
import com.example.loldashboard.configuration.ObjectMapperConfiguration;
import com.example.loldashboard.dto.model.MatchGameData;
import com.example.loldashboard.request.ApiRequester;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FinishedGameSender {

	@Autowired
	RabbitMQSender rabbitMQSender;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Autowired
	ObjectMapperConfiguration objectMapperConfiguration;

	private String getDate(String gameStartTime) {
		log.info("FinishedGameSender getDate start");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		Date date = new Date(Long.parseLong(gameStartTime));
		String dateString = formatter.format(date);
		log.info("FinishedGameSender getDate end");
		return dateString;
	}

	@Async
	public boolean getFinishedGameData(String gameId) throws Exception {
		log.info("FinishedGameSender getFinishedGameData start");
		String result;
		ApiRequester apiRequester = new ApiRequester();
		apiRequester.setRegion(EnumConfiguration.regionTypeName.Korea.getValue());
		apiRequester.setApiUri("/lol/match/v4/matches/" + gameId);
		result = apiRequester.requesGet();
		int responseCode = apiRequester.getResponsCode();
		boolean dataGetTrue = true;

		if (responseCode == 404) {
			log.info("not data");
			dataGetTrue = false;
		} else {
			MatchGameData matchGameData = this.objectMapperConfiguration.objectMapper.readValue(result,
				MatchGameData.class);
			log.info("게임 데이터 : " + matchGameData);
			rabbitMQSender.userMessageSender(matchGameData);
		}
		Thread.sleep(5000);

		log.info("FinishedGameSender getFinishedGameData end");
		return dataGetTrue;
	}

	@Scheduled(cron = "0 0 0/1 * * *")
	public void scheduleGameIdList() throws Exception {
		log.info("FinishedGameSender scheduleGameIdList start");
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		long millisecond = System.currentTimeMillis();
		String key = getDate(String.valueOf(millisecond));
		Set<String> values = setOperations.members(key);
		log.info("Gets the champion ID that played the game so far.");
		for (String string : values) {
			if (getFinishedGameData(string)) {
				setOperations.remove(key, string);
			}
			log.info("champion Id  : " + string);
		}

		log.info("FinishedGameSender scheduleGameIdList end");
	}
}
