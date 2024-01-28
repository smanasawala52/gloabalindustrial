package com.alpha.interview.wizard.contoller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.service.SpeechToTextService;

@Controller
public class MicrophoneCaptureControllerFullAudio {

	@Autowired
	@Qualifier("GoogleSpeechToTextApi")
	private SpeechToTextService speechToTextService;

	@PostMapping(value = "/captureAudioStream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> captureAudioStream(
			@RequestParam("audioData") MultipartFile audioData) {
		byte[] audioBytes;
		try {
			audioBytes = audioData.getBytes();
			String encodedMessage = speechToTextService.getText(audioBytes);
			return ResponseEntity.ok().body(encodedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
