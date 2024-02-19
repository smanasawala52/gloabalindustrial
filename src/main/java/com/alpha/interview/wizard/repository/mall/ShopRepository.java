package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
	Page<Shop> findAll(Pageable pageable);
	Page<Shop> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM Shop b WHERE LOWER(b.name) LIKE %:name%")
	Page<Shop> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	Shop findByName(String name);
	@Query("SELECT c.id, c.name FROM Shop c")
	List<Object[]> getAllIdAndName();

	// @Query("SELECT DISTINCT s FROM Shop s JOIN s.categories c WHERE
	// s.mallModel.id = :mallId AND c.id = :categoryId")
	// List<Shop> findAllByMallIdAndCategoryId(@Param("mallId") Long mallId,
	// @Param("categoryId") Long categoryId);
	//
	// @Query("SELECT DISTINCT s FROM Shop s JOIN s.categories c JOIN
	// c.subCategories sc WHERE s.mallModel.id = :mallId AND sc.id =
	// :subCategoryId")
	// List<Shop> findAllByMallIdAndSubCategoryId(@Param("mallId") Long mallId,
	// @Param("subCategoryId") Long subCategoryId);
}
