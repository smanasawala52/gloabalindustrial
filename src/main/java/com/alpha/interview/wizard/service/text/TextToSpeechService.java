package com.alpha.interview.wizard.service.text;

public interface TextToSpeechService {
	public byte[] convertToSpeech(String text);
	public TextToSpeechServiceTypeConstants getIdentity();
}
