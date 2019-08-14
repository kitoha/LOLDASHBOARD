package com.example.webserver.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.webserver.model.ParameterPair;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Component
@Data
public class ApiRequester {
	private String region;
	private String apiUri;
	private ArrayList<ParameterPair> params;
	private String key;
	private String pathParmeters;
	private int responsCode;

	public ApiRequester() {
		this.region = "";
		this.key = "RGAPI-37a8b69b-63ed-457e-965d-07505750bc84";
		this.apiUri = "";
		this.params = new ArrayList<>();
		this.responsCode = 0;
		this.pathParmeters = "";
	}

	public String requesGet() throws Exception {
		String urlString = "https://" + this.region + ".api.riotgames.com" + this.apiUri
			+ this.pathParmeters;
		String letter = "?";
		boolean firstChker = true;

		for (ParameterPair param : this.params) {
			if (firstChker)
				urlString += "?" + param.getName() + "=" + param.getData();
			else
				urlString += "&" + param.getName() + "=" + param.getData();
			letter = "&";
			firstChker = false;
		}

		urlString += letter + "api_key=" + this.key;

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
