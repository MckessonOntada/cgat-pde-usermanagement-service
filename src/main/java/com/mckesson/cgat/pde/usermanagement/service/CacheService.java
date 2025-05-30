package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface CacheService {

	Page<TherapyCatalogResponseDTO> findAllGlobalTherapies(String query, Pageable paging);

	void deleteConfigByMaterialNumber(
			@NotNull(message = "Material number cannot be null ") @NotEmpty(
					message = "Material number cannot be empty") String materialNumber)
			throws ResourceNotFoundException;

	MaterialDetailsResponseDTO findTherapyDetailsByMaterialNumber(String materialNumber)
			throws ResourceNotFoundException;

	void testCacheEvents();

}
