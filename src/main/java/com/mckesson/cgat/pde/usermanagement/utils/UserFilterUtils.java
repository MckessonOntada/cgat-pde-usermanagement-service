package com.mckesson.cgat.pde.usermanagement.utils;

import com.auth0.jwt.interfaces.Claim;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.jbat.auth.module.exception.BadRequestException;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

import static com.mckesson.jbat.common.util.constant.HeaderConstant.*;

@Component
@Slf4j
public class UserFilterUtils {

	@Autowired
	private UserService userService;

	private final String IMPERSONATE_HEADER = "x-imp-user-id";

	public LoggedInUserDetails buildUserContextDetail(String loggedInUserId, HttpServletRequest request)
			throws Exception {
		log.info("Building User Context Detail for userId {}.", loggedInUserId);
		LoggedInUserDetails loggedInUser = LoggedInUserDetails.builder().id(loggedInUserId).build();
		if (loggedInUserId != null) {
			User user = userService.getUser(Long.parseLong(loggedInUserId));
			loggedInUser = getLoggedInUserDetails(request, user);
		}

		// if (!CollectionUtils.isEmpty(payload)) {
		//
		// List<String> roleIds = payload.get(ROLE_CLAIM).asList(String.class);
		// List<String> permissions = payload.get(PERMISSION_CLAIM).asList(String.class);
		// loggedInUser.setRole(CollectionUtils.isEmpty(roleIds) ? null : roleIds.get(0));
		//
		// loggedInUser.setListOfPermissions(CollectionUtils.isEmpty(permissions) ? new
		// ArrayList<>() : permissions);
		// loggedInUser.setUserName(loggedInUserName);
		// }

		return loggedInUser;
	}

	public LoggedInUserDetails buildUserContextDetailFromOkta(String loggedInUserIdentifier, HttpServletRequest request)
			throws Exception {
		log.info("Building User Context Detail for user identifier {}.", loggedInUserIdentifier);
		User user = userService.getUserByEmail(loggedInUserIdentifier);

		LoggedInUserDetails loggedInUser = getLoggedInUserDetails(request, user);
		return loggedInUser;
	}

	public String getUserId(HttpServletRequest request, Map<String, Claim> payload) throws Exception {
		log.info("Fetching user id from request payload");
		String userId = null;
		if (!payload.containsKey(USER_CLAIM)) {
			throw new BadRequestException("Required Payload not found in JWT");
		}
		userId = payload.get(USER_CLAIM).asString();
		if (userId == null) {
			throw new ResourceNotFoundException("User id not found.");
		}
		return userId;
	}

	public String getUserName(HttpServletRequest request, Map<String, Claim> payload) throws Exception {
		log.info("Fetching user name from request payload");
		String userName = null;
		if (!payload.containsKey(USER_NAME_CLAIM)) {
			throw new BadRequestException("Required Payload not found in JWT");
		}
		userName = payload.get(USER_NAME_CLAIM).asString();
		if (userName == null) {
			throw new ResourceNotFoundException("User name not found.");
		}
		return userName;
	}

	private LoggedInUserDetails getLoggedInUserDetails(HttpServletRequest request, User principal)
			throws ResourceNotFoundException {

		LoggedInUserDetails currentLoggedInUser = buildLoggedInUser(principal);
		String impersonatedUserId = request.getHeader(IMPERSONATE_HEADER);
		if (StringUtils.isNotEmpty(impersonatedUserId)) {
			boolean hasOnBehalfPermission = principal.getPermissions().contains(Permission.ON_BEHALF_OF_USER);

			if (hasOnBehalfPermission) {
				// Make the impersonated user as the principal
				principal = userService.getUser(Long.parseLong(impersonatedUserId));
				LoggedInUserDetails loggedInUserDetails = buildLoggedInUser(principal);
				loggedInUserDetails.setImpersonatedByUser(currentLoggedInUser);
				return loggedInUserDetails;
			}

		}
		return currentLoggedInUser;
	}

	private LoggedInUserDetails buildLoggedInUser(User user) {
		Name userName = user.getName();
		return LoggedInUserDetails.builder()
			.id(String.valueOf(user.getId()))
			.name(userName)
			.contact(user.getContact())
			.role(user.getRole())
			.permissions(user.getPermissions())
			.practiceId(user.getPracticeId())
			.userName(user.getContact().getEmail())
			.build();
	}

}