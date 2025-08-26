package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.ResourceConflictException;
import com.mckesson.cgat.pde.usermanagement.http.CGATHttpClient;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.OktaUserPayload;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.service.OktaUserManagementService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class OktaUserManagementServiceImpl implements OktaUserManagementService {

	@Value("${okta.url}")
	private String oktaBaseUrl;

	@Value("${okta.apiToken}")
	private String oktaApiToken;

	@Value("${okta.associatedGroupId}")
	private String associatedGroupId;

	@Autowired
	private CGATHttpClient cgatHttpClient;

	private final ObjectMapper mapper = new ObjectMapper();

	private String OKTA_USER_PATH;

	private String OKTA_GROUP_PATH;

	@PostConstruct
	public void init() {
		OKTA_USER_PATH = oktaBaseUrl + "/api/v1/users";
		OKTA_GROUP_PATH = oktaBaseUrl + "/api/v1/groups/";
		Name name = Name.builder().firstName("firstName").lastName("lastName").build();
		Contact contact = Contact.builder().email("k10@yopmail.com").build();
		User newUser = User.builder().name(name).contact(contact).build();

		// updateOrCreateOktaUser(newUser);
	}

	@Override
	public void updateOrCreateOktaUser(User user) throws CGATHttpException {
		OktaUserPayload oktaUser = getOktaUser(user);
		try {

			/*
			 * Checking is user exists on okta, if yes -> assign the user to associated
			 * group if not -> create the okta user in the associated group
			 */
			String oktaUserId = findOktaUserIdByEmail(oktaUser.getEmail());

			if (StringUtils.isNotEmpty(oktaUserId)) {
				assignUserToOktaGroup(oktaUserId);
				return;
			}
			createOktaUser(oktaUser);

		}
		catch (Exception e) {
			log.error("Error occurred while creating user: {} ", e.getMessage());
			throw new CGATHttpException(e.getMessage());
		}
	}

	@Override
	public String findOktaUserIdByEmail(String email) throws CGATHttpException {
		HashMap<String, String> payload = new HashMap<>();
		payload.put("q", email);
		String response = cgatHttpClient.sendGetRequest(OKTA_USER_PATH, payload, "SSWS " + oktaApiToken);

		if (StringUtils.isNotEmpty(response)) {
			try {
				List<Map<String, Object>> usersList = mapper.readValue(response,
						new TypeReference<List<Map<String, Object>>>() {
						});
				if (!usersList.isEmpty()) {
					return (String) usersList.get(0).get("id");
				}
			}
			catch (Exception e) {
				log.error("Error parsing Okta user response: {}", e.getMessage(), e);
				throw new CGATHttpException("Error parsing Okta user response: " + e.getMessage());
			}
		}
		return null; // Return null if no user found or response is empty
	}

	private void assignUserToOktaGroup(String oktaUserId) {
		String pathname = OKTA_GROUP_PATH + associatedGroupId + "/users/" + oktaUserId;
		cgatHttpClient.sendPutRequest(pathname, "SSWS " + oktaApiToken);
	}

	private void createOktaUser(OktaUserPayload oktaUser) throws CGATHttpException, JsonProcessingException {
		HashMap<String, Object> payLoadMap = new HashMap<>();
		List<String> groupIds = new ArrayList<>();
		groupIds.add(associatedGroupId);

		payLoadMap.put("profile", oktaUser);
		payLoadMap.put("groupIds", groupIds);

		String payload = mapper.writeValueAsString(payLoadMap);

		HashMap<String, String> queryParams = new HashMap<>();
		queryParams.put("activate", "true");
		queryParams.put("provider", "false");

		String userCreatedResponse = cgatHttpClient.sendPostRequest(OKTA_USER_PATH, queryParams, payload,
				"SSWS " + oktaApiToken);
	}

	private OktaUserPayload getOktaUser(User user) {
		OktaUserPayload oktaUser = OktaUserPayload.builder()
			.firstName(user.getName().getFirstName())
			.lastName(user.getName().getLastName())
			.email(user.getContact().getEmail())
			.login(user.getContact().getEmail())
			.build();
		return oktaUser;
	}

	@Override
	public void triggerPasswordResetEmail(String oktaUserId) throws CGATHttpException {
		log.debug("Triggering reset password email for Okta ID: {}", oktaUserId);

		// Prepare the URL and query parameters
		String url = String.format("%s/%s/lifecycle/reset_password", OKTA_USER_PATH, oktaUserId);
		HashMap<String, String> queryParams = new HashMap<>();
		queryParams.put("sendEmail", "true");

		// Empty payload
		String payload = "";

		try {
			// Send POST request to trigger password reset email
			String response = cgatHttpClient.sendPostRequest(url, queryParams, payload, "SSWS " + oktaApiToken);

			log.info("Password reset email triggered successfully for Okta ID: {}. Response: {}", oktaUserId, response);

		}
		catch (CGATHttpException e) {
			log.error("Failed to trigger password reset email for Okta ID: {}. Error: {}", oktaUserId, e.getMessage(),
					e);
			throw e;
		}
	}

	@Override
	public void triggerReActivateEmail(String oktaUserId) throws CGATHttpException, JsonProcessingException {
		log.debug("Triggering re-activate email for Okta ID: {}", oktaUserId);

		try {
			String status = getOktaUserStatus(oktaUserId);

			log.debug("Okta user {} has status = {}", oktaUserId, status);

			if ("ACTIVE".equalsIgnoreCase(status)) {
				log.info("User {} is already ACTIVE, reactivation not allowed.", oktaUserId);
				throw new ResourceConflictException(List.of("user.already.active.conflict"));
			}

			triggerOktaReactivation(oktaUserId, status);

		}
		catch (CGATHttpException e) {
			log.error("Failed to trigger re-activate email for Okta ID: {}. Error: {}", oktaUserId, e.getMessage(), e);
			throw e;
		}
	}

	private String getOktaUserStatus(String oktaUserId) throws CGATHttpException, JsonProcessingException {
		String getUserUrl = buildOktaUserUrl(oktaUserId);
		HashMap<String, String> emptyParams = new HashMap<>();

		String userResponse = cgatHttpClient.sendGetRequest(getUserUrl, emptyParams, "SSWS " + oktaApiToken);
		JsonNode root = mapper.readTree(userResponse);

		return root.path("status").asText();
	}

	private void triggerOktaReactivation(String oktaUserId, String status) throws CGATHttpException {
		HashMap<String, String> queryParams = new HashMap<>();
		queryParams.put("sendEmail", "true");

		String url = buildOktaUserUrl(oktaUserId) + "/lifecycle/reactivate";

		log.info("User {} is {} → triggering re-activation email", oktaUserId, status);

		String response = cgatHttpClient.sendPostRequest(url, queryParams, "", "SSWS " + oktaApiToken);

		log.info("Re-activation email triggered successfully for Okta ID: {}. Response: {}", oktaUserId, response);
	}

	private String buildOktaUserUrl(String oktaUserId) {
		return String.format("%s/%s", OKTA_USER_PATH, oktaUserId);
	}

}
