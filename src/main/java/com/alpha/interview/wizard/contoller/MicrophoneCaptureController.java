package com.alpha.interview.wizard.contoller;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MicrophoneCaptureController {

    private final String WHISPER_ASR_API_URL = "https://api.openai.com/v1/audio/transcriptions";
    @Value("${api.key}")
    private String myToken;

    @PostMapping(value = "/capture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String captureAudio(@RequestParam("audioData") MultipartFile audioData, Model model) {
        byte[] audioBytes;
        System.out.println(System.getenv("API_KEY"));
        try {
            audioBytes = audioData.getBytes();
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
            headers.setBearerAuth(myToken);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(WHISPER_ASR_API_URL, HttpMethod.POST, requestEntity, String.class);
            String message = responseEntity.getBody();
			String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            encodedMessage = encodedMessage.replace("%0A", "");
            String redirectUrl = "https://www.globalindustrial.com/searchResult?q=" + encodedMessage;
            System.out.println("Redirecting to: " + redirectUrl);
            model.addAttribute("encodedMessage", encodedMessage);
            model.addAttribute("redirectUrl", redirectUrl);
            return "redirect:"+redirectUrl;
//            return new ModelAndView("redirect:/redirect-template");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
//        return new ModelAndView("redirect:/");
    }
}
