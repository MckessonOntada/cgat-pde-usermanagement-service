package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

public interface UserService {

	User getUser(Long userId) throws ResourceNotFoundException;

	void deleteUser(Long userId) throws ResourceNotFoundException;

	Page<User> findAll(Pageable paging, String query, List<String> status, Long practiceId);

	User updateUser(Long userId, User model) throws ResourceNotFoundException;

	User createUser(User model);

	User linkUserToAccounts(Long userId, List<Long> accountIds) throws ResourceNotFoundException;

	User linkUserToRoleAndPermissions(Long userId, Long roleId, Set<Permission> permissions)
			throws ResourceNotFoundException;

}
