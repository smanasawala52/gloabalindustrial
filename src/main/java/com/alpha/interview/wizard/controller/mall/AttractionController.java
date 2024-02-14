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

import com.alpha.interview.wizard.model.mall.Attraction;
import com.alpha.interview.wizard.repository.mall.AttractionRepository;

@Controller
@RequestMapping("/attraction")
public class AttractionController {

	private final AttractionRepository attractionRepository;

	@Autowired
	public AttractionController(AttractionRepository attractionRepository) {
		this.attractionRepository = attractionRepository;
	}

	@GetMapping("/form")
	public String showForm() {
		return "attraction-form";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveAttraction(
			@ModelAttribute("attraction") @Valid Attraction attraction,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		attraction.setUpdateTimestamp(new Date());
		attractionRepository.save(attraction);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<List<Attraction>> getAllAttractions() {
		List<Attraction> attractions = attractionRepository.findAll();
		return ResponseEntity.ok(attractions);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Attraction> updateAttraction(@PathVariable Long id,
			@RequestBody Attraction updatedAttraction) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(id);
		if (!attractionOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Attraction attraction = attractionOptional.get();
		// Update fields
		attraction.setName(updatedAttraction.getName());
		attraction.setDescription(updatedAttraction.getDescription());
		attraction.setImgUrl(updatedAttraction.getImgUrl());
		attraction.setFloor(updatedAttraction.getFloor());
		attraction.setShopNumber(updatedAttraction.getShopNumber());
		attraction.setLocation(updatedAttraction.getLocation());
		attraction.setHowToReach(updatedAttraction.getHowToReach());
		attraction.setFeatured(updatedAttraction.isFeatured());
		attraction
				.setAdditionalDetails(updatedAttraction.getAdditionalDetails());
		attraction.setUpdateTimestamp(new Date());
		// Save updated attraction
		attractionRepository.save(attraction);
		return ResponseEntity.ok(attraction);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteAttraction(@PathVariable Long id) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(id);
		if (!attractionOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		attractionRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
