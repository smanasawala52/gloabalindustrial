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
	@Query("SELECT NEW Category(c.id, c.name) FROM Category c")
	List<Category> getAllIdAndName();
	// @Query("SELECT c FROM Category c JOIN ShopCategory s ON c.id =
	// s.category.id JOIN MallModelShop ms ON s.shop.id = ms.shop.id WHERE
	// ms.mallModel.id = :mallId")
	// List<Category> findAllByMallId(@Param("mallId") Long mallId);

	@Query("SELECT NEW Category(m.id, m.name) FROM Category m JOIN m.subCategories a WHERE a.id = :id")
	List<Category> findBySubCategoryId(@Param("id") Long id);

	@Query("SELECT s FROM Category s WHERE s.id IN :ids")
	Page<Category> findByIds(@Param("ids") List<Long> ids, Pageable pageable);
}
