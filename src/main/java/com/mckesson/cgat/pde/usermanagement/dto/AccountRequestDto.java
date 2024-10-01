package com.mckesson.cgat.pde.usermanagement.dto;
import com.google.auto.value.AutoValue.Builder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRequestDto {

	@NotNull(message = "Name must not be null")
	@NotEmpty
	private String name;

	@NotNull(message = "Dc id must not be null")
	@NotEmpty
	private String dcId;

	@NotNull(message = "Dc Name must not be null")
	@NotEmpty
	private String dcName;
	
	@NotEmpty
	private Boolean isActive = false;

}
