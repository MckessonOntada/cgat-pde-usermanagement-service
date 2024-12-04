package com.mckesson.cgat.pde.usermanagement.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface PracticeService {

	Practice getPractice(String id) throws ResourceNotFoundException;

	void deletePractice(String id) throws ResourceNotFoundException;

	Page<Practice> findAll(Pageable paging, String query, List<String> status, List<String> location);

	Practice createPractice(Practice practice, MultipartFile logo) throws IllegalStateException, IOException;

}
