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
import com.alpha.interview.wizard.controller.mall.util.MallUtil;
import com.alpha.interview.wizard.model.mall.OtherAttraction;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.OtherAttractionRepository;

@Controller
@RequestMapping("/otherattraction")
public class OtherAttractionController {

	@Autowired
	private OtherAttractionRepository otherAttractionRepository;
	private int PAGE_SIZE = 2;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public OtherAttractionController(
			ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "otherAttraction");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveOtherAttraction(
			@ModelAttribute("otherAttraction") @Valid OtherAttraction otherAttraction,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		OtherAttraction existingOtherAttraction = otherAttractionRepository
				.findByName(MallUtil.formatName(otherAttraction.getName()));
		if (existingOtherAttraction != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("OtherAttraction with name '"
							+ otherAttraction.getName() + "' already exists");
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.BRAND,
								otherAttraction.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			otherAttraction.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		otherAttraction.setUpdateTimestamp(new Date());
		otherAttraction.setCreateTimestamp(new Date());
		otherAttractionRepository.save(otherAttraction);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<OtherAttraction>> getAllOtherAttractions(
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<OtherAttraction> otherAttractions = otherAttractionRepository
				.findAll(pageable);
		return ResponseEntity.ok(otherAttractions);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<OtherAttraction> updateOtherAttraction(
			@PathVariable Long id,
			@RequestParam(value = "imageFile", required = false) MultipartFile file,
			@RequestBody OtherAttraction updatedOtherAttraction) {
		Optional<OtherAttraction> otherAttractionOptional = otherAttractionRepository
				.findById(id);
		if (!otherAttractionOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		OtherAttraction otherAttraction = otherAttractionOptional.get();
		// Update fields
		otherAttraction.setName(updatedOtherAttraction.getName());
		otherAttraction.setDescription(updatedOtherAttraction.getDescription());
		// Upload new image if provided
		if (file != null && !file.isEmpty()) {
			try {
				try {
					String imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.BRAND,
									otherAttraction.getName());
					otherAttraction.setImgUrl(imageUrl);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(null);
			}
		}

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
