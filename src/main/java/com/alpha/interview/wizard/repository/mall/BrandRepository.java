package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
	Page<Brand> findAll(Pageable pageable);

	Brand findByName(String name);
}
