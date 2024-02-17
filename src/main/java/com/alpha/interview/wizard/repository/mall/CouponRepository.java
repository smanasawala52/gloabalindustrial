package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
	Page<Coupon> findAll(Pageable pageable);
	Page<Coupon> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM Coupon b WHERE LOWER(b.name) LIKE %:name%")
	Page<Coupon> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	Coupon findByName(String name);
}
