package com.alpha.interview.wizard.controller.mall;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.model.mall.OtherAttraction;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.alpha.interview.wizard.repository.mall.OtherAttractionRepository;

@Controller
@RequestMapping("/otherattraction")
public class OtherAttractionController {

	@Autowired
	private OtherAttractionRepository otherAttractionRepository;
	@Autowired
	private MallModelRepository mallModelRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public OtherAttractionController(
			ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "otherAttraction");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveOtherAttraction(
			@ModelAttribute("otherAttraction") @Valid OtherAttraction otherAttraction,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || otherAttraction.getName() == null
				|| (otherAttraction.getName() != null
						&& otherAttraction.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		OtherAttraction existingOtherAttraction = otherAttractionRepository
				.findByName(MallUtil.formatName(otherAttraction.getName()));
		if (existingOtherAttraction != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("OtherAttraction with name '"
							+ otherAttraction.getName() + "' already exists");
		}
		if (otherAttraction.getDisplayName() == null
				|| (otherAttraction.getDisplayName() != null
						&& otherAttraction.getDisplayName().isEmpty())) {
			otherAttraction.setDisplayName(otherAttraction.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file,
								ImageTypeConstants.OTHER_ATTRACTION,
								otherAttraction.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			otherAttraction.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		otherAttraction.setUpdateTimestamp(new Date());
		otherAttraction.setCreateTimestamp(new Date());
		otherAttractionRepository.save(otherAttraction);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<OtherAttraction>> getAllOtherAttractions(
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
		Page<OtherAttraction> otherAttractions = null;
		if (ids != null && !ids.isBlank()) {
			List<Long> lstShopIds = MallUtil.convertToLongList(ids);
			otherAttractions = otherAttractionRepository.findByIds(lstShopIds,
					pageable);
		} else if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			otherAttractions = otherAttractionRepository
					.findAllByNameContaining(escapedName.toLowerCase(),
							pageable);
		} else {
			otherAttractions = otherAttractionRepository.findAll(pageable);
		}
		return ResponseEntity.ok(otherAttractions);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveOtherAttractionImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		OtherAttraction existingOtherAttraction = otherAttractionRepository
				.getById(id);
		if (existingOtherAttraction != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file,
									ImageTypeConstants.OTHER_ATTRACTION,
									existingOtherAttraction.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingOtherAttraction.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingOtherAttraction.setUpdateTimestamp(new Date());
			otherAttractionRepository.save(existingOtherAttraction);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<OtherAttraction> updateOtherAttraction(
			@PathVariable Long id, @RequestBody Map<String, Object> updates) {
		Optional<OtherAttraction> otherAttractionOptional = otherAttractionRepository
				.findById(id);
		if (!otherAttractionOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		OtherAttraction otherAttraction = otherAttractionOptional.get();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = otherAttraction.getClass().getDeclaredField(key);
				field.setAccessible(true);
				if (key.equals("startDate") || key.equals("endDate")) {
					Date date = null;
					try {
						date = dateFormat.parse((String) value);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					field.set(otherAttraction, date);
				} else {
					field.set(otherAttraction, value);
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (otherAttraction.getDisplayName() == null
				|| (otherAttraction.getDisplayName() != null
						&& otherAttraction.getDisplayName().isEmpty())) {
			otherAttraction.setDisplayName(otherAttraction.getName());
		}
		otherAttraction.setUpdateTimestamp(new Date());
		// Save updated otherAttraction
		otherAttractionRepository.save(otherAttraction);
		return ResponseEntity.ok(otherAttraction);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteOtherAttraction(@PathVariable Long id) {
		Optional<OtherAttraction> otherAttractionOptional = otherAttractionRepository
				.findById(id);
		if (!otherAttractionOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		List<MallModel> mallModels = mallModelRepository
				.findByOtherAttractionId(id);
		if (mallModels != null && !mallModels.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(mallModels);
		}
		otherAttractionRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<OtherAttraction>> getAllById(
			@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(otherAttractionRepository.findAll());
		}
		List<OtherAttraction> lst = new ArrayList<>();
		Optional<OtherAttraction> otherAttractionOptional = otherAttractionRepository
				.findById(id);
		if (otherAttractionOptional.isPresent()) {
			lst.add(otherAttractionOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
}
