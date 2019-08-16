package com.example.lolconsumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.example.lolconsumer.configuration.EnumConfiguration;
import com.example.lolconsumer.dto.model.FeaturedGames;
import com.example.lolconsumer.dto.model.GameList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RabbitMqConsumer {

	@Autowired
	RedisTemplate<String,Object> redisTemplate;

	@RabbitListener(queues = "${queuename}")
	public void consumer(FeaturedGames featuredGames) {
		ZSetOperations<String, Object> zSetOperations =redisTemplate.opsForZSet();
		//System.out.println("Received : " + featuredGames.getGameList());
		for(GameList gameList : featuredGames.getGameList()){
			Double gameStartTime = Double.parseDouble(gameList.getGameStartTime());
			String gamemode = gameList.getGameMode();
			if(gamemode.equals("CLASSIC")) {
				String configId = gameList.getGameQueueConfigId();
				if (configId.equals(EnumConfiguration.gameTypeName.soloRank.getValue())) {
					log.info("5v5 Ranked Solo games");
					zSetOperations.add(EnumConfiguration.gameTypeName.soloRank.getValue(),gameList.getParticipants(),gameStartTime);
				} else if (configId.equals(EnumConfiguration.gameTypeName.normalGame.getValue())) {
					log.info("5v5 Blind Pick games");
					zSetOperations.add(EnumConfiguration.gameTypeName.normalGame.getValue(),gameList.getParticipants(),gameStartTime);
				}
			}
			else{
				log.info("It is ARAM!");
			}
		}

		//ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
		//valueOperations.set("test7",featuredGames);

	}
}
