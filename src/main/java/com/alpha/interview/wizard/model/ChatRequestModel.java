package com.alpha.interview.wizard.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import io.github.sashirestela.openai.domain.chat.message.ChatMsg;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@ToString
public class ChatRequestModel {
	private List<ChatMsg> chatRequestFirst = new ArrayList<ChatMsg>();
	private List<ChatMsg> chatRequestSession = new ArrayList<ChatMsg>();

	public List<ChatMsg> getChatRequestFirst() {
		return chatRequestFirst;
	}
	public void setChatRequestFirst(List<ChatMsg> chatRequestFirst) {
		this.chatRequestFirst = chatRequestFirst;
	}
	public List<ChatMsg> getChatRequestSession() {
		return chatRequestSession;
	}
	public void setChatRequestSession(List<ChatMsg> chatRequestSession) {
		this.chatRequestSession = chatRequestSession;
	}
}
