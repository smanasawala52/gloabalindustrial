package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Page<Product> findAll(Pageable pageable);
	Page<Product> findAllByName(Pageable pageable, String name);
	Product findByName(String name);

}
