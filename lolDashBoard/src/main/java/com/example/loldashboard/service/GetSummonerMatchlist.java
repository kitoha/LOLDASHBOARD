package com.example.loldashboard.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.loldashboard.configuration.EnumConfiguration;
import com.example.loldashboard.configuration.ObjectMapperConfiguration;
import com.example.loldashboard.dto.model.ParameterPair;
import com.example.loldashboard.request.ApiRequester;

@Service
public class GetSummonerMatchlist {
	@Autowired
	ObjectMapperConfiguration objectMapperConfiguration;

	public String getMatchlist(String accountId) throws Exception {
		String result;
		ApiRequester apiRequester = new ApiRequester();
		ArrayList<ParameterPair> params = new ArrayList<>();
		apiRequester.setRegion(EnumConfiguration.regionTypeName.Korea.getValue());
		params.add(new ParameterPair("beginIndex", "0"));
		params.add(new ParameterPair("endIndex", "10"));
		apiRequester.setPathParmeters(accountId);
		apiRequester.setApiUri("/lol/match/v4/matchlists/by-account/");
		apiRequester.setParams(params);
		result = apiRequester.requesGet();

		return result;
	}
}
