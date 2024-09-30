package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mckesson.cgat.pde.usermanagement.dto.UserModelRequestDto;
import com.mckesson.cgat.pde.usermanagement.model.UserModel;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

public interface UserModelService {

	UserModel createUser(String userId, UserModelRequestDto dto) throws Exception;

	List<UserModel> users(String userId) throws ResourceNotFoundException;

	String deleteUsers(String userId) throws ResourceNotFoundException;

	Page<UserModel> findAll(Pageable paging, String query, List<String> status);

}
