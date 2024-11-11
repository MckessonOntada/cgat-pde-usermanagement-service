package com.mckesson.cgat.pde.usermanagement.service.impl.utils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import com.mckesson.cgat.pde.usermanagement.model.Practice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Component
public class PracticeUtils {

	public String uploadLogo(MultipartFile file) throws IllegalStateException, IOException {
		File directory = new File(
				System.getProperty("user.dir") + "/src/main/resources/static/" + file.getOriginalFilename());
		// Create the directory if it doesn't exist
		// if (!directory.exists())
		// directory.mkdirs();
		// file.transferTo(directory);
		return directory.getPath();
	}

	public void validatePractice(Practice practice, BindingResult bindingResult) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Practice>> violations = validator.validate(practice);

		for (ConstraintViolation<Practice> violation : violations) {
			bindingResult
				.addError(new FieldError("Practice", violation.getPropertyPath().toString(), violation.getMessage()));
		}
	}

}
