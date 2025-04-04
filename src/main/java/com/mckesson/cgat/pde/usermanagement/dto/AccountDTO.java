package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

	 private Long practiceId;

	    @NotBlank(message = "Name is required.")
	    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
	    private String accountName;

	    @NotNull(message = "Valid channel type is required.")
	    @Enumerated(EnumType.STRING)
	    private ChannelType channelType;

	    @NotBlank(message = "Sales Org is required (Enter NA for SP account).")
	    private String salesOrg;

	    @NotBlank(message = "Distribution channel is required.")
	    private String distributionChannel;

	    @NotBlank(message = "Vistex customer is required (Enter NA for SP account).")
	    private String vistexCustomer;

	    @NotBlank(message = "Division is required (Enter NA for SP account).")
	    private String division;

	    @NotBlank(message = "Company code is required (Enter NA for SP account).")
	    private String companyCode;

	    @Valid
	    private Name name;

	    @Valid
	    private Contact contact;

	    @Valid
	    private Address address;

	    private Boolean isActive = true;

}
