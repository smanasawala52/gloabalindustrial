package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.TextToSpeech;
import com.alpha.interview.wizard.model.mall.TextToSpeechKey;

@Repository
public interface TextToSpeechRepository
		extends
			JpaRepository<TextToSpeech, TextToSpeechKey> {
	// You can add custom query methods if needed
}
