package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountsResponseDTOImpl implements AccountsResponseDTO {

	private String id;

	private String accountNumber;

	private String accountName;

	private String houseNumber;

	private String street;

	private String street2;

	private String street3;

	private String telephone;

	private String city;

	private String district;

	private String state;

	private String country;

	private String zipCode;

	private Boolean isDefault;

	private String salesOrg;

	private String companyCode;

	private String division;

	private String distChannel;

	private String cgatIdentifier;

	private ChannelType channelType;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getAccountNumber() {
		return accountNumber;
	}

	@Override
	public String getAccountName() {
		return accountName;
	}

	@Override
	public String getHouseNumber() {
		return houseNumber;
	}

	@Override
	public String getStreet() {
		return street;
	}

	@Override
	public String getStreet2() {
		return street2;
	}

	@Override
	public String getStreet3() {
		return street3;
	}

	@Override
	public String getTelephone() {
		return telephone;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String getDistrict() {
		return district;
	}

	@Override
	public String getState() {
		return state;
	}

	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public String getZipCode() {
		return zipCode;
	}

	@Override
	public Boolean getIsDefault() {
		return isDefault;
	}

	@Override
	public String getSalesOrg() {
		return salesOrg;
	}

	@Override
	public String getCompanyCode() {
		return companyCode;
	}

	@Override
	public String getDivision() {
		return division;
	}

	@Override
	public String getDistChannel() {
		return distChannel;
	}

	@Override
	public String getCgatIdentifier() {
		return cgatIdentifier;
	}

	@Override
	public ChannelType getChannelType() {
		return channelType;
	}

}
