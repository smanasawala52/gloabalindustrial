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
import org.springframework.web.util.HtmlUtils;

import com.alpha.interview.wizard.constants.mall.constants.ImageTypeConstants;
import com.alpha.interview.wizard.controller.mall.util.MallUtil;
import com.alpha.interview.wizard.model.mall.Kiosk;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.KioskRepository;
import com.alpha.interview.wizard.repository.mall.ShopRepository;

@Controller
@RequestMapping("/kiosk")
public class KioskController {

	@Autowired
	private KioskRepository kioskRepository;

	@Autowired
	private ShopRepository shopRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public KioskController(ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/mallmodel/{id}")
	public String login(@PathVariable Long id, Model model) {
		model.addAttribute("contentTemplate", "kiosk");
		model.addAttribute("shop", shopRepository.getAllIdAndName());
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveKiosk(
			@ModelAttribute("kiosk") @Valid Kiosk kiosk,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || kiosk.getName() == null
				|| (kiosk.getName() != null && kiosk.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Kiosk existingKiosk = kioskRepository
				.findByName(MallUtil.formatName(kiosk.getName()));
		if (existingKiosk != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(
					"Kiosk with name '" + kiosk.getName() + "' already exists");
		}
		if (kiosk.getDisplayName() == null || (kiosk.getDisplayName() != null
				&& kiosk.getDisplayName().isEmpty())) {
			kiosk.setDisplayName(kiosk.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.KIOSK,
								kiosk.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			kiosk.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		kiosk.setUpdateTimestamp(new Date());
		kiosk.setCreateTimestamp(new Date());
		kioskRepository.save(kiosk);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Kiosk>> getAllKiosks(
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "name", name = "s", required = false) String sort,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp,
			@RequestParam(defaultValue = "0", name = "ps", required = false) int ps,
			@RequestParam(defaultValue = "", name = "ids", required = false) String ids) {
		if (cp <= 0) {
			cp = 0;
		}
		if (ps <= 0) {
			ps = PAGE_SIZE;
		}
		if (ps > PAGE_SIZE) {
			ps = PAGE_SIZE;
		}
		Pageable pageable = PageRequest.of(cp, ps, Sort.by(sort).ascending());
		Page<Kiosk> kiosks = null;
		if (ids != null && !ids.isBlank()) {
			List<Long> lstShopIds = MallUtil.convertToLongList(ids);
			kiosks = kioskRepository.findByIds(lstShopIds, pageable);
		} else if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			kiosks = kioskRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			kiosks = kioskRepository.findAll(pageable);
		}
		return ResponseEntity.ok(kiosks);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveKioskImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		Kiosk existingKiosk = kioskRepository.getById(id);
		if (existingKiosk != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.KIOSK,
									existingKiosk.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingKiosk.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingKiosk.setUpdateTimestamp(new Date());
			kioskRepository.save(existingKiosk);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Kiosk> updateKiosk(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<Kiosk> kioskOptional = kioskRepository.findById(id);
		if (!kioskOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Kiosk kiosk = kioskOptional.get();

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = kiosk.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(kiosk, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (kiosk.getDisplayName() == null || (kiosk.getDisplayName() != null
				&& kiosk.getDisplayName().isEmpty())) {
			kiosk.setDisplayName(kiosk.getName());
		}
		kiosk.setUpdateTimestamp(new Date());
		// Save updated kiosk
		kioskRepository.save(kiosk);
		return ResponseEntity.ok(kiosk);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteKiosk(@PathVariable Long id) {
		Optional<Kiosk> kioskOptional = kioskRepository.findById(id);
		if (!kioskOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		kioskRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	@GetMapping("/{id}")
	public ResponseEntity<List<Kiosk>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(kioskRepository.findAll());
		}
		List<Kiosk> lst = new ArrayList<>();
		Optional<Kiosk> kioskOptional = kioskRepository.findById(id);
		if (kioskOptional.isPresent()) {
			lst.add(kioskOptional.get());
		}
		return ResponseEntity.ok(lst);
	}

}
