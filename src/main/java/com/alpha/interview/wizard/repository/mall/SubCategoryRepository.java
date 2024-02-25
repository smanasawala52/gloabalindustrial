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
	@Query("SELECT NEW SubCategory(c.id, c.name) FROM SubCategory c")
	List<SubCategory> getAllIdAndName();
	// @Query("SELECT sc FROM SubCategory sc JOIN CategorySubCategory c ON sc.id
	// = c.subCategory.id JOIN ShopCategory s ON c.category.id = s.category.id
	// JOIN MallModelShop ms ON s.shop.id = ms.shop.id WHERE ms.mallModel.id =
	// :mallId")
	// List<SubCategory> findAllByMallId(@Param("mallId") Long mallId);
	// @Query("SELECT sc FROM SubCategory sc JOIN CategorySubCategory c ON sc.id
	// = c.subCategory.id JOIN ShopCategory s ON c.category.id = s.category.id
	// JOIN MallModelShop ms ON s.shop.id = ms.shop.id WHERE ms.mallModel.id =
	// :mallId AND s.category.id = :categoryId")
	// List<SubCategory> findAllByMallIdAndCategoryId(@Param("mallId") Long
	// mallId,
	// @Param("categoryId") Long categoryId);

}
