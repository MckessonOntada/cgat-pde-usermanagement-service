package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;

import lombok.Data;

@Data
public class UsersRoleBulkUploadResponse {
	
	private List<UserRoleRequest> records;
	private boolean hasErrors;

}
