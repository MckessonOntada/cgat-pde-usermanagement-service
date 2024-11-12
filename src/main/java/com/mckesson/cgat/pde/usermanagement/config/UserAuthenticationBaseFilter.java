package com.mckesson.cgat.pde.usermanagement.config;

import com.auth0.jwt.interfaces.Claim;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.utils.UserFilterUtils;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import com.mckesson.jbat.auth.module.exception.UserAuthenticationException;
import com.mckesson.jbat.auth.module.util.JWTConstants;
import com.mckesson.jbat.auth.module.util.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class UserAuthenticationBaseFilter extends CGATBaseFilter {

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	private UserFilterUtils userFilterUtils;

	@Autowired
	private UserSecurityUtils userSecurityUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		LoggedInUserDetails userRequest = null;

		if (null != request.getHeader(JWTConstants.JWT_HEADER)) {
			try {
				userRequest = createUserRequestWithOKTAToken(request, response);
				userSecurityUtils.createContext(userRequest, request, response);
			}
			catch (Exception e) {
				log.error("Error occurred while creating context in GenericFilter {}", e.getMessage());
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
			}
		}
		else {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
		}

		filterChain.doFilter(request, response);

	}

	private LoggedInUserDetails createUserRequestWithOKTAToken(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Map<String, Claim> payload = securityUtils.getPayloadByToken(request, response);
			return userFilterUtils.buildUserContextDetail(request, payload);
		}
		catch (Exception e) {
			log.error("Error in createUserRequestWithOKTAToken method while Setting Current User Id {}",
					e.getMessage());
			throw new UserAuthenticationException("Error in createUserRequestWithOKTAToken", e);
		}
	}

}
