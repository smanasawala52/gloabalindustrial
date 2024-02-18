package com.alpha.interview.wizard.controller.mall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.constants.mall.constants.ImageTypeConstants;
import com.alpha.interview.wizard.model.mall.Parking;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.ParkingRepository;

@Controller
@RequestMapping("/parking")
public class ParkingController {

	@Autowired
	private ParkingRepository parkingRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public ParkingController(ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "parking");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveParking(
			@ModelAttribute("parking") @Valid Parking parking,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || getDisplayName(parking) == null
				|| (getDisplayName(parking) != null
						&& getDisplayName(parking).isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Parking existingParking = parkingRepository
				.findByFloorAndBlock(parking.getFloor(), parking.getBlock());
		if (existingParking != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Parking with name '" + getDisplayName(parking)
							+ "' already exists");
		}
		if (parking.getDisplayName() == null
				|| (parking.getDisplayName() != null
						&& parking.getDisplayName().isEmpty())) {
			parking.setDisplayName(getDisplayName(parking));
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.PARKING,
								getDisplayName(parking));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			parking.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		parking.setUpdateTimestamp(new Date());
		parking.setCreateTimestamp(new Date());
		parkingRepository.save(parking);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Parking>> getAllParkings(
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("floor").ascending());
		Page<Parking> parkings = parkingRepository.findAll(pageable);
		return ResponseEntity.ok(parkings);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveParkingImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		Parking existingParking = parkingRepository.getById(id);
		if (existingParking != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.PARKING,
									getDisplayName(existingParking));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingParking.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingParking.setUpdateTimestamp(new Date());
			parkingRepository.save(existingParking);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Parking> updateParking(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<Parking> parkingOptional = parkingRepository.findById(id);
		if (!parkingOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Parking parking = parkingOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = parking.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(parking, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (parking.getDisplayName() == null
				|| (parking.getDisplayName() != null
						&& parking.getDisplayName().isEmpty())) {
			parking.setDisplayName(getDisplayName(parking));
		}
		parking.setUpdateTimestamp(new Date());
		// Save updated parking
		parkingRepository.save(parking);
		return ResponseEntity.ok(parking);
	}

	private String getDisplayName(Parking parking) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(parking.getFloor());
		if (parking.getBlock() != null && !parking.getBlock().isEmpty()) {
			sb.append(parking.getBlock());
		}
		return sb.toString();
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteParking(@PathVariable Long id) {
		Optional<Parking> parkingOptional = parkingRepository.findById(id);
		if (!parkingOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		parkingRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<Parking>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(parkingRepository.findAll());
		}
		List<Parking> lst = new ArrayList<>();
		Optional<Parking> parkingOptional = parkingRepository
				.findById(id);
		if (parkingOptional.isPresent()) {
			lst.add(parkingOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
}
