package com.alpha.interview.wizard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.MallModel;

@Repository
public interface MallModelRepository extends JpaRepository<MallModel, Long> {
}
