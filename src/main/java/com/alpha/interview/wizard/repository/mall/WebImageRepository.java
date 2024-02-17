package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.WebImage;

@Repository
public interface WebImageRepository extends JpaRepository<WebImage, Long> {
	Page<WebImage> findAll(Pageable pageable);
	Page<WebImage> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM WebImage b WHERE LOWER(b.name) LIKE %:name%")
	Page<WebImage> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	WebImage findByName(String name);
	@Query("SELECT c.id, c.name FROM WebImage c")
	List<Object[]> getAllIdAndName();
}
