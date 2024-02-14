package com.alpha.interview.wizard.controller.mall;

import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import com.alpha.interview.wizard.constants.mall.ImageTypeConstants;
import com.alpha.interview.wizard.model.mall.Brand;
import com.alpha.interview.wizard.model.mall.util.ImageUpload;
import com.alpha.interview.wizard.repository.mall.BrandRepository;

@Controller
@RequestMapping("/brand")
public class BrandController {

	private final BrandRepository brandRepository;
	private int PAGE_SIZE = 2;
	@Autowired
	@Qualifier("UploadImageService")
	private ImageUpload uploadService;

	@Autowired
	public BrandController(BrandRepository brandRepository) {
		this.brandRepository = brandRepository;
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
		Brand existingBrand = brandRepository.findByName(brand.getName());
		if (existingBrand != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(
					"Brand with name '" + brand.getName() + "' already exists");
		}
		try {
			// Process image upload
			// Save the file to your desired location or cloud storage
			// For example, you can save it to a folder on your server or upload
			// it to Google Drive
			// Here, I'm assuming you have a method to handle file upload and
			// return the file path
			String imageUrl = null;
			try {
				imageUrl = uploadService.uploadImageFile(file,
						ImageTypeConstants.BRAND, brand.getName());
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
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Brand> brands = brandRepository.findAll(pageable);
		return ResponseEntity.ok(brands);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Brand> updateBrand(@PathVariable Long id,
			@RequestParam(value = "imageFile", required = false) MultipartFile file,
			@RequestBody Brand updatedBrand) {
		Optional<Brand> brandOptional = brandRepository.findById(id);
		if (!brandOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Brand brand = brandOptional.get();
		// Update fields
		brand.setName(updatedBrand.getName());
		brand.setDescription(updatedBrand.getDescription());
		// Upload new image if provided
		if (file != null && !file.isEmpty()) {
			try {
				try {
					String imageUrl = uploadService.uploadImageFile(file,
							ImageTypeConstants.BRAND, brand.getName());
					brand.setImgUrl(imageUrl);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// String fileName = UUID.randomUUID().toString() + "_"
				// + file.getOriginalFilename();
				// Path filePath = Paths.get(uploadDir).resolve(fileName)
				// .toAbsolutePath();
				// Files.copy(file.getInputStream(), filePath,
				// StandardCopyOption.REPLACE_EXISTING);
				// String imageUrl = "/image/" + fileName;
				// // Adjust the URL based on your image serving endpoint
				// brand.setImgUrl(imageUrl);
			} catch (Exception e) {
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
