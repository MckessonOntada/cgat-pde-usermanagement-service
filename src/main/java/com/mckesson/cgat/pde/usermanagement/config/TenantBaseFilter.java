package com.mckesson.cgat.pde.usermanagement.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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

@Component
@Slf4j
public class TenantBaseFilter extends CGATBaseFilter {

	private final String PRACTICE = "practice";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			/*
			 * Fetching practice id from the request url. It would be like:
			 * /practice/{practiceId}/
			 */
			List<String> servletPath = Arrays.asList(request.getServletPath().split("/"));
			long tenantIdFromRequest = 1L;
			if (servletPath.contains(PRACTICE)) {
				tenantIdFromRequest = Long.parseLong(servletPath.get(servletPath.indexOf(PRACTICE) + 1));
			}

			LoggedInUserDetails loggedInUser = UserSecurityUtils.getLoggedInUser();
			boolean systemAdmin = UserRequestAuthorizationValidator.isSystemAdmin(loggedInUser);

			/*
			 * Only system admins are allowed to fetch data of different practice ids.
			 * Normal users can fetch data of only assigned practice.
			 */
			if (!systemAdmin && loggedInUser.getPracticeId() != tenantIdFromRequest) {
				throw new UnauthorizedAccessException(CustomUtils.UNAUTHORIZED_MESSAGE);
			}
			else {
				OrganizationContext.setCurrentOrgId(tenantIdFromRequest);
			}

		}
		catch (Exception e) {
			log.error("Error occurred in TenantFilter. Message: {} ", e.getMessage(), e);
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		}
		filterChain.doFilter(request, response);
		// TODO: kapil - clear id from thread local
	}

}
