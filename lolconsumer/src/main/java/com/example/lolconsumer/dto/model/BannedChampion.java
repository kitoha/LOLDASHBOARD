package com.example.lolconsumer.dto.model;

import lombok.Data;

@Data
public class BannedChampion {
	private String teamId;
	private String championId;
	private String pickTurn;
}
