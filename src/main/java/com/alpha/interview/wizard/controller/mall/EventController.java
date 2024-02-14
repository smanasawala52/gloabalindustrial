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

import com.alpha.interview.wizard.model.mall.Event;
import com.alpha.interview.wizard.repository.mall.EventRepository;

@Controller
@RequestMapping("/event")
public class EventController {

	private final EventRepository eventRepository;

	@Autowired
	public EventController(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@GetMapping("/form")
	public String showForm() {
		return "event-form";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveEvent(
			@ModelAttribute("event") @Valid Event event,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(),
					HttpStatus.BAD_REQUEST);
		}
		event.setUpdateTimestamp(new Date());
		eventRepository.save(event);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<List<Event>> getAllEvents() {
		List<Event> events = eventRepository.findAll();
		return ResponseEntity.ok(events);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Event> updateEvent(@PathVariable Long id,
			@RequestBody Event updatedEvent) {
		Optional<Event> eventOptional = eventRepository.findById(id);
		if (!eventOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Event event = eventOptional.get();
		// Update fields
		event.setName(updatedEvent.getName());
		event.setDescription(updatedEvent.getDescription());
		event.setImgUrl(updatedEvent.getImgUrl());
		event.setAdditionalDetails(updatedEvent.getAdditionalDetails());
		event.setStartDate(updatedEvent.getStartDate());
		event.setEndDate(updatedEvent.getEndDate());
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
}
