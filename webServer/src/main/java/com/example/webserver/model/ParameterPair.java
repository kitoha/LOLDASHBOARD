package com.example.webserver.model;

import lombok.Data;

@Data
public class ParameterPair {
	private String name;
	private String data;

	public ParameterPair() {
		name = "";
		data = "";
	}

	public ParameterPair(String name, String data) {
		this.name = name;
		this.data = data;
	}
}
