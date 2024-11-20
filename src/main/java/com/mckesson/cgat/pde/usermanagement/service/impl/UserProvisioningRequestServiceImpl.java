package com.mckesson.cgat.pde.usermanagement.service.impl;

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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

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
		userRequest.setStatus(status.name());
		return userProvisioningRequestRepo.save(userRequest);
	}

	@Override
	public Page<UserProvisioningRequest> findAll(Pageable paging, String query, String status, Long praticeId) {
		query = StringUtils.toRootLowerCase(query);
		return userProvisioningRequestRepo.findAllByQueryText(query, status,
				paging, praticeId);
	}

}
