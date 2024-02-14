package com.alpha.interview.wizard.controller.mall;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alpha.interview.wizard.model.mall.SubCategory;
import com.alpha.interview.wizard.repository.mall.SubCategoryRepository;

@Controller
@RequestMapping("/subcategory")
public class SubCategoryController {

	private final SubCategoryRepository subCategoryRepository;

	@Autowired
	public SubCategoryController(SubCategoryRepository subCategoryRepository) {
		this.subCategoryRepository = subCategoryRepository;
	}

	@RequestMapping("/form")
	public String showForm(Model model) {
		model.addAttribute("subcategory", new SubCategory());
		return "subcategory-form";
	}

	@PostMapping("/save")
	public String saveSubCategory(
			@ModelAttribute("subcategory") SubCategory subCategory,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			// Handle validation errors
			return "redirect:/subcategory/form";
		}
		subCategory.setUpdateTimestamp(new Date()); // Set current timestamp
		subCategoryRepository.save(subCategory);
		return "redirect:/subcategory/form"; // Redirect to the form again or
												// any other appropriate page
	}
}
