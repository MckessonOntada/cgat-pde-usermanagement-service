package com.mckesson.cgat.pde.usermanagement.config;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Aspect
@Component
public class PermissionAspect {

	@Before("@annotation(requirePermissions)")
	public void checkPermissions(RequirePermissions requirePermissions) throws UnauthorizedAccessException {
		// Retrieve the permissions from the annotation
		Permission[] requiredPermissions = requirePermissions.value();

		LoggedInUserDetails loggedInUserDetails = UserSecurityUtils.getLoggedInUser();
		Set<Permission> permissions = loggedInUserDetails.getPermissions();

		// Check if user has all the required permissions
		boolean hasPermissions = Arrays.stream(requiredPermissions).allMatch(permissions::contains);

		if (!hasPermissions) {
			throw new UnauthorizedAccessException(CustomUtils.UNAUTHORIZED_MESSAGE);
		}
	}

}
