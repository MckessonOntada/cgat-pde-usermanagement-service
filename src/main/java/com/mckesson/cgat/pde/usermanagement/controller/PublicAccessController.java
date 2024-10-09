package com.mckesson.cgat.pde.usermanagement.controller;

import com.auth0.jwt.interfaces.Claim;
import com.mckesson.jbat.auth.module.service.BearerJWTExtractor;
import com.mckesson.jbat.auth.module.service.JWTExtractor;
import com.mckesson.jbat.auth.module.util.JWTHelper;
import com.mckesson.cgat.pde.usermanagement.dto.JwtResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.mckesson.cgat.pde.usermanagement.dto.IdentityProviderMetaData;
import com.mckesson.cgat.pde.usermanagement.service.IDPService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mckesson.jbat.common.util.constant.HeaderConstant.*;

/**
 *
 * Controller providing open API endpoints for direct and unauthenticated public interaction.
 *
 * Note: As this controller does not require authentication, ensure that sensitive operations and data
 * are not exposed through this API.
 */
@RestController
@Tag(name = "Public Access", description = "Open and unauthenticated API endpoints for public access.")
@RequestMapping(path = "/public", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@ComponentScan(basePackages = "com.mckesson.jbat.auth.module")
public class PublicAccessController {

	@Autowired
	private IDPService idpService;
	@Autowired
	JWTExtractor bearerJWTExtractor;
	@GetMapping(value = {"/idpConfig"})
	@ResponseBody
	@Operation(summary = "Retrieve Identity Provider Metadata")
	public IdentityProviderMetaData getIDPConfig() throws Exception {
		log.info("AuthenticationServiceWebAPI::getIDPConfig() starts");

		return idpService.getIDPInfo();
	}

    @GetMapping(value = {"/jwt"})
    @ResponseBody
    @Operation(summary = "Decode jwt token")
    public JwtResponseDto decodeJwtToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authToken) throws Exception {
        log.info("decodeJwtToken() starts");

        String bearerToken = bearerJWTExtractor.getJWTTokenFromHeader(authToken);
        Map<String, Claim> payload = JWTHelper.getPayload(bearerToken);
        if (!CollectionUtils.isEmpty(payload)) {
            List<String> roleNames = payload.get(ROLE_CLAIM).asList(String.class);
            List<String> permissions = payload.get(PERMISSION_CLAIM).asList(String.class);
            String userName = payload.get(USER_NAME_CLAIM).asString();
            String userId = payload.get(USER_CLAIM).asString();
            return JwtResponseDto.builder().roles(roleNames).permissions(permissions).userName(userName).userId(userId).build();
        }
        return null;
    }
}
