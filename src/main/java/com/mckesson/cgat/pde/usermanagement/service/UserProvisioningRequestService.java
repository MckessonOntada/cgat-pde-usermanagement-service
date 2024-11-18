package com.mckesson.cgat.pde.usermanagement.service;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;

import jakarta.validation.Valid;

@Service
public interface UserProvisioningRequestService {

	UserProvisioningRequest createUserProvisioning(@Valid UserProvisioningRequest request);

}
