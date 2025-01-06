package com.mckesson.cgat.pde.usermanagement.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CatalogPriceMapper {

	CatalogPriceMapper CATALOG_PRICE_MAPPER = Mappers.getMapper(CatalogPriceMapper.class);

	@Mapping(source = "variant", target = "variant")
	@Mapping(source = "variantDescription", target = "variantDescription")
	@BeanMapping(ignoreByDefault = true)
	CatalogPriceResponseDto toResponseDtoVariant(CatalogPrice catalogPrice);

	@Mapping(source = "salesOrg", target = "salesOrg")
	@Mapping(source = "distChannel", target = "distChannel")
	@Mapping(source = "sapMaterial", target = "sapMaterial")
	@Mapping(source = "variant", target = "variant")
	@Mapping(source = "variantDescription", target = "variantDescription")
	@Mapping(source = "ndc", target = "ndc")
	@BeanMapping(ignoreByDefault = true)
	CatalogPriceResponseDto toCatalogResponseDto(CatalogPrice catalogPrice);

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

	@IterableMapping(elementTargetType = CatalogPriceResponseDto.class)
	default List<CatalogPriceResponseDto> toCatalogResponseDtoList(List<CatalogPrice> catalogPriceList) {
		if (catalogPriceList == null) {
			return null;
		}
		// Use the toResponseDtoVariant method for each element of the list
		return catalogPriceList.stream()
			.map(this::toCatalogResponseDto) // Call the specific single item mapper
			.toList();
	}

}
