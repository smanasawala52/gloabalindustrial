package com.alpha.interview.wizard.service;

import com.alpha.interview.wizard.model.Message;

public interface SectorService {
	public String initializeChat();
	public Message getResponse(String input);
	public void resetChatSession();
	public SectorTypeConstants getIdentity();
}
