package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Attraction;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
	Page<Attraction> findAll(Pageable pageable);
	Page<Attraction> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM Attraction b WHERE LOWER(b.name) LIKE %:name%")
	Page<Attraction> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	Attraction findByName(String name);
	@Query("SELECT c.id, c.name FROM Attraction c")
	List<Object[]> getAllIdAndName();

	@Query("SELECT NEW Attraction(m.id, m.name) FROM Attraction m JOIN m.categories a WHERE a.id = :id")
	List<Attraction> findByCategoryId(@Param("id") Long id);

	@Query("SELECT NEW Attraction(m.id, m.name) FROM Attraction m JOIN m.brands a WHERE a.id = :id")
	List<Attraction> findByBrandId(@Param("id") Long id);

	@Query("SELECT NEW Attraction(m.id, m.name) FROM Attraction m JOIN m.coupons a WHERE a.id = :id")
	List<Attraction> findByCouponId(@Param("id") Long id);

	@Query("SELECT NEW Attraction(m.id, m.name) FROM Attraction m JOIN m.products a WHERE a.id = :id")
	List<Attraction> findByProductId(@Param("id") Long id);
}
