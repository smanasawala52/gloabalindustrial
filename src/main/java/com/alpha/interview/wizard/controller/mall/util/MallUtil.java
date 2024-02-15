package com.alpha.interview.wizard.controller.mall.util;

public class MallUtil {
	public static String formatName(String inputName) {
		if (inputName != null && !inputName.isEmpty()) {
			String name = inputName.trim();
			name = name.substring(0, 1).toUpperCase()
					+ name.substring(1).toLowerCase();
			return name;
		}
		return "";
	}
}
