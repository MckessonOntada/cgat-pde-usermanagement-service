package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.apache.coyote.BadRequestException;

import com.mckesson.cgat.pde.usermanagement.model.Users;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

public interface UserService {

	List<Users> users(Long userId) throws ResourceNotFoundException;

	void deleteUsers(Long userId) throws ResourceNotFoundException;

	Page<Users> findAll(Pageable paging, String query, List<String> status);

	void updateUser(Long userId, Users model) throws ResourceNotFoundException;

	Users createUser(Users model) throws BadRequestException;

}
