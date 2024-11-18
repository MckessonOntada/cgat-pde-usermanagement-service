package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.service.OktaUserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.dto.RolePermissionsPatchDTO;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.utils.UserRequestAuthorizationValidator;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final AccountRepository accountRepository;

	private final RoleRepository roleRepository;

	private final OktaUserManagementService oktaUserManagementService;

	public UserServiceImpl(UserRepository userRepository, AccountRepository repository, RoleRepository roleRepository,
			OktaUserManagementService oktaUserManagementService) {
		this.userRepository = userRepository;
		this.accountRepository = repository;
		this.roleRepository = roleRepository;
		this.oktaUserManagementService = oktaUserManagementService;
	}

	@Override
	public User createUser(User model) {
		UserProvisionedInOktaStatus oktaStatus = UserProvisionedInOktaStatus.PENDING;
		try {
			oktaUserManagementService.updateOrCreateOktaUser(model);
			oktaStatus = UserProvisionedInOktaStatus.PROVISIONED;
		}
		catch (CGATHttpException e) {
			log.error("Error occurred while provisioning user in okta, e: {}", e.getMessage());

		}
		model.setProvisionedInOktaStatus(oktaStatus);
		return userRepository.save(model);

	}

	@Override
	public User updateUser(Long userId, User model) throws ResourceNotFoundException {
		User user = getUser(userId);
		if (user != null)
			model.setId(userId);
		return userRepository.save(model);
	}

	@Override
	public User getUser(Long userId) throws ResourceNotFoundException {
		 return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(userId.toString()));
	}

	@Override
	public void deleteUser(Long userId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(userId.toString()));
		user.setIsActive(false);
		userRepository.save(user);
	}

	@Override
	public Page<User> findAll(Pageable paging, String query, List<String> status, Long praticeId, LocalDate startDate,
			LocalDate endDate) {
		query = StringUtils.toRootLowerCase(query);
		String statusActive = "ALL";

		if (!CollectionUtils.isEmpty(status)) {
			boolean hasActive = status.stream().anyMatch(i -> i.equalsIgnoreCase("ACTIVE"));
			boolean hasInactive = status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"));

			if (hasActive)
				statusActive = "ACTIVE";

			if (hasInactive)
				statusActive = "ACTIVE".equals(statusActive) ? "ALL" : "INACTIVE";
		}
		return userRepository.findAllByQueryText(statusActive, query, paging, praticeId, startDate, endDate);
	}

	@Override
	@Transactional
	public User linkUserToAccounts(Long userId, List<Long> accountIds) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with this Id :" + userId));

		List<Account> accounts = accountRepository.findAllById(accountIds);

		if (accounts.size() != accountIds.size())
			throw new ResourceNotFoundException("Some of the provided account IDs does not exist.");

		user.getAccounts().clear();
		user.getAccounts().addAll(accounts);
		return userRepository.save(user);

	}

	@Transactional
	@Override
	public User linkUserToRoleAndPermissions(Long userId, UserType userType, RolePermissionsPatchDTO rolePermissions)
			throws ResourceNotFoundException {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with this Id :" + userId));

		Role role = roleRepository.findByIdAndIsActiveTrue(rolePermissions.getRoleId())
			.orElseThrow(
					() -> new ResourceNotFoundException("Role not found with this Id :" + rolePermissions.getRoleId()));

		if (userType.equals(UserType.ADMIN)) {
			Role systemAdminRole = roleRepository.findByName(UserRequestAuthorizationValidator.SYSTEM_ADMIN_ROLE);
			user.getPermissions().clear();
			user.setRole(systemAdminRole);
			user.setPermissions(new HashSet<>(systemAdminRole.getPermissions()));
		}
		else {
			user.setRole(role);
			user.getPermissions().clear();
			user.setPermissions(rolePermissions.getPermissions());
		}
		return userRepository.save(user);
	}

}
