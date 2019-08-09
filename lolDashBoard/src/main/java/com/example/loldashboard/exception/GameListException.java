package com.example.loldashboard.exception;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class GameListException extends Exception {
	private String message;

	public GameListException() {
		this.message = "";
	}

	public GameListException(String message) {
		this.message = message;
	}
}
