package com.example.loldashboard.configuration;

public class EnumConfiguration {

	public enum regionTypeName {
		Korea("kr"),
		Japan("jp1"),
		NorthAmerica("na1"),
		EuropeWest("euw1"),
		EuropeEast("eun1"),
		Oceania("oc1"),
		Brazil("br1"),
		LAS("la1"),
		LAN("la2"),
		Russia("ru"),
		Turkey("tr1");

		private String regionName;

		regionTypeName(String name) {
			this.regionName = name;
		}

		public String getKey() {
			return name();
		}

		public String getValue() {
			return regionName;
		}
	}
}
