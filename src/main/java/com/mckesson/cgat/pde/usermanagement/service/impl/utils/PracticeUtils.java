package com.mckesson.cgat.pde.usermanagement.service.impl.utils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.model.Practice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Component
public class PracticeUtils {

	private final ObjectMapper objectMapper;

	public PracticeUtils(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

//	public Practice mapDtoToEntity(PracticeDto dto) {
//		return Practice.builder().name(dto.getPractice_name())
//				.contactPerson(
//						new Name(dto.getContact_first_name(), dto.getContact_middle_name(), dto.getContact_last_name()))
//				.address(Address.builder().address1(dto.getAddress1()).address2(dto.getAddress2()).city(dto.getCity())
//						.state(dto.getState()).zip(dto.getZip()).build())
//				.contact(new Contact(dto.getEmail(), dto.getPhone_number(), dto.getFax()))
//				.logoUrl(dto.getPractice_logo_url()).isActive(false).build();
//	}

	public String uploadLogo(MultipartFile file) throws IllegalStateException, IOException {
		File directory = new File(
				System.getProperty("user.dir") + "/src/main/resources/static/" + file.getOriginalFilename());
		// Create the directory if it doesn't exist
//		if (!directory.exists())
//			directory.mkdirs();
//		file.transferTo(directory);
		return directory.getPath();
	}

	public void validatePractice(Practice practice, BindingResult bindingResult) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Practice>> violations = validator.validate(practice);

		for (ConstraintViolation<Practice> violation : violations) {
			bindingResult.addError(new ObjectError(violation.getPropertyPath().toString(), violation.getMessage()));
		}
	}

}
