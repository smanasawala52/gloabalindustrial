package com.alpha.interview.wizard.model.mall.constants;

public enum ImageServiceTypeConstants {
	LOCAL("local"), GCP("gcp"), AWS("aws");

	private final String type;

	ImageServiceTypeConstants(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
