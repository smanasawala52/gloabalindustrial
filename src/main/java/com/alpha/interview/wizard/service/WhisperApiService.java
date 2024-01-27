package com.alpha.interview.wizard.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class WhisperApiService {
	
	private final String WHISPER_ASR_API_URL = "https://api.openai.com/v1/audio/transcriptions";
	public String getText(byte[] audioBytes) {
		String encodedMessage=""; 
		try {
		 MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
         ByteArrayResource resource = new ByteArrayResource(audioBytes) {
             @Override
             public String getFilename() {
                 return "C:\\openAI.mp3";
             }
         };
         parts.add("file", resource);
         parts.add("model", "whisper-1");
         parts.add("response_format", "text");

         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.MULTIPART_FORM_DATA);
         headers.setBearerAuth(System.getenv("API_KEY"));
         HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);

         RestTemplate restTemplate = new RestTemplate();
         ResponseEntity<String> responseEntity = restTemplate.exchange(WHISPER_ASR_API_URL, HttpMethod.POST, requestEntity, String.class);
         String message = responseEntity.getBody();
			try {
				encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          encodedMessage = encodedMessage.replace("%0A", "");
		} catch (Exception e) {
            e.printStackTrace();
        }
		return encodedMessage;
	}

}
