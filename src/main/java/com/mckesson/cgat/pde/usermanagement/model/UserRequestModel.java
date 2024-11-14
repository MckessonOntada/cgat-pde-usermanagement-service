package com.mckesson.cgat.pde.usermanagement.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestModel {

	private User user;

	private UserType userType;

}
