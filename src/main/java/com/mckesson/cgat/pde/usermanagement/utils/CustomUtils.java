package com.mckesson.cgat.pde.usermanagement.utils;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

	public static final String ALL = "ALL";

	private static final Set<String> INVENTORY_CATEGORIES = Set.of("NORM", "ZNRM", "ZFBC");

	private static final Set<String> TRUE_DROPSHIP_CATEGORIES = Set.of("BANS", "BANC");

	public static final String ACCOUNT_REGEX = "\\d{1,20}";

	public static final String PRODUCT_ID_REGEX = "[a-zA-Z0-9]{1,50}";

	public static final String STATUS_REGEX = "(?i)Link|Unlink";

	public static final String HEADER_ACCOUNT_NUMBER = "Account Number";

	public static final String HEADER_PRODUCT_ID = "SP Product ID";

	public static final String HEADER_STATUS = "Status";

	public static final String HEADER_ERROR_MESSAGE = "Error Message";

	public static final List<String> REQUIRED_HEADERS = List.of(HEADER_ACCOUNT_NUMBER, HEADER_PRODUCT_ID,
			HEADER_STATUS);

	public static final String CATALOG_ACTION_STATUS_LINK = "Link";

	public static final String CATALOG_ACTION_STATUS_UNLINK = "Unlink";

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

	public static String getConsolidatedStatus(List<String> status) {
		if (CollectionUtils.isEmpty(status))
			return object_status.ACTIVE.name();

		boolean hasActive = status.stream().anyMatch(i -> i.equalsIgnoreCase(object_status.ACTIVE.name()));
		boolean hasInactive = status.stream().anyMatch(i -> i.equalsIgnoreCase(object_status.INACTIVE.name()));

		if (hasActive && hasInactive)
			return object_status.ALL.name();
		if (hasActive)
			return object_status.ACTIVE.name();
		return object_status.INACTIVE.name();
	}

	public static String getConsolidatedChannelType(List<ChannelType> channelTypes) {
		if (CollectionUtils.isEmpty(channelTypes))
			return ChannelType.BUY_AND_BILL.name();

		boolean containsBNB = channelTypes.contains(ChannelType.BUY_AND_BILL);
		boolean containsSP = channelTypes.contains(ChannelType.SPECIALITY_PHARMA);

		if (containsBNB && containsSP)
			return ALL;
		if (containsSP)
			return ChannelType.SPECIALITY_PHARMA.name();
		return ChannelType.BUY_AND_BILL.name();
	}

}
