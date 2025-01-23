package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.CatalogConfiguration;
import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogResponseDTO {

	private CatalogConfiguration catalogConfiguration;

	private CatalogPrice catalogPrice;

}
