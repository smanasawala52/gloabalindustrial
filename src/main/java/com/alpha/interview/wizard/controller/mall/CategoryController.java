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
import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.SubCategory;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.SubCategoryRepository;

@Controller
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SubCategoryRepository subCategoryRepository;

	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public CategoryController(
			ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveCategory(
			@ModelAttribute("category") @Valid Category category,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Category existingCategory = categoryRepository
				.findByName(category.getName());
		if (existingCategory != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Category with name '" + category.getName()
							+ "' already exists");
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.BRAND,
								category.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			category.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		category.setUpdateTimestamp(new Date());
		category.setCreateTimestamp(new Date());
		categoryRepository.save(category);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Category>> getAllCategorys(
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Category> categorys = categoryRepository.findAll(pageable);
		return ResponseEntity.ok(categorys);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Category> updateCategory(@PathVariable Long id,
			@RequestParam(value = "imageFile", required = false) MultipartFile file,
			@RequestBody Category updatedCategory) {
		Optional<Category> categoryOptional = categoryRepository.findById(id);
		if (!categoryOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Category category = categoryOptional.get();
		// Update fields
		category.setName(updatedCategory.getName());
		category.setDescription(updatedCategory.getDescription());
		// Upload new image if provided
		if (file != null && !file.isEmpty()) {
			try {
				try {
					String imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.BRAND,
									category.getName());
					category.setImgUrl(imageUrl);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(null);
			}
		}

		category.setUpdateTimestamp(new Date());
		// Save updated category
		categoryRepository.save(category);
		return ResponseEntity.ok(category);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		Optional<Category> categoryOptional = categoryRepository.findById(id);
		if (!categoryOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		categoryRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping("/newSubcategory")
	public ResponseEntity<SubCategory> createSubcategory(
			@RequestBody SubCategory subCategory) {
		subCategory.setUpdateTimestamp(new Date());
		SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
		return ResponseEntity.ok(savedSubCategory);
	}
}
