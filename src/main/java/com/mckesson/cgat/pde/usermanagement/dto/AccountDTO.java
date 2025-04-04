package com.mckesson.cgat.pde.usermanagement.dto;

import org.hibernate.type.NumericBooleanConverter;

import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

	private Long practiceId;

	@NotEmpty(message = "Name is required.")
	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	@Column(name = "name_1")
	private String accountName;

	@Column(name = "sap_partner")
	private String accountNumber;

	@Column(name = "sap_customer")
	private String sapCustomer;

	@Column(name = "sales_org")
	private String salesOrg;

	@Column(name = "distribution_channel")
	private String distributionChannel;

	@Column(name = "vistex_customer")
	private String vistexCustomer;

	@Column(name = "division")
	private String division;

	@Column(name = "company_code")
	private String companyCode;

	@Embedded
	private Name name;

	@Embedded
	private Contact contact;

	@Embedded
	private Address address;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = true;

}
