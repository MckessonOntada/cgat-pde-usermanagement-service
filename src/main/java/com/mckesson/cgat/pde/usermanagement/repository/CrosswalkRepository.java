package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mckesson.cgat.pde.usermanagement.model.Crosswalk;

public interface CrosswalkRepository extends JpaRepository<Crosswalk, Long> {

	@Query(value = """
			SELECT
			 	cw.id,
			    m1.material_number AS bnb_material_number,
			    m1.material_description AS bnb_description,
				m1.ndc_number as bnb_ndc,
			    m2.material_number AS sp_material_number,
			    m2.material_description AS sp_description,
				m2.ndc_number as sp_ndc,
				cw.is_active
			FROM catalog.crosswalk cw
			JOIN catalog.material m1 ON cw.bnb_material_number = m1.material_number
			JOIN catalog.material m2 ON cw.sp_product_id = m2.material_number
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
				(:status = 'ALL' and (cw.is_active = 1 or cw.is_active = 0))
				OR ('ACTIVE' = :status and cw.is_active = 1 )
				OR ('INACTIVE' = :status and cw.is_active = 0 )
			)
			""", nativeQuery = true)
	Page<Crosswalk> findAllCrosswalks(Pageable paging, String query, @Param("status") String status);

}
