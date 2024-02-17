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
import com.alpha.interview.wizard.model.mall.Brand;
import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.Shop;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.BrandRepository;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.ShopRepository;

@Controller
@RequestMapping("/shop")
public class ShopController {

	@Autowired
	private ShopRepository shopRepository;
	@Autowired
	private BrandRepository brandRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public ShopController(ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "shop");
		return "common";
	}
	@GetMapping("/brand/{id}")
	public String loginBrand(@PathVariable Long id, Model model) {
		Optional<Shop> shopOptional = shopRepository.findById(id);
		if (!shopOptional.isPresent()) {
			model.addAttribute("contentTemplate", "shop");
		} else {
			model.addAttribute("contentTemplate", "shop-brand-xref");
			model.addAttribute("shop", shopOptional.get());
		}
		return "common";
	}
	@GetMapping("/brand/all/{id}")
	public ResponseEntity<Page<Brand>> getAllBrands(@PathVariable Long id,
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
		if (brands.hasContent()) {
			Optional<Shop> shopOptional = shopRepository.findById(id);
			if (shopOptional.isPresent()
					&& shopOptional.get().getBrands() != null) {
				Set<Long> brandIds = shopOptional.get().getBrands().stream()
						.map(Brand::getId).collect(Collectors.toSet());

				brands.getContent().forEach(brand -> {
					if (brandIds.contains(brand.getId())) {
						brand.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<Brand> mutableList = new ArrayList<>(brands.getContent());
				mutableList
						.sort(Comparator
								.comparing(Brand::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<Brand> sortedPage = new PageImpl<>(mutableList,
						brands.getPageable(), brands.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(brands);
	}

	@PostMapping("/{shopId}/brand/{brandId}")
	public ResponseEntity<String> updateBrand(@PathVariable Long shopId,
			@PathVariable Long brandId) {
		Optional<Shop> shopOptional = shopRepository.findById(shopId);
		if (shopOptional.isPresent()) {
			Optional<Brand> brandOptional = brandRepository.findById(brandId);
			if (brandOptional.isPresent()) {
				shopOptional.get().getBrands().add(brandOptional.get());
				shopRepository.save(shopOptional.get());
				shopRepository.flush();
			}
		}
		return ResponseEntity.ok("Brand linked successfully to shop");
	}

	@DeleteMapping("/{shopId}/brand/{brandId}")
	public ResponseEntity<String> removeBrand(@PathVariable Long shopId,
			@PathVariable Long brandId) {
		Optional<Shop> shopOptional = shopRepository.findById(shopId);
		if (shopOptional.isPresent()) {
			Optional<Brand> brandOptional = brandRepository.findById(brandId);
			if (brandOptional.isPresent()) {
				Long brandIdToRemove = brandOptional.get().getId();
				shopOptional.get().getBrands().removeIf(
						brand -> brand.getId().equals(brandIdToRemove));
				shopRepository.save(shopOptional.get());
				shopRepository.flush();
			}
		}
		return ResponseEntity.ok("Brand removed from shop");
	}

	@GetMapping("/category/{id}")
	public String loginCategory(@PathVariable Long id, Model model) {
		Optional<Shop> shopOptional = shopRepository.findById(id);
		if (!shopOptional.isPresent()) {
			model.addAttribute("contentTemplate", "shop");
		} else {
			model.addAttribute("contentTemplate", "shop-category-xref");
			model.addAttribute("shop", shopOptional.get());
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
			Optional<Shop> shopOptional = shopRepository.findById(id);
			if (shopOptional.isPresent()
					&& shopOptional.get().getCategories() != null) {
				Set<Long> categoryIds = shopOptional.get().getCategories()
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

	@PostMapping("/{shopId}/category/{categoryId}")
	public ResponseEntity<String> updateCategory(@PathVariable Long shopId,
			@PathVariable Long categoryId) {
		Optional<Shop> shopOptional = shopRepository.findById(shopId);
		if (shopOptional.isPresent()) {
			Optional<Category> categoryOptional = categoryRepository
					.findById(categoryId);
			if (categoryOptional.isPresent()) {
				shopOptional.get().getCategories().add(categoryOptional.get());
				shopRepository.save(shopOptional.get());
				shopRepository.flush();
			}
		}
		return ResponseEntity.ok("Category linked successfully to shop");
	}

	@DeleteMapping("/{shopId}/category/{categoryId}")
	public ResponseEntity<String> removeCategory(@PathVariable Long shopId,
			@PathVariable Long categoryId) {
		Optional<Shop> shopOptional = shopRepository.findById(shopId);
		if (shopOptional.isPresent()) {
			Optional<Category> categoryOptional = categoryRepository
					.findById(categoryId);
			if (categoryOptional.isPresent()) {
				Long categoryIdToRemove = categoryOptional.get().getId();
				shopOptional.get().getCategories().removeIf(category -> category
						.getId().equals(categoryIdToRemove));
				shopRepository.save(shopOptional.get());
				shopRepository.flush();
			}
		}
		return ResponseEntity.ok("Category removed from shop");
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveShop(@ModelAttribute("shop") @Valid Shop shop,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || shop.getName() == null
				|| (shop.getName() != null && shop.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Shop existingShop = shopRepository
				.findByName(MallUtil.formatName(shop.getName()));
		if (existingShop != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(
					"Shop with name '" + shop.getName() + "' already exists");
		}
		if (shop.getDisplayName() == null || (shop.getDisplayName() != null
				&& shop.getDisplayName().isEmpty())) {
			shop.setDisplayName(shop.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.SHOP,
								shop.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			shop.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		shop.setUpdateTimestamp(new Date());
		shop.setCreateTimestamp(new Date());
		shopRepository.save(shop);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Shop>> getAllShops(
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Shop> shops = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			shops = shopRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			shops = shopRepository.findAll(pageable);
		}
		return ResponseEntity.ok(shops);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveShopImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		Shop existingShop = shopRepository.getById(id);
		if (existingShop != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.SHOP,
									existingShop.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingShop.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingShop.setUpdateTimestamp(new Date());
			shopRepository.save(existingShop);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Shop> updateShop(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<Shop> shopOptional = shopRepository.findById(id);
		if (!shopOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Shop shop = shopOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = shop.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(shop, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (shop.getDisplayName() == null || (shop.getDisplayName() != null
				&& shop.getDisplayName().isEmpty())) {
			shop.setDisplayName(shop.getName());
		}
		shop.setUpdateTimestamp(new Date());
		// Save updated shop
		shopRepository.save(shop);
		return ResponseEntity.ok(shop);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
		Optional<Shop> shopOptional = shopRepository.findById(id);
		if (!shopOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		shopRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
