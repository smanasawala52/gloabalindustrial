package com.alpha.interview.wizard.controller.mall;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alpha.interview.wizard.model.mall.Shop;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.ShopRepository;

@Controller
@RequestMapping("/shop")
public class ShopController {

	private final ShopRepository shopRepository;
	private final CategoryRepository categoryRepository;

	@Autowired
	public ShopController(ShopRepository shopRepository,
			CategoryRepository categoryRepository) {
		this.shopRepository = shopRepository;
		this.categoryRepository = categoryRepository;
	}

	@GetMapping("/form")
	public String showForm(Model model) {
		model.addAttribute("shop", new Shop());
		model.addAttribute("categories", categoryRepository.findAll());
		return "shop-form";
	}

	@PostMapping("/save")
	public String saveShop(@ModelAttribute("shop") @Valid Shop shop,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			// Handle validation errors
			return "redirect:/shop/form";
		}
		shop.setUpdateTimestamp(new Date());
		shopRepository.save(shop);
		return "redirect:/shop/form";
	}

	@GetMapping("/all")
	public ResponseEntity<List<Shop>> getAllShops() {
		List<Shop> shops = shopRepository.findAll();
		return ResponseEntity.ok(shops);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Shop> updateShop(@PathVariable Long id,
			@RequestBody Shop updatedShop) {
		Optional<Shop> shopOptional = shopRepository.findById(id);
		if (!shopOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Shop shop = shopOptional.get();
		// Update fields
		shop.setName(updatedShop.getName());
		shop.setDescription(updatedShop.getDescription());
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
