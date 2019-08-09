package com.example.loldashboard.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Component
@Data
public class ApiRequester {
	private String region;
	private String apiUri;
	private ArrayList<String> params;
	private String key;
	private int responsCode;

	public ApiRequester() {
		this.region = "";
		this.key = "RGAPI-a45ade7c-070f-418a-971a-681c95933324";
		this.apiUri = "";
		this.params = new ArrayList<>();
		this.responsCode = 0;
	}

	public String requesGet() throws Exception {
		String urlString = "https://" + this.region + ".api.riotgames.com" + this.apiUri;
		for (String param : this.params) {
			urlString += "/" + param;
		}

		urlString += "?api_key=" + this.key;

		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");

		ObjectMapper objectMapper = new ObjectMapper();

		this.responsCode = con.getResponseCode();

		BufferedReader br;
		if (responsCode == 200) {
			br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
			br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}

		String returnMessage = br.lines().collect(Collectors.joining());

		return returnMessage;
	}
}
