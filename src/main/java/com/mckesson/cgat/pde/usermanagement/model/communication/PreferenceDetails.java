package com.mckesson.cgat.pde.usermanagement.model.communication;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenceDetails {

	private Boolean setPreference;

	private Boolean email;

	private Boolean inApp;

	private Boolean sms;

}