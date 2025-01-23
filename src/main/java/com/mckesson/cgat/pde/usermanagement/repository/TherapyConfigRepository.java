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

	List<TherapyConfig> findByMaterialNumberIn(@Param("materialNumbers") List<String> materialNumbers);

	@Query(value = """
			    SELECT m.id,
			           m.material_number as therapyId,
			           m.material_description as therapyDescription,
			           m.ndc_number as ndc,
			           m.strength,
			           m.dosage,
			           m.selling_unit_code,
			           m.vendor_name,
			           m.upc_code,
			           m.dropship_indicator,
			           m.item_status as status,
			           m.base_unit_of_measure,
			           m.cgat_identifier,
			           m.sales_organization,
			           m.cgat_type,
			           m.product_level_pricing,
			           m.scan_qty,
			           tc.is_coi_id_required,
			        	tc.is_patient_information_required
			    FROM catalog.material m
			    JOIN catalog.material_config tc ON m.material_number = tc.material_number
			    WHERE m.material_number = :materialNumber
			""", nativeQuery = true)
	Optional<MaterialConfigDTO> findDetailedByMaterialNumber(@Param("materialNumber") String materialNumber);

}
