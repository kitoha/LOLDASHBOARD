package com.example.loldashboard.model;

import java.util.List;

import lombok.Data;

@Data
public class GameList {
	private String gameId;
	private String gameStartTime;
	private String platformId;
	private String gameMode;
	private String mapId;
	private String gameType;
	private String gameQueueConfigId;
	List<Participant> participants;

}
