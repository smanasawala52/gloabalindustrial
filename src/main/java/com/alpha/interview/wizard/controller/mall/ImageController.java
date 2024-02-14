package com.alpha.interview.wizard.controller.mall;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.interview.wizard.constants.mall.ImageTypeConstants;
import com.alpha.interview.wizard.model.mall.util.ImageUpload;

@RestController
public class ImageController {

	@Autowired
	@Qualifier("UploadImageService")
	private ImageUpload uploadService;

	@GetMapping("/image/{imageType}/{name}")
	public ResponseEntity<Resource> getImage(@PathVariable String imageType,
			@PathVariable String name) throws IOException {
		byte[] imageBytes = uploadService
				.getImage(ImageTypeConstants.fromType(imageType), name);
		ByteArrayResource resource = new ByteArrayResource(imageBytes);
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
				.contentType(MediaType.IMAGE_PNG)
				.contentLength(imageBytes.length).body(resource);
	}
}
