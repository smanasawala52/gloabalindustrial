package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Kiosk;

@Repository
public interface KioskRepository extends JpaRepository<Kiosk, Long> {
	Page<Kiosk> findAll(Pageable pageable);
	Page<Kiosk> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM Kiosk b WHERE LOWER(b.name) LIKE %:name%")
	Page<Kiosk> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	Kiosk findByName(String name);
	@Query("SELECT NEW Kiosk(c.id, c.name) FROM Kiosk c")
	List<Kiosk> getAllIdAndName();

	@Query("SELECT s FROM Kiosk s WHERE s.id IN :ids")
	Page<Kiosk> findByIds(@Param("ids") List<Long> ids, Pageable pageable);
}
