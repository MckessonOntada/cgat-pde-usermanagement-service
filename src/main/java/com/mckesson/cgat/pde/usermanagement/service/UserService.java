package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

public interface UserService {

	User getUser(Long userId) throws ResourceNotFoundException;

	void deleteUser(Long userId) throws ResourceNotFoundException;

	Page<User> findAll(Pageable paging, String query, List<String> status,String praciceId);

	User updateUser(Long userId, User model) throws ResourceNotFoundException;

	User createUser(User model) throws BadRequestException;



}
