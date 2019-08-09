package com.example.loldashboard.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
public class ScheduledException {
	@Autowired
	GameListException gameListException;
	private Logger logger = LoggerFactory.getLogger(ScheduledException.class);

	@Bean
	public TaskScheduler taskScheduler() {
		ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler();
		scheduler.setErrorHandler(throwable -> {
			this.logger.info("scheduling error = " + throwable.getMessage());
		});

		return scheduler;
	}
}
