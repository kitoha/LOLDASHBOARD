package com.example.lolconsumer.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.example.lolconsumer.configuration.EnumConfiguration;
import com.example.lolconsumer.dto.model.BannedChampion;
import com.example.lolconsumer.dto.model.CharacterInfoData;
import com.example.lolconsumer.dto.model.FeaturedGames;
import com.example.lolconsumer.dto.model.GameList;
import com.example.lolconsumer.dto.model.MatchGameData;
import com.example.lolconsumer.dto.model.Participant;
import com.example.lolconsumer.dto.model.ParticipantData;
import com.example.lolconsumer.dto.model.TeamData;
import com.example.lolconsumer.request.ElasticApi;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RabbitMqConsumer {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Autowired
	RedisPublisher redisPublisher;

	@Autowired
	ElasticApi elasticApi;

	private String getDate(String gameStartTime) {
		log.info("RabbitMqConsumer getDate start");
		SimpleDateFormat formatter = new SimpleDateFormat("kk:mm-dd/MM/yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		Date date = new Date(Long.parseLong(gameStartTime));
		String dateString = formatter.format(date);
		log.info("RabbitMqConsumer getDate end");
		return dateString;
	}

	@RabbitListener(queues = "${queuename}")
	public void consumer(FeaturedGames featuredGames) {
		log.info("RabbitMqConsumer ingame Listener start");
		if (featuredGames.getGameList() == null) {
			log.info("ingame data is empty");
			return;
		}

		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
		SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
		String dateString = "";
		String key = "";
		String banKey = "";

		for (GameList gameList : featuredGames.getGameList()) {
			String gamemode = gameList.getGameMode();
			dateString = getDate(gameList.getGameStartTime());
			log.info("날짜 " + dateString);

			if (gamemode.equals("CLASSIC")) {
				String configId = gameList.getGameQueueConfigId();
				if (configId.equals(EnumConfiguration.gameTypeName.soloRank.getValue())) {
					log.info("5v5 Ranked Solo games");
					key = EnumConfiguration.gameTypeName.soloRank.name() + dateString;
					banKey = "BAN" + EnumConfiguration.gameTypeName.soloRank.name() + dateString;
					for (BannedChampion bannedChampion : gameList.getBannedChampions()) {
						zSetOperations.add(banKey, bannedChampion.getChampionId(), 1);
					}
				} else if (configId.equals(EnumConfiguration.gameTypeName.normalGame.getValue())) {
					log.info("5v5 Blind Pick games");
					key = EnumConfiguration.gameTypeName.normalGame.name() + dateString;
				}

				for (Participant participant : gameList.getParticipants()) {
					zSetOperations.add(key,
						participant.getChampionId(), 1);
					redisTemplate.expire(key, 604800, TimeUnit.SECONDS);
				}

				setOperations.add(dateString.substring(6), gameList.getGameId());
			} else {
				log.info("It is ARAM!");
			}
		}

		ChannelTopic channelTopic = new ChannelTopic("champion");

		redisPublisher.publish(channelTopic, "send message");

		log.info("RabbitMqConsumer ingame Listener end");
	}

	@RabbitListener(queues = "${user_score_queuename}")
	public void userScoreConsumer(MatchGameData matchGameData) {
		log.info("RabbitMqConsumer finishedgame Listener start");
		log.info("user consumer : " + matchGameData);
		if (matchGameData.getTeams() == null) {
			log.info("finisedgame data is empty");
			return;
		}
		SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
		Integer winTeam = 0;
		String championKey = "championId";
		long milisecondes = System.currentTimeMillis();
		String date = getDate(String.valueOf(milisecondes)).substring(6);

		for (TeamData teamData : matchGameData.getTeams()) {
			if (teamData.getWin().equals("Win")) {
				winTeam = teamData.getTeamId();
			}
		}

		for (ParticipantData participant : matchGameData.getParticipants()) {
			CharacterInfoData characterInfoData = new CharacterInfoData(participant.getChampionId().toString(), true,
				date);
			if (participant.getTeamId() != winTeam) {
				characterInfoData.setWin(false);
			}
			elasticApi.callElasticApi("POST", "lol/classic", characterInfoData, null);
			log.info("elasticApi posted characterInfoData");
			setOperations.add(championKey, participant.getChampionId().toString());
			log.info("add set finished game champion Id at Redis");
		}

		log.info("RabbitMqConsumer finishedgame Listener end");

	}
}
