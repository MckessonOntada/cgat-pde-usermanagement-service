package com.mckesson.cgat.pde.usermanagement.utils;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.AccountRequestDto;
import com.mckesson.cgat.pde.usermanagement.model.Account;

@Service
public class AccountUtil {

	public Account mapDtoTOEntity(AccountRequestDto dto) {
		return new Account(null, dto.getName(), dto.getDcId(), dto.getDcName(), dto.getIsActive());

	}

}
