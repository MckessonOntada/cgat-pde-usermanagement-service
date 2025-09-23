package com.mckesson.cgat.pde.usermanagement.model.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.model.communication.Module;
import com.mckesson.cgat.pde.usermanagement.model.communication.ModuleCategory;
import com.mckesson.cgat.pde.usermanagement.model.communication.PreferenceDetails;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;

@Converter
public class PreferencesMapConverter
		implements AttributeConverter<Map<Module, Map<ModuleCategory, PreferenceDetails>>, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final TypeReference<Map<Module, Map<ModuleCategory, PreferenceDetails>>> TYPE_REFERENCE = new TypeReference<Map<Module, Map<ModuleCategory, PreferenceDetails>>>() {
	};

	@Override
	public String convertToDatabaseColumn(Map<Module, Map<ModuleCategory, PreferenceDetails>> preferences) {
		try {
			return objectMapper.writeValueAsString(preferences);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting Preferences Map to JSON", e);
		}
	}

	@Override
	public Map<Module, Map<ModuleCategory, PreferenceDetails>> convertToEntityAttribute(String json) {
		try {
			return objectMapper.readValue(json, TYPE_REFERENCE);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting JSON to Preferences Map", e);
		}
	}

}
