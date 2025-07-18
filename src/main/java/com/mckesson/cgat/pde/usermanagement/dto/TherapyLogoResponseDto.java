package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;

@Data
@Builder
public class TherapyLogoResponseDto {

	private byte[] logo;

	private MediaType logoExtension;

}
