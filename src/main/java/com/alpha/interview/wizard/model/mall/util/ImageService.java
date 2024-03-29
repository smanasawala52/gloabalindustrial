package com.alpha.interview.wizard.model.mall.util;

import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.constants.mall.constants.ImageTypeConstants;
import com.alpha.interview.wizard.model.mall.constants.ImageServiceTypeConstants;

public interface ImageService {
	public String uploadImageFile(MultipartFile file,
			ImageTypeConstants imageType, String name) throws Exception;
	public byte[] getImage(ImageTypeConstants imageType, String name)
			throws Exception;

	public default String getImagePath(ImageTypeConstants imageType,
			String uploadDir) {
		String path = uploadDir + "/" + imageType.getType();
		path = path.toLowerCase();
		System.out.println("Path: " + path);
		return path;
	}
	public default String cleanUp(String name) {
		String cleanedName = name.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
		cleanedName = cleanedName.replace(" ", "_");
		cleanedName = cleanedName.replaceAll("_+", "_");
		return cleanedName;
	}
	public default String getFileExtension(MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename != null && originalFilename.contains(".")) {
			return originalFilename
					.substring(originalFilename.lastIndexOf(".") + 1);
		}
		return null;
	}
	public ImageServiceTypeConstants getIdentity();
}
