package com.mckesson.cgat.pde.usermanagement.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.mckesson.jbat.auth.module.util.CurrentRequestContext;
import com.mckesson.jbat.auth.module.util.SecurityUtils;
import com.mckesson.jbat.common.util.constant.UserPermissionConstants;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.jbat.common.util.constant.UserRole;
import com.mckesson.cgat.pde.usermanagement.dto.UserResponse;
import com.mckesson.cgat.pde.usermanagement.dto.UserStudyResponse;
import com.mckesson.cgat.pde.usermanagement.dto.UsersRoleBulkUploadResponse;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.FileValidationUtils;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.UserAbstractionSyncUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller providing open API endpoints for direct and unauthenticated public interaction.
 * <p>
 * Note: As this controller does not require authentication, ensure that sensitive operations and data
 * are not exposed through this API.
 */
@RestController
@Tag(name = "Users", description = "Controller for Manages user-related operations and data.")
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {

    private static final String SORT_ORDER_ASC = "asc";

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private UserAbstractionSyncUtils userAbstractionSyncUtils;
    
    
    @GetMapping("/{userId}")
    @Operation(summary = "Fetch User Details")
	public ResponseEntity<MappingJacksonValue> assignRoleToUser(@PathVariable String userId)
			throws ResourceNotFoundException {
		User user = userService.getUser(userId);
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id","name", "label", "shortLabel",
				"roleType");
		FilterProvider filters = new SimpleFilterProvider().addFilter("PermissionFilter", filter);
		MappingJacksonValue mappingJacksonUserValue = new MappingJacksonValue(user);
		mappingJacksonUserValue.setFilters(filters);
		return new ResponseEntity<>(mappingJacksonUserValue, HttpStatus.OK);
	}
      

    @PostMapping("/{userId}/role")
    @Operation(summary = "Assign roles to a user.")
    @PreAuthorize("@securityUtils.hasPermission('"+ UserPermissionConstants.UPDT_INDIVIDUAL_USER_ROLE +"')")
    public ResponseEntity<User> assignRoleToUser(@PathVariable String userId, @RequestParam String role) throws ResourceNotFoundException {
        User user = userService.assignRole(userId, role);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    
    @PostMapping("/roles")
    @Operation(summary = "Assign roles to all users.")
    @PreAuthorize("@securityUtils.hasPermission('"+ UserPermissionConstants.BULK_UPLD_USERS +"')")
    public ResponseEntity<UsersRoleBulkUploadResponse> assignRoleForAll(@RequestParam("userRoleMappingCSV") MultipartFile userRoleFile, @RequestParam("preview") Boolean preview, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) throws FileNotFoundException {
        FileValidationUtils.validateMultiPartFile(userRoleFile);
        UsersRoleBulkUploadResponse bulkUploadResponse = userService.assignRoleforAllUsers(userRoleFile,!preview, authToken);
        return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
    }


    @GetMapping
//    @PreAuthorize("@securityUtils.hasPermission('"+ UserPermissionConstants.VIEW_USERS +"')") //Commented to allow user search from CA search screen
    @Operation(summary = "Fetch all Users based on filter provided with pagination capabilities.")
    Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "displayName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) List<String> roles) {
        Sort sort =
                SORT_ORDER_ASC.equalsIgnoreCase(sortOrder)
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();
        Pageable paging = PageRequest.of(pageNo, pageSize, sort);

        Page<UserResponse> pagedResult = userService.findAll(paging, query, roles, status);

        return pagedResult;
    }

    @PostMapping("/filterByRole")
    @Operation(summary = "Filter Users for a specific role")
    public List<UserStudyResponse> getUsersBasedOnRole(
            @RequestParam UserRole role,
            @RequestBody List<String> userIds) {
        List<UUID> userIdsUuid = userIds.stream().map(UUID::fromString).collect(Collectors.toList());
        return userService.getUsersBasedOnRole(role, userIdsUuid);
    }

    @PostMapping("/assign/study")
    @Operation(summary = "Filter Users for assigning to a study")
    public Mono<ResponseEntity<String>> getUsersToAssignStudy(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        return userAbstractionSyncUtils.syncPatientswithChartAbstraction(authToken);
    }

    @PostMapping("/postLogin")
    @Operation(summary = "Set Last Logged In time to users")
    public ResponseEntity<String> setLastLoggedInTimeForUser() {
        CurrentRequestContext loginInfo = (CurrentRequestContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = loginInfo.getLoggedInUser().getId();
        return userService.setLastLoggedInTimeForUser(userId);
    }
    @PostMapping("/postLogout")
    @Operation(summary = "Set Logout time to users")
    public ResponseEntity<String> setLogoutTimeForUser() {
        CurrentRequestContext loginInfo = (CurrentRequestContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = loginInfo.getLoggedInUser().getId();
        return userService.setLogoutTimeForUser(userId);
    }

}
