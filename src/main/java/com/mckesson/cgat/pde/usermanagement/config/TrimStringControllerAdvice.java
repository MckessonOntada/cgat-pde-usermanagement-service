package com.mckesson.cgat.pde.usermanagement.config;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class TrimStringControllerAdvice {

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new java.beans.PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(text == null ? null : text.trim());
			}
		});
	}

}