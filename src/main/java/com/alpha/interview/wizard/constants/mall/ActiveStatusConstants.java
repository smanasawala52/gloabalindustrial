package com.alpha.interview.wizard.constants.mall;

public enum ActiveStatusConstants {
	ACTIVE(1), INACTIVE(2);

	private final int type;

	ActiveStatusConstants(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
