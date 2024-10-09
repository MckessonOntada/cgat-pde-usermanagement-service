package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.jbat.common.util.constant.HeaderConstant;
import com.mckesson.cgat.pde.usermanagement.constant.WebhookConstants;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.Users;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.OktaUserWebhookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.mckesson.cgat.pde.usermanagement.constant.WebhookConstants.*;

@Service
@Slf4j
public class OktaUserWebhookServiceImpl implements OktaUserWebhookService {


    @Value("${okta.token.inline.hook.auth.header}")
    private String oktaAuthHeader;

    @Value("${okta.token.inline.hook.auth.token}")
    private String oktaAuthToken;

    
    private final UserRepository userRepository;


    public OktaUserWebhookServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
    public boolean validateToken(HttpHeaders headers) {
        Predicate<HttpHeaders> isValidToken = tokenHeader -> (tokenHeader.containsKey(oktaAuthHeader) && headers.get(oktaAuthHeader).get(0).equals(oktaAuthToken));
        return isValidToken.test(headers);
    }

    @Override
    public void addUpdateUserEvent(JSONObject requestBody) {
        log.info("Request from okta {}", StringEscapeUtils.escapeJava(requestBody.toString()));
        if (requestBody.has("data")) {
            JSONObject data = (JSONObject) requestBody.get("data");
            if (data.has("events")) {
                JSONArray events = data.getJSONArray("events");
                events.iterator().forEachRemaining(element ->
                    handleUserEvent((JSONObject) element)
                );
            }
        }
    }

    private void handleUserEvent(JSONObject element) {
        if (element.has(EVENT_TYPE)) {
            if (StringUtils.equalsIgnoreCase("group.user_membership.add", element.getString(EVENT_TYPE))) {
                handleUserAddedEvent(element);
            } else if (StringUtils.equalsIgnoreCase("group.user_membership.remove", element.getString(EVENT_TYPE))) {
                handleUserRemovedEvent(element);
            }else if (StringUtils.equalsIgnoreCase(USER_LIFECYCLE_DEACTIVATE, element.getString(EVENT_TYPE))) {
                handleUserDeactivationEvent(element);
            } else {
                log.error("No valid event type");
            }
        }
    }

    private Users extractUserDetails(JSONObject element) {
        Users userRequest = new Users();

        if (element.has(TARGET)) {
            JSONArray targets = element.getJSONArray(TARGET);
            targets.iterator().forEachRemaining(target -> {
                JSONObject userJson = (JSONObject) target;
                if (StringUtils.equalsIgnoreCase(USER, userJson.getString(TYPE))) {
                    String email = StringUtils.toRootLowerCase(userJson.getString("alternateId"));
                    String displayName = userJson.getString("displayName");
                    userRequest.setEmail(email);
//                    userRequest.setDisplayName(displayName);
                }
            });

        }
        return userRequest;
    }

    private Users extractModifyBy(JSONObject element, Users userRequest) {
        if (element.has(ACTOR)) {
            JSONObject actor = element.getJSONObject(ACTOR);
            if (StringUtils.equalsIgnoreCase(USER, actor.getString(TYPE))) {
                String oktaId = actor.getString("id");
               //To-DO-CGAT  userRequest.setUpdatedBy(oktaId);
            }
        }
        return userRequest;
    }

    private Users extractCreatedBy(JSONObject element, Users userRequest) {
        if (element.has(ACTOR)) {
            JSONObject actor = element.getJSONObject(ACTOR);
            if (StringUtils.equalsIgnoreCase(USER, actor.getString(TYPE))) {
                String oktaId = actor.getString("id");
               //To-DO-CGAT userRequest.setCreatedBy(oktaId);
            }
        }
        return userRequest;
    }

