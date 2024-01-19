package org.egov.pg.service.gateways.nttdata;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
public class AtomSignature {
	public static String generateSignature(String hashKey, String param) {
		String resp = null;
		StringBuilder sb = new StringBuilder(param);
		
		try {
			System.out.println("String =" + sb.toString());
			resp = byteToHexString(encodeWithHMACSHA2(sb.toString(), hashKey));
		} catch (Exception e) {
			System.out.println("Unable to encocd value with key :" + hashKey + " and input :" + 
					sb.toString());
			e.printStackTrace();
		}
		return resp;
	}
	private static byte[] encodeWithHMACSHA2(String text, String keyString)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		Key sk = new SecretKeySpec(keyString.getBytes("UTF-8"), "HMACSHA512");
		Mac mac = Mac.getInstance(sk.getAlgorithm());
		mac.init(sk);
		byte[] hmac = mac.doFinal(text.getBytes("UTF-8"));
		return hmac;
	}
	public static String byteToHexString(byte byData[]) {
		StringBuilder sb = new StringBuilder(byData.length * 2);
		for (int i = 0; i < byData.length; i++) {
			int v = byData[i] & 0xff;
			if (v < 16)
				sb.append('0');
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}
}