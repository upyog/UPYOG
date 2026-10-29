package org.egov.pg.service.gateways.icici;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.egov.pg.constants.PgConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class providing helper methods for the ICICI payment gateway integration.
 * <p>
 * Includes methods for generating transaction timestamps and for computing the
 * secure hash (HMAC SHA256) required to authenticate requests sent to and
 * responses received from the ICICI payment gateway.
 * </p>
 */

@Slf4j
class ICICIUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String getCurrentTxnDate() {

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /*============================================ GENERATE PLAIN HASH TEXT==============*/
    /**
     * Generates the plain (unhashed) text used as input for secure hash computation.
     * <p>
     * Converts the given request object into a sorted map (alphabetical by key), removes
     * any existing {@code secureHash} field, and concatenates the non-null values of all
     * remaining fields in key-sorted order.
     * </p>
     *
     * @param request the request object (or map) to be converted into plain hash text
     * @return the concatenated string of field values used for hash generation
     * @throws RuntimeException if the request cannot be converted or processed
     */
    public static  String generatePlainHashText(Object request) {

        try {

            Map<String, Object> map = OBJECT_MAPPER.convertValue(request, TreeMap.class);

            map.remove("secureHash");

            StringBuilder plainHash = new StringBuilder();

            for (Map.Entry<String, Object> entry : map.entrySet()) {

                Object value = entry.getValue();

                if (value != null) {
                    plainHash.append(value);
                }
            }

            return plainHash.toString();

        } catch (Exception ex) {

            throw new RuntimeException("Failed to generate plain hash text", ex);
        }
    }


    /*============================================ HMAC SHA256==============*/
    /**
     * Generates the secure hash for a given request using HMAC SHA256.
     * <p>
     * Converts the request object into a sorted map (alphabetical by key), removes any
     * existing {@code secureHash} field, concatenates the non-null values of the
     * remaining fields in key-sorted order to form the plain hash text, and computes
     * the HMAC SHA256 of that text using the provided secret key. The resulting hash
     * bytes are returned as a lowercase hexadecimal string.
     * </p>
     *
     * @param request   the request object (or map) for which the secure hash is to be generated
     * @param secretKey the merchant secret key used as the HMAC key
     * @return the hexadecimal string representation of the computed HMAC SHA256 hash
     * @throws RuntimeException if hash generation fails due to an invalid algorithm,
     *                           key, or any other processing error
     */
    public static String generateSecureHash(Object request, String secretKey) {

        try {

            // Convert request object to map
            Map<String, Object> requestMap = OBJECT_MAPPER.convertValue(request, TreeMap.class);

            // Remove secureHash field if already present
            requestMap.remove("secureHash");

            // Create plain hash text
            StringBuilder plainHashText = new StringBuilder();

            for (Map.Entry<String, Object> entry : requestMap.entrySet()) {

                Object value = entry.getValue();

                if (value != null) {
                    plainHashText.append(value);
                }
            }

            log.info("ICICI Plain Hash Text : {}", plainHashText);

            // Generate HMAC SHA256
            Mac mac = Mac.getInstance(PgConstants.HMAC_SHA256);

            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), PgConstants.HMAC_SHA256);

            mac.init(keySpec);

            byte[] hashBytes = mac.doFinal(plainHashText.toString().getBytes(StandardCharsets.UTF_8));

            String secureHash = HexFormat.of().formatHex(hashBytes);

            log.info("ICICI Secure Hash : {}", secureHash);

            return secureHash;

        } catch (Exception ex) {

            log.error("Failed to generate secure hash", ex);

            throw new RuntimeException("Failed to generate secure hash", ex);
        }
    }
}