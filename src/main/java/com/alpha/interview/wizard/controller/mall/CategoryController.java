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

import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.SubCategory;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.SubCategoryRepository;

@Controller
@RequestMapping("/category")
public class CategoryController {

	private final CategoryRepository categoryRepository;
	private final SubCategoryRepository subCategoryRepository;

	@Autowired
	public CategoryController(CategoryRepository categoryRepository,
			SubCategoryRepository subCategoryRepository) {
		this.categoryRepository = categoryRepository;
		this.subCategoryRepository = subCategoryRepository;
	}

	@GetMapping("/form")
	public String showForm(Model model) {
		model.addAttribute("category", new Category());
		model.addAttribute("subcategories", subCategoryRepository.findAll());
		return "category-form";
	}

	@PostMapping("/save")
	public String saveCategory(
			@ModelAttribute("category") @Valid Category category,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			// Handle validation errors
			return "redirect:/category/form";
		}
		category.setUpdateTimestamp(new Date());
		categoryRepository.save(category);
		return "redirect:/category/form";
	}

	@GetMapping("/all")
	public ResponseEntity<List<Category>> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		return ResponseEntity.ok(categories);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Category> updateCategory(@PathVariable Long id,
			@RequestBody Category updatedCategory) {
		Optional<Category> categoryOptional = categoryRepository.findById(id);
		if (!categoryOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Category category = categoryOptional.get();
		// Update fields
		category.setName(updatedCategory.getName());
		category.setDescription(updatedCategory.getDescription());
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

	@PostMapping("/newSubcategory")
	public ResponseEntity<SubCategory> createSubcategory(
			@RequestBody SubCategory subCategory) {
		subCategory.setUpdateTimestamp(new Date());
		SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
		return ResponseEntity.ok(savedSubCategory);
	}
}
