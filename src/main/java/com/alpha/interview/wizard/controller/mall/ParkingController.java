package com.alpha.interview.wizard.controller.mall;

import java.util.Date;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Parking existingParking = parkingRepository
				.findByFloorAndBlock(parking.getFloor(), parking.getBlock());
		if (existingParking != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Parking with floor and block '" + parking.getFloor()
							+ "_" + parking.getBlock() + "' already exists");
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.BRAND,
								parking.getFloor() + "_" + parking.getBlock());
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
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Parking> parkings = parkingRepository.findAll(pageable);
		return ResponseEntity.ok(parkings);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Parking> updateParking(@PathVariable Long id,
			@RequestParam(value = "imageFile", required = false) MultipartFile file,
			@RequestBody Parking updatedParking) {
		Optional<Parking> parkingOptional = parkingRepository.findById(id);
		if (!parkingOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Parking parking = parkingOptional.get();
		// Update fields
		// Upload new image if provided
		if (file != null && !file.isEmpty()) {
			try {
				try {
					String imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.BRAND,
									parking.getFloor() + "_"
											+ parking.getBlock());
					parking.setImgUrl(imageUrl);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(null);
			}
		}

		parking.setUpdateTimestamp(new Date());
		// Save updated parking
		parkingRepository.save(parking);
		return ResponseEntity.ok(parking);
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
}
