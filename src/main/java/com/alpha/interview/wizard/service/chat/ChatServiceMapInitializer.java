package com.alpha.interview.wizard.service.chat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ChatServiceMapInitializer {

	private final Map<String, ChatService> serviceMap;

	public ChatServiceMapInitializer(List<ChatService> services) {
		// Initialize the serviceMap using a Map constructor
		serviceMap = services.stream()
				.collect(Collectors.toMap(
						service -> service.getIdentity().getType(),
						Function.identity()));
	}

	public Map<String, ChatService> getServiceMap() {
		return serviceMap;
	}
}
