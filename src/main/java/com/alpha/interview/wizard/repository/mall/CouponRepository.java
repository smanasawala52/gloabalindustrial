package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
	Page<Coupon> findAll(Pageable pageable);
	Page<Coupon> findAllByName(Pageable pageable, String name);
	Coupon findByName(String name);
}
