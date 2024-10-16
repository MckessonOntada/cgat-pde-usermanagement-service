package com.mckesson.jbat.usermanagement.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import org.junit.jupiter.api.Test;

class CustomUtilsTest {

	@Test
	public void testValidateEmail_ValidEmail() {
		String[] validEmails = { "test@example.com", "user123@gmail.com", "john.doe@company.co.uk" };
		for (String email : validEmails) {
			assertTrue(CustomUtils.validateEmail(email), "Email '" + email + "' should be valid");
		}
	}

	@Test
	public void testValidateEmail_InvalidEmail() {
		String[] invalidEmails = { "invalid-email.com", "user@domain", "user@domain.", "@domain.com", "user@.com",
				"user@domain..com", "user@-domain.com" };
		for (String email : invalidEmails) {
			assertFalse(CustomUtils.validateEmail(email), "Email '" + email + "' should be invalid");
		}
	}

}
