package com.appcrossings.config;

public class StringUtils {

	public static boolean hasText(String string) {
		return (string != null && !string.trim().equals(""));
	}

}
