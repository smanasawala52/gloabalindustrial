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
import com.alpha.interview.wizard.model.mall.Coupon;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.model.mall.Product;
import com.alpha.interview.wizard.model.mall.Shop;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.BrandRepository;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.CouponRepository;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.alpha.interview.wizard.repository.mall.ProductRepository;
import com.alpha.interview.wizard.repository.mall.ShopRepository;

@Controller
@RequestMapping("/shop")
public class ShopController {

	@Autowired
	private ShopRepository shopRepository;
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
	@GetMapping("/product/{id}")
	public String loginProduct(@PathVariable Long id, Model model) {
		Optional<Shop> shopOptional = shopRepository.findById(id);
		if (!shopOptional.isPresent()) {
			model.addAttribute("contentTemplate", "shop");
		} else {
			model.addAttribute("contentTemplate", "shop-product-xref");
			model.addAttribute("shop", shopOptional.get());
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
			Optional<Shop> shopOptional = shopRepository.findById(id);
			if (shopOptional.isPresent()
					&& shopOptional.get().getProducts() != null) {
				Set<Long> productIds = shopOptional.get().getProducts().stream()
						.map(Product::getId).collect(Collectors.toSet());

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

	@PostMapping("/{shopId}/product/{productId}")
	public ResponseEntity<String> updateProduct(@PathVariable Long shopId,
			@PathVariable Long productId) {
		Optional<Shop> shopOptional = shopRepository.findById(shopId);
		if (shopOptional.isPresent()) {
			Optional<Product> productOptional = productRepository
					.findById(productId);
			if (productOptional.isPresent()) {
				shopOptional.get().getProducts().add(productOptional.get());
				shopRepository.save(shopOptional.get());
				shopRepository.flush();
			}
		}
		return ResponseEntity.ok("Product linked successfully to shop");
	}

	@DeleteMapping("/{shopId}/product/{productId}")
	public ResponseEntity<String> removeProduct(@PathVariable Long shopId,
			@PathVariable Long productId) {
		Optional<Shop> shopOptional = shopRepository.findById(shopId);
		if (shopOptional.isPresent()) {
			Optional<Product> productOptional = productRepository
					.findById(productId);
			if (productOptional.isPresent()) {
				Long productIdToRemove = productOptional.get().getId();
				shopOptional.get().getProducts().removeIf(
						product -> product.getId().equals(productIdToRemove));
				shopRepository.save(shopOptional.get());
				shopRepository.flush();
			}
		}
		return ResponseEntity.ok("Product removed from shop");
	}
	@GetMapping("/coupon/{id}")
	public String loginCoupon(@PathVariable Long id, Model model) {
		Optional<Shop> shopOptional = shopRepository.findById(id);
		if (!shopOptional.isPresent()) {
			model.addAttribute("contentTemplate", "shop");
		} else {
			model.addAttribute("contentTemplate", "shop-coupon-xref");
			model.addAttribute("shop", shopOptional.get());
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
			Optional<Shop> shopOptional = shopRepository.findById(id);
			if (shopOptional.isPresent()
					&& shopOptional.get().getCoupons() != null) {
				Set<Long> couponIds = shopOptional.get().getCoupons().stream()
						.map(Coupon::getId).collect(Collectors.toSet());

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

	@PostMapping("/{shopId}/coupon/{couponId}")
	public ResponseEntity<String> updateCoupon(@PathVariable Long shopId,
			@PathVariable Long couponId) {
		Optional<Shop> shopOptional = shopRepository.findById(shopId);
		if (shopOptional.isPresent()) {
			Optional<Coupon> couponOptional = couponRepository
					.findById(couponId);
			if (couponOptional.isPresent()) {
				shopOptional.get().getCoupons().add(couponOptional.get());
				shopRepository.save(shopOptional.get());
				shopRepository.flush();
			}
		}
		return ResponseEntity.ok("Coupon linked successfully to shop");
	}

	@DeleteMapping("/{shopId}/coupon/{couponId}")
	public ResponseEntity<String> removeCoupon(@PathVariable Long shopId,
			@PathVariable Long couponId) {
		Optional<Shop> shopOptional = shopRepository.findById(shopId);
		if (shopOptional.isPresent()) {
			Optional<Coupon> couponOptional = couponRepository
					.findById(couponId);
			if (couponOptional.isPresent()) {
				Long couponIdToRemove = couponOptional.get().getId();
				shopOptional.get().getCoupons().removeIf(
						coupon -> coupon.getId().equals(couponIdToRemove));
				shopRepository.save(shopOptional.get());
				shopRepository.flush();
			}
		}
		return ResponseEntity.ok("Coupon removed from shop");
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
	public ResponseEntity<?> deleteShop(@PathVariable Long id) {
		Optional<Shop> shopOptional = shopRepository.findById(id);
		if (!shopOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		List<MallModel> mallModels = mallModelRepository.findByShopId(id);
		if (mallModels != null && !mallModels.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(mallModels);
		}
		shopRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<Shop>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(shopRepository.findAll());
		}
		List<Shop> lst = new ArrayList<>();
		Optional<Shop> shopOptional = shopRepository.findById(id);
		if (shopOptional.isPresent()) {
			lst.add(shopOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
	// @GetMapping("/{mallId}/category/{categoryId}")
	// public ResponseEntity<List<Shop>> getAllShopsByMallIdAndCategoryId(
	// @PathVariable Long mallId, @PathVariable Long categoryId) {
	// List<Shop> shops = shopRepository.findAllByMallIdAndCategoryId(mallId,
	// categoryId);
	// return new ResponseEntity<>(shops, HttpStatus.OK);
	// }
	//
	// @GetMapping("/{mallId}/subcategory/{subCategoryId}")
	// public ResponseEntity<List<Shop>> getAllShopsByMallIdAndSubCategoryId(
	// @PathVariable Long mallId, @PathVariable Long subCategoryId) {
	// List<Shop> shops = shopRepository
	// .findAllByMallIdAndSubCategoryId(mallId, subCategoryId);
	// return new ResponseEntity<>(shops, HttpStatus.OK);
	// }
}
