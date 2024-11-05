package com.mckesson.cgat.pde.usermanagement.utils;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class UserCurrentRequestContext extends User {

	private static final long serialVersionUID = -4716641214965509012L;

	private LoggedInUserDetails loggedInUser;

	public UserCurrentRequestContext(String userName, String password,
			Collection<? extends GrantedAuthority> authorities, LoggedInUserDetails loggedInUser) {
		super(userName, password, authorities);
		this.loggedInUser = loggedInUser;
	}

	public String getUserId() {
		return loggedInUser.getId();
	}

}
