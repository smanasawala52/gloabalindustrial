package com.alpha.interview.wizard.controller.mall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alpha.interview.wizard.service.mall.MallModelDataLoadService;

@Controller
@RequestMapping("/mallmodel")
public class MallModelLoaderController {

	@Autowired
	private MallModelDataLoadService mallModelDataLoadService;

	@GetMapping("/upload-form")
	public String showUploadForm() {
		return "uploadMallModelJsonForm";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		if (file.isEmpty() && file.getOriginalFilename().endsWith(".json")) {
			redirectAttributes.addFlashAttribute("message", "Please select a JSON file to upload.");
			return "redirect:/mallmodel/upload-form";
		}

		try {
			mallModelDataLoadService.loadMallModelData(file);
			redirectAttributes.addFlashAttribute("message", "Mall file uploaded and processed successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Error processing CSV file: " + e.getMessage());
		}

		return "redirect:/mallmodel/upload-success";
	}

	@GetMapping("/upload-success")
	public String showUploadSuccess() {
		return "uploadMallModelJsonSuccess";
	}
}
