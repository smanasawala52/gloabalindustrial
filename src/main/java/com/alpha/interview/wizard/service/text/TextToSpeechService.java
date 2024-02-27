package com.alpha.interview.wizard.service.text;

public interface TextToSpeechService {
	public byte[] convertToSpeech(String text, String language);
	public TextToSpeechServiceTypeConstants getIdentity();
}
