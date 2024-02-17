package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.MallModel;

@Repository
public interface MallModelRepository extends JpaRepository<MallModel, Long> {
	Page<MallModel> findAll(Pageable pageable);
	Page<MallModel> findAllByName(Pageable pageable, String name);
	MallModel findByName(String name);

}
