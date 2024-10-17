package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;

import com.mckesson.cgat.pde.usermanagement.model.Account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LinkAccountToUserDto {

	private List<Long> accounts;

}
