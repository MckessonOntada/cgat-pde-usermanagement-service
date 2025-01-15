package com.mckesson.cgat.pde.usermanagement.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mckesson.cgat.pde.usermanagement.dto.RolePermissionsPatchDTO;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

public interface UserService {

	User getUser(Long userId) throws ResourceNotFoundException;

	void deleteUser(Long userId) throws ResourceNotFoundException;

	void activateUser(Long userId) throws ResourceNotFoundException;

	void delinkUserFromAccount(Long userId, Long accountId) throws ResourceNotFoundException;

	Page<User> findAll(Pageable paging, String query, List<String> status, Long practiceId, LocalDate startDate,
			LocalDate endDate, List<Long> roleIds);

	User updateUser(Long userId, User model) throws ResourceNotFoundException;

	User createUser(User model);

	User linkUserToAccounts(Long userId, List<Long> accountIds) throws ResourceNotFoundException;

	User linkUserToRoleAndPermissions(Long userId, UserType userType, RolePermissionsPatchDTO rolePermissions)
			throws ResourceNotFoundException;

	User getUserByEmail(String email);

	User setDefaultPayToAccount(Long userId, String payToAccount) throws ResourceNotFoundException;

	User retryOktaProvisioning(Long id) throws ResourceNotFoundException;

}
