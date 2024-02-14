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

import com.alpha.interview.wizard.model.mall.OtherAttraction;
import com.alpha.interview.wizard.repository.mall.OtherAttractionRepository;

@Controller
@RequestMapping("/otherattraction")
public class OtherAttractionController {

	private final OtherAttractionRepository otherAttractionRepository;

	@Autowired
	public OtherAttractionController(
			OtherAttractionRepository otherAttractionRepository) {
		this.otherAttractionRepository = otherAttractionRepository;
	}

	@GetMapping("/form")
	public String showForm() {
		return "otherattraction-form";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveOtherAttraction(
			@ModelAttribute("otherAttraction") @Valid OtherAttraction otherAttraction,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		otherAttraction.setUpdateTimestamp(new Date());
		otherAttractionRepository.save(otherAttraction);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<List<OtherAttraction>> getAllOtherAttractions() {
		List<OtherAttraction> otherAttractions = otherAttractionRepository
				.findAll();
		return ResponseEntity.ok(otherAttractions);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<OtherAttraction> updateOtherAttraction(
			@PathVariable Long id,
			@RequestBody OtherAttraction updatedOtherAttraction) {
		Optional<OtherAttraction> otherAttractionOptional = otherAttractionRepository
				.findById(id);
		if (!otherAttractionOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		OtherAttraction otherAttraction = otherAttractionOptional.get();
		// Update fields
		otherAttraction.setFloor(updatedOtherAttraction.getFloor());
		otherAttraction.setLocation(updatedOtherAttraction.getLocation());
		otherAttraction.setHowToReach(updatedOtherAttraction.getHowToReach());
		otherAttraction.setName(updatedOtherAttraction.getName());
		otherAttraction.setDescription(updatedOtherAttraction.getDescription());
		otherAttraction.setImgUrl(updatedOtherAttraction.getImgUrl());
		otherAttraction.setAdditionalDetails(
				updatedOtherAttraction.getAdditionalDetails());
		otherAttraction.setStartDate(updatedOtherAttraction.getStartDate());
		otherAttraction.setEndDate(updatedOtherAttraction.getEndDate());
		otherAttraction.setUpdateTimestamp(new Date());
		// Save updated otherAttraction
		otherAttractionRepository.save(otherAttraction);
		return ResponseEntity.ok(otherAttraction);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteOtherAttraction(@PathVariable Long id) {
		Optional<OtherAttraction> otherAttractionOptional = otherAttractionRepository
				.findById(id);
		if (!otherAttractionOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		otherAttractionRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
