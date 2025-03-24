package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.model.User;

import java.util.List;
import java.util.Map;

public interface OktaUserManagementService {

	void updateOrCreateOktaUser(User user) throws CGATHttpException;

	List<Map<String, Object>> findOktaUserByEmail(String email) throws CGATHttpException;

	void triggerPasswordResetEmail(String oktaUserId);

}
