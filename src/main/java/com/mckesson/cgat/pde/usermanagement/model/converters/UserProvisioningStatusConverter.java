package com.mckesson.cgat.pde.usermanagement.model.converters;

import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningStaus;

import jakarta.persistence.AttributeConverter;

public class UserProvisioningStatusConverter implements AttributeConverter<UserProvisioningStaus, String> {

	@Override
	public String convertToDatabaseColumn(UserProvisioningStaus attribute) {
		return attribute.getUserRequestType();
	}

	@Override
	public UserProvisioningStaus convertToEntityAttribute(String dbData) {
		return UserProvisioningStaus.valueFor(dbData);
	}

}


