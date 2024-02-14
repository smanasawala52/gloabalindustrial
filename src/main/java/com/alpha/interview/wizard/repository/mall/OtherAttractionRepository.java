package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.OtherAttraction;

@Repository
public interface OtherAttractionRepository
		extends
			JpaRepository<OtherAttraction, Long> {
}
