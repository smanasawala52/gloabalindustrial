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
import com.alpha.interview.wizard.model.mall.Attraction;
import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.AttractionRepository;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;

@Controller
@RequestMapping("/attraction")
public class AttractionController {

	@Autowired
	private AttractionRepository attractionRepository;
	@Autowired
	private CategoryRepository categoryRepository;
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

	@GetMapping("/category/{id}")
	public String loginCategory(@PathVariable Long id, Model model) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(id);
		if (!attractionOptional.isPresent()) {
			model.addAttribute("contentTemplate", "attraction");
		} else {
			model.addAttribute("contentTemplate", "attraction-category-xref");
			model.addAttribute("attraction", attractionOptional.get());
		}
		return "common";
	}
	@GetMapping("/category/all/{id}")
	public ResponseEntity<Page<Category>> getAllCategories(
			@PathVariable Long id,
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Category> categories = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			categories = categoryRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			categories = categoryRepository.findAll(pageable);
		}
		if (categories.hasContent()) {
			Optional<Attraction> attractionOptional = attractionRepository
					.findById(id);
			if (attractionOptional.isPresent()
					&& attractionOptional.get().getCategories() != null) {
				Set<Long> categoryIds = attractionOptional.get().getCategories()
						.stream().map(Category::getId)
						.collect(Collectors.toSet());

				categories.getContent().forEach(category -> {
					if (categoryIds.contains(category.getId())) {
						category.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<Category> mutableList = new ArrayList<>(
						categories.getContent());
				mutableList
						.sort(Comparator
								.comparing(Category::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<Category> sortedPage = new PageImpl<>(mutableList,
						categories.getPageable(),
						categories.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(categories);
	}

	@PostMapping("/{attractionId}/category/{categoryId}")
	public ResponseEntity<String> updateCategory(
			@PathVariable Long attractionId, @PathVariable Long categoryId) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(attractionId);
		if (attractionOptional.isPresent()) {
			Optional<Category> categoryOptional = categoryRepository
					.findById(categoryId);
			if (categoryOptional.isPresent()) {
				attractionOptional.get().getCategories()
						.add(categoryOptional.get());
				attractionRepository.save(attractionOptional.get());
				attractionRepository.flush();
			}
		}
		return ResponseEntity.ok("Category linked successfully to attraction");
	}

	@DeleteMapping("/{attractionId}/category/{categoryId}")
	public ResponseEntity<String> removeCategory(
			@PathVariable Long attractionId, @PathVariable Long categoryId) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(attractionId);
		if (attractionOptional.isPresent()) {
			Optional<Category> categoryOptional = categoryRepository
					.findById(categoryId);
			if (categoryOptional.isPresent()) {
				Long categoryIdToRemove = categoryOptional.get().getId();
				attractionOptional.get().getCategories()
						.removeIf(category -> category.getId()
								.equals(categoryIdToRemove));
				attractionRepository.save(attractionOptional.get());
				attractionRepository.flush();
			}
		}
		return ResponseEntity.ok("Category removed from attraction");
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveAttraction(
			@ModelAttribute("attraction") @Valid Attraction attraction,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || attraction.getName() == null
				|| (attraction.getName() != null
						&& attraction.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Attraction existingAttraction = attractionRepository
				.findByName(MallUtil.formatName(attraction.getName()));
		if (existingAttraction != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Attraction with name '" + attraction.getName()
							+ "' already exists");
		}
		if (attraction.getDisplayName() == null
				|| (attraction.getDisplayName() != null
						&& attraction.getDisplayName().isEmpty())) {
			attraction.setDisplayName(attraction.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.ATTRACTION,
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
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Attraction> attractions = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			attractions = attractionRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			attractions = attractionRepository.findAll(pageable);
		}
		return ResponseEntity.ok(attractions);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveAttractionImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		Attraction existingAttraction = attractionRepository.getById(id);
		if (existingAttraction != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file,
									ImageTypeConstants.ATTRACTION,
									existingAttraction.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingAttraction.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingAttraction.setUpdateTimestamp(new Date());
			attractionRepository.save(existingAttraction);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Attraction> updateAttraction(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(id);
		if (!attractionOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Attraction attraction = attractionOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = attraction.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(attraction, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (attraction.getDisplayName() == null
				|| (attraction.getDisplayName() != null
						&& attraction.getDisplayName().isEmpty())) {
			attraction.setDisplayName(attraction.getName());
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
