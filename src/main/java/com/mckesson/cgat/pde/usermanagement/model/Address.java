package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	@Size(min = 2, max = 100)
	@Pattern(regexp = "^[-0-9A-Za-z,./ ]+$",
			message = "Alphanumeric, along with below chararcters allowed Space, hypen, comma, dot. slash.")
	@NotEmpty(message = "Address 1 is required.")
	private String address1;

	@Size(max = 100)
	@Pattern(regexp = "^[0-9A-Za-z-,./ ]*$",
			message = "Alphanumeric, along with below chararcters allowed Space, hypen, comma, dot. slash.")
	private String address2;

  @NotEmpty(message = "City is required.")
	@Size(min = 2, max = 50)
	@Pattern(regexp = "^[A-Za-z ]+$", message = "City must only contain alphabets.")
	private String city;
  
  @Size(min = 2, max = 50)
	@NotEmpty(message = "State is required.")
	@Pattern(
			regexp = "^(A[LKZR]|C[AOT]|D[EC]|FL|GA|HI|I[DLNA]|K[SY]|LA|M[DEHINSOT]|N[CDEHJMVY]|O[HKR]|P[AR]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY]|Alabama|Alaska|Arizona|Arkansas|California|Colorado|Connecticut|Delaware|Florida|Georgia|Hawaii|Idaho|Illinois|Indiana|Iowa|Kansas|Kentucky|Louisiana|Maine|Maryland|Massachusetts|Michigan|Minnesota|Mississippi|Missouri|Montana|Nebraska|Nevada|New Hampshire|New Jersey|New Mexico|New York|North Carolina|North Dakota|Ohio|Oklahoma|Oregon|Pennsylvania|Rhode Island|South Carolina|South Dakota|Tennessee|Texas|Utah|Vermont|Virginia|Washington|West Virginia|Wisconsin|Wyoming)$",
			message = "State must be a valid state abbreviation (e.g., CA) or full name (e.g., California)")
	private String state;

  @NotEmpty(message = "ZIP code is required")
	@Size(min = 2, max = 10, message = "Enter a valid Zip Code.")
	@Pattern(regexp = "^[\\d]+$", message = "Must be numeric")
	private String zip;

}
