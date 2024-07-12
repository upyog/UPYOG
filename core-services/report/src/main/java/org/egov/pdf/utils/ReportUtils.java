package org.egov.pdf.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class ReportUtils {

	// Map to store HTML character replacements
	private static final Map<String, String> HTML_CHARACTER_REPLACEMENT_MAP = new HashMap<>();

	// Static initializer to populate the replacement map
	static {
		// HTML entity for non-breaking space
		HTML_CHARACTER_REPLACEMENT_MAP.put("&nbsp;", "&#160;");

		// HTML entity for left double quotation mark
		HTML_CHARACTER_REPLACEMENT_MAP.put("&ldquo;", "&#8220;");

		// HTML entity for right double quotation mark
		HTML_CHARACTER_REPLACEMENT_MAP.put("&rdquo;", "&#8221;");

		// HTML entity for left single quotation mark
		HTML_CHARACTER_REPLACEMENT_MAP.put("&lsquo;", "&#8216;");

		// HTML entity for right single quotation mark
		HTML_CHARACTER_REPLACEMENT_MAP.put("&rsquo;", "&#8217;");

		// HTML entity for horizontal ellipsis (three dots)
		HTML_CHARACTER_REPLACEMENT_MAP.put("&hellip;", "&#8230;");
	}

	/**
	 * Cleans up HTML content by replacing specified target strings with their
	 * corresponding replacement values.
	 *
	 * @param input The input HTML content string to be cleaned up.
	 * @return The cleaned-up HTML content string with replacements applied.
	 */
	public static String htmlContentCleanUp(String input) {
		StringBuilder stringBuilder = new StringBuilder(input);
		for (Map.Entry<String, String> entry : HTML_CHARACTER_REPLACEMENT_MAP.entrySet()) {
			String target = Pattern.quote(entry.getKey());
			String replacement = entry.getValue();
			stringBuilder = new StringBuilder(stringBuilder.toString().replaceAll(target, replacement));
		}
		return stringBuilder.toString();
	}
	
	public static String addMissingClosingTags(String html) {
		html = doSelfClosingTags(html);
		Document doc = Jsoup.parse(html, "", Parser.xmlParser());
		doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		return doc.html();
	}
	
	private static String doSelfClosingTags(String html) {
		// Define an array of self-closing tags
		String[] selfClosingTags = { "br", "img", "hr", "input", "meta", "link", "base", "col", "embed", "source",
				"track", "wbr" };

		// Loop through each self-closing tag and replace it with a proper closing tag
		for (String tag : selfClosingTags) {
			// Replace self-closing tags with a trailing slash, but not already properly
			// closed tags
			html = html.replaceAll("(?i)<" + tag + "(\\s*[^>/]*)/>", "<" + tag + "$1></" + tag + ">");
			// Replace self-closing tags without a trailing slash, but not already properly
			// closed tags
			html = html.replaceAll("(?i)<" + tag + "(\\s*[^>/]*)>(?!</" + tag + ">)", "<" + tag + "$1></" + tag + ">");
		}

		return html;
	}
}
