package com.example.loldashboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoldashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoldashboardApplication.class, args);
	}

}
