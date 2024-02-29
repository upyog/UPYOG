package org.egov.pg.service.gateways.ccAvanue;
import lombok.Data;


import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AesUtil {
	Cipher ecipher;
	Cipher dcipher;

	/**
	 * Input a string that will be md5 hashed to create the key.
	 * @return void, cipher initialized
	 */

	public AesUtil(){
		try{
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			this.setupCrypto(kgen.generateKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public AesUtil(String key){
		SecretKeySpec skey = new SecretKeySpec(getMD5(key), "AES");
		this.setupCrypto(skey);
	}

	private void setupCrypto(SecretKey key){
		// Create an 8-byte initialization vector
		byte[] iv = new byte[]
				{
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
				};

		AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		try
		{
			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// CBC requires an initialization vector
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Buffer used to transport the bytes from one stream to another
	byte[] buf = new byte[1024];

	public void encrypt(InputStream in, OutputStream out){
		try {
			// Bytes written to out will be encrypted
			out = new CipherOutputStream(out, ecipher);

			// Read in the cleartext bytes and write to out to encrypt
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0){
				out.write(buf, 0, numRead);
			}
			out.close();
		}
		catch (java.io.IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Input is a string to encrypt.
	 * @return a Hex string of the byte array
	 */
	public String encrypt(String plaintext){
		try{
			byte[] ciphertext = ecipher.doFinal(plaintext.getBytes("UTF-8"));
			return this.byteToHex(ciphertext);
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

	public void decrypt(InputStream in, OutputStream out){
		try {
			// Bytes read from in will be decrypted
			in = new CipherInputStream(in, dcipher);

			// Read in the decrypted bytes and write the cleartext to out
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Input encrypted String represented in HEX
	 * @return a string decrypted in plain text
	 */
	public String decrypt(String hexCipherText){
		try{
			String plaintext = new String(dcipher.doFinal(this.hexToByte(hexCipherText)), "UTF-8");
			return  plaintext;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String decrypt(byte[] ciphertext){
		try{
			String plaintext = new String(dcipher.doFinal(ciphertext), "UTF-8");
			return  plaintext;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] getMD5(String input){
		try{
			byte[] bytesOfMessage = input.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(bytesOfMessage);
		}  catch (Exception e){
			return null;
		}
	}

	static final String HEXES = "0123456789ABCDEF";

	public static String byteToHex( byte [] raw ) {
		if ( raw == null ) {
			return null;
		}
		String result = "";
		for (int i=0; i < raw.length; i++) {
			result +=
					Integer.toString( ( raw[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}

	public static byte[] hexToByte( String hexString){
		int len = hexString.length();
		byte[] ba = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			ba[i/2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i+1), 16));
		}
		return ba;
	}

	public static String asHex(byte buf[]){
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		for(int i = 0; i < buf.length; i++){
			if((buf[i] & 0xff) < 16)
				strbuf.append("0");
			strbuf.append(Long.toString(buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}


	public static void main1(String args[]){
		String result=null;
		String err=null;
		String key = null;
		String data = null;
		String action = null;

		if(args==null || args.length<3) 
			err="error: missing one or more arguments. Usage: AesCryptUtil key data <enc|dec>";
		else {
			key = args[0];
			data = args[1];
			action = args[2];

			if(key==null)
				err="error: no key";
			else
				if(key.length()<32)
					err="error: key length less than 32 bytes";
				else
					if(data==null || action==null)
						err="error: no data";
					else
						if(action==null)
							err="error: no action";
						else
							if(!action.equals("enc") && !action.equals("dec"))
								err="error: invalid action";
		}

		if(err==null) {
			try {
				AesUtil encrypter = new AesUtil(key);

				if(action.equals("enc"))
					result = encrypter.encrypt(data);
				else
					result = encrypter.decrypt(data);
			}
			catch (Exception e) {
				err="error : Exception in performing the requested operation : " + e;
			}
		}
		if(result!=null)
			System.out.println(result);
		else
			System.out.println(err);
	}
	
	public static void main(String[] args) {
		AesUtil aes = new AesUtil("8E60F88CDB7BECBDA1D41800E9196AFD");
		System.out.println(aes.decrypt("2332bf0d22f3ea59766188ee1e67c8338684a3ca89d0aeaa65c81003f6fd996c7a774f89b29a37aa3a1044f75a13e5c3ac8426fff403cc8e2951c439e10003be26e97842225442ecf7d424e82354b040a2a1b2655c5a05369b65aac6e4bbb6d970e7eae9fc94ff3ce11f1017196e7539bef4447090941804a45c811d841d0a6410529567c3521e87c13461c53b53dadee96b2fd7c6488b374610487a590ced99c563f6fad177c38162c6738db12b806f69797aa56a8a89ef174dbf04a6a8f193"));
	}
}
