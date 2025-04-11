package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;

public interface CrosswalkService {

	Page<CrosswalkResponseDTO> findAll(Pageable paging, String query, List<String> status);

}
