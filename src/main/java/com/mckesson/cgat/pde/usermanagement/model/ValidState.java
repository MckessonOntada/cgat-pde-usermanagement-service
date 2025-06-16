package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidStateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidState {

	String message() default "Invalid state";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
