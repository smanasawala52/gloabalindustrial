package com.alpha.interview.wizard.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.MallModel;

@Repository
public interface MallModelRepository extends JpaRepository<MallModel, Long> {
	Page<MallModel> findAll(Pageable pageable);
	Page<MallModel> findAllByName(Pageable pageable, String name);
	@Query("SELECT b FROM MallModel b WHERE LOWER(b.name) LIKE %:name%")
	Page<MallModel> findAllByNameContaining(@Param("name") String name,
			Pageable pageable);
	MallModel findByName(String name);
	@Query("SELECT NEW MallModel(c.id, c.name) FROM MallModel c")
	List<MallModel> getAllIdAndName();

	@Query("SELECT NEW MallModel(c.id, c.displayName) FROM MallModel c")
	List<MallModel> getAllIdAndDisplayName();

	@Query("SELECT NEW MallModel(m.id, m.name) FROM MallModel m JOIN m.images a WHERE a.id = :id")
	List<MallModel> findByWebImageId(@Param("id") Long id);

	@Query("SELECT NEW MallModel(m.id, m.name) FROM MallModel m JOIN m.shops a WHERE a.id = :id")
	List<MallModel> findByShopId(@Param("id") Long id);

	@Query("SELECT NEW MallModel(m.id, m.name) FROM MallModel m JOIN m.attractions a WHERE a.id = :id")
	List<MallModel> findByAttractionId(@Param("id") Long id);

	@Query("SELECT NEW MallModel(m.id, m.name) FROM MallModel m JOIN m.parkings a WHERE a.id = :id")
	List<MallModel> findByParkingId(@Param("id") Long id);

	@Query("SELECT NEW MallModel(m.id, m.name) FROM MallModel m JOIN m.events a WHERE a.id = :id")
	List<MallModel> findByEventId(@Param("id") Long id);

	@Query("SELECT NEW MallModel(m.id, m.name) FROM MallModel m JOIN m.otherAttractions a WHERE a.id = :id")
	List<MallModel> findByOtherAttractionId(@Param("id") Long id);

}
