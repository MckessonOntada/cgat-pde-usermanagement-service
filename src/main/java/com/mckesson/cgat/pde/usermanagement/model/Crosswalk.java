package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "crosswalk", schema = "catalog")
public class Crosswalk extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crosswalk_sequence")
	@SequenceGenerator(name = "crosswalk_sequence", schema = "catalog", sequenceName = "crosswalk_sequence",
			allocationSize = 1)
	private Long id;

	@Size(max = 50)
	@Column(name = "bnb_ndc_number", nullable = false)
	private String bnbNdcNumber;

	@Size(max = 50)
	@Column(name = "sp_product_id", nullable = false)
	private String spProductId;

	@Convert(converter = NumericBooleanConverter.class)
	@Column(name = "is_active")
	private Boolean isActive = true;

}
