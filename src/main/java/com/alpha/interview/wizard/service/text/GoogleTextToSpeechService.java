package com.alpha.interview.wizard.service.text;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alpha.interview.wizard.model.mall.Language;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

@Service
@Component("GoogleTextToSpeechApi")
public class GoogleTextToSpeechService implements TextToSpeechService {
	private final Map<String, Language> languageSettings = new HashMap<>();

	@Value("${text.to.speech.languages}")
	private String languageSettingsString;

	@PostConstruct
	public void init() {
		// Split the language settings string by comma
		String[] languageSettingsArray = languageSettingsString.split(",");

		for (String setting : languageSettingsArray) {
			// Split each setting by colon to get language code and name
			String[] parts = setting.split(":");
			if (parts.length == 3) {
				String languageType = parts[0];
				String languageCode = parts[1];
				String name = parts[2];

				// Create Language object and set properties
				Language language = new Language();
				language.setLanguageCode(languageCode);
				language.setName(name);

				languageSettings.put(languageType.toLowerCase(), language);
			}
		}
	}

	// Getter for accessing language settings
	public Map<String, Language> getLanguageSettings() {
		return languageSettings;
	}

	public byte[] convertToSpeech(String text, String language) {
		Language languageModel = languageSettings.get(language.toLowerCase());
		if (languageModel == null) {
			languageModel = languageSettings.get("english".toLowerCase());
		}
		try (TextToSpeechClient textToSpeechClient = TextToSpeechClient
				.create()) {
			SynthesisInput input = SynthesisInput.newBuilder().setText(text)
					.build();

			VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
					.setLanguageCode(languageModel.getLanguageCode())
					.setName(languageModel.getName()).build();

			AudioConfig audioConfig = AudioConfig.newBuilder()
					.setAudioEncoding(AudioEncoding.LINEAR16).build();

			SynthesizeSpeechResponse response = textToSpeechClient
					.synthesizeSpeech(input, voice, audioConfig);

			ByteString audioContent = response.getAudioContent();
			return audioContent.toByteArray();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TextToSpeechServiceTypeConstants getIdentity() {
		return TextToSpeechServiceTypeConstants.GCP;
	}
}
