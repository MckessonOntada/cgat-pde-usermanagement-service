package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.service.OnBehalfAuidtService;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/on-behalf")
public class OnBehalfAudController {

	@Autowired
	private OnBehalfAuidtService auditService;

	@PostMapping("/log")
	public ResponseEntity<String> logImpersonation() {
		LoggedInUserDetails user = UserSecurityUtils.getLoggedInUser();

		if (user.getImpersonatedByUser() == null) {
			return ResponseEntity.badRequest().body("User is not impersonated");
		}

		auditService.logImpersonation(user, user.getImpersonatedByUser());
		return ResponseEntity.ok("Impersonation event logged successfully");
	}

}