package com.mckesson.cgat.pde.usermanagement.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningStaus;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Service
public interface UserProvisioningRequestService {

	UserProvisioningRequest createUserProvisioning(UserProvisioningRequest request);

	UserProvisioningRequest updateUserProvisioningStatus(Long userProvisioningReqId,
			UserProvisioningStaus statusToBeUpdated) throws ResourceNotFoundException;

	Page<UserProvisioningRequest> findAll(Pageable paging, String query, UserProvisioningStaus status, Long praticeId,
			LocalDate startDate, LocalDate endDate);

	UserProvisioningRequest getUserProvisioningRequest(Long requestId) throws ResourceNotFoundException;

}
