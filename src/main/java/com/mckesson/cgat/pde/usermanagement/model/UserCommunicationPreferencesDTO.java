package com.mckesson.cgat.pde.usermanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCommunicationPreferencesDTO {

	private CommunicationPreferences roleCommunicationPreferences;

	private UserCommunicationPreferences userCommunicationPreferences;

}
