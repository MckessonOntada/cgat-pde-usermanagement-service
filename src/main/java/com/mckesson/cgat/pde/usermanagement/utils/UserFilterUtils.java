package com.mckesson.cgat.pde.usermanagement.utils;

import com.auth0.jwt.interfaces.Claim;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.jbat.auth.module.exception.BadRequestException;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

import static com.mckesson.jbat.common.util.constant.HeaderConstant.*;

@Component
@Slf4j
public class UserFilterUtils {

	@Autowired
	private UserService userService;

	public LoggedInUserDetails buildUserContextDetail(String loggedInUserId, String loggedInUserName,
			Map<String, Claim> payload) throws Exception {
		log.info("Building User Context Detail for userId {}.", loggedInUserId);
		LoggedInUserDetails loggedInUser = LoggedInUserDetails.builder().id(loggedInUserId).build();
		// TODO - kapil, remove the following code to fetch the data from db. Use payload
		// claims for it
		if (loggedInUserId != null) {
			User user = userService.getUser(Long.parseLong(loggedInUserId));
			if (user != null) {
				loggedInUser.setName(user.getName());
				loggedInUser.setRole(user.getRole());
				loggedInUser.setListOfPermissions(user.getPermissions());
				loggedInUser.setPracticeId(user.getPracticeId());
			}
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

	public LoggedInUserDetails buildUserContextDetail(HttpServletRequest request, Map<String, Claim> payload)
			throws Exception {
		String loggedInUserId = getUserId(request, payload);
		String loggedInUserName = getUserName(request, payload);
		return buildUserContextDetail(loggedInUserId, loggedInUserName, payload);
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

}