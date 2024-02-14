package com.alpha.interview.wizard.model.mall.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.constants.mall.ImageTypeConstants;

@Service
@Component("UploadImageService")
public class UploadImageService implements ImageUpload {

	@Value("${local.image.upload.dir}")
	private String uploadDir;

	@Override
	public String uploadImageFile(MultipartFile file,
			ImageTypeConstants imageType, String name) throws Exception {
		// Generate a unique file name
		String fileName = cleanUp(name) + ".jpg";
		fileName = fileName.toLowerCase();
		try {
			String path = getImagePath(imageType, uploadDir);
			System.out.println("UploadImageService path: " + path);
			File directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			// Resolve the file path
			Path filePath = Paths.get(path).resolve(fileName);
			// Copy the file to the target location
			Files.copy(file.getInputStream(), filePath,
					StandardCopyOption.REPLACE_EXISTING);
			return "/image/" + imageType.getType() + "/" + fileName;
		} catch (Exception ex) {
			throw new RuntimeException(
					"Failed to store file " + fileName + ". Please try again!",
					ex);
		}
	}

	@Override
	public byte[] getImage(ImageTypeConstants imageType, String fileName)
			throws IOException {
		try {
			String path = getImagePath(imageType, uploadDir);
			System.out.println(path + fileName);
			return Files.readAllBytes(Paths.get(path + "/" + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Files.readAllBytes(Paths.get("/mall/noImage.jpg"));
	}
}
