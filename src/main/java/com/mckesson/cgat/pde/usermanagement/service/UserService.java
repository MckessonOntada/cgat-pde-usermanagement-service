package com.mckesson.cgat.pde.usermanagement.service;


import com.mckesson.cgat.pde.usermanagement.dto.UserResponse;
import com.mckesson.cgat.pde.usermanagement.dto.UserStudyResponse;
import com.mckesson.cgat.pde.usermanagement.dto.UsersRoleBulkUploadResponse;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.jbat.common.util.constant.UserRole;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User assignRole(String userId, String role) throws ResourceNotFoundException;

    Page<UserResponse> findAll(Pageable paging, String query, List<String> roles, List<String> status);

	UsersRoleBulkUploadResponse assignRoleforAllUsers(MultipartFile userRoleFile, boolean b, String authToken);
	
	User getUser(String userId) throws ResourceNotFoundException;

    List<UserStudyResponse> getUsersBasedOnRole(UserRole role, List<UUID> userIds);

    ResponseEntity<String> setLastLoggedInTimeForUser(String userId);
    ResponseEntity<String> setLogoutTimeForUser(String userId);
}
