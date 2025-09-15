package com.mckesson.cgat.pde.usermanagement.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Stream;

import static com.mckesson.jbat.auth.module.constant.UtilsConstant.SWAGGER_URL_TO_EXCLUDE;

@Component
@Slf4j
public class CGATBaseFilter extends OncePerRequestFilter {

	private AntPathMatcher pathMatcher;

	public static final String[] URLS_TO_EXCLUDE = { "/actuator/**", "/swagger-ui.html", "/swagger-ui/**",
			"/v3/api-docs/**", "/ws", "/favicon.ico", "/users-provisioning-requests",
			"/users-provisioning-requests/practices", "/public/idpConfig", "/practice/*/logo", "/global-config",
			"/contact-us", "/acs/**" };

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		filterChain.doFilter(request, response);
	}

	@PostConstruct
	public void init() {
		this.pathMatcher = new AntPathMatcher();
	}

	@Override
	public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		if (pathMatcher == null) {
			pathMatcher = new AntPathMatcher();
		}
		return Stream.of(URLS_TO_EXCLUDE).anyMatch(f -> pathMatcher.match(f, request.getServletPath()));
	}

}
