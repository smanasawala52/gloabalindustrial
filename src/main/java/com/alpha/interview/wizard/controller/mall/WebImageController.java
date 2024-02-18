package com.alpha.interview.wizard.controller.mall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.alpha.interview.wizard.model.mall.WebImage;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.WebImageRepository;

@Controller
@RequestMapping("/webImage")
public class WebImageController {

	@Autowired
	private WebImageRepository webImageRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public WebImageController(
			ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "webImage");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveWebImage(
			@ModelAttribute("webImage") @Valid WebImage webImage,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || webImage.getName() == null
				|| (webImage.getName() != null
						&& webImage.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		WebImage existingWebImage = webImageRepository
				.findByName(MallUtil.formatName(webImage.getName()));
		if (existingWebImage != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("WebImage with name '" + webImage.getName()
							+ "' already exists");
		}
		if (webImage.getDisplayName() == null
				|| (webImage.getDisplayName() != null
						&& webImage.getDisplayName().isEmpty())) {
			webImage.setDisplayName(webImage.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.WEB_IMAGE,
								webImage.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			webImage.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		webImage.setUpdateTimestamp(new Date());
		webImage.setCreateTimestamp(new Date());
		webImageRepository.save(webImage);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<WebImage>> getAllWebImages(
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<WebImage> webImages = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			webImages = webImageRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			webImages = webImageRepository.findAll(pageable);
		}
		return ResponseEntity.ok(webImages);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveWebImageImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		WebImage existingWebImage = webImageRepository.getById(id);
		if (existingWebImage != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.WEB_IMAGE,
									existingWebImage.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingWebImage.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingWebImage.setUpdateTimestamp(new Date());
			webImageRepository.save(existingWebImage);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<WebImage> updateWebImage(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<WebImage> webImageOptional = webImageRepository.findById(id);
		if (!webImageOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		WebImage webImage = webImageOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = webImage.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(webImage, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (webImage.getDisplayName() == null
				|| (webImage.getDisplayName() != null
						&& webImage.getDisplayName().isEmpty())) {
			webImage.setDisplayName(webImage.getName());
		}
		webImage.setUpdateTimestamp(new Date());
		// Save updated webImage
		webImageRepository.save(webImage);
		return ResponseEntity.ok(webImage);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteWebImage(@PathVariable Long id) {
		Optional<WebImage> webImageOptional = webImageRepository.findById(id);
		if (!webImageOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		webImageRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<WebImage>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(webImageRepository.findAll());
		}
		List<WebImage> lst = new ArrayList<>();
		Optional<WebImage> webImageOptional = webImageRepository.findById(id);
		if (webImageOptional.isPresent()) {
			lst.add(webImageOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
}
