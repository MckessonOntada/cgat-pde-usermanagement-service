package com.mckesson.cgat.pde.usermanagement.service.impl.utils;

import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.jbat.common.util.service.MessageResolver;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

public class FileValidationUtils {

	public static void validateMultiPartFile(MultipartFile file) throws FileNotFoundException {
		if (file == null || file.getSize() == 0) {
			throw new FileNotValidException(MessageResolver.resolve("userupload.message.blankFileMsg"));
		}
		if (!file.getOriginalFilename().endsWith(".csv")) {
			throw new FileNotValidException(MessageResolver.resolve("userupload.message.fileNotCsvMsg"));
		}

	}

}
