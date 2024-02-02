package com.alpha.interview.wizard.contoller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alpha.interview.wizard.service.chat.ChatService;
import com.alpha.interview.wizard.service.mall.CsvService;

@Controller
@RequestMapping("/csv")
public class CsvController {

	@Autowired
	private CsvService csvService;
	@Autowired
	@Qualifier("OpenAIChat")
	private ChatService chatService;

	@GetMapping("/upload-form")
	public String showUploadForm() {
		return "uploadForm";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message",
					"Please select a CSV file to upload.");
			return "redirect:/csv/upload-form";
		}

		try {
			csvService.processCsvFile(file);
			redirectAttributes.addFlashAttribute("message",
					"CSV file uploaded and processed successfully.");
			return chatService.initializeChat();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message",
					"Error processing CSV file: " + e.getMessage());
		}

		return "redirect:/csv/upload-success";
	}

	@GetMapping("/upload-success")
	public String showUploadSuccess() {
		return "uploadSuccess";
	}
}
