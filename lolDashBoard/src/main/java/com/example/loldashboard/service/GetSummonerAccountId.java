package com.example.loldashboard.service;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.loldashboard.configuration.EnumConfiguration;
import com.example.loldashboard.configuration.ObjectMapperConfiguration;
import com.example.loldashboard.dto.model.ParameterPair;
import com.example.loldashboard.dto.model.SummonerInfo;
import com.example.loldashboard.request.ApiRequester;

@Service
public class GetSummonerAccountId {
	@Autowired
	ObjectMapperConfiguration objectMapperConfiguration;

	public String getAccountId(String userName) throws Exception {
		ApiRequester apiRequester = new ApiRequester();
		String result;
		ArrayList<ParameterPair> params = new ArrayList<>();
		String encoderResult = URLEncoder.encode(userName, "UTF-8");
		apiRequester.setRegion(EnumConfiguration.regionTypeName.Korea.getValue());
		apiRequester.setApiUri("/lol/summoner/v4/summoners/by-name/");
		apiRequester.setPathParmeters(encoderResult);
		result = apiRequester.requesGet();

		SummonerInfo summonerInfo = objectMapperConfiguration.objectMapper.readValue(result, SummonerInfo.class);

		return summonerInfo.getAccountId();
	}
}
