package com.mckesson.cgat.pde.usermanagement.model;

import com.mckesson.cgat.pde.usermanagement.dto.ContactUsDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Entity
@Table(name = "contact_us_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "CONTACT_US_METADATA_ID_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_GENERATOR_NAME)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private Long id;

	@Column(name = "form_data", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private ContactUsDTO formData;

}
