package com.mckesson.cgat.pde.usermanagement.service.impl.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import java.util.Arrays;
import java.util.Set;

import org.ini4j.InvalidFileFormatException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.jbat.common.util.service.AzureBlobContainerFileUploadService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Component
public class PracticeUtils {

	private final AzureBlobContainerFileUploadService azureBlobContainerFileUploadService;

	public PracticeUtils(AzureBlobContainerFileUploadService azureBlobContainerFileUploadService) {
		this.azureBlobContainerFileUploadService = azureBlobContainerFileUploadService;
	}

	public String uploadLogo(MultipartFile file, Long practiceId) throws IllegalStateException, IOException {
		return azureBlobContainerFileUploadService.uploadAndGetURL(file.getBytes(),
				Path.of("logos/" + practiceId + "." + FilenameUtils.getExtension(file.getOriginalFilename())), true);
	}

	public void validatePractice(Practice practice, BindingResult bindingResult, MultipartFile logo)
			throws InvalidFileFormatException {
		if (null != logo && !logo.isEmpty() && !isValidImageFormat(logo.getOriginalFilename()))
			throw new InvalidFileFormatException(
					"The file you uploaded is not in a supported image format. Please upload an image in one of the following formats: JPEG/JPG, PNG, GIF, BMP, TIFF/TIF.");

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Practice>> violations = validator.validate(practice);

		for (ConstraintViolation<Practice> violation : violations) {
			bindingResult
				.addError(new FieldError("Practice", violation.getPropertyPath().toString(), violation.getMessage()));
		}
	}

	public boolean isValidImageFormat(String fileName) {
		String[] validExtensions = { "jpeg", "jpg", "png", "tiff", "tif", "bmp", "gif" };
		String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		return Arrays.asList(validExtensions).contains(fileExtension);
	}

}
