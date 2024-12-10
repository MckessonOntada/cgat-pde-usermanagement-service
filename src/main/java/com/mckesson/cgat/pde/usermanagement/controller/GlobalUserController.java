package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Users", description = "Controller for Managing user-related operations and data.")
@RequestMapping(path = "/user", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalUserController {

	@GetMapping("/current")
	@Operation(summary = "Fetch current logged in user from Security context")
	public LoggedInUserDetails getLoggedInUserDetails() {
		return UserSecurityUtils.getLoggedInUser();
	}

}
