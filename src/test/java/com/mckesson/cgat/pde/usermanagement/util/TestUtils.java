package com.mckesson.cgat.pde.usermanagement.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TestUtils {

	/**
	 * Converts a date string into a java.util.Date object based on the given format.
	 * @param dateString the date string to parse
	 * @param format the expected date format (e.g., "dd/MM/yyyy", "yyyy-MM-dd")
	 * @return the parsed Date object
	 * @throws IllegalArgumentException if the date string is invalid or doesn't match the
	 * format
	 */
	public static Date parseDate(String dateString, String format) {
		if (dateString == null || format == null) {
			throw new IllegalArgumentException("Date string and format must not be null");
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		dateFormatter.setLenient(false); // Ensures strict date parsing
		try {
			return dateFormatter.parse(dateString);
		}
		catch (ParseException e) {
			throw new IllegalArgumentException("Invalid date format. Expected format: " + format, e);
		}
	}

	public static LocalDate parseLocalDate(String dateStr, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDate.parse(dateStr, formatter);
	}

}
