package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.mckesson.cgat.pde.usermanagement.model.Users;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Users createUser(Users model) throws BadRequestException {
		return userRepository.save(model);

	}

	@Override
	public void updateUser(Long userId, Users model) throws ResourceNotFoundException {
		Optional<Users> user = userRepository.findById(userId);
		boolean isActive = user.map(Users::getIsActive).orElse(false);
		if (!user.isPresent() || !isActive)
			throw new ResourceNotFoundException("User not found with given userId = " + userId);
		model.setId(userId);
		userRepository.save(model);
	}

	@Override
	public List<Users> users(Long userId) throws ResourceNotFoundException {
		List<Users> userList = new ArrayList<>();
		if (userId == 0)
			userList = userRepository.findAll();
		else {
			Optional<Users> user = userRepository.findById(userId);
			if (user.isPresent())
				userList.add(user.get());
			else
				throw new ResourceNotFoundException("User not found with given Id = " + userId);

			return userList;
		}
		return userList;
	}

	@Override
	public void deleteUsers(Long userId) throws ResourceNotFoundException {
		Optional<Users> user = userRepository.findById(userId);
		boolean isActive = user.map(Users::getIsActive).orElse(false);
		if (!user.isPresent() || !isActive)
			throw new ResourceNotFoundException("User not found with given Id = " + userId);
		user.get().setIsActive(false);
		userRepository.save(user.get());
	}

	@Override
	public Page<Users> findAll(Pageable paging, String query, List<String> status) {
		query = (null != query) ? query.toLowerCase() : null;
		String statusActive = "Active";
		if (!CollectionUtils.isEmpty(status)) {
			boolean hasActive = status.stream().anyMatch(i -> i.equalsIgnoreCase("Active"));
			boolean hasInactive = status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"));
			if (hasActive && hasInactive)
				statusActive = "ALL";
			else if (hasInactive)
				statusActive = "INACTIVE";
		}
		return userRepository.findAllByQueryText(query, statusActive, paging);
	}

}
