package com.mckesson.cgat.pde.usermanagement.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoggedInUserDetails {

	private String id;

	private String userName;

	private Set<Permission> listOfPermissions = new HashSet<>();

	private Role role;

	private Name name;

	private Contact contact;

	private long practiceId;

}
