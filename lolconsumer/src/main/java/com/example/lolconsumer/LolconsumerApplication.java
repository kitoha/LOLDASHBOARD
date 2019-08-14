package com.example.lolconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.lolconsumer.repository.MatchRepository;

@SpringBootApplication
public class LolconsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LolconsumerApplication.class, args);
	}

}
