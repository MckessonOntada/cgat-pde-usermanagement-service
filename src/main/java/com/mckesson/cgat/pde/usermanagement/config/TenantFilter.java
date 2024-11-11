package com.mckesson.cgat.pde.usermanagement.config;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.cgat.pde.usermanagement.utils.UserRequestAuthorizationValidator;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class TenantFilter extends GeneralFilter {

	private final String PRACTICE = "practice";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			List<String> servletPatch = Arrays.asList(request.getServletPath().split("/"));
			long tenantIdFromRequest = 1L;
			if (servletPatch.contains(PRACTICE)) {
				tenantIdFromRequest = Long.parseLong(servletPatch.get(servletPatch.indexOf(PRACTICE) + 1));
			}

			LoggedInUserDetails loggedInUser = UserSecurityUtils.getLoggedInUser();
			boolean systemAdmin = UserRequestAuthorizationValidator.isSystemAdmin(loggedInUser);

			if (!systemAdmin && loggedInUser.getPracticeId() != tenantIdFromRequest) {
				throw new UnauthorizedAccessException(CustomUtils.UNAUTHORIZED_MESSAGE);
			}
			else {
				OrganizationContext.setCurrentOrgId(tenantIdFromRequest);
			}

		}
		catch (Exception e) {
			log.error("Error occurred in TenantFilter. Message: {} ", e.getMessage());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		}
		filterChain.doFilter(request, response);
	}

}
