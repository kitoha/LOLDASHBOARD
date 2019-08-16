package com.example.loldashboard.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.example.loldashboard.dto.model.ParameterPair;
import lombok.Data;

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
		this.key = "RGAPI-c1a53618-ff4f-4cc8-82ed-15295cf8097e";
		this.apiUri = "";
		this.params = new ArrayList<>();
		this.responsCode = 0;
		this.pathParmeters = "";
	}

	public String requesGet() throws Exception {
		StringBuilder urlString = new StringBuilder();
		urlString.append("https://" + this.region + ".api.riotgames.com" + this.apiUri
			+ this.pathParmeters);
		urlString.append("?" + "api_key=" + this.key);
		for (ParameterPair param : this.params) {
			urlString.append("&" + param.getName() + "=" + param.getData());
		}

		URL url = new URL(urlString.toString());
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");

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
