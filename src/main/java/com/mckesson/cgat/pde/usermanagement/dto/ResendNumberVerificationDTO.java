package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendNumberVerificationDTO {

	private Boolean isNumberVerified;

	private String number;

	private OffsetDateTime lastResendSMSTime;

}
