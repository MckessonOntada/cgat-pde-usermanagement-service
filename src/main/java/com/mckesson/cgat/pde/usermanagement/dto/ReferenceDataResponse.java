package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReferenceDataResponse {
   private  List<KeyValueDto> roles;
}
