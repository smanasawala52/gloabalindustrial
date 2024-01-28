package com.alpha.interview.wizard.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.Transcript;

@Service
@Component("AssembyAIApi")
public class AssemblyAIApiService implements SpeechToTextService {
	
	public String getText(byte[] audioBytes) {
		long stTime = System.currentTimeMillis();
		String encodedMessage = "";
		AssemblyAI aai = AssemblyAI.builder().apiKey(System.getenv("ASSEMBY_AI_API_KEY")).build();
		AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, false);// Create an audio input stream from the generated audio data
        AudioInputStream audioInputStream = new AudioInputStream(
                new ByteArrayInputStream(audioBytes), audioFormat, audioBytes.length / audioFormat.getFrameSize());
        // Write the audio data to a file
        File file = new File("transcribe.mp3");
        try {
			AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Path tempAudioFile = null;
		try {
			tempAudioFile = Files.createTempFile("temp_audio_"+stTime, ".wav");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Write the audio bytes to the temporary file
        try {
			Files.write(tempAudioFile, audioBytes, StandardOpenOption.WRITE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Transcript transcribe = null;
		try {
			File file1 = new File("C:\\openAI.mp3");
			transcribe = aai.transcripts().transcribe(tempAudioFile.toFile());
			encodedMessage = transcribe.getText().get(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("time taken: " + (System.currentTimeMillis()-stTime));
		return encodedMessage;
	}

}
