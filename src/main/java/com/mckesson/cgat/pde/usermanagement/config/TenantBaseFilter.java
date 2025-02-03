package com.mckesson.cgat.pde.usermanagement.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
import org.springframework.util.AntPathMatcher;

import static com.mckesson.jbat.auth.module.constant.UtilsConstant.SWAGGER_URL_TO_EXCLUDE;

@Component
@Slf4j
public class TenantBaseFilter extends CGATBaseFilter {

	private final String PRACTICE = "practices";

	private AntPathMatcher pathMatcher;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			/*
			 * Fetching practice id from the request url. It would be like:
			 * /practice/{practiceId}/
			 */
			String requestServletPath = request.getServletPath().substring(1);
			List<String> servletPath = Arrays.asList(requestServletPath.split("/"));
			long tenantIdFromRequest = 1L;
			if (servletPath.contains(PRACTICE) && !requestServletPath.matches(PRACTICE)) {
				tenantIdFromRequest = Long.parseLong(servletPath.get(servletPath.indexOf(PRACTICE) + 1));
			}

			LoggedInUserDetails loggedInUser = UserSecurityUtils.getLoggedInUser();

			/*
			 * Only system admins are allowed to fetch data of different practice ids.
			 * Normal users can fetch data of only assigned practice.
			 */
			if (!isUserAuthorized(loggedInUser, tenantIdFromRequest)) {
				throw new UnauthorizedAccessException(CustomUtils.UNAUTHORIZED_MESSAGE);
			}
			else {
				OrganizationContext.setCurrentOrgId(tenantIdFromRequest);
			}

			filterChain.doFilter(request, response);

		}
		catch (Exception e) {

			log.error("Error occurred in TenantFilter. Message: {} ", e.getMessage(), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@Override
	public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		if (pathMatcher == null) {
			pathMatcher = new AntPathMatcher();
		}
		String[] urlsToExclude = { "/public/**", "/user/current", "/questionnaires/**", "/therapies/config/**",
				"/global/**", "/catalogs/**" };

		return Stream.of(urlsToExclude, CGATBaseFilter.URLS_TO_EXCLUDE)
			.flatMap(Stream::of)
			.anyMatch(f -> pathMatcher.match(f, request.getServletPath()));
	}

	public static boolean isUserAuthorized(LoggedInUserDetails loggedInUser, Long tenantIdFromRequest) {
		return (UserRequestAuthorizationValidator.isSystemAdmin(loggedInUser)
				|| loggedInUser.getPracticeId() == tenantIdFromRequest);
	}

}
