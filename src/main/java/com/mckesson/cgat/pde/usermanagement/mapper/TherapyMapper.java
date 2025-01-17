package com.mckesson.cgat.pde.usermanagement.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import com.mckesson.cgat.pde.usermanagement.dto.TherapyResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TherapyMapper {

	TherapyMapper THERAPY_MAPPER = Mappers.getMapper(TherapyMapper.class);

	@Mapping(source = "materialNumber", target = "materialNumber")
	@Mapping(source = "ndcNumber", target = "ndcNumber")
	@Mapping(source = "strength", target = "strength")
	@Mapping(source = "dosage", target = "dosage")
	@Mapping(source = "materialDescription", target = "materialDescription")
	@Mapping(source = "genericName", target = "genericName")
	@BeanMapping(ignoreByDefault = true)
	TherapyResponseDto toTherapyResponseDto(Therapy therapy);

	TherapyResponseDto toResponseDto(Therapy therapy);

	@IterableMapping(elementTargetType = TherapyResponseDto.class)
	default List<TherapyResponseDto> toTherapiesResponseDtoList(List<Therapy> therapies) {
		if (therapies == null) {
			return null;
		}
		// Use the toResponseDtoVariant method for each element of the list
		return therapies.stream()
			.map(this::toTherapyResponseDto) // Call the specific single item mapper
			.toList();
	}

}
