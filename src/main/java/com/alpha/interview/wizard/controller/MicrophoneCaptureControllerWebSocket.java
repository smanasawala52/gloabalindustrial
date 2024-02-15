package com.alpha.interview.wizard.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.model.Message;
import com.alpha.interview.wizard.service.speech.SpeechToTextService;
import com.alpha.interview.wizard.service.speech.SpeechToTextServiceMapInitializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MicrophoneCaptureControllerWebSocket {

	@Value("${speech.to.text.service.impl}")
	private String speechToTextServiceImpl;

	private final Map<String, SpeechToTextService> speechToTextServiceMap;
	@Autowired
	public MicrophoneCaptureControllerWebSocket(
			SpeechToTextServiceMapInitializer serviceMapInitializer) {
		this.speechToTextServiceMap = serviceMapInitializer.getServiceMap();
	}

	@MessageMapping("/audio")
	@SendTo("/topic/audioStream")
	public String handleAudio(String jsonString) {
		ObjectMapper objectMapper = new ObjectMapper();
		// Convert the JSON string to a Java object
		Message myObject = null;
		try {
			myObject = objectMapper.readValue(jsonString, Message.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Handle the received audio data (you may want to save it, process it,
		// etc.)
		try {
			// // System.out.println("Received audio data: "
			// + myObject.getAudioData().length + " bytes");
			String encodedMessage = speechToTextServiceMap
					.get(speechToTextServiceImpl)
					.getText(myObject.getAudioData());
			// // System.out.println("Redirecting to: " + encodedMessage);
			return encodedMessage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@MessageMapping("/audioData")
	@SendTo("/topic/audioStream")
	public String captureAudio(
			@RequestParam("audioData") MultipartFile audioData) {
		// // System.out.println(
		// "Received audio data: " + audioData.getSize() + " bytes");
		byte[] audioBytes;
		try {
			audioBytes = audioData.getBytes();
			String encodedMessage = speechToTextServiceMap
					.get(speechToTextServiceImpl).getText(audioBytes);
			// // System.out.println("Redirecting to: " + encodedMessage);
			return encodedMessage;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
