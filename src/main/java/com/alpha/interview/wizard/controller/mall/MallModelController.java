package com.alpha.interview.wizard.controller.mall;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import com.alpha.interview.wizard.model.mall.Attraction;
import com.alpha.interview.wizard.model.mall.Brand;
import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.Coupon;
import com.alpha.interview.wizard.model.mall.Event;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.model.mall.OtherAttraction;
import com.alpha.interview.wizard.model.mall.Parking;
import com.alpha.interview.wizard.model.mall.Shop;
import com.alpha.interview.wizard.model.mall.WebImage;
import com.alpha.interview.wizard.model.mall.util.ImageService;
import com.alpha.interview.wizard.model.mall.util.ImageServiceMapInitializer;
import com.alpha.interview.wizard.repository.mall.AttractionRepository;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.EventRepository;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.alpha.interview.wizard.repository.mall.OtherAttractionRepository;
import com.alpha.interview.wizard.repository.mall.ParkingRepository;
import com.alpha.interview.wizard.repository.mall.ShopRepository;
import com.alpha.interview.wizard.repository.mall.SubCategoryRepository;
import com.alpha.interview.wizard.repository.mall.WebImageRepository;
import com.alpha.interview.wizard.service.chat.ChatService;
import com.alpha.interview.wizard.service.chat.ChatServiceMapInitializer;
import com.alpha.interview.wizard.service.mall.MallModelService;

@Controller
@RequestMapping("/mallmodel")
public class MallModelController {

	@Autowired
	private MallModelRepository mallModelRepository;
	@Autowired
	private ShopRepository shopRepository;
	@Autowired
	private WebImageRepository webImageRepository;
	@Autowired
	private AttractionRepository attractionRepository;

	@Autowired
	private ParkingRepository parkingRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SubCategoryRepository subCategoryRepository;
	@Autowired
	private OtherAttractionRepository otherAttractionRepository;

	private int PAGE_SIZE = 20;
	@Value("${image.service.impl}")
	private String imageServiceImpl;
	@Value("${chat.service.impl}")
	private String chatServiceImpl;

