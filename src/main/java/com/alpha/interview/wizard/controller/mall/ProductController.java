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
import com.alpha.interview.wizard.model.mall.Attraction;
import com.alpha.interview.wizard.model.mall.Product;
import com.alpha.interview.wizard.model.mall.Shop;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.AttractionRepository;
import com.alpha.interview.wizard.repository.mall.ProductRepository;
import com.alpha.interview.wizard.repository.mall.ShopRepository;

@Controller
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ShopRepository shopRepository;
	@Autowired
	private AttractionRepository attractionRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public ProductController(ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "product");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveProduct(
			@ModelAttribute("product") @Valid Product product,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || product.getName() == null
				|| (product.getName() != null && product.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Product existingProduct = productRepository
				.findByName(MallUtil.formatName(product.getName()));
		if (existingProduct != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Product with name '" + product.getName()
							+ "' already exists");
		}
		if (product.getDisplayName() == null
				|| (product.getDisplayName() != null
						&& product.getDisplayName().isEmpty())) {
			product.setDisplayName(product.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.PRODUCT,
								product.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			product.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		product.setUpdateTimestamp(new Date());
		product.setCreateTimestamp(new Date());
		productRepository.save(product);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Product>> getAllProducts(
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
		Page<Product> products = null;
		if (ids != null && !ids.isBlank()) {
			List<Long> lstShopIds = MallUtil.convertToLongList(ids);
			products = productRepository.findByIds(lstShopIds, pageable);
		} else if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			products = productRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			products = productRepository.findAll(pageable);
		}
		return ResponseEntity.ok(products);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveProductImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		Product existingProduct = productRepository.getById(id);
		if (existingProduct != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.PRODUCT,
									existingProduct.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingProduct.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingProduct.setUpdateTimestamp(new Date());
			productRepository.save(existingProduct);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<Product> productOptional = productRepository.findById(id);
		if (!productOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Product product = productOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = product.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(product, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (product.getDisplayName() == null
				|| (product.getDisplayName() != null
						&& product.getDisplayName().isEmpty())) {
			product.setDisplayName(product.getName());
		}
		product.setUpdateTimestamp(new Date());
		// Save updated product
		productRepository.save(product);
		return ResponseEntity.ok(product);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
		Optional<Product> productOptional = productRepository.findById(id);
		if (!productOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		List<Shop> shops = shopRepository.findByBrandId(id);
		if (shops != null && !shops.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(shops);
		}
		List<Attraction> attractions = attractionRepository.findByBrandId(id);
		if (attractions != null && !attractions.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(attractions);
		}

		productRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<Product>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(productRepository.findAll());
		}
		List<Product> lst = new ArrayList<>();
		Optional<Product> productOptional = productRepository.findById(id);
		if (productOptional.isPresent()) {
			lst.add(productOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
}
