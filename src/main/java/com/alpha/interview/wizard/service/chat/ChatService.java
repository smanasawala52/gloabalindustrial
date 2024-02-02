package com.alpha.interview.wizard.service.chat;

import com.alpha.interview.wizard.model.Message;

public interface ChatService {
	public String initializeChat();
	public Message getResponse(String input);
	public void resetChatSession();
}
