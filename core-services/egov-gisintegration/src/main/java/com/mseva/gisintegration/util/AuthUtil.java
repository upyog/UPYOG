package com.mseva.gisintegration.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthUtil {

    public static String md5Hash(String input) {
        try {
            // Compute MD5 hash on raw input without normalization
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAuthorized(String authorizationHeader, JsonNode usersNode) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
                return false;
            }
            String base64Credentials = authorizationHeader.substring("Basic ".length());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            if (values.length != 2) {
                return false;
            }
            String username = values[0];
            String password = values[1];
            if (usersNode == null) {
                return false;
            }
            for (JsonNode userNode : usersNode) {
                String storedUsernameMd5 = userNode.has("usernameMd5") ? userNode.get("usernameMd5").asText() : null;
                String storedPasswordMd5 = userNode.get("passwordMd5").asText();

                if (storedUsernameMd5 != null && storedUsernameMd5.equals(md5Hash(username)) && storedPasswordMd5.equals(md5Hash(password))) {
                    return true;
                }
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
