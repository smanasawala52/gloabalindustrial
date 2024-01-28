package com.alpha.interview.wizard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeRequest;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

@Service
@Component("GoogleSpeechToTextApi")
public class GoogleSpeechToTextApiService implements SpeechToTextService {

	@Autowired
	private SpeechClient speechClient;

	@Autowired
	private RecognitionConfig recognitionConfig;

	public String getText(byte[] audioBytes) {
		long stTime = System.currentTimeMillis();
		String encodedMessage = "";
		try {
			System.out.println("Audio Bytes Length: " + audioBytes.length);
			RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
					.setContent(ByteString.copyFrom(audioBytes)).build();

			// RecognizeResponse response = speechClient
			// .recognize(recognitionConfig, recognitionAudio);
			OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response = speechClient
					.longRunningRecognizeAsync(
							LongRunningRecognizeRequest.newBuilder()
									.setConfig(recognitionConfig)
									.setAudio(RecognitionAudio.newBuilder()
											.setContent(ByteString
													.copyFrom(audioBytes)))
									.build());

			System.out.println(response);
			List<SpeechRecognitionResult> results = response.get()
					.getResultsList();

			StringBuilder transcript = new StringBuilder();
			for (SpeechRecognitionResult result : results) {
				transcript.append(result.getAlternatives(0).getTranscript());
			}

			encodedMessage = transcript.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(
				"time taken: " + (System.currentTimeMillis() - stTime));
		return encodedMessage;
	}

}
