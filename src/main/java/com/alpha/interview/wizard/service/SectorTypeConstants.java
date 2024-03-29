package com.alpha.interview.wizard.service;

public enum SectorTypeConstants {
	MALL("mall"), IPL("ipl"), INTERVIEW("interview"), TRAVEL(
			"travel"), MALL_MODEL("mallmodel");

	private final String type;

	SectorTypeConstants(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
