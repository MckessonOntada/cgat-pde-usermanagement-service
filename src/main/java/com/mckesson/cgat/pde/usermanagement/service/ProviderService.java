package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;

public interface ProviderService {

	Boolean uploadUsers(MultipartFile userDataFile, Long practiceId, OutputStream outputStream)
			throws FileNotValidException;

}
