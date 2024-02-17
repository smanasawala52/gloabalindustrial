package com.alpha.interview.wizard.controller.mall;

import java.lang.reflect.Field;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import com.alpha.interview.wizard.constants.mall.constants.ImageTypeConstants;
import com.alpha.interview.wizard.controller.mall.util.MallUtil;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;

@Controller
@RequestMapping("/mallmodel")
public class MallModelController {

	@Autowired
	private MallModelRepository mallModelRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public MallModelController(
			ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "mallModel");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveMallModel(
			@ModelAttribute("mallModel") @Valid MallModel mallModel,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || mallModel.getName() == null
				|| (mallModel.getName() != null
						&& mallModel.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		MallModel existingMallModel = mallModelRepository
				.findByName(MallUtil.formatName(mallModel.getName()));
		if (existingMallModel != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("MallModel with name '" + mallModel.getName()
							+ "' already exists");
		}
		if (mallModel.getDisplayName() == null
				|| (mallModel.getDisplayName() != null
						&& mallModel.getDisplayName().isEmpty())) {
			mallModel.setDisplayName(mallModel.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.MALL_MODEL,
								mallModel.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mallModel.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		mallModel.setUpdateTimestamp(new Date());
		mallModel.setCreateTimestamp(new Date());
		mallModelRepository.save(mallModel);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<MallModel>> getAllMallModels(
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<MallModel> mallModels = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			mallModels = mallModelRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			mallModels = mallModelRepository.findAll(pageable);
		}
		return ResponseEntity.ok(mallModels);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveMallModelImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		MallModel existingMallModel = mallModelRepository.getById(id);
		if (existingMallModel != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file,
									ImageTypeConstants.MALL_MODEL,
									existingMallModel.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingMallModel.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingMallModel.setUpdateTimestamp(new Date());
			mallModelRepository.save(existingMallModel);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<MallModel> updateMallModel(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		MallModel mallModel = mallModelOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = mallModel.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(mallModel, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (mallModel.getDisplayName() == null
				|| (mallModel.getDisplayName() != null
						&& mallModel.getDisplayName().isEmpty())) {
			mallModel.setDisplayName(mallModel.getName());
		}
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
