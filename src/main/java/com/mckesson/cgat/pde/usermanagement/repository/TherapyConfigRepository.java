package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialConfigDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
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

	@Query(value = "SELECT * FROM catalog.material_config WHERE material_number IN (:materialNumbers)",
			nativeQuery = true)
	List<TherapyConfig> findAllByMaterialNumber(@Param("materialNumbers") List<String> materialNumbers);

}
