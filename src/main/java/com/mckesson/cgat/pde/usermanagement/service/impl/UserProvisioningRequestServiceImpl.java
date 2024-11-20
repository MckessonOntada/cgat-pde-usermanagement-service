package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningStaus;
import com.mckesson.cgat.pde.usermanagement.repository.UserProvisioningRequestRepo;
import com.mckesson.cgat.pde.usermanagement.service.UserProvisioningRequestService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public class UserProvisioningRequestServiceImpl implements UserProvisioningRequestService {

	@Autowired
	private UserProvisioningRequestRepo userProvisioningRequestRepo;

	@Override
	public UserProvisioningRequest createUserProvisioning(UserProvisioningRequest request) {
		return userProvisioningRequestRepo.save(request);
	}

	@Override
	public UserProvisioningRequest updateUserProvisioningStatus(Long userId, UserProvisioningStaus status)
			throws ResourceNotFoundException {
		UserProvisioningRequest userRequest = userProvisioningRequestRepo.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User id not found : " + userId));
		userRequest.setStatus(status);
		return userProvisioningRequestRepo.save(userRequest);
	}

	@Override
	public Page<UserProvisioningRequest> findAll(Pageable paging, String query, UserProvisioningStaus status,
			Long praticeId, LocalDate startDate, LocalDate endDate) {
		query = StringUtils.toRootLowerCase(query);
		return userProvisioningRequestRepo.findAllByQueryText(query,
				status == null ? null : status.getUserRequestType(), paging, praticeId, startDate, endDate);
	}

	@Override
	public List<UserProvisioningRequest> getAllUserProvisioning() throws ResourceNotFoundException {
		List<UserProvisioningRequest> userRequest = userProvisioningRequestRepo.findAllByPracticeId(1L);
		if (userRequest.isEmpty())
			throw new ResourceNotFoundException("User Request is empty");
		return userRequest;
	}

}
