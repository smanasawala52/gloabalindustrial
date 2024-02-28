package com.alpha.interview.wizard.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.interview.wizard.service.text.TextToSpeechService;
import com.alpha.interview.wizard.service.text.TextToSpeechServiceMapInitializer;

@RestController
public class TextToSpeechController {

	@Value("${text.to.speech.service.impl}")
	private String textToSpeechServiceImpl;

	private final Map<String, TextToSpeechService> textToSpeechServiceMap;

	@Autowired
	public TextToSpeechController(
			TextToSpeechServiceMapInitializer serviceMapInitializer) {
		this.textToSpeechServiceMap = serviceMapInitializer.getServiceMap();
	}
	@PostMapping("/convertToSpeech")
	public ResponseEntity<byte[]> convertToSpeech(
			@RequestParam String inputText,
			@RequestParam(defaultValue = "English", name = "language", required = false) String language) {
		byte[] audioData = textToSpeechServiceMap.get(textToSpeechServiceImpl)
				.convertToSpeech(inputText, language);
		// TextToSpeechKey key = new TextToSpeechKey(language, inputText);
		// TextToSpeech textToSpeech = textToSpeechRepository.findById(key)
		// .orElse(null);
		// if (textToSpeech != null) {
		// return ResponseEntity.ok().body(textToSpeech.getAudioData());
		// } else {
		// audioData = textToSpeechServiceMap.get(textToSpeechServiceImpl)
		// .convertToSpeech(inputText, language);
		// try {
		// textToSpeech = new TextToSpeech();
		// textToSpeech.setId(key);
		// textToSpeech.setAudioData(audioData);
		// textToSpeechRepository.save(textToSpeech);
		// } catch (Exception e) {
		// System.err.println(e.getMessage());
		// }
		// }
		//
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", "audio.mp3");

		return new ResponseEntity<>(audioData, headers, HttpStatus.OK);
	}
}