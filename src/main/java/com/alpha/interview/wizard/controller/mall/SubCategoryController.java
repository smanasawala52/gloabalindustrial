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
import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.SubCategory;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.SubCategoryRepository;

@Controller
@RequestMapping("/subcategory")
public class SubCategoryController {

	@Autowired
	private SubCategoryRepository subCategoryRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public SubCategoryController(
			ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "subcategory");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveSubCategory(
			@ModelAttribute("subCategory") @Valid SubCategory subCategory,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || subCategory.getName() == null
				|| (subCategory.getName() != null
						&& subCategory.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		SubCategory existingSubCategory = subCategoryRepository
				.findByName(MallUtil.formatName(subCategory.getName()));
		if (existingSubCategory != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("SubCategory with name '" + subCategory.getName()
							+ "' already exists");
		}
		if (subCategory.getDisplayName() == null
				|| (subCategory.getDisplayName() != null
						&& subCategory.getDisplayName().isEmpty())) {
			subCategory.setDisplayName(subCategory.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.SUB_CATEGORY,
								subCategory.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			subCategory.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		subCategory.setUpdateTimestamp(new Date());
		subCategory.setCreateTimestamp(new Date());
		subCategoryRepository.save(subCategory);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<SubCategory>> getAllSubCategorys(
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "name", name = "s", required = false) String sort,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp,
			@RequestParam(defaultValue = "0", name = "ps", required = false) int ps,
			@RequestParam(defaultValue = "", name = "ids", required = false) String ids) {
		if (cp <= 0) {
			cp = 0;
		}
		if (ps <= 0) {
			ps = PAGE_SIZE;
		}
		if (ps > PAGE_SIZE) {
			ps = PAGE_SIZE;
		}
		Pageable pageable = PageRequest.of(cp, ps, Sort.by(sort).ascending());
		Page<SubCategory> subCategories = null;
		if (ids != null && !ids.isBlank()) {
			List<Long> lstShopIds = MallUtil.convertToLongList(ids);
			subCategories = subCategoryRepository.findByIds(lstShopIds,
					pageable);
		} else if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			subCategories = subCategoryRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			subCategories = subCategoryRepository.findAll(pageable);
		}
		return ResponseEntity.ok(subCategories);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveSubCategoryImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		SubCategory existingSubCategory = subCategoryRepository.getById(id);
		if (existingSubCategory != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file,
									ImageTypeConstants.SUB_CATEGORY,
									existingSubCategory.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingSubCategory.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingSubCategory.setUpdateTimestamp(new Date());
			subCategoryRepository.save(existingSubCategory);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<SubCategory> updateSubCategory(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<SubCategory> subCategoryOptional = subCategoryRepository
				.findById(id);
		if (!subCategoryOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		SubCategory subCategory = subCategoryOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = subCategory.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(subCategory, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (subCategory.getDisplayName() == null
				|| (subCategory.getDisplayName() != null
						&& subCategory.getDisplayName().isEmpty())) {
			subCategory.setDisplayName(subCategory.getName());
		}
		subCategory.setUpdateTimestamp(new Date());
		// Save updated subCategory
		subCategoryRepository.save(subCategory);
		return ResponseEntity.ok(subCategory);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteSubCategory(@PathVariable Long id) {
		Optional<SubCategory> subCategoryOptional = subCategoryRepository
				.findById(id);
		if (!subCategoryOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		List<Category> categories = categoryRepository.findBySubCategoryId(id);
		if (categories != null && !categories.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(categories);
		}
		subCategoryRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<SubCategory>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(subCategoryRepository.findAll());
		}
		List<SubCategory> lst = new ArrayList<>();
		Optional<SubCategory> subCategoryOptional = subCategoryRepository
				.findById(id);
		if (subCategoryOptional.isPresent()) {
			lst.add(subCategoryOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
}
