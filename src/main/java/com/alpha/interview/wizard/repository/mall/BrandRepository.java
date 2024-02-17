package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
	Page<Brand> findAll(Pageable pageable);
	Page<Brand> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE %:name%")
	Page<Brand> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	Brand findByName(String name);
	@Query("SELECT c.id, c.name FROM Brand c")
	List<Object[]> getAllIdAndName();
}
