package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mckesson.cgat.pde.usermanagement.model.UserModel;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

public interface UserModelService {


	List<UserModel> users(String userId) throws ResourceNotFoundException;

	String deleteUsers(String userId) throws ResourceNotFoundException;

	Page<UserModel> findAll(Pageable paging, String query, List<String> status);

	void updateUser(String userId, UserModel model) throws ResourceNotFoundException;

	UserModel createUser(UserModel model) throws BadRequestException;


}