    private void handleUserRemovedEvent(JSONObject element) {
        log.info("handleUserRemovedEvent");
        Users user = extractUserDetails(element);
        Users existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null && existingUser.getIsActive()) {
                extractModifyBy(element, existingUser);
                existingUser.setIsActive(false);
                userRepository.save(existingUser);
            }
    }
    private void handleUserDeactivationEvent(JSONObject element) {
        log.info("handleUserDeactivationEvent");
        Users user = extractUserDetails(element);
        Users existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null && existingUser.getIsActive()) {
                extractModifyBy(element, existingUser);
                existingUser.setIsActive(false);
                userRepository.save(existingUser);
            }
    }

    private void handleUserAddedEvent(JSONObject element) {
        log.info("handleUserAddedEvent");
        Users user = extractUserDetails(element);
        Users existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null && existingUser.getIsActive()) {
            extractModifyBy(element, existingUser);
            existingUser.setIsActive(true);
            userRepository.save(existingUser);
        }
    }


    @Override
    public JSONObject getCommandsForTokenInlineWebhook(JSONObject authenticationResponse) {
        /**
         * Please refer
         * <p>
         * https://developer.okta.com/docs/reference/token-hook/#sample-json-payload-of-a-request
         * </p>
         * for the request body and response payload that needs to be returned in the
         * API response.
         */
        JSONObject commands = new JSONObject();
        log.info("Request from okta {}", StringEscapeUtils.escapeJava(authenticationResponse.toString()));
        commands.put("commands", new JSONArray());
        if (authenticationResponse.has("data")) {
            JSONObject data = (JSONObject) authenticationResponse.get("data");
            if (data.has("access")) {
                JSONObject access = (JSONObject) data.get("access");
                if (access.has("claims")) {
                    JSONObject claims = (JSONObject) access.get("claims");
                    if (claims.has("sub")) {
                        String email = StringUtils.toRootLowerCase(claims.getString("sub"));
                        try {
                            log.info("Started Generating commands");
                            Users userInfo = userRepository.findByEmail(email);
//                            List<Role> roles = userInfo.getRoles();
//                            JSONArray userRoles = buildUserRole(roles);
//                            JSONArray userPermissions = generatePermissionArray(roles);
                            JSONArray command = commands.getJSONArray("commands");
//                            addCommand(WebhookConstants.PATCH, WebhookConstants.ADD_OPERTATION,
//                                    HeaderConstant.ROLE_CLAIM, command, userRoles);
//                            addCommand(WebhookConstants.PATCH, WebhookConstants.ADD_OPERTATION,
//                                    HeaderConstant.PERMISSION_CLAIM, command, userPermissions);
                            addCommand(WebhookConstants.PATCH, WebhookConstants.ADD_OPERTATION,
                                    HeaderConstant.USER_CLAIM, command, userInfo.getId());
//                            addCommand(WebhookConstants.PATCH, WebhookConstants.ADD_OPERTATION,
//                                    HeaderConstant.USER_NAME_CLAIM, command, userInfo.getDisplayName());

                        } catch (Exception e) {
                            log.error("Exception on generating scopes {}", e);
                        }
                    }
                }
            }
        }
        return commands;
    }

    public JSONArray buildUserRole(List<Role> roles) {
        JSONArray userRoles = new JSONArray();
        roles.forEach(role -> userRoles.put(role.getName()));
        return userRoles;
    }

    private JSONArray generatePermissionArray(List<Role> userRoles) {
        JSONArray userPermissions = new JSONArray();
        List<String> allActivePermissions = userRoles.stream()
                .flatMap(role -> role.getRolePermissionMappings().stream().map(p -> p.getPermission().getName()))
                .distinct().collect(Collectors.toList());

        userPermissions.putAll(allActivePermissions);

        return userPermissions;
    }

    public void addCommand(String commandType, String operation, String claimName, JSONArray existingCommand,
                           Object valueForClaim) {
        JSONObject accessPath = new JSONObject();
        accessPath.put(TYPE, commandType);

        JSONArray accessPathValue = new JSONArray();

        JSONObject rolePayload = new JSONObject();
        rolePayload.put("op", operation);
        rolePayload.put("path", "/claims/" + claimName);
        valueForClaim = (ObjectUtils.isEmpty(valueForClaim)) ? "" : valueForClaim;
        rolePayload.put("value", valueForClaim);
        accessPathValue.put(rolePayload);

        accessPath.put("value", accessPathValue);
        existingCommand.put(accessPath);
    }
}
