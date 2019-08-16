package com.example.loldashboard.dto.model;

import java.util.List;

import lombok.Data;

@Data
public class GameList {
	List<Participant> participants;
	private String gameId;
	private String gameStartTime;
	private String platformId;
	private String gameMode;
	private String mapId;
	private String gameType;
	private String gameQueueConfigId;

}
