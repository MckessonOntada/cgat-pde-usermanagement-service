package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.service.OnBehalfAuidtService;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/practices/{practiceId}/on-behalf-notification")
@Tag(name = "On Behalf", description = "Controller for sending email and auditing impersonated and user details.")
public class OnBehalfAudController {

	@Autowired
	private OnBehalfAuidtService auditService;

	@PostMapping
	@Operation(summary = "Audit log for User and Impersonated User along with sending email about the login.")
	public ResponseEntity<String> logImpersonation() {
		LoggedInUserDetails user = UserSecurityUtils.getLoggedInUser();

		if (user.getImpersonatedByUser() == null) {
			return ResponseEntity.badRequest().body("User is not impersonated");
		}

		auditService.logImpersonation(user, user.getImpersonatedByUser());
		return ResponseEntity.ok("Impersonation event logged successfully");
	}

}