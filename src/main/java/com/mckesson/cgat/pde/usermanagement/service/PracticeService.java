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

	String createOrUpdatePractice(MultipartFile logo, String data) throws IllegalStateException, IOException, ResourceNotFoundException;

	Practice getPractice(String id);

	List<Practice> getAllPractices();

	String deletePractice(String id) throws ResourceNotFoundException;

	Page<Practice> findAll(Pageable paging, String query, List<String> roles, List<String> status);

}
