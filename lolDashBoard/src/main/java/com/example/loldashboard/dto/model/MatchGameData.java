package com.example.loldashboard.dto.model;

import java.util.List;

import lombok.Data;

@Data
public class MatchGameData {
	List<TeamData> teams;
	List<ParticipantData> participants;
}
