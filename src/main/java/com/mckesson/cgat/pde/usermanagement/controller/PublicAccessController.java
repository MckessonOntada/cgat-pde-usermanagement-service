package com.mckesson.cgat.pde.usermanagement.controller;

import static com.mckesson.jbat.common.util.constant.HeaderConstant.PERMISSION_CLAIM;
import static com.mckesson.jbat.common.util.constant.HeaderConstant.ROLE_CLAIM;
import static com.mckesson.jbat.common.util.constant.HeaderConstant.USER_CLAIM;
import static com.mckesson.jbat.common.util.constant.HeaderConstant.USER_NAME_CLAIM;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.Claim;
import com.mckesson.cgat.pde.usermanagement.dto.IdentityProviderMetaData;
import com.mckesson.cgat.pde.usermanagement.dto.JwtResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.service.IDPService;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.jbat.auth.module.service.JWTExtractor;
import com.mckesson.jbat.auth.module.util.JWTHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * Controller providing open API endpoints for direct and unauthenticated public
 * interaction.
 *
 * Note: As this controller does not require authentication, ensure that sensitive
 * operations and data are not exposed through this API.
 */
@ComponentScan(basePackages = "com.mckesson.jbat.auth.module")
@RestController
@Tag(name = "Public Access", description = "Open and unauthenticated API endpoints for public access.")
@RequestMapping(path = "/public", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@ComponentScan(basePackages = "com.mckesson.jbat.auth.module")
public class PublicAccessController {

	@Autowired
	private IDPService idpService;

	@Autowired
	private UserService userService;

	@Autowired
	JWTExtractor bearerJWTExtractor;

	@GetMapping(value = { "/idpConfig" })
	@ResponseBody
	@Operation(summary = "Retrieve Identity Provider Metadata")
	public IdentityProviderMetaData getIDPConfig() throws Exception {
		log.info("AuthenticationServiceWebAPI::getIDPConfig() starts");

		return idpService.getIDPInfo();
	}

	@GetMapping(value = { "/jwt" })
	@ResponseBody
	@Operation(summary = "Decode jwt token")
	public JwtResponseDto decodeJwtToken(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authToken) throws Exception {
		log.info("decodeJwtToken() starts");

		String bearerToken = bearerJWTExtractor.getJWTTokenFromHeader(authToken);
		Map<String, Claim> payload = JWTHelper.getPayload(bearerToken);
		if (!CollectionUtils.isEmpty(payload)) {
			Map<String, Object> profileMap = payload.get("Profile").asMap();
			String userIdentifier = String.valueOf(profileMap.get("login"));
			User principal = userService.getUserByEmail(userIdentifier);
			return JwtResponseDto.builder()
				.roles(Collections.singletonList(principal.getRole().getName()))
				.permissions(principal.getPermissions().stream().map(Permission::name).toList())
				.userName(principal.getName().getFirstName() + " " + principal.getName().getLastName())
				.userId(String.valueOf(principal.getId()))
				.build();
		}
		return null;
	}

}
