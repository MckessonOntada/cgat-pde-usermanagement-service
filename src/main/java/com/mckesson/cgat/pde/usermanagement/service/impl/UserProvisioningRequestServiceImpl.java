package com.mckesson.cgat.pde.usermanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.repository.UserProvisioningRequestRepo;
import com.mckesson.cgat.pde.usermanagement.service.UserProvisioningRequestService;

import jakarta.validation.Valid;

@Service
public class UserProvisioningRequestServiceImpl implements UserProvisioningRequestService {

	@Autowired
	private UserProvisioningRequestRepo userProvisioningRequestRepo;

	@Override
	public UserProvisioningRequest createUserProvisioning(@Valid UserProvisioningRequest request) {
		return userProvisioningRequestRepo.save(request);
	}

}
