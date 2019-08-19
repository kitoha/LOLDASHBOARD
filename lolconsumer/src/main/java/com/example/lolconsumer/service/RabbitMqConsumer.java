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

	/*밀리초 데이터를 date포맷으로 변경해주는 함수.*/
	private String getDate(String gameStartTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm-dd/MM/yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		Date date = new Date(Long.parseLong(gameStartTime));
		String dateString = formatter.format(date);
		return dateString;
	}

	/*rabbit mq consumer 리스너*/
	@RabbitListener(queues = "${queuename}")
	public void consumer(FeaturedGames featuredGames) {
		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet(); // redis zset 명령어 선언.
		String dateString = ""; // 포맷하여 변경된 날짜 데이터 넣을 변수.
		for (GameList gameList : featuredGames.getGameList()) {
			String gamemode = gameList.getGameMode(); // gameList에 있는 현재 게임 모드 종류 코드.
			dateString = getDate(gameList.getGameStartTime());
			log.info("날짜 " + dateString);
			if (gamemode.equals("CLASSIC")) {
				String configId = gameList.getGameQueueConfigId(); // gameList에 있는 Classic모드 게임에서 SOLO RANK와 NORMAL RANK의 ID.
				if (configId.equals(EnumConfiguration.gameTypeName.soloRank.getValue())) { // SOLO RANK 일경우.
					log.info("5v5 Ranked Solo games");
					for (Participant participant : gameList.getParticipants()) {
						zSetOperations.add(EnumConfiguration.gameTypeName.soloRank.name() + dateString, // REDIS ZADD 명령어로 게임에 참가한 사람들이 선택한 캐릭터 수를 1 추가.
							participant.getChampionId(), 1);
					}
				} else if (configId.equals(EnumConfiguration.gameTypeName.normalGame.getValue())) { // NORMAL RANK 일 경우.
					log.info("5v5 Blind Pick games");
					for (Participant participant : gameList.getParticipants()) {
						zSetOperations.add(EnumConfiguration.gameTypeName.normalGame.name() + dateString,
							participant.getChampionId(), 1);
					}
				}
			} else {
				log.info("It is ARAM!"); // CLASSIC 모드 X.
			}
		}
	}
}
