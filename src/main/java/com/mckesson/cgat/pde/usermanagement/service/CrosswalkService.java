package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Crosswalk;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

public interface CrosswalkService {

	Crosswalk createAccount(Crosswalk crosswalk) throws ResourceNotFoundException;

	Page<CrosswalkResponseDTO> findAll(Pageable paging, String query, List<String> status);

}
