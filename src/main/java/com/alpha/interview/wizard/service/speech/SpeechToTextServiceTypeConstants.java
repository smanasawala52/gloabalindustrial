package com.alpha.interview.wizard.service.speech;

public enum SpeechToTextServiceTypeConstants {
	WHISPER("openai"), GCP("gcp"), ASSEMBLYAI("assemblyai");

	private final String type;

	SpeechToTextServiceTypeConstants(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
