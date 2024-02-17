package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.SubCategory;

@Repository
public interface SubCategoryRepository
		extends
			JpaRepository<SubCategory, Long> {
	Page<SubCategory> findAll(Pageable pageable);
	Page<SubCategory> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM SubCategory b WHERE LOWER(b.name) LIKE %:name%")
	Page<SubCategory> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	SubCategory findByName(String name);
	@Query("SELECT c.id, c.name FROM SubCategory c")
	List<Object[]> getAllIdAndName();
}
