package com.alpha.interview.wizard.controller.mall;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.alpha.interview.wizard.model.mall.Coupon;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.CouponRepository;

@Controller
@RequestMapping("/coupon")
public class CouponController {

	@Autowired
	private CouponRepository couponRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public CouponController(ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "coupon");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveCoupon(
			@ModelAttribute("coupon") @Valid Coupon coupon,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || coupon.getName() == null
				|| (coupon.getName() != null && coupon.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Coupon existingCoupon = couponRepository
				.findByName(MallUtil.formatName(coupon.getName()));
		if (existingCoupon != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Coupon with name '" + coupon.getName()
							+ "' already exists");
		}
		if (coupon.getDisplayName() == null || (coupon.getDisplayName() != null
				&& coupon.getDisplayName().isEmpty())) {
			coupon.setDisplayName(coupon.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.COUPON,
								coupon.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			coupon.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		coupon.setUpdateTimestamp(new Date());
		coupon.setCreateTimestamp(new Date());
		couponRepository.save(coupon);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Coupon>> getAllCoupons(
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
		return ResponseEntity.ok(coupons);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveCouponImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		Coupon existingCoupon = couponRepository.getById(id);
		if (existingCoupon != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.COUPON,
									existingCoupon.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingCoupon.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingCoupon.setUpdateTimestamp(new Date());
			couponRepository.save(existingCoupon);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<Coupon> couponOptional = couponRepository.findById(id);
		if (!couponOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Coupon coupon = couponOptional.get();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = coupon.getClass().getDeclaredField(key);
				field.setAccessible(true);
				if (key.equals("startDate") || key.equals("endDate")) {
					Date date = null;
					try {
						date = dateFormat.parse((String) value);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					field.set(coupon, date);
				} else {
					field.set(coupon, value);
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (coupon.getDisplayName() == null || (coupon.getDisplayName() != null
				&& coupon.getDisplayName().isEmpty())) {
			coupon.setDisplayName(coupon.getName());
		}
		coupon.setUpdateTimestamp(new Date());
		// Save updated coupon
		couponRepository.save(coupon);
		return ResponseEntity.ok(coupon);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
		Optional<Coupon> couponOptional = couponRepository.findById(id);
		if (!couponOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		couponRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<Coupon>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(couponRepository.findAll());
		}
		List<Coupon> lst = new ArrayList<>();
		Optional<Coupon> couponsOptional = couponRepository.findById(id);
		if (couponsOptional.isPresent()) {
			lst.add(couponsOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
}
