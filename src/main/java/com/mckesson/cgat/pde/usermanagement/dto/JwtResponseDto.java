package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtResponseDto {

	private List<String> roles;

	private List<String> permissions;

	private String userName;

	private String userId;

}
