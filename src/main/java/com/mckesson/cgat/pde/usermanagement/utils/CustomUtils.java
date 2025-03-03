package com.mckesson.cgat.pde.usermanagement.utils;

import com.mckesson.cgat.pde.usermanagement.model.Name;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

public class CustomUtils {

	public static final String UNAUTHORIZED_MESSAGE = "User is not authorized for this action";

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

	public static final String ORG_ADMIN_ROLE_NAME = "Organization Admin";

	public static final String ORG_ADMIN_ROLE_DESC = "Organization Specific Admin Role";

	public static final String SORT_ORDER_ASC = "asc";

	public static final String API_DATE_FORMAT = "dd/MM/yyyy";

	private static final String INVENTORY = "I";

	private static final String TRUE_DROPSHIP = "T";

	private static final Set<String> INVENTORY_CATEGORIES = Set.of("NORM", "ZNRM", "ZFBC");

	private static final Set<String> TRUE_DROPSHIP_CATEGORIES = Set.of("BANS", "BANC");

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

	public static boolean isValidDate(String input) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
		try {
			format.parse(input);
			return true;
		}
		catch (ParseException e) {
			return false;
		}
	}

	public static String getFullName(Name name) {
		return name.getFirstName() + " " + name.getLastName();
	}

	public static String determineDropshipIndicator(String itemCategoryGroup) {
		if (itemCategoryGroup == null) {
			return null;
		}
		else if (INVENTORY_CATEGORIES.contains(itemCategoryGroup)) {
			return INVENTORY;
		}
		else if (TRUE_DROPSHIP_CATEGORIES.contains(itemCategoryGroup)) {
			return TRUE_DROPSHIP;
		}
		return null;
	}

}