	private final Map<String, ImageService> imageServiceMap;
	private final Map<String, ChatService> chatServiceMap;
	@Autowired
	public MallModelController(
			ImageServiceMapInitializer imageServiceMapInitializer,
			ChatServiceMapInitializer chatServiceMapInitializer) {
		this.chatServiceMap = chatServiceMapInitializer.getServiceMap();
		this.imageServiceMap = imageServiceMapInitializer.getServiceMap();
	}

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "mallModel");
		return "common";
	}
	@GetMapping("/{id}/page")
	public String mallModelPage(@PathVariable Long id, Model model) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			model.addAttribute("contentTemplate", "mallModel");
		} else {
			MallModel mallModel = mallModelOptional.get();
			model.addAttribute("contentTemplate", "mallModel-display");
			model.addAttribute("mallModel", mallModel);
			Set<Brand> brands = new HashSet<>();
			Set<Category> categories = new HashSet<>();
			Set<Coupon> coupons = new HashSet<>();
			if (mallModel.getShops() != null) {
				for (Shop shop : mallModel.getShops()) {
					brands.addAll(shop.getBrands());
					categories.addAll(shop.getCategories());
					coupons.addAll(shop.getCoupons());
				}
			}
			if (mallModel.getAttractions() != null) {
				for (Attraction shop : mallModel.getAttractions()) {
					brands.addAll(shop.getBrands());
					categories.addAll(shop.getCategories());
					coupons.addAll(shop.getCoupons());
				}
			}
			model.addAttribute("brands", brands);
			model.addAttribute("coupons", coupons);
			model.addAttribute("categories", categories);
		}
		return "common";
	}
	@GetMapping("/shop/{id}")
	public String loginShop(@PathVariable Long id, Model model) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			model.addAttribute("contentTemplate", "mallModel");
		} else {
			model.addAttribute("contentTemplate", "mallModel-shop-xref");
			model.addAttribute("mallModel", mallModelOptional.get());
		}
		return "common";
	}
	@GetMapping("/shop/all/{id}")
	public ResponseEntity<Page<Shop>> getAllShops(@PathVariable Long id,
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Shop> shops = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			shops = shopRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			shops = shopRepository.findAll(pageable);
		}
		if (shops.hasContent()) {
			Optional<MallModel> mallModelOptional = mallModelRepository
					.findById(id);
			if (mallModelOptional.isPresent()
					&& mallModelOptional.get().getShops() != null) {
				Set<Long> shopIds = mallModelOptional.get().getShops().stream()
						.map(Shop::getId).collect(Collectors.toSet());

				shops.getContent().forEach(shop -> {
					if (shopIds.contains(shop.getId())) {
						shop.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<Shop> mutableList = new ArrayList<>(shops.getContent());
				mutableList
						.sort(Comparator
								.comparing(Shop::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<Shop> sortedPage = new PageImpl<>(mutableList,
						shops.getPageable(), shops.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(shops);
	}

	@PostMapping("/{mallModelId}/shop/{shopId}")
	public ResponseEntity<String> updateShop(@PathVariable Long mallModelId,
			@PathVariable Long shopId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<Shop> shopOptional = shopRepository.findById(shopId);
			if (shopOptional.isPresent()) {
				mallModelOptional.get().getShops().add(shopOptional.get());
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("Shop linked successfully to mallModel");
	}

	@DeleteMapping("/{mallModelId}/shop/{shopId}")
	public ResponseEntity<String> removeShop(@PathVariable Long mallModelId,
			@PathVariable Long shopId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<Shop> shopOptional = shopRepository.findById(shopId);
			if (shopOptional.isPresent()) {
				Long shopIdToRemove = shopOptional.get().getId();
				mallModelOptional.get().getShops()
						.removeIf(shop -> shop.getId().equals(shopIdToRemove));
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("Shop removed from mallModel");
	}
	@GetMapping("/webImage/{id}")
	public String loginWebImage(@PathVariable Long id, Model model) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			model.addAttribute("contentTemplate", "mallModel");
		} else {
			model.addAttribute("contentTemplate", "mallModel-webImage-xref");
			model.addAttribute("mallModel", mallModelOptional.get());
		}
		return "common";
	}
	@GetMapping("/webImage/all/{id}")
	public ResponseEntity<Page<WebImage>> getAllImages(@PathVariable Long id,
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<WebImage> webImages = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			webImages = webImageRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			webImages = webImageRepository.findAll(pageable);
		}
		if (webImages.hasContent()) {
			Optional<MallModel> mallModelOptional = mallModelRepository
					.findById(id);
			if (mallModelOptional.isPresent()
					&& mallModelOptional.get().getImages() != null) {
				Set<Long> webImageIds = mallModelOptional.get().getImages()
						.stream().map(WebImage::getId)
						.collect(Collectors.toSet());

				webImages.getContent().forEach(webImage -> {
					if (webImageIds.contains(webImage.getId())) {
						webImage.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<WebImage> mutableList = new ArrayList<>(
						webImages.getContent());
				mutableList
						.sort(Comparator
								.comparing(WebImage::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<WebImage> sortedPage = new PageImpl<>(mutableList,
						webImages.getPageable(), webImages.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(webImages);
	}

	@PostMapping("/{mallModelId}/webImage/{webImageId}")
	public ResponseEntity<String> updateWebImage(@PathVariable Long mallModelId,
			@PathVariable Long webImageId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<WebImage> webImageOptional = webImageRepository
					.findById(webImageId);
			if (webImageOptional.isPresent()) {
				mallModelOptional.get().getImages().add(webImageOptional.get());
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("WebImage linked successfully to mallModel");
	}

	@DeleteMapping("/{mallModelId}/webImage/{webImageId}")
	public ResponseEntity<String> removeWebImage(@PathVariable Long mallModelId,
			@PathVariable Long webImageId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<WebImage> webImageOptional = webImageRepository
					.findById(webImageId);
			if (webImageOptional.isPresent()) {
				Long webImageIdToRemove = webImageOptional.get().getId();
				mallModelOptional.get().getImages()
						.removeIf(webImage -> webImage.getId()
								.equals(webImageIdToRemove));
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("WebImage removed from mallModel");
	}
	@GetMapping("/attraction/{id}")
	public String loginAttraction(@PathVariable Long id, Model model) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			model.addAttribute("contentTemplate", "mallModel");
		} else {
			model.addAttribute("contentTemplate", "mallModel-attraction-xref");
			model.addAttribute("mallModel", mallModelOptional.get());
		}
		return "common";
	}
	@GetMapping("/attraction/all/{id}")
	public ResponseEntity<Page<Attraction>> getAllAttractions(
			@PathVariable Long id,
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<Attraction> attractions = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			attractions = attractionRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			attractions = attractionRepository.findAll(pageable);
		}
		if (attractions.hasContent()) {
			Optional<MallModel> mallModelOptional = mallModelRepository
					.findById(id);
			if (mallModelOptional.isPresent()
					&& mallModelOptional.get().getAttractions() != null) {
				Set<Long> attractionIds = mallModelOptional.get()
						.getAttractions().stream().map(Attraction::getId)
						.collect(Collectors.toSet());

				attractions.getContent().forEach(attraction -> {
					if (attractionIds.contains(attraction.getId())) {
						attraction.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<Attraction> mutableList = new ArrayList<>(
						attractions.getContent());
				mutableList
						.sort(Comparator
								.comparing(Attraction::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<Attraction> sortedPage = new PageImpl<>(mutableList,
						attractions.getPageable(),
						attractions.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(attractions);
	}

	@PostMapping("/{mallModelId}/attraction/{attractionId}")
	public ResponseEntity<String> updateAttraction(
			@PathVariable Long mallModelId, @PathVariable Long attractionId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<Attraction> attractionOptional = attractionRepository
					.findById(attractionId);
			if (attractionOptional.isPresent()) {
				mallModelOptional.get().getAttractions()
						.add(attractionOptional.get());
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("Attraction linked successfully to mallModel");
	}

	@DeleteMapping("/{mallModelId}/attraction/{attractionId}")
	public ResponseEntity<String> removeAttraction(
			@PathVariable Long mallModelId, @PathVariable Long attractionId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<Attraction> attractionOptional = attractionRepository
					.findById(attractionId);
			if (attractionOptional.isPresent()) {
				Long attractionIdToRemove = attractionOptional.get().getId();
				mallModelOptional.get().getAttractions()
						.removeIf(attraction -> attraction.getId()
								.equals(attractionIdToRemove));
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("Attraction removed from mallModel");
	}
	@GetMapping("/parking/{id}")
	public String loginParking(@PathVariable Long id, Model model) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			model.addAttribute("contentTemplate", "mallModel");
		} else {
			model.addAttribute("contentTemplate", "mallModel-parking-xref");
			model.addAttribute("mallModel", mallModelOptional.get());
		}
		return "common";
	}
	@GetMapping("/parking/all/{id}")
	public ResponseEntity<Page<Parking>> getAllParkings(@PathVariable Long id,
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("floor").ascending());
		Page<Parking> parkings = parkingRepository.findAll(pageable);
		if (parkings.hasContent()) {
			Optional<MallModel> mallModelOptional = mallModelRepository
					.findById(id);
			if (mallModelOptional.isPresent()
					&& mallModelOptional.get().getParkings() != null) {
				Set<Long> parkingIds = mallModelOptional.get().getParkings()
						.stream().map(Parking::getId)
						.collect(Collectors.toSet());

				parkings.getContent().forEach(parking -> {
					if (parkingIds.contains(parking.getId())) {
						parking.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<Parking> mutableList = new ArrayList<>(
						parkings.getContent());
				mutableList
						.sort(Comparator
								.comparing(Parking::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<Parking> sortedPage = new PageImpl<>(mutableList,
						parkings.getPageable(), parkings.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(parkings);
	}

	@PostMapping("/{mallModelId}/parking/{parkingId}")
	public ResponseEntity<String> updateParking(@PathVariable Long mallModelId,
			@PathVariable Long parkingId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<Parking> parkingOptional = parkingRepository
					.findById(parkingId);
			if (parkingOptional.isPresent()) {
				mallModelOptional.get().getParkings()
						.add(parkingOptional.get());
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("Parking linked successfully to mallModel");
	}

	@DeleteMapping("/{mallModelId}/parking/{parkingId}")
	public ResponseEntity<String> removeParking(@PathVariable Long mallModelId,
			@PathVariable Long parkingId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<Parking> parkingOptional = parkingRepository
					.findById(parkingId);
			if (parkingOptional.isPresent()) {
				Long parkingIdToRemove = parkingOptional.get().getId();
				mallModelOptional.get().getParkings().removeIf(
						parking -> parking.getId().equals(parkingIdToRemove));
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("Parking removed from mallModel");
	}
	@GetMapping("/event/{id}")
	public String loginEvent(@PathVariable Long id, Model model) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			model.addAttribute("contentTemplate", "mallModel");
		} else {
			model.addAttribute("contentTemplate", "mallModel-event-xref");
			model.addAttribute("mallModel", mallModelOptional.get());
		}
		return "common";
	}
	@GetMapping("/event/all/{id}")
	public ResponseEntity<Page<Event>> getAllEvents(@PathVariable Long id,
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
		if (events.hasContent()) {
			Optional<MallModel> mallModelOptional = mallModelRepository
					.findById(id);
			if (mallModelOptional.isPresent()
					&& mallModelOptional.get().getEvents() != null) {
				Set<Long> eventIds = mallModelOptional.get().getEvents()
						.stream().map(Event::getId).collect(Collectors.toSet());

				events.getContent().forEach(event -> {
					if (eventIds.contains(event.getId())) {
						event.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<Event> mutableList = new ArrayList<>(events.getContent());
				mutableList
						.sort(Comparator
								.comparing(Event::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<Event> sortedPage = new PageImpl<>(mutableList,
						events.getPageable(), events.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(events);
	}

	@PostMapping("/{mallModelId}/event/{eventId}")
	public ResponseEntity<String> updateEvent(@PathVariable Long mallModelId,
			@PathVariable Long eventId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<Event> eventOptional = eventRepository.findById(eventId);
			if (eventOptional.isPresent()) {
				mallModelOptional.get().getEvents().add(eventOptional.get());
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("Event linked successfully to mallModel");
	}

	@DeleteMapping("/{mallModelId}/event/{eventId}")
	public ResponseEntity<String> removeEvent(@PathVariable Long mallModelId,
			@PathVariable Long eventId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<Event> eventOptional = eventRepository.findById(eventId);
			if (eventOptional.isPresent()) {
				Long eventIdToRemove = eventOptional.get().getId();
				mallModelOptional.get().getEvents().removeIf(
						event -> event.getId().equals(eventIdToRemove));
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("Event removed from mallModel");
	}
	@GetMapping("/otherAttraction/{id}")
	public String loginOtherAttraction(@PathVariable Long id, Model model) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			model.addAttribute("contentTemplate", "mallModel");
		} else {
			model.addAttribute("contentTemplate",
					"mallModel-otherAttraction-xref");
			model.addAttribute("mallModel", mallModelOptional.get());
		}
		return "common";
	}
	@GetMapping("/otherAttraction/all/{id}")
	public ResponseEntity<Page<OtherAttraction>> getAllOtherAttractions(
			@PathVariable Long id,
			@RequestParam(defaultValue = "", name = "name", required = false) String name,
			@RequestParam(defaultValue = "0", name = "cp", required = false) int cp) {
		if (cp <= 0) {
			cp = 0;
		}
		Pageable pageable = PageRequest.of(cp, PAGE_SIZE,
				Sort.by("name").ascending());
		Page<OtherAttraction> otherAttractions = null;
		if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			otherAttractions = otherAttractionRepository
					.findAllByNameContaining(escapedName.toLowerCase(),
							pageable);
		} else {
			otherAttractions = otherAttractionRepository.findAll(pageable);
		}
		if (otherAttractions.hasContent()) {
			Optional<MallModel> mallModelOptional = mallModelRepository
					.findById(id);
			if (mallModelOptional.isPresent()
					&& mallModelOptional.get().getOtherAttractions() != null) {
				Set<Long> otherAttractionIds = mallModelOptional.get()
						.getOtherAttractions().stream()
						.map(OtherAttraction::getId)
						.collect(Collectors.toSet());

				otherAttractions.getContent().forEach(otherAttraction -> {
					if (otherAttractionIds.contains(otherAttraction.getId())) {
						otherAttraction.setLinked(true);
					}
				});

				// Sort the content of the page by the "linked" attribute
				// Copy the content to a mutable list and sort it
				List<OtherAttraction> mutableList = new ArrayList<>(
						otherAttractions.getContent());
				mutableList
						.sort(Comparator
								.comparing(OtherAttraction::isLinked,
										Comparator.nullsLast(
												Comparator.naturalOrder()))
								.reversed());

				// Create a new Page object with the sorted list and existing
				// pagination information
				Page<OtherAttraction> sortedPage = new PageImpl<>(mutableList,
						otherAttractions.getPageable(),
						otherAttractions.getTotalElements());
				return ResponseEntity.ok(sortedPage);
			}
		}
		return ResponseEntity.ok(otherAttractions);
	}

	@PostMapping("/{mallModelId}/otherAttraction/{otherAttractionId}")
	public ResponseEntity<String> updateOtherAttraction(
			@PathVariable Long mallModelId,
			@PathVariable Long otherAttractionId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<OtherAttraction> otherAttractionOptional = otherAttractionRepository
					.findById(otherAttractionId);
			if (otherAttractionOptional.isPresent()) {
				mallModelOptional.get().getOtherAttractions()
						.add(otherAttractionOptional.get());
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity
				.ok("OtherAttraction linked successfully to mallModel");
	}

	@DeleteMapping("/{mallModelId}/otherAttraction/{otherAttractionId}")
	public ResponseEntity<String> removeOtherAttraction(
			@PathVariable Long mallModelId,
			@PathVariable Long otherAttractionId) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallModelId);
		if (mallModelOptional.isPresent()) {
			Optional<OtherAttraction> otherAttractionOptional = otherAttractionRepository
					.findById(otherAttractionId);
			if (otherAttractionOptional.isPresent()) {
				Long otherAttractionIdToRemove = otherAttractionOptional.get()
						.getId();
				mallModelOptional.get().getOtherAttractions()
						.removeIf(otherAttraction -> otherAttraction.getId()
								.equals(otherAttractionIdToRemove));
				mallModelRepository.save(mallModelOptional.get());
				mallModelRepository.flush();
			}
		}
		return ResponseEntity.ok("OtherAttraction removed from mallModel");
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveMallModel(
			@ModelAttribute("mallModel") @Valid MallModel mallModel,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors() || mallModel.getName() == null
				|| (mallModel.getName() != null
						&& mallModel.getName().isEmpty())) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		MallModel existingMallModel = mallModelRepository
				.findByName(MallUtil.formatName(mallModel.getName()));
		if (existingMallModel != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("MallModel with name '" + mallModel.getName()
							+ "' already exists");
		}
		if (mallModel.getDisplayName() == null
				|| (mallModel.getDisplayName() != null
						&& mallModel.getDisplayName().isEmpty())) {
			mallModel.setDisplayName(mallModel.getName());
		}
		try {
			// Process image upload
			String imageUrl = null;
			try {
				imageUrl = imageServiceMap.get(imageServiceImpl)
						.uploadImageFile(file, ImageTypeConstants.MALL_MODEL,
								mallModel.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mallModel.setImgUrl(imageUrl);
		} catch (Exception e) {
			// Handle file upload error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while uploading image");
		}

		mallModel.setUpdateTimestamp(new Date());
		mallModel.setCreateTimestamp(new Date());
		mallModelRepository.save(mallModel);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<Page<MallModel>> getAllMallModels(
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
		Page<MallModel> mallModels = null;
		if (ids != null && !ids.isBlank()) {
			List<Long> lstShopIds = MallUtil.convertToLongList(ids);
			mallModels = mallModelRepository.findByIds(lstShopIds, pageable);
		} else if (name != null && !name.isEmpty()) {
			String escapedName = HtmlUtils.htmlEscape(name);
			mallModels = mallModelRepository.findAllByNameContaining(
					escapedName.toLowerCase(), pageable);
		} else {
			mallModels = mallModelRepository.findAll(pageable);
		}
		return ResponseEntity.ok(mallModels);
	}

	@PostMapping("/save/{id}")
	public ResponseEntity<?> saveMallModelImage(@PathVariable Long id,
			@RequestParam("imageFile") MultipartFile file) {
		MallModel existingMallModel = mallModelRepository.getById(id);
		if (existingMallModel != null) {
			try {
				// Process image upload
				String imageUrl = null;
				try {
					imageUrl = imageServiceMap.get(imageServiceImpl)
							.uploadImageFile(file,
									ImageTypeConstants.MALL_MODEL,
									existingMallModel.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				existingMallModel.setImgUrl(imageUrl);
			} catch (Exception e) {
				// Handle file upload error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while uploading image");
			}

			existingMallModel.setUpdateTimestamp(new Date());
			mallModelRepository.save(existingMallModel);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<MallModel> updateMallModel(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		MallModel mallModel = mallModelOptional.get();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// Update fields using reflection
		updates.forEach((key, value) -> {
			try {
				Field field = mallModel.getClass().getDeclaredField(key);
				field.setAccessible(true);
				if (key.equals("startDate") || key.equals("endDate")) {
					Date date = null;
					try {
						date = dateFormat.parse((String) value);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					field.set(mallModel, date);
				} else if (field.getType().equals(int.class)
						&& value instanceof String) {
					int intValue = Integer.parseInt((String) value);
					field.set(mallModel, intValue);
				} else {
					field.set(mallModel, value);
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace(); // Handle exception properly
			}
		});
		if (mallModel.getDisplayName() == null
				|| (mallModel.getDisplayName() != null
						&& mallModel.getDisplayName().isEmpty())) {
			mallModel.setDisplayName(mallModel.getName());
		}
		mallModel.setUpdateTimestamp(new Date());
		// Save updated mallModel
		mallModelRepository.save(mallModel);
		return ResponseEntity.ok(mallModel);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteMallModel(@PathVariable Long id) {
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (!mallModelOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		mallModelRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<MallModel>> getAllById(@PathVariable Long id) {
		if (id <= 0) {
			return ResponseEntity.ok(mallModelRepository.findAll());
		}
		List<MallModel> lst = new ArrayList<>();
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(id);
		if (mallModelOptional.isPresent()) {
			lst.add(mallModelOptional.get());
		}
		return ResponseEntity.ok(lst);
	}
	// @GetMapping("/{mallId}/categories")
	// public ResponseEntity<List<Category>> getAllCategoriesByMallId(
	// @PathVariable Long mallId) {
	// List<Category> categories = categoryRepository.findAllByMallId(mallId);
	// return new ResponseEntity<>(categories, HttpStatus.OK);
	// }
	// @GetMapping("/{mallId}/categories/{categoryId}")
	// public ResponseEntity<Category> getAllSubCategoriesByMallIdAndCategoryId(
	// @PathVariable Long mallId, @PathVariable Long categoryId) {
	// Optional<Category> category = categoryRepository.findById(categoryId);
	// return new ResponseEntity<>(category.get(), HttpStatus.OK);
	// }

	@Autowired
	private MallModelService mallmodelService;

	@GetMapping("/{id}/loadAI")
	public String initializeloadAI(Model model, @PathVariable Long id) {
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("mallId", String.valueOf(id));
		mallmodelService.initializeChat(queryParams);
		model.addAttribute("contentTemplate", "mallModel");
		return "common";
	}
	@GetMapping("/{id}/chat")
	public String initializeChat(Model model, @PathVariable Long id) {
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("mallId", String.valueOf(id));
		model.addAttribute("mallModels",
				mallModelRepository.getAllIdAndDisplayName());
		model.addAttribute("contentTemplate", "mallModel-chat");
		return "common";
	}
}
