package com.mckesson.cgat.pde.usermanagement.util;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.cgat.pde.usermanagement.utils.UserCurrentRequestContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

	public static void setupPrincipal() {
		LoggedInUserDetails loggedInUser = LoggedInUserDetails.builder()
			.id("1")
			.name(Name.builder().firstName("John").lastName("Doe").build())
			.role(Role.builder().name("System Admin").build())
			.permissions(Permission.getPermissions(UserType.ADMIN))
			.practiceId(1)
			.userName("John Doe")
			.build();
		List<String> roleNames = new ArrayList<>();
		roleNames.add(loggedInUser.getRole().getName());
		Collection<? extends GrantedAuthority> authorities = roleNames.stream()
			.map(SimpleGrantedAuthority::new)
			.toList();
		UserCurrentRequestContext principal = new UserCurrentRequestContext(loggedInUser.getUserName(), "", authorities,
				loggedInUser);
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
		OrganizationContext.setCurrentOrgId(1L);
	}

}
