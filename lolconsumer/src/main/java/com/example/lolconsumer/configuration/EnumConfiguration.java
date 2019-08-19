package com.example.lolconsumer.configuration;

public class EnumConfiguration {

	public enum gameTypeName {
		soloRank("420"),
		normalGame("430");

		private String typeName;

		gameTypeName(String typeName) {
			this.typeName = typeName;
		}

		public String getKey() {
			return name();
		}

		public String getValue() {
			return typeName;
		}
	}
}
