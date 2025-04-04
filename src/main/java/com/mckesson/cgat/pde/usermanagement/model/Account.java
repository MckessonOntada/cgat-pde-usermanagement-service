package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.type.NumericBooleanConverter;

import com.mckesson.cgat.pde.usermanagement.model.converters.AccountPartnerFunctionConverter;
import com.mckesson.cgat.pde.usermanagement.model.converters.DistributionChannelConverter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "ACCOUNT_SEQUENCE";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_GENERATOR_NAME)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private Long id;

	@Column(name = "tenant_id")
	// @TenantId
	private Long practiceId;

	@NotEmpty(message = "Name is required.")
	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	@Column(name = "name_1")
	private String accountName;

	@Column(name = "sap_partner")
	private String accountNumber;

	@Column(name = "partner_function")
	@Convert(converter = AccountPartnerFunctionConverter.class)
	private AccountPartnerFunction partnerFunction;

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

	@Column(name = "search_term_1")
	private String searchTerm1;

	@Embedded
	private AccountAddress address;

	@Column(name = "cgat_identifier")
	private String cgatIdentifier;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_account_mapping", joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "account_id"))
	private Set<User> user;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = true;

	@Enumerated(EnumType.STRING)
	private ChannelType channelType;

}
