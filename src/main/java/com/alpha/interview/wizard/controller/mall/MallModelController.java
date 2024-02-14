package com.alpha.interview.wizard.controller.mall;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;

@Controller
@RequestMapping("/mallmodel")
public class MallModelController {

	private final MallModelRepository mallModelRepository;

	@Autowired
	public MallModelController(MallModelRepository mallModelRepository) {
		this.mallModelRepository = mallModelRepository;
	}

	@GetMapping("/form")
	public String showForm() {
		return "mallmodel-form";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveMallModel(
			@ModelAttribute("mallModel") @Valid MallModel mallModel,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		mallModel.setUpdateTimestamp(new Date());
		mallModelRepository.save(mallModel);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<List<MallModel>> getAllMallModels() {
		List<MallModel> mallModels = mallModelRepository.findAll();
		return ResponseEntity.ok(mallModels);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<MallModel> updateMallModel(@PathVariable Long id,
			@RequestBody MallModel updatedMallModel) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		MallModel mallModel = mallModelOptional.get();
		// Update fields
		mallModel.setCountry(updatedMallModel.getCountry());
		mallModel.setCity(updatedMallModel.getCity());
		mallModel.setName(updatedMallModel.getName());
		mallModel.setDescription(updatedMallModel.getDescription());
		mallModel.setLocation(updatedMallModel.getLocation());
		mallModel.setFloors(updatedMallModel.getFloors());
		mallModel.setUpdateTimestamp(new Date());
		// Save updated mallModel
		mallModelRepository.save(mallModel);
		return ResponseEntity.ok(mallModel);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteMallModel(@PathVariable Long id) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		mallModelRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
