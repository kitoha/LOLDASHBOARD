package com.example.lolconsumer.service;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.example.lolconsumer.configuration.GameTypName;
import com.example.lolconsumer.model.ChampionData;
import com.example.lolconsumer.model.FeaturedGames;
import com.example.lolconsumer.model.GameList;

@Component
public class RabbitMQConsumer {

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
				if (configId.equals(GameTypName.soloRank) ) {
					System.out.println("5v5 Ranked Solo games");
					zSetOperations.add(GameTypName.soloRank,gameList.getParticipants(),gameStartTime);
				} else if (configId.equals(GameTypName.normalGame)) {
					System.out.println("5v5 Blind Pick games");
					zSetOperations.add(GameTypName.soloRank,gameList.getParticipants(),gameStartTime);
				}
			}
			else{
				System.out.println("It is ARAM!");
			}
		}

		//ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
		//valueOperations.set("test7",featuredGames);

	}
}
