package com.mckesson.cgat.pde.usermanagement.utils;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.UserType;

public class UserRequestAuthorizationValidator {

	public static final String SYSTEM_ADMIN_ROLE = "System Admin";

	private static final String UNAUTHORIZED_MESSAGE = "User is not authorized for this action";

	private static final Long SYSTEM_LEVEL_ORGANISATION_ID = 1L;

	private static boolean isSystemAdmin(LoggedInUserDetails loggedInUser) {
		return SYSTEM_ADMIN_ROLE.equals(loggedInUser.getRole().getName());
	}

	public static void canLoggedInUserManageUserType(UserType requestedUserType) throws UnauthorizedAccessException {
		LoggedInUserDetails loggedInUser = UserSecurityUtils.getLoggedInUser();

		if (UserType.ADMIN.equals(requestedUserType)) {
			/*
			 * Only System Admins are allowed to view or create/edit System Admins. Also
			 * System Admins can be created only under global organization having the
			 * identifier as 1
			 */
			if (!isSystemAdmin(loggedInUser)
					|| !OrganizationContext.getCurrentOrgId().equals(SYSTEM_LEVEL_ORGANISATION_ID)) {
				throw new UnauthorizedAccessException(UNAUTHORIZED_MESSAGE);
			}
		}
		else {
			/*
			 * Request is to view create/edit General user, so any one with the permission
			 * to Create/Edit or View a user is allowed to do so. Also General users cant
			 * be created under Global Org.
			 */
			if (!hasPermission(loggedInUser, "User_Create")
					|| OrganizationContext.getCurrentOrgId().equals(SYSTEM_LEVEL_ORGANISATION_ID)) {
				throw new UnauthorizedAccessException(UNAUTHORIZED_MESSAGE);
			}

		}
	}

	private static boolean hasPermission(LoggedInUserDetails loggedInUser, String permission) {
		/*
		 * Set<String> permissions = loggedInUser.getPermissions() .stream()
		 * .map(Permission::getName) .collect(Collectors.toSet());
		 */
		// To:do return permissions.contains(permission);
		return true;
	}

}
