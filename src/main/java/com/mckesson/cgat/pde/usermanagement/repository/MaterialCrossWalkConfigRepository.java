package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Crosswalk;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public interface MaterialCrossWalkConfigRepository extends JpaRepository<Crosswalk, Long> {

	List<Crosswalk> findMaterialCrossWalkConfigBySpProductIdIn(List<String> spProductIds);

	void deleteBySpProductIdIn(List<String> spProductIds);

	@Query(value = "SELECT DISTINCT m.bnb_material_number FROM catalog.crosswalk m WHERE m.bnb_material_number IN :bnbMaterialNumbers",
			nativeQuery = true)
	List<String> findExistingBnbMaterialNumbers(@Param("bnbMaterialNumbers") Set<String> bnbMaterialNumbers);

	@Query(value = """
			SELECT
			 	cw.id,
			 	m1.material_number as bnbMaterialNumber,
			    m1.material_description as bnbMaterialDescription,
				m1.ndc_number as bnbNdc,
				m2.material_number as spProductId,
			    m2.material_description as spProductName,
				m2.ndc_number as spNdc
			FROM catalog.crosswalk cw
			JOIN catalog.material m1 ON cw.bnb_material_number = m1.material_number and m1.channel_type = 'BUY_AND_BILL'
			JOIN catalog.material m2 ON cw.sp_product_id = m2.material_number and m2.channel_type = 'SPECIALITY_PHARMA'
			WHERE
			(
			    :query IS NULL
			    OR LOWER(m1.material_number) LIKE %:query%
			    OR LOWER(m1.ndc_number) LIKE %:query%
			    OR LOWER(m1.material_description) LIKE %:query%
				OR LOWER(m2.material_number) LIKE %:query%
			    OR LOWER(m2.ndc_number) LIKE %:query%
			    OR LOWER(m2.material_description) LIKE %:query%
			)
			AND
			(
				(:status = 'ALL')
				OR ('ACTIVE' = :status and cw.is_active = 1 )
				OR ('INACTIVE' = :status and cw.is_active = 0 )
			)
			""", nativeQuery = true)
	Page<CrosswalkResponseDTO> findAllCrosswalks(Pageable paging, String query, @Param("status") String status);

	List<Crosswalk> findByBnbMaterialNumber(String bnbMaterialNumber);

	List<Crosswalk> findAllBySpProductIdIn(Set<String> validSpIds);

}
