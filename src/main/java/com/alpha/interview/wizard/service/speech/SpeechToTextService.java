package com.alpha.interview.wizard.service.speech;

public interface SpeechToTextService {
	public String getText(byte[] audioBytes);
}
