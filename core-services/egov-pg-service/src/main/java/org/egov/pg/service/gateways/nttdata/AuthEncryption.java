package org.egov.pg.service.gateways.nttdata;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;



public class AuthEncryption {
	private static String encMsg = "never give up on encryption logic";
	private final Cipher cipher;

	public static String getAuthDecrypted(String encData, String key) {
		String decrypted = null;
		try {
			decrypted = AtomEncryption.decrypt(encData, key);
		} catch (Exception e) {
			System.out.println(": EXCEPTION IN DECRYPTION ::" + e.getMessage());
		}
		return decrypted;
	}

	public static String getAuthEncrypted(String plainData, String key) {
		String encrypted = null;
		try {
			encrypted = AtomEncryption.encrypt(plainData, key);
		} catch (Exception e) {
			System.out.println(": EXCEPTION IN ENCRYPTION ::" + e.getMessage());
		}
		return encrypted;
	}

	public AuthEncryption() throws Exception {
		try {
			this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (Exception e) {
			throw fail(e);
		}
	}

	public String getDecryptedData(String tempData, String info1, String info2) {
		String decryptedData = null;
		try {
			tempData = tempData.replace(" ", "+");
			decryptedData = decrypt(info1, info2, tempData);
		} catch (Exception e) {
			System.out.println(": EXCEPTION IN DECRYPTION ::" + e.getMessage());
		}

		return decryptedData;
	}

	public String decrypt(String salt, String iv, String ciphertext) {
		try {
			SecretKey key = generateKey(salt, encMsg);

			byte[] decrypted = doFinal(2, key, iv, base64(ciphertext));
			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (Exception e) {
			System.out.println(": EXCEPTION IN DECODING ::" + e.getMessage());
			throw fail(e);
		}
	}

	public String encrypt(String salt, String iv, String plaintext) {
		try {
			SecretKey key = generateKey(salt, encMsg);
			byte[] encrypted = doFinal(1, key, iv, plaintext.getBytes(StandardCharsets.UTF_8));
			return base64(encrypted);
		} catch (Exception e) {
			throw fail(e);
		}
	}

	private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
		try {
			this.cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
			return this.cipher.doFinal(bytes);
		} catch (Exception e) {
			System.out.println(": EXCEPTION IN ENC/DEC ::" + e.getMessage());
			throw fail(e);
		}
	}

	private static SecretKey generateKey(String salt, String passphrase) throws NoSuchAlgorithmException {
		try {
			int iterationCount = 1000;
			int keySize = 128;
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), iterationCount, keySize);
			return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		} catch (java.security.spec.InvalidKeySpecException e) {
			System.out.println(": EXCEPTION IN GENERATION OF ENC/DEC KEY ::" + e.getMessage());
			throw fail(e);
		}
	}

	private static IllegalStateException fail(Exception e) {
		return new IllegalStateException(e);
	}

	public static String random(int length) {
		byte[] salt = new byte[length];
		new SecureRandom().nextBytes(salt);
		return hex(salt);
	}

	public static String base64(byte[] bytes) {
		return new String(Base64.getEncoder().encode(bytes));
	}

	public static byte[] base64(String str) {
		str = str.replace(" ", "+").trim();

		return Base64.getDecoder().decode(str.getBytes());
	}

	public static String hex(byte[] bytes) {
		return new String(Hex.encodeHex(bytes));
	}

	public static byte[] hex(String str) {
		try {
			return Hex.decodeHex(str.toCharArray());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}