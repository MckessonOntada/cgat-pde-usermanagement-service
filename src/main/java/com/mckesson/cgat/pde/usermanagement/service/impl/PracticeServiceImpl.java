package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mckesson.cgat.pde.usermanagement.dto.PracticeDto;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.PracticeUtils;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

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
	public String createPractice(MultipartFile logo, String data)
			throws IllegalStateException, IOException, ResourceNotFoundException {
		PracticeDto practiceRequestDto = practiceUtils.saveImage(logo, data);
		Practice modelObj = null;
		modelObj = practiceUtils.mapDtoToEntity(practiceRequestDto);
		if (practiceRequestDto.getId() != null) {
			if (!practiceRepository.findById(UUID.fromString(practiceRequestDto.getId())).isPresent())
				throw new ResourceNotFoundException("Requested Practice not found");
			modelObj.setId(UUID.fromString(practiceRequestDto.getId()));
			practiceRepository.save(modelObj);
			return CustomUtils.operation_type.update.name();
		}
		practiceRepository.save(modelObj);
		return CustomUtils.operation_type.create.name();
	}

	@Override
	public Practice getPractice(String id) {
		Optional<Practice> byId = practiceRepository.findById(UUID.fromString(id));
		if (byId == null || byId.isEmpty() || !byId.isPresent()) {
		}
		return byId.get();
	}

	@Override
	public List<Practice> getAllPractices() {
		return practiceRepository.findAll();
	}

	@Override
	public String deletePractice(String id) throws ResourceNotFoundException {
		Optional<Practice> byId = practiceRepository.findById(UUID.fromString(id));
		if (byId != null && !byId.isEmpty()) {
			byId.get().setIsActive(false);
			practiceRepository.save(byId.get());
			return byId.get().getName();
		}
		throw new ResourceNotFoundException("No Profile found with id : " + id);
	}

	@Override
	public Page<Practice> findAll(Pageable paging, String query, List<String> roles, List<String> status) {
//        log.info("Searching practices based on filter query:{},  roles:{}", query, roles);
		query = StringUtils.toRootLowerCase(query);
		Page<Practice> response;
		String statusActive = "ALL";
		if (!CollectionUtils.isEmpty(status)) {
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase("ACTIVE"))) {
				statusActive = "ACTIVE";
			}
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"))) {
				statusActive = "ACTIVE".equals(statusActive) ? "ALL" : "INACTIVE";
			}
		}

		response = practiceRepository.findAllByQueryText(query, statusActive, paging);

		return response;
	}

}
