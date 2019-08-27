package com.example.loldashboard.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loldashboard.configuration.EnumConfiguration;
import com.example.loldashboard.configuration.ObjectMapperConfiguration;
import com.example.loldashboard.dto.model.MatchGameData;
import com.example.loldashboard.request.ApiRequester;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FinishedGameSender {

	@Autowired
	RabbitMQSender rabbitMQSender;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Autowired
	ObjectMapperConfiguration objectMapperConfiguration;

	private String getDate(String gameStartTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		Date date = new Date(Long.parseLong(gameStartTime));
		String dateString = formatter.format(date);
		return dateString;
	}

	@Async
	public boolean getFinishedGameData(String gameId) throws Exception {
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
		return dataGetTrue;
	}

	@GetMapping("/testvalue")
	//@Scheduled(cron = "0 0 0/1 * * *")
	public void scheduleGameIdList() throws Exception {
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		long millisecond = System.currentTimeMillis();
		String key = getDate(String.valueOf(millisecond));
		Set<String> values = setOperations.members(key);

		log.info("test {}", key);
		for (String string : values) {
			if (getFinishedGameData(string)) {
				setOperations.remove(key, string);
			}
			log.info("values test  : " + string);

		}
		log.info("test다!");
	}
}
