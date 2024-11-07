package com.mckesson.cgat.pde.usermanagement.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class CustomUtils {

	private final static String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
			+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

	public enum operation_type {

		CREATE, UPDATE, DELETE

	};

	public enum permission_type {

		NONE, VIEW, FULL

	};

	public enum object_status {

		ALL, ACTIVE, INACTIVE

	}
	
	public static final String ORG_ADMIN_ROLE_NAME = "ORGANIZATION_ADMIN_ROLE";
	
	public static final String ORG_ADMIN_ROLE_DESC = "Organization Specific Admin Role";
	
	public static final String SORT_ORDER_ASC = "asc";

	public static boolean validateEmail(String emailAddress) {
		return Pattern.compile(regexPattern).matcher(emailAddress).matches();
	}

	public static String formatDateToString(Date date) {
		// Check if the date is null
		if (date == null) {
			return null;
		}

		// Define the desired date format for the date and time portion
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		// Convert Date to String with milliseconds
		String formattedDate = formatter.format(date);

		// Since java.util.Date only supports milliseconds, append "000" for
		// microseconds
		String formattedDateWithMicroseconds = formattedDate + "000";

		return formattedDateWithMicroseconds;
	}

}
