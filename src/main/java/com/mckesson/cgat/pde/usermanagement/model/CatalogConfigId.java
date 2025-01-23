package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CatalogConfigId implements Serializable {

	private String materialId;

	private String variant;

	private String shipToAccount;

	private String soldToAccount;

}
