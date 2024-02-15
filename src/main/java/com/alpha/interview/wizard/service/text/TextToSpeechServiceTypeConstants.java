package com.alpha.interview.wizard.service.text;

public enum TextToSpeechServiceTypeConstants {
	WHISPER("openai"), GCP("gcp"), ASSEMBLYAI("assemblyai");

	private final String type;

	TextToSpeechServiceTypeConstants(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
