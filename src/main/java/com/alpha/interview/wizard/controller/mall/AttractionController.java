package com.alpha.interview.wizard.controller.mall;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.constants.mall.constants.ImageTypeConstants;
import com.alpha.interview.wizard.model.mall.Attraction;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.AttractionRepository;

@Controller
@RequestMapping("/attraction")
public class AttractionController {

	@Autowired
	private AttractionRepository attractionRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public AttractionController(
			ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "attraction");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveAttraction(
			@ModelAttribute("attraction") @Valid Attraction attraction,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Attraction existingAttraction = attractionRepository
				.findByName(attraction.getName());
		if (existingAttraction != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Attraction with name '" + attraction.getName()
							+ "' already exists");
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.BRAND,
								attraction.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			attraction.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		attraction.setUpdateTimestamp(new Date());
		attraction.setCreateTimestamp(new Date());
		attractionRepository.save(attraction);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Attraction>> getAllAttractions(
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Attraction> attractions = attractionRepository.findAll(pageable);
		return ResponseEntity.ok(attractions);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Attraction> updateAttraction(@PathVariable Long id,
			@RequestParam(value = "imageFile", required = false) MultipartFile file,
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
		// Upload new image if provided
		if (file != null && !file.isEmpty()) {
			try {
				try {
					String imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.BRAND,
									attraction.getName());
					attraction.setImgUrl(imageUrl);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(null);
			}
		}

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