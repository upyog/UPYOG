package org.egov.garbageservice.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class GrbgUtils {

	public static String toCamelCase(String str) {
		// Convert the entire string to lowercase and then capitalize the first letter
		if (str == null || str.isEmpty()) {
			return str;
		}
		// Convert the first letter to uppercase and the rest to lowercase
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static String removeFirstAndLastChar(String str) {
		// Check if the string is long enough to remove first and last characters
		if (str == null || str.length() <= 2) {
			return ""; // Return empty string if length is 2 or less
		}

		// Use substring to remove first and last character
		return str.substring(1, str.length() - 1);
	}

	public String getContentAsString(ClassPathResource resource) {
		String htmlContent = "";
		try {
			// Read all lines from the file and join them into a single string
			htmlContent = Files.lines(Paths.get(resource.getURI())).collect(Collectors.joining("\n"));
			// Output the content of the HTML file
		} catch (IOException e) {
			// Handle exception if the file is not found or can't be read
//			e.printStackTrace();
		}
		return htmlContent;
	}

}
