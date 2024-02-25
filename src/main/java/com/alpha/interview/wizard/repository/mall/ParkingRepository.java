package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Parking;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {
	Page<Parking> findAll(Pageable pageable);
	Page<Parking> findAllByFloorAndBlock(Pageable pageable, String floor,
			String Block);
	Page<Parking> findAllByFloor(Pageable pageable, String floor);
	Parking findByFloorAndBlock(String floor, String block);

	@Query("SELECT s FROM Parking s WHERE s.id IN :ids")
	Page<Parking> findByIds(@Param("ids") List<Long> ids, Pageable pageable);
}
