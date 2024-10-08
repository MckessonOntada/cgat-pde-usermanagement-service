package com.mckesson.cgat.pde.usermanagement.service.impl.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.dto.PracticeDto;
import com.mckesson.cgat.pde.usermanagement.model.Practice;

@Component
public class PracticeUtils {

	@Autowired
	private ObjectMapper objectMapper;

	public Practice mapDtoToEntity(PracticeDto dto) {
		return Practice.builder()
				//.address1(dto.getAddress1()).address2(dto.getAddress2()).city(dto.getCity())
				.firstName(dto.getContact_first_name()).middleName(dto.getContact_middle_name())
				.lastName(dto.getContact_last_name()).email(dto.getEmail()).fax(dto.getFax())
				.phone(dto.getPhone_number()).logoUrl(dto.getPractice_logo_url()).name(dto.getPractice_name()).build();

	}

	public PracticeDto saveImage(MultipartFile file, String data) throws IllegalStateException, IOException {
		PracticeDto practiceRequestDto = null;
		File localFile = convertMultipartFileToFile(file);
		try {
			practiceRequestDto = objectMapper.readValue(data, PracticeDto.class);
			practiceRequestDto.setPractice_logo_url(localFile.getPath().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return practiceRequestDto;
	}

	private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
		File directory = new File(
				System.getProperty("user.dir") + "/src/main/resources/static/" + multipartFile.getOriginalFilename());
		// Create the directory if it doesn't exist
		if (!directory.exists())
			if (directory.mkdirs())
				;
		multipartFile.transferTo(directory);
		return directory;
	}

}
