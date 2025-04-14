package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.MaterialCrossWalkConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialCrossWalkConfigRepository extends JpaRepository<MaterialCrossWalkConfig, Long> {

	List<MaterialCrossWalkConfig> findMaterialCrossWalkConfigBySpProductIdIn(List<String> spProductIds);

	void deleteBySpProductIdIn(List<String> spProductIds);

	boolean existsBySpProductId(String spProductId);

	boolean existsByBnbMaterialNumber(String bnbMaterialNumber);

}
