package com.mckesson.cgat.pde.usermanagement.utils;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.jbat.auth.module.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class UserSecurityUtils {

	@Autowired
	private SecurityUtils securityUtils;

	@Value("${filter.enableDevLoginFilter:false}")
	private boolean enableDevLoginFilter;

	public void createContext(LoggedInUserDetails loggedInUser, HttpServletRequest request,
			HttpServletResponse response) {
		List<String> roleNames = new ArrayList<>();

		roleNames.add(loggedInUser.getRole().getName());
		Collection<? extends GrantedAuthority> authorities = roleNames.stream()
			.map(SimpleGrantedAuthority::new)
			.toList();
		UserCurrentRequestContext principal = new UserCurrentRequestContext(loggedInUser.getUserName(), "", authorities,
				loggedInUser);
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(principal,
					enableDevLoginFilter ? null : securityUtils.getToken(request, response), authorities));
		MDC.put("correlation_id", ("Correlation Id: " + securityUtils.getCorrelationId(request)));
		MDC.put("user_Id", (" User Id: " + loggedInUser.getId()));

	}

	public static LoggedInUserDetails getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			log.info("No logged in user found");
			return null;
		}

		UserCurrentRequestContext user = (UserCurrentRequestContext) authentication.getPrincipal();
		return user.getLoggedInUser();

	}

}
