package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import com.mckesson.cgat.pde.usermanagement.model.User;
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
	public User createUser(User model) throws BadRequestException {
		return userRepository.save(model);

	}

	@Override
	public User updateUser(Long userId, User model) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(userId.toString()));
		if (Boolean.FALSE.equals(user.getIsActive()))
			throw new ResourceNotFoundException(userId.toString());
		model.setId(userId);
		return userRepository.save(model);
	}

	@Override
	public User getUser(Long userId) throws ResourceNotFoundException {
		Optional<User> user = Optional.of(userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(userId.toString())));
		return user.get();
	}

	@Override
	public void deleteUsers(Long userId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(userId.toString()));
		if (Boolean.FALSE.equals(user.getIsActive()))
			throw  new ResourceNotFoundException(userId.toString());
		user.setIsActive(false);
		userRepository.save(user);
	}

	@Override
	public Page<User> findAll(Pageable paging, String query, List<String> status,String praciceId) {
		String statusActive = "ALL";

		if (!CollectionUtils.isEmpty(status)) {
			boolean hasActive = status.stream().anyMatch(i -> i.equalsIgnoreCase("ACTIVE"));
			boolean hasInactive = status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"));

			if (hasActive)
				statusActive = "ACTIVE";

			if (hasInactive)
				statusActive = "ACTIVE".equals(statusActive) ? "ALL" : "INACTIVE";
		}
		return userRepository.findAllByQueryText(query, statusActive, paging,praciceId);
	}


	@Override
	public Page<User> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public User linkUserToAccount(Long userId, Map<String,Object> user) throws ResourceNotFoundException {
		User userObj = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(userId.toString()));
		user.forEach((key ,value)->{
		Field field = ReflectionUtils.findField(User.class, key);
		field.setAccessible(true);
		ReflectionUtils.setField(field, userObj, value);
		});
		return userRepository.save(userObj);
	}

}
