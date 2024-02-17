package com.alpha.interview.wizard.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDateConverter implements Converter<String, Date> {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	@Override
	public Date convert(String source) {
		if (source == null || source.isEmpty()) {
			return null;
		}
		try {
			return dateFormat.parse(source);
		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"Invalid date format. Please use yyyy-MM-dd format.", e);
		}
	}
}
