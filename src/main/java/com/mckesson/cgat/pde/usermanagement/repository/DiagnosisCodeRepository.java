package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.DiagnosisCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisCodeRepository extends JpaRepository<DiagnosisCode, Long> {

	Page<DiagnosisCode> findByCodeContainingIgnoreCaseOrLongDescriptionContainingIgnoreCase(String code,
			String shortDescription, Pageable pageable);

}
