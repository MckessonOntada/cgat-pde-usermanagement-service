package com.mckesson.cgat.pde.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.DistributionChannel;

public interface AccountsResponseDTO {

	String getId();

	String getAccountNumber();

	String getAccountName();

	String getHouseNumber();

	String getStreet();

	String getStreet2();

	String getStreet3();

	String getTelephone();

	String getCity();

	String getDistrict();

	String getState();

	String getCountry();

	String getZipCode();

	Boolean getIsDefault();

	String getSalesOrg();

	String getCompanyCode();

	String getDivision();

	ChannelType getChannelType();

	@JsonIgnore
	String getDistChannel();

	String getCgatIdentifier();

	default String getDistributionChannel() {
		return getDistChannel();
	}

}
