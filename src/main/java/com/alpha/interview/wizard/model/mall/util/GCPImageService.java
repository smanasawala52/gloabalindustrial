package com.alpha.interview.wizard.model.mall.util;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.constants.mall.constants.ImageTypeConstants;
import com.alpha.interview.wizard.model.mall.constants.ImageServiceTypeConstants;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

@Service
@Component("GCPImageService")
public class GCPImageService implements ImageService {

	private final Storage storage;
	@Value("${gcs.bucketName}")
	private String bucketName;

	public GCPImageService(Storage storage) {
		this.storage = storage;
	}

	@Override
	public String uploadImageFile(MultipartFile file,
			ImageTypeConstants imageType, String name) throws Exception {
		String fileName = cleanUp(name) + ".jpg";
		fileName = fileName.toLowerCase();
		BlobInfo blobInfo = BlobInfo
				.newBuilder(bucketName,
						getImagePath(imageType, bucketName) + "/" + fileName)
				.build();
		Blob blob = storage.create(blobInfo, file.getBytes());
		return "/image/" + imageType.getType() + "/" + fileName;
	}

	@Override
	public byte[] getImage(ImageTypeConstants imageType, String name)
			throws Exception {
		String fileName = cleanUp(name);
		fileName = fileName.toLowerCase();
		BlobId blobId = BlobId.of(bucketName,
				getImagePath(imageType, bucketName) + "/" + fileName);
		Blob blob = storage.get(blobId);

		if (blob != null) {
			return blob.getContent();
		} else {
			throw new FileNotFoundException("Image not found: " + fileName);
		}
	}

	@Override
	public ImageServiceTypeConstants getIdentity() {
		return ImageServiceTypeConstants.GCP;
	}
}
