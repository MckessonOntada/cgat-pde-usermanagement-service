package com.mckesson.cgat.pde.usermanagement.utils;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.UserModelRequestDto;
import com.mckesson.cgat.pde.usermanagement.model.UserModel;

@Service
public class UserModelUtil {

	public UserModel mapDtoTOEntity(UserModelRequestDto dto) {
		return new UserModel(null, dto.getFirstName(), dto.getMiddleName(), dto.getLastName(), dto.getSuffix(),
				dto.getAddress_1(), dto.getAddress_2(), dto.getCity(), dto.getState(), dto.getZip(), dto.getEmailId(),
				dto.getPhoneNumber(),dto.getIsActive());

	}

}
