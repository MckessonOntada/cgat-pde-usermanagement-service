package com.mckesson.cgat.pde.usermanagement.config;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.utils.UserFilterUtils;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Component
public class DevUserAuthenticationBaseFilter extends CGATBaseFilter {

	@Autowired
	private UserFilterUtils userFilterUtils;

	@Autowired
	private UserSecurityUtils userSecurityUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		LoggedInUserDetails userRequest;
		String userId = request.getHeader("userId");

		if (StringUtils.isNotEmpty(userId)) {
			try {
				userRequest = userFilterUtils.buildUserContextDetail(userId, "devUserName", new HashMap<>());
				userSecurityUtils.createContext(userRequest, request, response);
			}
			catch (Exception e) {
				log.error("Error occurred while creating context in GenericFilter {}", e.getMessage());
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
			}
		}

		else {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			log.error("userId not found in header");
			return;
		}
		filterChain.doFilter(request, response);
	}

}