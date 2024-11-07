package com.mckesson.cgat.pde.usermanagement.utils;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserType;

import java.util.Set;
import java.util.stream.Collectors;

public class UserManagementUtils {

	private static boolean checkIfSystemAdmin(long roleId, long practiceId) {
		return roleId == 1 && practiceId == 1;
	}

	public static void validateUserCreation(LoggedInUserDetails loggedInUser, UserType userType)
			throws UnauthorizedAccessException {

		boolean ifSystemAdminUser = checkIfSystemAdmin(loggedInUser.getRole().getId(), loggedInUser.getPracticeId());
		if (UserType.SystemAdmin.equals(userType)) {
			if (!ifSystemAdminUser) {
				throw new UnauthorizedAccessException("User is not authorized for this action");
			}
			else
				return;
		}
		if (!ifSystemAdminUser) {
			// TODO: kapil replace string with enum
			boolean hasPermission = hasPermission(loggedInUser, "User_Create");
			if (!hasPermission) {
				throw new UnauthorizedAccessException("User is not authorized for this action");
			}
		}
	}

	private static boolean hasPermission(LoggedInUserDetails loggedInUser, String permission) {
		Set<String> permissions = loggedInUser.getPermissions()
			.stream()
			.map(Permission::getName)
			.collect(Collectors.toSet());
		return permissions.contains(permission);
	}

	public static void validateFetchUserRequest(LoggedInUserDetails loggedInUser, String practiceId)
			throws UnauthorizedAccessException {
		boolean ifSystemAdminUser = checkIfSystemAdmin(loggedInUser.getRole().getId(), loggedInUser.getPracticeId());
		if (!ifSystemAdminUser && Long.parseLong(practiceId) == 1) {
			throw new UnauthorizedAccessException("User is not authorized for this action");
		}
	}

}
