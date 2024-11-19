package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.model.User;

public interface OktaUserManagementService {

	void createUser(User user) throws CGATHttpException;

}
