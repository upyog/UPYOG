package org.egov.pg.service.gateways.razorpay;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RazorPayModelUtils {


	/**
	 * Verifies the signature of a payment using HMAC-SHA256.
	 *
	 * @param razorpayOrderId   The Razorpay order ID
	 * @param razorpayPaymentId The Razorpay payment ID
	 * @param razorpaySignature The signature to verify
	 * @param secretKey         The secret key used for HMAC-SHA256
	 * @return true if the signature is verified, false otherwise
	 */
	public static boolean isSignatureVerified(String razorpayOrderId, String razorpayPaymentId,
			String razorpaySignature, String secretKey) {
		try {
			String generatedSignature = generateSignature(secretKey, razorpayOrderId + "|" + razorpayPaymentId);
			return StringUtils.equals(razorpaySignature, generatedSignature);
		} catch (Exception e) {
			log.error("Exception occurred while verifying the payment gateway signature.", e);
		}
		return false;
	}

	/**
	 * Generates the HMAC-SHA256 signature for the given data using the secret key.
	 *
	 * @param key  The secret key
	 * @param data The data to be signed
	 * @return The hexadecimal representation of the HMAC-SHA256 signature
	 * @throws NoSuchAlgorithmException     If the algorithm is not available
	 * @throws UnsupportedEncodingException If the encoding is not supported
	 * @throws InvalidKeyException          If the key is invalid
	 */
	private static String generateSignature(String key, String data)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		sha256_HMAC.init(secret_key);

		byte[] hash = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
		return Hex.encodeHexString(hash);
	}


}
