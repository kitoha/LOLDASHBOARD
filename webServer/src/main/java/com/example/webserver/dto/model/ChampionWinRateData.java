package com.example.webserver.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChampionWinRateData {
	String id;
	Double winRate;
	Integer playCount;
}
