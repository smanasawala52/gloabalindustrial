package com.alpha.interview.wizard.controller.mall.util;

import java.util.ArrayList;
import java.util.List;

import com.alpha.interview.wizard.model.mall.MallModel;

public class MallUtil {
	public static String formatName(String inputName) {
		if (inputName != null && !inputName.isBlank()) {
			String name = inputName.trim();
			name = name.substring(0, 1).toUpperCase()
					+ name.substring(1).toLowerCase();
			return name;
		}
		return "";
	}
	public static List<String> initalizeChatInput(MallModel inputMallModel) {
		List<String> input = new ArrayList<String>();
		if (inputMallModel.getShops() != null) {
			inputMallModel.getShops().stream().forEach(shop -> {
				// code to convert shop object to proper prompt
				StringBuilder sb = new StringBuilder();
				sb.append("Shop id: ").append(shop.getId());
				sb.append(" name: ").append(shop.getDisplayName());
				if (shop.getDescription() != null
						&& !shop.getDescription().isBlank()) {
					sb.append(" details: ").append(shop.getDescription());
				}
				if (shop.getAdditionalDetails() != null
						&& !shop.getAdditionalDetails().isBlank()) {
					sb.append(" ").append(shop.getAdditionalDetails());
				}
				if (shop.getCategories() != null
						&& !shop.getCategories().isEmpty()) {
					StringBuilder sbCategories = new StringBuilder();

				}

				sb.append(" name: ").append(shop.getDisplayName());
				input.add(sb.toString());
			});
		}
		return input;
	}
}
