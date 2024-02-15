package com.alpha.interview.wizard.service.speech;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class SpeechToTextServiceMapInitializer {

	private final Map<String, SpeechToTextService> serviceMap;

	public SpeechToTextServiceMapInitializer(
			List<SpeechToTextService> services) {
		// Initialize the serviceMap using a Map constructor
		serviceMap = services.stream()
				.collect(Collectors.toMap(
						service -> service.getIdentity().getType(),
						Function.identity()));
	}

	public Map<String, SpeechToTextService> getServiceMap() {
		return serviceMap;
	}
}
