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
import org.springframework.web.servlet.ModelAndView;

import com.alpha.interview.wizard.service.WhisperApiService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MicrophoneCaptureControllerFullAudio {

	@Autowired
	private WhisperApiService whisperApiService;
    
    @PostMapping(value = "/capture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String captureAudio(@RequestParam("audioData") MultipartFile audioData) {
        byte[] audioBytes;
        try {
            audioBytes = audioData.getBytes();
            String encodedMessage = whisperApiService.getText(audioBytes);
			String redirectUrl = "https://www.globalindustrial.com/searchResult?q=" + encodedMessage;
            System.out.println("Redirecting to: " + redirectUrl);
            return "redirect:"+redirectUrl;
//            return new ModelAndView("redirect:/redirect-template");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
//        return new ModelAndView("redirect:/");
    }
    @PostMapping(value = "/captureAudioStream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String captureAudioStream(@RequestParam("audioData") MultipartFile audioData) {
        byte[] audioBytes;
        try {
            audioBytes = audioData.getBytes();
            String encodedMessage = whisperApiService.getText(audioBytes);
            return encodedMessage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
