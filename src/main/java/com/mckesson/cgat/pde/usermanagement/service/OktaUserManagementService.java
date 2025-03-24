package com.mckesson.cgat.pde.usermanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.model.User;

public interface OktaUserManagementService {

	void updateOrCreateOktaUser(User user) throws CGATHttpException;

	String findOktaUserIdByEmail(String email);

	void triggerPasswordResetEmail(String oktaUserId);

}
