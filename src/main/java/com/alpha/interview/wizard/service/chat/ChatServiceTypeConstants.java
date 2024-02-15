package com.alpha.interview.wizard.service.chat;

public enum ChatServiceTypeConstants {
	OpenAIChatService("openai"), Gemini("gemini"), LangChain("langchain");

	private final String type;

	ChatServiceTypeConstants(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
