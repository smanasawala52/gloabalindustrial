package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Page<Category> findAll(Pageable pageable);
	Page<Category> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM Category b WHERE LOWER(b.name) LIKE %:name%")
	Page<Category> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	Category findByName(String name);
	@Query("SELECT c.id, c.name FROM Category c")
	List<Object[]> getAllIdAndName();
}
