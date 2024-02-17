package com.alpha.interview.wizard.constants.mall.constants;

public enum ImageTypeConstants {
	BRAND("brand"), Category("category"), SUB_CATEGORY("sub-category");

	private final String type;

	ImageTypeConstants(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	public static ImageTypeConstants fromType(String type) {
		for (ImageTypeConstants value : ImageTypeConstants.values()) {
			if (value.getType().equalsIgnoreCase(type)) {
				return value;
			}
		}
		throw new IllegalArgumentException(
				"Invalid ImageTypeConstant type: " + type);
	}
}
