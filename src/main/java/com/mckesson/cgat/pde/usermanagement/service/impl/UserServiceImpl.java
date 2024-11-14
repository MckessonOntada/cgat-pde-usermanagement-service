package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.dto.RolePermissionsPatchDto;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.utils.UserRequestAuthorizationValidator;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final AccountRepository accountRepository;

	private final RoleRepository roleRepository;

	public UserServiceImpl(UserRepository userRepository, AccountRepository repository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.accountRepository = repository;
		this.roleRepository = roleRepository;
	}

	@Override
	public User createUser(User model) {
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
	public Page<User> findAll(Pageable paging, String query, List<String> status, Long praticeId, String startDate,
			String endDate) {
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
		if (!startDate.isEmpty() && !endDate.isEmpty()) {
			return userRepository.findAllByQueryText(statusActive, query, paging, praticeId,
					java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
		}
		else
			return userRepository.findAllByQueryText(statusActive, query, paging, praticeId);
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
	public User linkUserToRoleAndPermissions(Long userId, RolePermissionsPatchDto dto)
			throws ResourceNotFoundException {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with this Id :" + userId));

		Role role = roleRepository.findByIdAndIsActiveTrue(dto.getRoleId())
			.orElseThrow(() -> new ResourceNotFoundException("Role not found with this Id :" + dto.getRoleId()));

		user.setRole(role);
		if (dto.getUserType().equals(UserType.ADMIN)) {
			Role systemAdminRole = roleRepository.findByName(UserRequestAuthorizationValidator.SYSTEM_ADMIN_ROLE);
			if (systemAdminRole != null)
				user.setRole(systemAdminRole);
		}
		user.getPermissions().clear();
		user.setPermissions(dto.getPermissions());
		return userRepository.save(user);

	}

}
