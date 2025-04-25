package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.cgat.pde.usermanagement.model.ShippingLocation;
import com.mckesson.cgat.pde.usermanagement.repository.ShippingLocationRepository;
import com.mckesson.cgat.pde.usermanagement.service.ShippingLocationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
			.orElseThrow(() -> new ResourceNotFoundException(
					"Shipping Location with ID " + shippingAccountNumber + " not found."));

		shippingLocation.setId(existingLocation.getId());
		return shippingLocationRepository.save(shippingLocation);
	}

	@Override
	public ShippingLocation findById(Long shippingId) throws ResourceNotFoundException {
		return shippingLocationRepository.findById(shippingId)
			.orElseThrow(
					() -> new ResourceNotFoundException("Shipping Location with ID " + shippingId + " not found."));
	}

	@Override
	public void removeShippingLocations(List<Long> shippingLocationIds) throws ResourceNotFoundException {
		if (shippingLocationIds == null || shippingLocationIds.isEmpty()) {
			return;
		}

		List<ShippingLocation> locationsToDeactivate = shippingLocationRepository.findAllById(shippingLocationIds);

		if (locationsToDeactivate.size() != shippingLocationIds.size()) {
			List<Long> foundIds = locationsToDeactivate.stream().map(ShippingLocation::getId).toList();

			for (Long id : shippingLocationIds) {
				if (!foundIds.contains(id)) {
					throw new ResourceNotFoundException("Shipping Location with ID " + id + " not found.");
				}
			}
		}

		locationsToDeactivate.forEach(location -> location.setIsActive(false));
		shippingLocationRepository.saveAll(locationsToDeactivate);
	}

}
