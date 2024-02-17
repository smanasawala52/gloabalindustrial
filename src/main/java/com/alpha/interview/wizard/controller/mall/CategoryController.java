package com.alpha.interview.wizard.controller.mall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "category");
		return "common";
	}
	@GetMapping("/subcategory/{id}")
	public String loginSubcategory(@PathVariable Long id, Model model) {
		Optional<Category> categoryOptional = categoryRepository.findById(id);
		if (!categoryOptional.isPresent()) {
			model.addAttribute("contentTemplate", "category");
		} else {
			model.addAttribute("contentTemplate", "category-subcategory-xref");
			model.addAttribute("category", categoryOptional.get());
		}
		return "common";
	}
	@GetMapping("/subcategory/all/{id}")
	public ResponseEntity<Page<SubCategory>> getAllSubCategories(
			@PathVariable Long id,
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<SubCategory> subCategories = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			subCategories = subCategoryRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			subCategories = subCategoryRepository.findAll(pageable);
		}
		if (subCategories.hasContent()) {
			Optional<Category> categoryOptional = categoryRepository
					.findById(id);
			if (categoryOptional.isPresent()
					&& categoryOptional.get().getSubCategories() != null) {
				Set<Long> subCategoryIds = categoryOptional.get()
						.getSubCategories().stream().map(SubCategory::getId)
						.collect(Collectors.toSet());

				subCategories.getContent().forEach(subCategory -> {
					if (subCategoryIds.contains(subCategory.getId())) {
						subCategory.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<SubCategory> mutableList = new ArrayList<>(
						subCategories.getContent());
				mutableList
						.sort(Comparator
								.comparing(SubCategory::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<SubCategory> sortedPage = new PageImpl<>(mutableList,
						subCategories.getPageable(),
						subCategories.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(subCategories);
	}

	@PostMapping("/{categoryId}/subcategory/{subcategoryId}")
	public ResponseEntity<String> updateSubcategory(
			@PathVariable Long categoryId, @PathVariable Long subcategoryId) {
		Optional<Category> categoryOptional = categoryRepository
				.findById(categoryId);
		if (categoryOptional.isPresent()) {
			Optional<SubCategory> subCategoryOptional = subCategoryRepository
					.findById(subcategoryId);
			if (subCategoryOptional.isPresent()) {
				categoryOptional.get().getSubCategories()
						.add(subCategoryOptional.get());
				categoryRepository.save(categoryOptional.get());
				categoryRepository.flush();
			}
		}
		return ResponseEntity.ok("Subcategory linked successfully to category");
	}

	@DeleteMapping("/{categoryId}/subcategory/{subcategoryId}")
	public ResponseEntity<String> removeSubcategory(
			@PathVariable Long categoryId, @PathVariable Long subcategoryId) {
		Optional<Category> categoryOptional = categoryRepository
				.findById(categoryId);
		if (categoryOptional.isPresent()) {
			Optional<SubCategory> subCategoryOptional = subCategoryRepository
					.findById(subcategoryId);
			if (subCategoryOptional.isPresent()) {
				Long subCategoryIdToRemove = subCategoryOptional.get().getId();
				categoryOptional.get().getSubCategories()
						.removeIf(subCategory -> subCategory.getId()
								.equals(subCategoryIdToRemove));
				categoryRepository.save(categoryOptional.get());
				categoryRepository.flush();
			}
		}
		return ResponseEntity.ok("Subcategory removed from category");
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveCategory(
			@ModelAttribute("category") @Valid Category category,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || category.getName() == null
				|| (category.getName() != null
						&& category.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Category existingCategory = categoryRepository
				.findByName(MallUtil.formatName(category.getName()));
		if (existingCategory != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Category with name '" + category.getName()
							+ "' already exists");
		}
		if (category.getDisplayName() == null
				|| (category.getDisplayName() != null
						&& category.getDisplayName().isEmpty())) {
			category.setDisplayName(category.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.CATEGORY,
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
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Category> categorys = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			categorys = categoryRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			categorys = categoryRepository.findAll(pageable);
		}
		return ResponseEntity.ok(categorys);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveCategoryImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		Category existingCategory = categoryRepository.getById(id);
		if (existingCategory != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.CATEGORY,
									existingCategory.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingCategory.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingCategory.setUpdateTimestamp(new Date());
			categoryRepository.save(existingCategory);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Category> updateCategory(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<Category> categoryOptional = categoryRepository.findById(id);
		if (!categoryOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Category category = categoryOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = category.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(category, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (category.getDisplayName() == null
				|| (category.getDisplayName() != null
						&& category.getDisplayName().isEmpty())) {
			category.setDisplayName(category.getName());
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
}
