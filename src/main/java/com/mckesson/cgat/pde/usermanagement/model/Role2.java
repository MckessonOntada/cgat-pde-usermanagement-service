package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Role2 extends AuditColumns implements Serializable {

	@Id
	private String id;

	private String name;

	private String description;

	private String permissionType;

	private List<String> permissions;
}
