package com.example.loldashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.loldashboard.service.GetSummonerAccountId;
import com.example.loldashboard.service.GetSummonerMatchlist;

@RestController
@RequestMapping(value = "/search")
public class UserSearchController {
	@Autowired
	GetSummonerAccountId getSummonerAccountId;
	@Autowired
	GetSummonerMatchlist getSummonerMatchlist;

	@GetMapping(value = "/summoner")
	public String searchUserName(@RequestParam("username") String userName) throws Exception {
		String accountId = getSummonerAccountId.getAccountId(userName);
		String result = getSummonerMatchlist.getMatchlist(accountId);

		return result;
	}
}
