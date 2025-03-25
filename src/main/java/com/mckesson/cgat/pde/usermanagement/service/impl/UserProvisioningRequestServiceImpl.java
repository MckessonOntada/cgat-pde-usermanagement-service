package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.exception.custom.ForbiddenActionException;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningStatus;
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
	public UserProvisioningRequest updateUserProvisioningStatus(Long requestId,
			UserProvisioningStatus statusToBeUpdated) throws ResourceNotFoundException {
		UserProvisioningRequest userRequest = userProvisioningRequestRepo.findById(requestId)
			.orElseThrow(
					() -> new ResourceNotFoundException("User provisoning request not found for id: " + requestId));
		// First clause is to make request Idempotent. Second clause is to make sure
		// Approved or Rejected request cant be updated.
		if (userRequest.getStatus() == statusToBeUpdated || userRequest.getStatus() == UserProvisioningStatus.PENDING)
			userRequest.setStatus(statusToBeUpdated);
		else
			throw new ForbiddenActionException(
					"User provisoning request once approved or rejected cant be updated. Forbidden Action for id :"
							+ requestId);
		return userProvisioningRequestRepo.save(userRequest);
	}

	@Override
	public Page<UserProvisioningRequest> findAll(Pageable paging, String query,
			List<UserProvisioningStatus> provisioningStatuses, Long praticeId, LocalDate startDate, LocalDate endDate) {
		query = StringUtils.toRootLowerCase(query);
		return userProvisioningRequestRepo.findAllByQueryText(query,
				provisioningStatuses.stream().map(Enum::name).toList(), paging, praticeId, startDate, endDate);
	}

	@Override
	public UserProvisioningRequest getUserProvisioningRequest(Long requestId) throws ResourceNotFoundException {
		UserProvisioningRequest userRequest = userProvisioningRequestRepo.findById(requestId)
			.orElseThrow(
					() -> new ResourceNotFoundException("User provisoning request not found for id: :" + requestId));
		return userRequest;
	}

}
