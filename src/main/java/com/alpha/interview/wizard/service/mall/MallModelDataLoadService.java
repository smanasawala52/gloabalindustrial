package com.alpha.interview.wizard.service.mall;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

@Component("MallModelDataLoadService")
@Service
public class MallModelDataLoadService {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MallModelRepository mallModelRepository;

	public void loadMallModelData(MultipartFile file) throws Exception {
		System.out.println("Multipart Dataloading started");
		try {
			String jsonArrayData = new String(file.getBytes());
			objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
			try {
				List<MallModel> entities = Arrays.asList(objectMapper.readValue(jsonArrayData, MallModel[].class));
				mallModelRepository.saveAll(entities);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
