package com.mckesson.cgat.pde.usermanagement.model.converters;

import com.mckesson.cgat.pde.usermanagement.model.DistributionChannel;

import jakarta.persistence.AttributeConverter;

public class DistributionChannelConverter implements AttributeConverter<DistributionChannel, String> {

	@Override
	public String convertToDatabaseColumn(DistributionChannel attribute) {
		return attribute.getCode();
	}

	@Override
	public DistributionChannel convertToEntityAttribute(String dbData) {
		return DistributionChannel.fromCode(dbData);
	}

}
