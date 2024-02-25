package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	Page<Event> findAll(Pageable pageable);
	Page<Event> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM Event b WHERE LOWER(b.name) LIKE %:name%")
	Page<Event> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	Event findByName(String name);
	@Query("SELECT NEW Event(c.id, c.name) FROM Event c")
	List<Event> getAllIdAndName();

}
