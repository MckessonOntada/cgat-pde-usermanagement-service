package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			List<Map<String, Object>> usersList = findOktaUserByEmail(oktaUser.getEmail());

			if (!usersList.isEmpty()) {
				Map<String, Object> oktaExistingUserData = usersList.get(0);
				String oktaUserId = (String) oktaExistingUserData.get("id");
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
	public List<Map<String, Object>> findOktaUserByEmail(String email) throws CGATHttpException {
		HashMap<String, String> payload = new HashMap<>();
		payload.put("q", email);
		String response = cgatHttpClient.sendGetRequest(OKTA_USER_PATH, payload, "SSWS " + oktaApiToken);

		if (StringUtils.isNotEmpty(response)) {
			try {
				return mapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {
				});
			}
			catch (Exception e) {
				log.error("Error parsing Okta user response: {}", e.getMessage(), e);
				throw new CGATHttpException("Error parsing Okta user response: " + e.getMessage());
			}
		}
		return List.of(); // Return empty list if no user found or response is empty
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
		catch (Exception e) {
			log.error("Failed to trigger password reset email for Okta ID: {}. Error: {}", oktaUserId, e.getMessage(),
					e);
			throw new CGATHttpException("Failed to trigger password reset email: " + e.getMessage());
		}
	}

}
