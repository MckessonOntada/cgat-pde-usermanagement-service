package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;

public interface OnBehalfAuidtService {

	void logImpersonation(LoggedInUserDetails impersonatedUser, LoggedInUserDetails actualUser);

}