package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.model.User;

public interface OktaUserManagementService {

	void updateOrCreateOktaUser(User user) throws CGATHttpException;

	String findOktaUserIdByEmail(String email) throws CGATHttpException;

	void triggerPasswordResetEmail(String oktaUserId);

}
