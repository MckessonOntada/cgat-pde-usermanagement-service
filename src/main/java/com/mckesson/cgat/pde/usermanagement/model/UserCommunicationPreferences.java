package com.mckesson.cgat.pde.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mckesson.cgat.pde.usermanagement.model.communication.Module;
import com.mckesson.cgat.pde.usermanagement.model.communication.ModuleCategory;
import com.mckesson.cgat.pde.usermanagement.model.communication.PreferenceDetails;
import com.mckesson.cgat.pde.usermanagement.model.converters.PreferencesMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "user_communication_preferences")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCommunicationPreferences {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_communication_preferences_sequence")
	@SequenceGenerator(name = "user_communication_preferences_sequence",
			sequenceName = "user_communication_preferences_sequence", allocationSize = 1)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	private User user;

	@Column(name = "preferences", columnDefinition = "TEXT")
	@Convert(converter = PreferencesMapConverter.class)
	private Map<Module, Map<ModuleCategory, PreferenceDetails>> preferences = new HashMap<>();

}
