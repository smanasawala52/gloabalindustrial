package com.alpha.interview.wizard.model.mall;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@ToString
public class TextToSpeech {
	@EmbeddedId
	private TextToSpeechKey id;
	@Lob
	@Column(name = "AUDIO_DATA", length = 5000)
	private byte[] audioData;
	public TextToSpeechKey getId() {
		return id;
	}
	public void setId(TextToSpeechKey id) {
		this.id = id;
	}
	public byte[] getAudioData() {
		return audioData;
	}
	public void setAudioData(byte[] audioData) {
		this.audioData = audioData;
	}

}
