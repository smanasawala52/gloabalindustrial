package com.alpha.interview.wizard.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.model.Message;
import com.alpha.interview.wizard.service.WhisperApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

@Controller
public class MicrophoneCaptureController {

	@Autowired
	private WhisperApiService whisperApiService;

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


        // Handle the received audio data (you may want to save it, process it, etc.)
        try {
        	System.out.println("Received audio data: " + myObject.getAudioData().length + " bytes");
            String encodedMessage = whisperApiService.getText(myObject.getAudioData());
            System.out.println("Redirecting to: " + encodedMessage);
            return encodedMessage;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return "";
    }
    
    @PostMapping("/audioData")
    public String captureAudio(@RequestParam("audioData") MultipartFile audioData) {
    	System.out.println("Received audio data: " + audioData.getSize() + " bytes");
    	byte[] audioBytes;
        try {
            audioBytes = audioData.getBytes();
            String encodedMessage = whisperApiService.getText(audioBytes);
            System.out.println("Redirecting to: " + encodedMessage);
            return encodedMessage;
        } catch (IOException e) {
            e.printStackTrace();
        }
		return "";
    }
}
