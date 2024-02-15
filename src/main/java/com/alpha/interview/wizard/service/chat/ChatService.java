package com.alpha.interview.wizard.service.chat;

import java.util.List;

import com.alpha.interview.wizard.model.Message;
import com.alpha.interview.wizard.service.SectorTypeConstants;

public interface ChatService {
	public void initializeChat(String initSystemMessage, List<String> inputJson,
			SectorTypeConstants sector);
	public Message getResponse(String input, SectorTypeConstants sector);
	public void resetChatSession(SectorTypeConstants sector);
	public ChatServiceTypeConstants getIdentity();
}
