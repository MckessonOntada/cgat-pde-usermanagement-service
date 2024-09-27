package com.mckesson.cgat.pde.usermanagement.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mckesson.cgat.pde.usermanagement.dto.KeyValueDto;
import com.mckesson.cgat.pde.usermanagement.dto.UserResponse;
import com.mckesson.cgat.pde.usermanagement.model.User;

@Component
public class Transformer {

	public UserResponse toUserDto(User user) {
		UserResponse userResponse = new UserResponse();
		userResponse.setActive(user.getIsActive());
		userResponse.setId(user.getId().toString());
		userResponse.setEmail(user.getEmail());
		userResponse.setDisplayName(user.getDisplayName());
		userResponse.setRoles(user.getRoles().stream()
				.map(role -> new KeyValueDto(role.getLabel(), role.getShortLabel(), role.getName(), role.getRoleType()))
				.collect(Collectors.toList()));
		userResponse.setUpdatedDate(user.getUpdatedDate());
		return userResponse;
	}

}
