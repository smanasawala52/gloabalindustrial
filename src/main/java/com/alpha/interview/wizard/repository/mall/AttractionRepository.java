package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Attraction;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
	Page<Attraction> findAll(Pageable pageable);
	Page<Attraction> findAllByName(Pageable pageable, String name);
	Attraction findByName(String name);
}
