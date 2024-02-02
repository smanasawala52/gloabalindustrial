package com.alpha.interview.wizard.contoller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.service.chat.ChatService;
import com.alpha.interview.wizard.service.speech.SpeechToTextService;
@Controller
public class MicrophoneCaptureControllerFullAudio {

	@Autowired
	@Qualifier("GoogleSpeechToTextApi")
	private SpeechToTextService speechToTextService;

	@Autowired
	@Qualifier("OpenAIChat")
	private ChatService chatService;

	@PostMapping(value = "/captureAudioStream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> captureAudioStream(
			@RequestParam("audioData") MultipartFile audioData) {
		byte[] audioBytes;
		try {
			audioBytes = audioData.getBytes();
			String encodedMessage = speechToTextService.getText(audioBytes);
			return ResponseEntity.ok()
					.body(chatService.getResponse(encodedMessage));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@PostMapping(value = "/captureQuery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> captureQuery(@RequestParam("query") String query) {
		try {
			return ResponseEntity.ok().body(chatService.getResponse(query));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@GetMapping("/resetChatSession")
	public String showHomeMall() {
		chatService.resetChatSession();
		return "redirect:/";
	}
}
