package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
