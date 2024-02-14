package com.alpha.interview.wizard.controller.mall;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.alpha.interview.wizard.model.mall.Coupon;
import com.alpha.interview.wizard.repository.mall.CouponRepository;

@Controller
@RequestMapping("/coupon")
public class CouponController {

	private final CouponRepository couponRepository;

	@Autowired
	public CouponController(CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}

	@GetMapping("/form")
	public String showForm() {
		return "coupon-form";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveCoupon(
			@ModelAttribute("coupon") @Valid Coupon coupon,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		coupon.setUpdateTimestamp(new Date());
		couponRepository.save(coupon);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<List<Coupon>> getAllCoupons() {
		List<Coupon> coupons = couponRepository.findAll();
		return ResponseEntity.ok(coupons);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id,
			@RequestBody Coupon updatedCoupon) {
		Optional<Coupon> couponOptional = couponRepository.findById(id);
		if (!couponOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Coupon coupon = couponOptional.get();
		// Update fields
		coupon.setName(updatedCoupon.getName());
		coupon.setDescription(updatedCoupon.getDescription());
		coupon.setImgUrl(updatedCoupon.getImgUrl());
		coupon.setAdditionalDetails(updatedCoupon.getAdditionalDetails());
		coupon.setStartDate(updatedCoupon.getStartDate());
		coupon.setEndDate(updatedCoupon.getEndDate());
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
}
