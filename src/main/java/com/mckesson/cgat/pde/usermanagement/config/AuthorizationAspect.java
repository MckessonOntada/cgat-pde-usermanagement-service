package com.mckesson.cgat.pde.usermanagement.config;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Aspect
@Slf4j
@Component
public class AuthorizationAspect {

	@Autowired
	private HttpServletRequest request;

	@Before("@annotation(requirePermissions)")
	public void checkPermissions(RequirePermissions requirePermissions) throws UnauthorizedAccessException {
		// Retrieve the permissions from the annotation
		Permission[] requiredPermissions = requirePermissions.value();

		LoggedInUserDetails loggedInUserDetails = UserSecurityUtils.getLoggedInUser();
		Set<Permission> permissions = loggedInUserDetails.getPermissions();

		// Check if user has all the required permissions
		boolean hasPermissions = Arrays.stream(requiredPermissions).anyMatch(permissions::contains);

		if (!hasPermissions) {
			String requestServletPath = request.getServletPath();
			log.error("{} not authorized to access url: {} , ", loggedInUserDetails.getId(), requestServletPath);
			throw new UnauthorizedAccessException(CustomUtils.UNAUTHORIZED_MESSAGE);
		}
	}

	@Before("@annotation(requireRole)")
	public void checkRoleAuthentication(RequireRole requireRole) throws UnauthorizedAccessException {
		// Retrieve the role from the annotation
		LoggedInUserDetails loggedInUserDetails = UserSecurityUtils.getLoggedInUser();
		Role role = loggedInUserDetails.getRole();

		// Check if user has the required role

		if (!Arrays.asList(requireRole.value()).contains(role.getName())) {
			throw new UnauthorizedAccessException(CustomUtils.UNAUTHORIZED_MESSAGE);
		}

	}

}
