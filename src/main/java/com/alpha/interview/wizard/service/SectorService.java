package com.alpha.interview.wizard.service;

import java.util.Map;

import com.alpha.interview.wizard.model.Message;

public interface SectorService {
	public String initializeChat(Map<String, String> queryParams);
	public Message getResponse(String input, Map<String, String> queryParams);
	public void resetChatSession(Map<String, String> queryParams);
	public SectorTypeConstants getIdentity();
}
