package com.mckesson.cgat.pde.usermanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

import com.mckesson.cgat.pde.usermanagement.dto.AccountDTO;
import com.mckesson.cgat.pde.usermanagement.model.Account;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AccountMapper {

	Account mapToModel(AccountDTO accountDto);

}
