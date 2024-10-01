package com.mckesson.cgat.pde.usermanagement.model;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

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
public class Role2 {

	@Id
	@UuidGenerator
	private UUID id;

	private String name;

	private String description;

	private String permissionType;

	private List<String> permissions;
}
