package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.TherapyConfig;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TherapyConfigRepository extends JpaRepository<TherapyConfig, Long> {

	List<TherapyConfig> findByMaterialNumberIn(@Param("materialNumbers") List<String> materialNumbers);

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,tc)
			    FROM Therapy m
			    LEFT JOIN TherapyConfig tc
			    ON m.materialNumber = tc.materialNumber
			    WHERE m.materialNumber = :materialNumber
			""")
	Optional<MaterialResponseDTO> findDetailedByMaterialNumber(@Param("materialNumber") String materialNumber);

	int removeTherapyConfigByMaterialNumber(String materialNumber);

}
