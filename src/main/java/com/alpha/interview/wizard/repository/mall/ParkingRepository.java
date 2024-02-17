package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Parking;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {
	Page<Parking> findAll(Pageable pageable);
	Page<Parking> findAllByFloorAndBlock(Pageable pageable, String floor,
			String Block);
	Parking findByFloorAndBlock(String floor, String block);

}
