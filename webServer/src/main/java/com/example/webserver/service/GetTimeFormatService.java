package com.example.webserver.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

@Service
public class GetTimeFormatService {
	private String getDate(String gameStartTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("kk:mm--dd/MM/yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		Date date = new Date(Long.parseLong(gameStartTime));
		String dateString = formatter.format(date);
		return dateString;
	}

	public String getDayString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
		return simpleDateFormat.format(calendar.getTime());

	}

	public String getFromWeekString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -7);

		return simpleDateFormat.format(calendar.getTime());
	}

	public String getHourPattern(String gamemode) {
		long hour = 3600000;
		long millisecond = System.currentTimeMillis();
		long millisecond2 = millisecond - hour;
		String curTime = getDate(String.valueOf(millisecond));
		String beforeTime = getDate(String.valueOf(millisecond2));
		String pattern = String.format("%s[%s-%s][%s-%s]:*-[%s-%s][%s-%s]%s",
			gamemode,
			beforeTime.substring(0, 1),
			curTime.substring(0, 1),
			beforeTime.substring(1, 2),
			curTime.substring(1, 2),
			beforeTime.substring(7, 8),
			curTime.substring(7, 8),
			beforeTime.substring(8, 9),
			curTime.substring(8, 9),
			beforeTime.substring(9));

		return pattern;
	}

	public String getDayPattern(String gamemode) {
		long millisecond = System.currentTimeMillis();
		String curTime = getDate(String.valueOf(millisecond));
		String pattern = String.format("%s*:*-%s",
			gamemode,
			curTime.substring(7));

		return pattern;
	}

	public String getWeekPattern(String gamemode) {
		String pattern = String.format("%s*",
			gamemode);

		return pattern;
	}
}
