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
import com.alpha.interview.wizard.model.mall.Event;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.EventRepository;

@Controller
@RequestMapping("/event")
public class EventController {

	@Autowired
	private EventRepository eventRepository;
	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	@Autowired
	public EventController(ImageServiceMapInitializer serviceMapInitializer) {
		this.imageServiceMap = serviceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "event");
		return "common";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveEvent(
			@ModelAttribute("event") @Valid Event event,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || event.getName() == null
				|| (event.getName() != null && event.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		Event existingEvent = eventRepository
				.findByName(MallUtil.formatName(event.getName()));
		if (existingEvent != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(
					"Event with name '" + event.getName() + "' already exists");
		}
		if (event.getDisplayName() == null || (event.getDisplayName() != null
				&& event.getDisplayName().isEmpty())) {
			event.setDisplayName(event.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.EVENT,
								event.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			event.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		event.setUpdateTimestamp(new Date());
		event.setCreateTimestamp(new Date());
		eventRepository.save(event);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Event>> getAllEvents(
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Event> events = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			events = eventRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			events = eventRepository.findAll(pageable);
		}
		return ResponseEntity.ok(events);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveEventImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		Event existingEvent = eventRepository.getById(id);
		if (existingEvent != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file, ImageTypeConstants.EVENT,
									existingEvent.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingEvent.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingEvent.setUpdateTimestamp(new Date());
			eventRepository.save(existingEvent);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Event> updateEvent(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<Event> eventOptional = eventRepository.findById(id);
		if (!eventOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Event event = eventOptional.get();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = event.getClass().getDeclaredField(key);
				field.setAccessible(true);
				if (key.equals("startDate") || key.equals("endDate")) {
					Date date = null;
					try {
						date = dateFormat.parse((String) value);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					field.set(event, date);
				} else {
					field.set(event, value);
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (event.getDisplayName() == null || (event.getDisplayName() != null
				&& event.getDisplayName().isEmpty())) {
			event.setDisplayName(event.getName());
		}
		event.setUpdateTimestamp(new Date());
		// Save updated event
		eventRepository.save(event);
		return ResponseEntity.ok(event);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
		Optional<Event> eventOptional = eventRepository.findById(id);
		if (!eventOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		eventRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<Event>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(eventRepository.findAll());
		}
		List<Event> lst = new ArrayList<>();
		Optional<Event> eventOptional = eventRepository.findById(id);
		if (eventOptional.isPresent()) {
			lst.add(eventOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
}
