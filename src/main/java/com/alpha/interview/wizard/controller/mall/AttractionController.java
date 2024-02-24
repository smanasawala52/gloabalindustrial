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
import com.alpha.interview.wizard.model.mall.Brand;
import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.Coupon;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.model.mall.Product;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.AttractionRepository;
import com.alpha.interview.wizard.repository.mall.BrandRepository;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.CouponRepository;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.alpha.interview.wizard.repository.mall.ProductRepository;

@Controller
@RequestMapping("/attraction")
public class AttractionController {

	@Autowired
	private AttractionRepository attractionRepository;
	@Autowired
	private MallModelRepository mallModelRepository;
	@Autowired
	private BrandRepository brandRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CouponRepository couponRepository;
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
	@GetMapping("/brand/{id}")
	public String loginBrand(@PathVariable Long id, Model model) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(id);
		if (!attractionOptional.isPresent()) {
			model.addAttribute("contentTemplate", "attraction");
		} else {
			model.addAttribute("contentTemplate", "attraction-brand-xref");
			model.addAttribute("attraction", attractionOptional.get());
		}
		return "common";
	}
	@GetMapping("/product/{id}")
	public String loginProduct(@PathVariable Long id, Model model) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(id);
		if (!attractionOptional.isPresent()) {
			model.addAttribute("contentTemplate", "attraction");
		} else {
			model.addAttribute("contentTemplate", "attraction-product-xref");
			model.addAttribute("attraction", attractionOptional.get());
		}
		return "common";
	}
	@GetMapping("/product/all/{id}")
	public ResponseEntity<Page<Product>> getAllProducts(@PathVariable Long id,
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Product> products = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			products = productRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			products = productRepository.findAll(pageable);
		}
		if (products.hasContent()) {
			Optional<Attraction> attractionOptional = attractionRepository
					.findById(id);
			if (attractionOptional.isPresent()
					&& attractionOptional.get().getProducts() != null) {
				Set<Long> productIds = attractionOptional.get().getProducts()
						.stream().map(Product::getId)
						.collect(Collectors.toSet());

				products.getContent().forEach(product -> {
					if (productIds.contains(product.getId())) {
						product.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<Product> mutableList = new ArrayList<>(
						products.getContent());
				mutableList
						.sort(Comparator
								.comparing(Product::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<Product> sortedPage = new PageImpl<>(mutableList,
						products.getPageable(), products.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(products);
	}

	@PostMapping("/{attractionId}/product/{productId}")
	public ResponseEntity<String> updateProduct(@PathVariable Long attractionId,
			@PathVariable Long productId) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(attractionId);
		if (attractionOptional.isPresent()) {
			Optional<Product> productOptional = productRepository
					.findById(productId);
			if (productOptional.isPresent()) {
				attractionOptional.get().getProducts()
						.add(productOptional.get());
				attractionRepository.save(attractionOptional.get());
				attractionRepository.flush();
			}
		}
		return ResponseEntity.ok("Product linked successfully to attraction");
	}

	@DeleteMapping("/{attractionId}/product/{productId}")
	public ResponseEntity<String> removeProduct(@PathVariable Long attractionId,
			@PathVariable Long productId) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(attractionId);
		if (attractionOptional.isPresent()) {
			Optional<Product> productOptional = productRepository
					.findById(productId);
			if (productOptional.isPresent()) {
				Long productIdToRemove = productOptional.get().getId();
				attractionOptional.get().getProducts().removeIf(
						product -> product.getId().equals(productIdToRemove));
				attractionRepository.save(attractionOptional.get());
				attractionRepository.flush();
			}
		}
		return ResponseEntity.ok("Product removed from attraction");
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
			Optional<Attraction> attractionOptional = attractionRepository
					.findById(id);
			if (attractionOptional.isPresent()
					&& attractionOptional.get().getBrands() != null) {
				Set<Long> brandIds = attractionOptional.get().getBrands()
						.stream().map(Brand::getId).collect(Collectors.toSet());

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

	@PostMapping("/{attractionId}/brand/{brandId}")
	public ResponseEntity<String> updateBrand(@PathVariable Long attractionId,
			@PathVariable Long brandId) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(attractionId);
		if (attractionOptional.isPresent()) {
			Optional<Brand> brandOptional = brandRepository.findById(brandId);
			if (brandOptional.isPresent()) {
				attractionOptional.get().getBrands().add(brandOptional.get());
				attractionRepository.save(attractionOptional.get());
				attractionRepository.flush();
			}
		}
		return ResponseEntity.ok("Brand linked successfully to attraction");
	}

	@DeleteMapping("/{attractionId}/brand/{brandId}")
	public ResponseEntity<String> removeBrand(@PathVariable Long attractionId,
			@PathVariable Long brandId) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(attractionId);
		if (attractionOptional.isPresent()) {
			Optional<Brand> brandOptional = brandRepository.findById(brandId);
			if (brandOptional.isPresent()) {
				Long brandIdToRemove = brandOptional.get().getId();
				attractionOptional.get().getBrands().removeIf(
						brand -> brand.getId().equals(brandIdToRemove));
				attractionRepository.save(attractionOptional.get());
				attractionRepository.flush();
			}
		}
		return ResponseEntity.ok("Brand removed from attraction");
	}
	@GetMapping("/coupon/{id}")
	public String loginCoupon(@PathVariable Long id, Model model) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(id);
		if (!attractionOptional.isPresent()) {
			model.addAttribute("contentTemplate", "attraction");
		} else {
			model.addAttribute("contentTemplate", "attraction-coupon-xref");
			model.addAttribute("attraction", attractionOptional.get());
		}
		return "common";
	}
	@GetMapping("/coupon/all/{id}")
	public ResponseEntity<Page<Coupon>> getAllCoupons(@PathVariable Long id,
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Coupon> coupons = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			coupons = couponRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			coupons = couponRepository.findAll(pageable);
		}
		if (coupons.hasContent()) {
			Optional<Attraction> attractionOptional = attractionRepository
					.findById(id);
			if (attractionOptional.isPresent()
					&& attractionOptional.get().getCoupons() != null) {
				Set<Long> couponIds = attractionOptional.get().getCoupons()
						.stream().map(Coupon::getId)
						.collect(Collectors.toSet());

				coupons.getContent().forEach(coupon -> {
					if (couponIds.contains(coupon.getId())) {
						coupon.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<Coupon> mutableList = new ArrayList<>(
						coupons.getContent());
				mutableList
						.sort(Comparator
								.comparing(Coupon::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<Coupon> sortedPage = new PageImpl<>(mutableList,
						coupons.getPageable(), coupons.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(coupons);
	}

	@PostMapping("/{attractionId}/coupon/{couponId}")
	public ResponseEntity<String> updateCoupon(@PathVariable Long attractionId,
			@PathVariable Long couponId) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(attractionId);
		if (attractionOptional.isPresent()) {
			Optional<Coupon> couponOptional = couponRepository
					.findById(couponId);
			if (couponOptional.isPresent()) {
				attractionOptional.get().getCoupons().add(couponOptional.get());
				attractionRepository.save(attractionOptional.get());
				attractionRepository.flush();
			}
		}
		return ResponseEntity.ok("Coupon linked successfully to attraction");
	}

	@DeleteMapping("/{attractionId}/coupon/{couponId}")
	public ResponseEntity<String> removeCoupon(@PathVariable Long attractionId,
			@PathVariable Long couponId) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(attractionId);
		if (attractionOptional.isPresent()) {
			Optional<Coupon> couponOptional = couponRepository
					.findById(couponId);
			if (couponOptional.isPresent()) {
				Long couponIdToRemove = couponOptional.get().getId();
				attractionOptional.get().getCoupons().removeIf(
						coupon -> coupon.getId().equals(couponIdToRemove));
				attractionRepository.save(attractionOptional.get());
				attractionRepository.flush();
			}
		}
		return ResponseEntity.ok("Coupon removed from attraction");
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
	public ResponseEntity<?> deleteAttraction(@PathVariable Long id) {
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(id);
		if (!attractionOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		// delete any links from MallModel
		List<MallModel> mallModels = mallModelRepository.findByAttractionId(id);
		if (mallModels != null && !mallModels.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(mallModels);
		}
		// delete attraction repository
		attractionRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<Attraction>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(attractionRepository.findAll());
		}
		List<Attraction> lst = new ArrayList<>();
		Optional<Attraction> attractionOptional = attractionRepository
				.findById(id);
		if (attractionOptional.isPresent()) {
			lst.add(attractionOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
}
