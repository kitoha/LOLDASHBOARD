package com.example.webserver.configuration;

public class EnumConfiguration {

	public enum dayTypeName {
		Hour(3600),
		Day(86400),
		Week(604800);

		private Integer dayName;

		dayTypeName(Integer name) {
			this.dayName = name;
		}

		public String getKey() {
			return name();
		}

		public Integer getValue() {
			return dayName;
		}

	}
}
