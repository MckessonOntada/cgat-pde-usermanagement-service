package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.PracticeUtils;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PracticeServiceImpl implements PracticeService {

	private final PracticeUtils practiceUtils;

	private final PracticeRepository practiceRepository;

	private final RoleRepository roleRepository;

	public PracticeServiceImpl(PracticeUtils practiceUtils, PracticeRepository practiceRepository,
			RoleRepository roleRepository) {
		this.practiceRepository = practiceRepository;
		this.practiceUtils = practiceUtils;
		this.roleRepository = roleRepository;
	}

	@Override
	@Transactional
	public Practice createPractice(Practice practice, MultipartFile logo) throws IllegalStateException, IOException {
		boolean isCreatePractice = false;
		if (practice.getId() == null)
			isCreatePractice = true;
		Practice savedPractice = practiceRepository.save(practice);
		if (null != logo && !logo.isEmpty()) {
			String logoURL = practiceUtils.uploadLogo(logo, savedPractice.getId());
			practice.setLogoUrl(logoURL);
		}
		if (isCreatePractice) {
			Role adminRole = Role.builder()
				.name(CustomUtils.ORG_ADMIN_ROLE_NAME)
				.description(CustomUtils.ORG_ADMIN_ROLE_DESC)
				.isActive(Boolean.TRUE)
				.practiceId(savedPractice)
				.permissions(Set.of(Permission.values()))
				.build();
			roleRepository.save(adminRole);
		}
		return savedPractice;
	}

	@Override
	public Practice getPractice(String id) throws ResourceNotFoundException {
		Optional<Practice> practiceOpt = practiceRepository.findById(Long.valueOf(id));
		return practiceOpt.orElseThrow(() -> new ResourceNotFoundException("Practice not found with id : " + id));
	}

	@Override
	public byte[] getPracticeLogo(String id) throws ResourceNotFoundException {
		Practice practice = practiceRepository.findById(Long.valueOf(id))
			.orElseThrow(() -> new ResourceNotFoundException("Practice not found with id : " + id));
		return practiceUtils.fetchLogo(practice.getLogoUrl());
	}

	@Override
	public void deletePractice(String id) throws ResourceNotFoundException {
		Optional<Practice> practiceOpt = practiceRepository.findById(Long.valueOf(id));
		Practice practice = practiceOpt
			.orElseThrow(() -> new ResourceNotFoundException("Practice not found with id : " + id));
		practice.setIsActive(false);
		practiceRepository.save(practice);
	}

	@Override
	public Page<Practice> findAll(Pageable paging, String query, List<String> status, List<String> location) {
		log.debug("Searching practices based on filter query:{},  roles:{}", query);
		query = StringUtils.toRootLowerCase(query);
		Page<Practice> response;
		String statusActive = CustomUtils.object_status.ALL.name();
		if (!CollectionUtils.isEmpty(status)) {
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.ACTIVE.name()))) {
				statusActive = CustomUtils.object_status.ACTIVE.name();
			}
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.INACTIVE.name()))) {
				statusActive = CustomUtils.object_status.ACTIVE.name().equals(statusActive)
						? CustomUtils.object_status.ALL.name() : CustomUtils.object_status.INACTIVE.name();
			}
		}
		response = practiceRepository.findAllByQueryText(query, statusActive, paging, location);
		return response;
	}

}
