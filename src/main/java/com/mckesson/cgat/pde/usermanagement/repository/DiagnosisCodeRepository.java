package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.DiagnosisCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DiagnosisCodeRepository extends JpaRepository<DiagnosisCode, Long> {

	Page<DiagnosisCode> findByCodeContainingIgnoreCaseOrLongDescriptionContainingIgnoreCase(String code,
			String shortDescription, Pageable pageable);

	@Query(nativeQuery = true, value = """
			select dc.*
			FROM usermanagement.diagnosis_code dc
			         JOIN catalog.sp_therapy_config stc
			         on stc.sp_product_id = :spProductId
			         LEFT JOIN catalog.sp_therapy_config_diagnosis_code stcdc
			         ON dc.id = stcdc.diagnosis_code_id
			         AND stcdc.sp_therapy_config_id = stc.id
			WHERE
			(
				(
					:query IS NULL
				 	OR LOWER(dc.code) LIKE %:query%
				 	OR LOWER(dc.long_description) LIKE %:query%
				)
				AND
					stcdc.sp_therapy_config_id IS NULL
			)""")
	Page<DiagnosisCode> findAllDiagnosisCodesNotLinkedToSPProduct(String query, Pageable paging, String spProductId);

	@Query(nativeQuery = true, value = """
			select dc.*
			FROM usermanagement.diagnosis_code dc
			         left JOIN catalog.sp_therapy_config stc
			         on stc.sp_product_id = :spProductId
			         LEFT JOIN catalog.sp_therapy_config_diagnosis_code stcdc
			         ON dc.id = stcdc.diagnosis_code_id
			         AND stcdc.sp_therapy_config_id = stc.id
			WHERE
			(
				(
					:query IS NULL
				 	OR LOWER(dc.code) LIKE %:query%
				 	OR LOWER(dc.long_description) LIKE %:query%
				)
			)""")
	Page<DiagnosisCode> findAllDiagnosisCodesLinkedToSPProduct(String query, Pageable paging, String spProductId);

}
