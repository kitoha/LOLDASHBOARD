package com.example.lolconsumer.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.example.lolconsumer.configuration.EnumConfiguration;
import com.example.lolconsumer.dto.model.FeaturedGames;
import com.example.lolconsumer.dto.model.GameList;
import com.example.lolconsumer.dto.model.Participant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RabbitMqConsumer {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	private String getDate(String gameStartTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm-dd/MM/yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		Date date = new Date(Long.parseLong(gameStartTime));
		String dateString = formatter.format(date);
		return dateString;
	}

	@RabbitListener(queues = "${queuename}")
	public void consumer(FeaturedGames featuredGames) {
		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
		String dateString = "";
		for (GameList gameList : featuredGames.getGameList()) {
			String gamemode = gameList.getGameMode();
			dateString = getDate(gameList.getGameStartTime());
			log.info("날짜 " + dateString);
			if (gamemode.equals("CLASSIC")) {
				String configId = gameList.getGameQueueConfigId();
				if (configId.equals(EnumConfiguration.gameTypeName.soloRank.getValue())) {
					log.info("5v5 Ranked Solo games");
					for (Participant participant : gameList.getParticipants()) {
						zSetOperations.add(EnumConfiguration.gameTypeName.soloRank.name() + dateString,
							participant.getChampionId(), 1);
					}
				} else if (configId.equals(EnumConfiguration.gameTypeName.normalGame.getValue())) {
					log.info("5v5 Blind Pick games");
					for (Participant participant : gameList.getParticipants()) {
						zSetOperations.add(EnumConfiguration.gameTypeName.normalGame.name() + dateString,
							participant.getChampionId(), 1);
					}
				}
			} else {
				log.info("It is ARAM!");
			}
		}
	}
}
