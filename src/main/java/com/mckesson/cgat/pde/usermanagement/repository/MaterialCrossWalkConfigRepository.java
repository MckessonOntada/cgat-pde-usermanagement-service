package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.Crosswalk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MaterialCrossWalkConfigRepository extends JpaRepository<Crosswalk, Long> {

	List<Crosswalk> findMaterialCrossWalkConfigBySpProductIdIn(List<String> spProductIds);

	void deleteBySpProductIdIn(List<String> spProductIds);

	@Query(value = "SELECT DISTINCT m.sp_product_id FROM catalog.crosswalk m WHERE m.sp_product_id IN :spProductIds",
			nativeQuery = true)
	List<String> findExistingSpProductIds(@Param("spProductIds") Set<String> spProductIds);

	@Query(value = "SELECT DISTINCT m.bnb_material_number FROM catalog.crosswalk m WHERE m.bnb_material_number IN :bnbMaterialNumbers",
			nativeQuery = true)
	List<String> findExistingBnbMaterialNumbers(@Param("bnbMaterialNumbers") Set<String> bnbMaterialNumbers);

}
