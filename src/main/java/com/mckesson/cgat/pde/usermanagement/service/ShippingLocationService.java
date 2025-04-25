package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.cgat.pde.usermanagement.model.ShippingLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShippingLocationService {

	void removeShippingLocations(List<Long> shippingLocationIds) throws ResourceNotFoundException;

	Page<ShippingLocation> findAll(Pageable paging, String query, Long practiceId, Long accountId)
			throws ResourceNotFoundException;

	ShippingLocation createShippingLocation(ShippingLocation shippingLocation);

	ShippingLocation updateShippingLocation(Long shippingAccountNumber, ShippingLocation shippingLocation)
			throws ResourceNotFoundException;

	ShippingLocation findById(Long shippingId) throws ResourceNotFoundException;

}
