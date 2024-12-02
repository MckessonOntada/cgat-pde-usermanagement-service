package com.mckesson.cgat.pde.usermanagement.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OktaUserPayload {

	private String firstName;

	private String lastName;

	private String email;

	private String login;

}
