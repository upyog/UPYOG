package org.upyog.adv.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for shortening URLs in the Advertisement Booking Service.
 */
@Slf4j
@Component
public class UrlShortenerUtil {

	private final RestTemplate restTemplate;

	@Value("${egov.url.shortner.host}")
	private String urlShortnerHost;

	@Value("${egov.url.shortner.endpoint}")
	private String urShortnerPath;

	public UrlShortenerUtil(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getShortenedUrl(String url) {
		Map<String, String> body = new HashMap<>();
		body.put("url", url);
		StringBuilder builder = new StringBuilder(urlShortnerHost);
		builder.append(urShortnerPath);
		String res = restTemplate.postForObject(builder.toString(), body, String.class);

		if (StringUtils.isEmpty(res)) {
			log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
			return url;
		}
		return res;
	}

}
