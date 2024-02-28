package com.alpha.interview.wizard.model.mall;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class TextToSpeechKey implements Serializable {

	private String language;
	private String inputText;
	public TextToSpeechKey() {
	}
	public TextToSpeechKey(String language, String inputText) {
		this.language = language;
		this.inputText = inputText;
	}

	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getInputText() {
		return inputText;
	}
	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

}
