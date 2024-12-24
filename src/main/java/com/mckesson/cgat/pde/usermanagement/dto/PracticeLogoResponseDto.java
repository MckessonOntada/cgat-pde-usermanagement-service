package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;

@Getter
@Setter
@Builder
public class PracticeLogoResponseDto {

	private byte[] logo;

	private MediaType logoExtension;

}
