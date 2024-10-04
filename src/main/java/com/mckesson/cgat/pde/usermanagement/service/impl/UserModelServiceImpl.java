package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.dto.UserModelRequestDto;
import com.mckesson.cgat.pde.usermanagement.model.UserModel;
import com.mckesson.cgat.pde.usermanagement.repository.UserModelRepository;
import com.mckesson.cgat.pde.usermanagement.service.UserModelService;
import com.mckesson.cgat.pde.usermanagement.utils.UserModelUtil;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public class UserModelServiceImpl implements UserModelService {

	
	private  final UserModelRepository userModelRepository;

	public UserModelServiceImpl(UserModelRepository userModelRepository) {
		this.userModelRepository = userModelRepository;
	}

	@Override
	public UserModel createUser(UserModel model) throws BadRequestException {
		UserModel createdUser = userModelRepository.save(model);
		if (createdUser == null)
			throw new BadRequestException("Bad request..!!");
		return createdUser;
	}

	@Override
	public void updateUser(String userId, UserModel model) throws ResourceNotFoundException {
		Optional<UserModel> user = userModelRepository.findById(userId);
		if (!user.isPresent())
			throw new ResourceNotFoundException("User not found with given userId = " + userId);
		model.setId(userId);
		userModelRepository.save(model);
	}

	@Override
	public List<UserModel> users(String userId) throws ResourceNotFoundException {
		List<UserModel> userList = new ArrayList<>();
		if (userId == null || userId.isEmpty())
			userList = userModelRepository.findAll();
		else {
			Optional<UserModel> user = userModelRepository.findById(userId);
			if (user.isPresent())
				userList.add(user.get());
			else
				throw new ResourceNotFoundException("User not found with given Id = " + userId);

			return userList;
		}
		return userList;
	}

	@Override
	public String deleteUsers(String userId) throws ResourceNotFoundException {
		Optional<UserModel> user = userModelRepository.findById(userId);
		if (!user.isPresent())
			throw new ResourceNotFoundException("User not found with given Id = " + userId);
		user.get().setIsActive(false);
		userModelRepository.save(user.get());
		return user.get().getFirstName();
	}

	@Override
	public Page<UserModel> findAll(Pageable paging, String query, List<String> status) {
		query = StringUtils.toRootLowerCase(query);
		Page<UserModel> response;
		String statusActive = "Active";
		if (!CollectionUtils.isEmpty(status)) {
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase("Active"))) {
				statusActive = "Active";
			}
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"))) {
				statusActive = "ACTIVE".equals(statusActive) ? "ALL" : "INACTIVE";
			}
		}
		response = userModelRepository.findAllByQueryText(query, statusActive, paging);
		return response;
	}

//	@Override
//	public UserModel updateUser(String userId, UserModelRequestDto dto) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}

	

}
