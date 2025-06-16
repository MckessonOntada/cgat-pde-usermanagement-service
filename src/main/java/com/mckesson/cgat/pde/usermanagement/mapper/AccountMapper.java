package com.mckesson.cgat.pde.usermanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

import com.mckesson.cgat.pde.usermanagement.dto.AccountDTO;
import com.mckesson.cgat.pde.usermanagement.model.Account;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AccountMapper {

	@Mapping(source = "firstName", target = "name.firstName")
	@Mapping(source = "middleName", target = "name.middleName")
	@Mapping(source = "lastName", target = "name.lastName")
	@Mapping(source = "address.address1", target = "address.street")
	@Mapping(source = "address.address2", target = "address.street2")
	@Mapping(source = "address.city", target = "address.city")
	@Mapping(source = "address.state", target = "address.state")
	@Mapping(source = "address.state", target = "address.country")
	@Mapping(source = "address.zip", target = "address.zipCode")
	Account mapToModel(AccountDTO accountDto);

}
