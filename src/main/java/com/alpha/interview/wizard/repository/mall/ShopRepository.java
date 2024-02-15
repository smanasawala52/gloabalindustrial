package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
	Page<Shop> findAll(Pageable pageable);
	Shop findByName(String name);

}
