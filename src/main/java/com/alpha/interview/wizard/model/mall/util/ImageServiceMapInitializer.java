package com.alpha.interview.wizard.model.mall.util;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ImageServiceMapInitializer {

	private final Map<String, ImageService> imageServiceMap;

	public ImageServiceMapInitializer(List<ImageService> services) {
		// Initialize the serviceMap using a Map constructor
		imageServiceMap = services.stream()
				.collect(Collectors.toMap(
						service -> service.getIdentity().getType(),
						Function.identity()));
	}

	public Map<String, ImageService> getServiceMap() {
		return imageServiceMap;
	}
}
