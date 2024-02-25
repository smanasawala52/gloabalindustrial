package com.alpha.interview.wizard.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.service.SectorService;
import com.alpha.interview.wizard.service.SectorServiceMapInitializer;
import com.alpha.interview.wizard.service.speech.SpeechToTextService;
import com.alpha.interview.wizard.service.speech.SpeechToTextServiceMapInitializer;
@Controller
public class MicrophoneCaptureControllerFullAudio {

	@Value("${speech.to.text.service.impl}")
	private String speechToTextServiceImpl;

	private final Map<String, SpeechToTextService> speechToTextServiceMap;
	private final Map<String, SectorService> serviceMap;

	@Autowired
	public MicrophoneCaptureControllerFullAudio(
			SpeechToTextServiceMapInitializer speechToTextServiceMapInitializer,
			SectorServiceMapInitializer serviceMapInitializer) {
		this.speechToTextServiceMap = speechToTextServiceMapInitializer
				.getServiceMap();
		this.serviceMap = serviceMapInitializer.getServiceMap();
	}

	@PostMapping(value = "/{type}/captureAudioStream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> captureAudioStream(
			@PathVariable("type") String type,
			@RequestParam("audioData") MultipartFile audioData,
			@RequestParam Map<String, String> queryParams) {
		byte[] audioBytes;
		try {
			audioBytes = audioData.getBytes();
			String encodedMessage = speechToTextServiceMap
					.get(speechToTextServiceImpl).getText(audioBytes);
			SectorService sectorService = serviceMap.get(type);
			return ResponseEntity.ok().body(
					sectorService.getResponse(encodedMessage, queryParams));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@PostMapping(value = "/{type}/captureQuery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> captureQuery(@PathVariable("type") String type,
			@RequestParam("query") String query,
			@RequestParam Map<String, String> queryParams) {
		try {
			SectorService sectorService = serviceMap.get(type);
			return ResponseEntity.ok()
					.body(sectorService.getResponse(query, queryParams));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@GetMapping("/{type}/resetChatSession")
	public String showHomeMall(@PathVariable("type") String type,
			@RequestParam Map<String, String> queryParams) {
		SectorService sectorService = serviceMap.get(type);
		sectorService.resetChatSession(queryParams);
		return "redirect:/";
	}
}
