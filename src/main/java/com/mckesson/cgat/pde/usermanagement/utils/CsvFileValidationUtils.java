package com.mckesson.cgat.pde.usermanagement.utils;

import com.mckesson.cgat.pde.usermanagement.exception.custom.CsvFileNotValidException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class CsvFileValidationUtils {

	public static void validateCSVFile(MultipartFile file) {
		String contentType = file.getContentType();
		String originalFilename = file.getOriginalFilename();

		boolean isValidType = StringUtils.isNotEmpty(contentType)
				&& (contentType.equals("text/csv") || contentType.equals("application/vnd.ms-excel"));

		boolean isValidExtension = StringUtils.isNotEmpty(originalFilename)
				&& originalFilename.toLowerCase().endsWith(".csv");

		if (!isValidType || !isValidExtension) {
			throw new CsvFileNotValidException(List.of("invalid.csv.file.type"));
		}

		if (file.isEmpty()) {
			throw new CsvFileNotValidException(List.of("invalid.csv.file.empty"));
		}
	}

}
