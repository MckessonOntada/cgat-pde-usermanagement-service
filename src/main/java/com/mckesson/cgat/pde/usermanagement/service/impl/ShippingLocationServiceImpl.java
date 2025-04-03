package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.cgat.pde.usermanagement.model.ShippingLocation;
import com.mckesson.cgat.pde.usermanagement.repository.ShippingLocationRepository;
import com.mckesson.cgat.pde.usermanagement.service.ShippingLocationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingLocationServiceImpl implements ShippingLocationService {

	private final ShippingLocationRepository shippingLocationRepository;

	public ShippingLocationServiceImpl(ShippingLocationRepository shippingLocationRepository) {
		this.shippingLocationRepository = shippingLocationRepository;
	}

	@Override
	public Page<ShippingLocation> findAll(Pageable paging, String query, Long practiceId, Long accountId)
			throws ResourceNotFoundException {
		query = StringUtils.toRootLowerCase(query);
		return shippingLocationRepository.findAllByQueryText(query, paging, practiceId, accountId);
	}

	@Override
	public ShippingLocation createShippingLocation(ShippingLocation shippingLocation) {
		return shippingLocationRepository.save(shippingLocation);
	}

	@Override
	public ShippingLocation updateShippingLocation(Long shippingAccountNumber, ShippingLocation shippingLocation)
			throws ResourceNotFoundException {
		ShippingLocation existingLocation = shippingLocationRepository.findById(shippingAccountNumber)
			.orElseThrow(() -> new ResourceNotFoundException(shippingAccountNumber.toString()));

		shippingLocation.setShippingAccountNumber(shippingAccountNumber);
		return shippingLocationRepository.save(shippingLocation);
	}

	@Override
	public void removeShippingLocation(Long shippingAccountNumber) throws ResourceNotFoundException {
		ShippingLocation location = shippingLocationRepository.findById(shippingAccountNumber)
			.orElseThrow(() -> new ResourceNotFoundException(
					"An error occurred while removing the Shipping Location. Please try again later."));
		location.setIsActive(false);
		shippingLocationRepository.save(location);
	}

	@Override
	public void removeMultipleShippingLocations(List<ShippingLocation> shippingLocations)
			throws ResourceNotFoundException {
		if (shippingLocations == null || shippingLocations.isEmpty()) {
			return;
		}
		List<Long> shippingAccountNumbers = shippingLocations.stream()
			.map(ShippingLocation::getShippingAccountNumber)
			.toList();
		List<ShippingLocation> locationsToDeactivate = shippingLocationRepository.findAllById(shippingAccountNumbers);

		if (locationsToDeactivate.size() != shippingAccountNumbers.size()) {
			List<Long> foundIds = locationsToDeactivate.stream()
				.map(ShippingLocation::getShippingAccountNumber)
				.toList();

			for (Long id : shippingAccountNumbers) {
				if (!foundIds.contains(id)) {
					throw new ResourceNotFoundException(
							"An error occurred while removing the Shipping Location. Please try again later.");
				}
			}
		}
		locationsToDeactivate.forEach(location -> location.setIsActive(false));
		shippingLocationRepository.saveAll(locationsToDeactivate);
	}

}
