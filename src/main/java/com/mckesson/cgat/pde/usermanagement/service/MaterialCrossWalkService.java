package com.mckesson.cgat.pde.usermanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Crosswalk;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public interface MaterialCrossWalkService {

	Boolean uploadCrossWalks(MultipartFile crossWalkData, ByteArrayOutputStream outputStream);

	Crosswalk createCrosswalk(Crosswalk crosswalk) throws ResourceNotFoundException;

	Page<CrosswalkResponseDTO> findAll(Pageable paging, String query, List<String> status);

}
