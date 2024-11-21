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
			String existingUser = checkIfUserExistsInOkta(oktaUser.getEmail());

			if (StringUtils.isNotEmpty(existingUser)) {
				List<Map<String, Object>> usersList = mapper.readValue(existingUser,
						new TypeReference<List<Map<String, Object>>>() {
						});

				if (!usersList.isEmpty()) {
					Map<String, Object> oktaExistingUserData = usersList.get(0);
					String oktaUserId = (String) oktaExistingUserData.get("id");
					assignUserToOktaGroup(oktaUserId);
					return;
				}

			}
			createOktaUser(oktaUser);

		}
		catch (Exception e) {
			log.error("Error occurred while creating user: {} ", e.getMessage());
			throw new CGATHttpException(e.getMessage());
		}
	}

	private String checkIfUserExistsInOkta(String email) {
		HashMap<String, String> payload = new HashMap<>();
		payload.put("q", email);
		// params.put("after", "string");
		// params.put("limit", "200");
		// params.put("filter", "string");
		// params.put("search", "string");
		// params.put("sortBy", "string");
		// params.put("sortOrder", "string");
		return cgatHttpClient.sendGetRequest(OKTA_USER_PATH, payload, "SSWS " + oktaApiToken);
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

}
