package com.example.lolconsumer.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.lolconsumer.Entity.Match;
import com.example.lolconsumer.dao.repository.MatchRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MatchRepositoryTest {
	@Autowired
	MatchRepository matchRepository;

	private Match match;

	@Before
	public void initTest(){
		match = new Match();
	}

	@Test
	public void saveMatchTest(){
		match.setName("test");
		matchRepository.save(match);
	}
}
