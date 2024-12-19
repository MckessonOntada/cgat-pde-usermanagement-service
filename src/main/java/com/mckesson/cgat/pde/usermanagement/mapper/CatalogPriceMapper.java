package com.mckesson.cgat.pde.usermanagement.mapper;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CatalogPriceMapper {

	CatalogPriceMapper CATALOG_PRICE_MAPPER = Mappers.getMapper(CatalogPriceMapper.class);

	@Mapping(source = "variant", target = "variant")
	@Mapping(source = "variantDescription", target = "variantDescription")
	@BeanMapping(ignoreByDefault = true)
	CatalogPriceResponseDto toResponseDtoVariant(CatalogPrice catalogPrice);

	CatalogPriceResponseDto toResponseDto(CatalogPrice catalogPrice);

	@IterableMapping(elementTargetType = CatalogPriceResponseDto.class)
	default List<CatalogPriceResponseDto> toResponseDtoVariantsList(List<CatalogPrice> catalogPriceList) {
		if (catalogPriceList == null) {
			return null;
		}
		// Use the toResponseDtoVariant method for each element of the list
		return catalogPriceList.stream()
			.map(this::toResponseDtoVariant) // Call the specific single item mapper
			.toList();
	}

}
