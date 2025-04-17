package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Crosswalk;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.CrosswalkRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.CrosswalkService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CrosswalkServiceImpl implements CrosswalkService {

	CrosswalkRepository crosswalkRepository;

	TherapyRepository therapyRepository;

	public CrosswalkServiceImpl(CrosswalkRepository crosswalkRepository, TherapyRepository therapyRepository) {
		this.crosswalkRepository = crosswalkRepository;
		this.therapyRepository = therapyRepository;
	}

	@Override
	public Crosswalk createAccount(Crosswalk crosswalk) throws ResourceNotFoundException {
		log.info(" Inside method - createCrosswalk ", crosswalk);
		List<Therapy> therapies = therapyRepository.findAllById(
				List.of(Long.valueOf(crosswalk.getBnbMaterialNumber()), Long.valueOf(crosswalk.getSpProductId())));

		// validate sp product id and bnb material number
		Optional<Therapy> bnbTherapy = therapies.stream()
			.filter(t -> ChannelType.BUY_AND_BILL.equals(t.getChannelType()))
			.findFirst();
		Optional<Therapy> spTherapy = therapies.stream()
			.filter(t -> ChannelType.SPECIALITY_PHARMA.equals(t.getChannelType()))
			.findFirst();
		if (bnbTherapy.isEmpty() || spTherapy.isEmpty()) {
			throw new ResourceNotFoundException("SP Product Id or Bnb Material Number not found");
		}

		// Check if SP is already crosswalked
		if (null != crosswalkRepository.findBySpProductId(crosswalk.getSpProductId()))
			throw new IllegalArgumentException(
					"The SP product ID is already mapped to a Material, please review the existing mapping and retry.");
		return crosswalkRepository.save(crosswalk);
	}

	public Page<CrosswalkResponseDTO> findAll(Pageable paging, String query, List<String> status) {
		try {
			log.info(" Inside method - findAll, query: {} ", query);
			query = StringUtils.toRootLowerCase(query);
			String statusActive = CustomUtils.getStatus(status);

			return crosswalkRepository.findAllCrosswalks(paging, query, statusActive);
		}
		catch (Exception ex) {
			log.error("Exception occurred in fetching crosswalks", ex);
			throw ex;
		}
	}

}
