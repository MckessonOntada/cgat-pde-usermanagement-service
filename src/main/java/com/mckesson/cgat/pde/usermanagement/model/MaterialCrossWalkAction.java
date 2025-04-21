package com.mckesson.cgat.pde.usermanagement.model;

public enum MaterialCrossWalkAction {

	LINK, UNLINK;

	public static MaterialCrossWalkAction fromString(String input) {
		for (MaterialCrossWalkAction action : MaterialCrossWalkAction.values()) {
			if (action.name().equalsIgnoreCase(input)) {
				return action;
			}
		}
		throw new IllegalArgumentException("Invalid MaterialCrossWalkAction: " + input);
	}

}
