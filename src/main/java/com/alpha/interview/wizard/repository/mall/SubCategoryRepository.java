package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.SubCategory;

@Repository
public interface SubCategoryRepository
		extends
			JpaRepository<SubCategory, Long> {
	Page<SubCategory> findAll(Pageable pageable);
	Page<SubCategory> findAllByName(Pageable pageable, String name);
	SubCategory findByName(String name);
}
