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
import com.alpha.interview.wizard.model.mall.Brand;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.BrandRepository;

@Controller
@RequestMapping("/brand")
public class BrandController {

	@Autowired
	private BrandRepository brandRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public BrandController(ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "brand");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveBrand(
			@ModelAttribute("brand") @Valid Brand brand,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Brand existingBrand = brandRepository
				.findByName(MallUtil.formatName(brand.getName()));
		if (existingBrand != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(
					"Brand with name '" + brand.getName() + "' already exists");
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.BRAND,
								brand.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			brand.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		brand.setUpdateTimestamp(new Date());
		brand.setCreateTimestamp(new Date());
		brandRepository.save(brand);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Brand>> getAllBrands(
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Brand> brands = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			brands = brandRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			brands = brandRepository.findAll(pageable);
		}
		return ResponseEntity.ok(brands);
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Brand> updateBrand(@PathVariable Long id,
			@RequestParam(value = "imageFile", required = false) MultipartFile file,
			@RequestBody Map<String, Object> updates) {

		Optional<Brand> brandOptional = brandRepository.findById(id);
		if (!brandOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Brand brand = brandOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = brand.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(brand, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});

		// Upload new image if provided
		if (file != null && !file.isEmpty()) {
			try {
				String imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.BRAND,
								brand.getName());
				brand.setImgUrl(imageUrl);
			} catch (Exception e) {
				e.printStackTrace(); // Handle exception properly
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(null);
			}
		}

		brand.setUpdateTimestamp(new Date());
		// Save updated brand
		brandRepository.save(brand);
		return ResponseEntity.ok(brand);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
		Optional<Brand> brandOptional = brandRepository.findById(id);
		if (!brandOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		brandRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
