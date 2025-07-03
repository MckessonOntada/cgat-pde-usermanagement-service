package com.mckesson.cgat.pde.usermanagement.service.impl.utils;

import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.jbat.common.util.service.MessageResolver;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class FileValidationUtils {

	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/png", "image/jpg", "image/jpeg");

	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg");

	public static boolean isImageValid(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return false;
		}

		String contentType = file.getContentType();
		String fileName = file.getOriginalFilename();

		// Check MIME type
		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			return false;
		}

		// Optional: Check file extension
		if (fileName != null && fileName.contains(".")) {
			String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
			return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
		}

		return false;
	}

	public static void validateMultiPartFile(MultipartFile file) throws FileNotValidException {
		if (file == null || file.getSize() == 0) {
			throw new FileNotValidException(MessageResolver.resolve("userupload.message.blankFileMsg"));
		}
		if (!file.getOriginalFilename().endsWith(".csv")) {
			throw new FileNotValidException(MessageResolver.resolve("userupload.message.fileNotCsvMsg"));
		}

	}

}
