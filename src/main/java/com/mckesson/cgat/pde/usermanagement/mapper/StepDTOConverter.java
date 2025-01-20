package com.mckesson.cgat.pde.usermanagement.mapper;

import com.mckesson.cgat.pde.usermanagement.dto.StepDTO;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@Converter
public class StepDTOConverter implements AttributeConverter<List<StepDTO>, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<StepDTO> stepDTOList) {
		try {
			return objectMapper.writeValueAsString(stepDTOList);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Error converting List<StepDTO> to String", e);
		}
	}

	@Override
	public List<StepDTO> convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData,
					objectMapper.getTypeFactory().constructCollectionType(List.class, StepDTO.class));
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Error converting String to List<StepDTO>", e);
		}
	}

}
