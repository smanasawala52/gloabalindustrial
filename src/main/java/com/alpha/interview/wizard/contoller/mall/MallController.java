package com.alpha.interview.wizard.contoller.mall;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.service.mall.MallService;

@RestController
@RequestMapping("/mall")
public class MallController {

	private final MallService mallService;

	@Autowired
	public MallController(MallService mallService) {
		this.mallService = mallService;
	}

	@GetMapping("/getAll")
	public List<MallModel> getAllMallModels() {
		return mallService.getAllMallModels();
	}
}
