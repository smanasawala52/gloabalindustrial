package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Parking;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
