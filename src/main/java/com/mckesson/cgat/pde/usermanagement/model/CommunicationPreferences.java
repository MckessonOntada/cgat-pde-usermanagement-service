package com.mckesson.cgat.pde.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mckesson.cgat.pde.usermanagement.model.communication.Module;
import com.mckesson.cgat.pde.usermanagement.model.communication.ModuleCategory;
import com.mckesson.cgat.pde.usermanagement.model.communication.PreferenceDetails;
import com.mckesson.cgat.pde.usermanagement.model.converters.PreferencesMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Table(name = "communication_preferences")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunicationPreferences {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "communication_preferences_sequence")
	@SequenceGenerator(name = "communication_preferences_sequence", sequenceName = "communication_preferences_sequence",
			allocationSize = 1)
	private Long id;

	@OneToOne
	@JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	private Role role;

	@Column(name = "preferences", columnDefinition = "TEXT")
	@Convert(converter = PreferencesMapConverter.class)
	private Map<Module, Map<ModuleCategory, PreferenceDetails>> preferences;

}
