package com.alpha.interview.wizard.service.mall;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alpha.interview.wizard.model.MallModel;
import com.alpha.interview.wizard.repository.MallModelRepository;

@Service
public class MallService {
	@Autowired
	private MallModelRepository mallModelRepository;
	public MallModel saveMallModel(MallModel mallModel) {
		return mallModelRepository.save(mallModel);
	}

	public List<MallModel> getAllMallModels() {
		return mallModelRepository.findAll();
	}

}
