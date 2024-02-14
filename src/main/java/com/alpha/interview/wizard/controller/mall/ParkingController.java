package com.alpha.interview.wizard.controller.mall;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alpha.interview.wizard.model.mall.Parking;
import com.alpha.interview.wizard.repository.mall.ParkingRepository;

@Controller
@RequestMapping("/parking")
public class ParkingController {

	private final ParkingRepository parkingRepository;

	@Autowired
	public ParkingController(ParkingRepository parkingRepository) {
		this.parkingRepository = parkingRepository;
	}

	@GetMapping("/form")
	public String showForm() {
		return "parking-form";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveParking(
			@ModelAttribute("parking") @Valid Parking parking,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		parking.setUpdateTimestamp(new Date());
		parkingRepository.save(parking);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<List<Parking>> getAllParkings() {
		List<Parking> parkings = parkingRepository.findAll();
		return ResponseEntity.ok(parkings);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Parking> updateParking(@PathVariable Long id,
			@RequestBody Parking updatedParking) {
		Optional<Parking> parkingOptional = parkingRepository.findById(id);
		if (!parkingOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Parking parking = parkingOptional.get();
		// Update fields
		parking.setFloor(updatedParking.getFloor());
		parking.setBlock(updatedParking.getBlock());
		parking.setAdditionalDetails(updatedParking.getAdditionalDetails());
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
