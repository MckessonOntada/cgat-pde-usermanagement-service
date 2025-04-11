package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Crosswalk;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.CrosswalkRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.CrosswalkService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;

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

	public Page<CrosswalkResponseDTO> findAll(Pageable paging, String query, List<String> status) {
		try {
			log.info(" Inside method - findAll, query: {} ", query);
			query = StringUtils.toRootLowerCase(query);
			String statusActive = CustomUtils.object_status.ALL.name();
			if (!CollectionUtils.isEmpty(status)) {
				boolean hasActive = status.stream()
					.anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.ACTIVE.name()));
				boolean hasInactive = status.stream()
					.anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.INACTIVE.name()));

				if (hasActive)
					statusActive = CustomUtils.object_status.ACTIVE.name();

				if (hasInactive)
					statusActive = CustomUtils.object_status.ACTIVE.name().equals(statusActive)
							? CustomUtils.object_status.ALL.name() : CustomUtils.object_status.INACTIVE.name();
			}

			Page<Crosswalk> crosswalkPage = crosswalkRepository.findAllCrosswalks(paging, query, statusActive);
			List<Therapy> allTherapies = therapyRepository.findAll();

			Map<String, Therapy> therapyNumberWithTypeMap = allTherapies.stream()
				.collect(Collectors.toMap(t -> t.getMaterialNumber() + "_" + t.getChannelType(), t -> t));

			List<CrosswalkResponseDTO> crosswalkResponseDtos = crosswalkPage.getContent()
				.stream()
				.map(crosswalk -> mapToDto(crosswalk, therapyNumberWithTypeMap))
				.collect(Collectors.toList());

			return new PageImpl<>(crosswalkResponseDtos, paging, crosswalkPage.getTotalElements());
		}
		catch (Exception ex) {
			log.error("Exception occurred in fetching crosswalks", ex);
			throw ex;
		}
	}

	private CrosswalkResponseDTO mapToDto(Crosswalk crosswalk, Map<String, Therapy> therapyMap) {
		Therapy bnbTherapy = therapyMap.get(crosswalk.getBnbMaterialNumber() + "_" + ChannelType.BUY_AND_BILL);
		Therapy spTherapy = therapyMap.get(crosswalk.getBnbMaterialNumber() + "_" + ChannelType.SPECIALITY_PHARMA);

		return CrosswalkResponseDTO.builder()
			.bnbMaterialDescription(bnbTherapy.getMaterialDescription())
			.bnbMaterialNumber(bnbTherapy.getMaterialNumber())
			.bnbNdc(bnbTherapy.getNdcNumber())
			.spProductName(spTherapy.getMaterialDescription())
			.spProductId(spTherapy.getMaterialNumber())
			.spNdc(spTherapy.getNdcNumber())
			.build();
	}

}
