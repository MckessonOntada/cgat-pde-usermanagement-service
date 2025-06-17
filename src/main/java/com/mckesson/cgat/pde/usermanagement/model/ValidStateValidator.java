package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidStateValidator implements ConstraintValidator<ValidState, String> {

	public static final Set<String> VALID_US_STATES = Set.of("alabama", "alaska", "arizona", "arkansas", "california",
			"colorado", "connecticut", "delaware", "florida", "georgia", "hawaii", "idaho", "illinois", "indiana",
			"iowa", "kansas", "kentucky", "louisiana", "maine", "maryland", "massachusetts", "michigan", "minnesota",
			"mississippi", "missouri", "montana", "nebraska", "nevada", "new hampshire", "new jersey", "new mexico",
			"new york", "north carolina", "north dakota", "ohio", "oklahoma", "oregon", "pennsylvania", "rhode island",
			"south carolina", "south dakota", "tennessee", "texas", "utah", "vermont", "virginia", "washington",
			"west virginia", "wisconsin", "wyoming", "puerto rico", "virgin islands", "washington, d.c.",
			"us armed forces pacific", "american samoa", "guam", "palau", "federated states of micronesia",
			"northern mariana islands", "marshall islands", "us armed forces europe");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty())
			return false;

		return VALID_US_STATES.contains(value.trim().toLowerCase());
	}

}
