package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Page<Category> findAll(Pageable pageable);
	Page<Category> findAllByName(Pageable pageable, String name);
	Category findByName(String name);
}
