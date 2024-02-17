package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.OtherAttraction;

@Repository
public interface OtherAttractionRepository
		extends
			JpaRepository<OtherAttraction, Long> {
	Page<OtherAttraction> findAll(Pageable pageable);
	Page<OtherAttraction> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM OtherAttraction b WHERE LOWER(b.name) LIKE %:name%")
	Page<OtherAttraction> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	OtherAttraction findByName(String name);

}
