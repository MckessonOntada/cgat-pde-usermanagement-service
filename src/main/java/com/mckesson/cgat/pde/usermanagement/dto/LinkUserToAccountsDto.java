package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LinkUserToAccountsDto {
	
	private List<Long> accounts;
	

}
