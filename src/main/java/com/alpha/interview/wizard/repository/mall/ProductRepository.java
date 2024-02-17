package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Page<Product> findAll(Pageable pageable);
	Page<Product> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM Product b WHERE LOWER(b.name) LIKE %:name%")
	Page<Product> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	Product findByName(String name);
	@Query("SELECT c.id, c.name FROM Product c")
	List<Object[]> getAllIdAndName();
}
