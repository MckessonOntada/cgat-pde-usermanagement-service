package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
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

	public PracticeServiceImpl(PracticeUtils practiceUtils, PracticeRepository practiceRepository) {
		this.practiceRepository = practiceRepository;
		this.practiceUtils = practiceUtils;
	}

	@Override
	@Transactional
	public Practice createPractice(Practice practice, MultipartFile logo) throws IllegalStateException, IOException {
		String logoURL = practiceUtils.uploadLogo(logo); // "http://some-random-url.com";
		practice.setLogoUrl(logoURL);
		return practiceRepository.save(practice);
	}

	@Override
	public Practice getPractice(String id) throws ResourceNotFoundException {
		Optional<Practice> practiceOpt = practiceRepository.findById(Long.valueOf(id));
		return practiceOpt.orElseThrow(() -> new ResourceNotFoundException("Practice not found with id : " + id));
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
	public Page<Practice> findAll(Pageable paging, String query, List<String> status) {
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
		response = practiceRepository.findAllByQueryText(query, statusActive, paging);
		return response;
	}

}
