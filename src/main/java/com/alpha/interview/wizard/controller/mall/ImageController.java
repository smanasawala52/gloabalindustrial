package com.alpha.interview.wizard.controller.mall;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.interview.wizard.constants.mall.constants.ImageTypeConstants;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;

@RestController
public class ImageController {

	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public ImageController(ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/image/{imageType}/{name}")
	public ResponseEntity<Resource> getImage(@PathVariable String imageType,
			@PathVariable String name) throws IOException {
		byte[] imageBytes = imageServiceMap.get(imageServiceImpl)
				.getImage(ImageTypeConstants.fromType(imageType), name);
		ByteArrayResource resource = new ByteArrayResource(imageBytes);
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
				.contentType(MediaType.IMAGE_PNG)
				.contentLength(imageBytes.length).body(resource);
	}
}
