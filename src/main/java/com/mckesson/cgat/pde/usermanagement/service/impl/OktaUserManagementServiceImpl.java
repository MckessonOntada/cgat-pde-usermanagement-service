package com.mckesson.cgat.pde.usermanagement.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	@PostConstruct
	public void init() {
		Name name = Name.builder().firstName("firstName").lastName("lastName").build();
		Contact contact = Contact.builder().email("k10@yopmail.com").build();
		User newUser = User.builder().name(name).contact(contact).build();

		// createUser(newUser);
	}

	@Override
	public void createUser(User user) throws CGATHttpException {
		OktaUserPayload oktaUser = getOktaUser(user);
		try {
			HashMap<String, Object> payLoadMap = new HashMap<>();
			List<String> groupIds = new ArrayList<>();
			groupIds.add(associatedGroupId);

			payLoadMap.put("profile", oktaUser);
			payLoadMap.put("groupIds", groupIds);

			String payload = mapper.writeValueAsString(payLoadMap);

			HashMap<String, String> queryParams = new HashMap<>();
			queryParams.put("activate", "true");
			queryParams.put("provider", "false");

			String userCreationPath = oktaBaseUrl + "/api/v1/users";

			String userCreatedResponse = cgatHttpClient.sendPostRequest(userCreationPath, queryParams, payload,
					"SSWS " + oktaApiToken);
			System.out.println(userCreatedResponse);
		}
		catch (Exception e) {
			log.error("Error occurred while creating user: {} ", e.getMessage());
			throw new CGATHttpException(e);
		}
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
