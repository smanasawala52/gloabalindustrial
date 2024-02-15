package com.alpha.interview.wizard.service.text;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class TextToSpeechServiceMapInitializer {

	private final Map<String, TextToSpeechService> serviceMap;

	public TextToSpeechServiceMapInitializer(
			List<TextToSpeechService> services) {
		// Initialize the serviceMap using a Map constructor
		serviceMap = services.stream()
				.collect(Collectors.toMap(
						service -> service.getIdentity().getType(),
						Function.identity()));
	}

	public Map<String, TextToSpeechService> getServiceMap() {
		return serviceMap;
	}
}
