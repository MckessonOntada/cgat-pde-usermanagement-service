package com.mckesson.cgat.pde.usermanagement.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@Service
public interface MaterialCrossWalkService {

	Boolean uploadCrossWalks(MultipartFile crossWalkData, ByteArrayOutputStream outputStream);

}
