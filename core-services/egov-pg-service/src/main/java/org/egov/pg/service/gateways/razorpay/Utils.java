package org.egov.pg.service.gateways.razorpay;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;

public class Utils {
	  // This is an array for creating hex chars
    private static final char[] HEX_TABLE = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	 private static final DecimalFormat CURRENCY_FORMATTER_RUPEE = new DecimalFormat("0.00");
	    private static final DecimalFormat CURRENCY_FORMATTER_PAISE = new DecimalFormat("0");

	    private Utils() {
	    };

	    public static String formatAmtAsRupee(String txnAmount) {
	        return CURRENCY_FORMATTER_RUPEE.format(Double.valueOf(txnAmount));
	    }

	    public static String formatAmtAsPaise(String txnAmount) {
	        return CURRENCY_FORMATTER_PAISE.format(Double.valueOf(txnAmount) * 100);
	    }

	    public static String convertPaiseToRupee(String paise){
	        return new BigDecimal(paise).movePointLeft(2).toPlainString();
	    }

  public static boolean verifyPaymentSignature(JSONObject attributes, String apiSecret)
      throws RazorpayException {
    String expectedSignature = attributes.getString("razorpay_signature");
    String orderId = attributes.getString("razorpay_order_id");
    String paymentId = attributes.getString("razorpay_payment_id");
    String payload = orderId + '|' + paymentId;
    return verifySignature(payload, expectedSignature, apiSecret);
  }

  public static boolean verifyWebhookSignature(String payload, String expectedSignature,
      String webhookSecret) throws RazorpayException {
    return verifySignature(payload, expectedSignature, webhookSecret);
  }

  public static boolean verifySignature(String payload, String expectedSignature, String secret)
      throws RazorpayException {
    String actualSignature = getHash(payload, secret);
    return isEqual(actualSignature.getBytes(), expectedSignature.getBytes());
  }

  public static String getHash(String payload, String secret) throws RazorpayException {
    Mac sha256_HMAC;
    try {
      sha256_HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
      sha256_HMAC.init(secret_key);
      byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
      return new String(Hex.encodeHex(hash));
    } catch (Exception e) {
      throw new RazorpayException(e.getMessage());
    }
  }

  /**
   * We are not using String.equals() method because of security issue mentioned in
   * <a href="http://security.stackexchange.com/a/83670">StackOverflow</a>
   * 
   * @param a
   * @param b
   * @return boolean
   */
  private static boolean isEqual(byte[] a, byte[] b) {
    if (a.length != b.length) {
      return false;
    }
    int result = 0;
    for (int i = 0; i < a.length; i++) {
      result |= a[i] ^ b[i];
    }
    return result == 0;
  }
  
  
  private static byte[] fromHexString(String s, int offset, int length) {
      if ((length % 2) != 0)
          return null;
      byte[] byteArray = new byte[length / 2];
      int j = 0;
      int end = offset + length;
      for (int i = offset; i < end; i += 2) {
          int high_nibble = Character.digit(s.charAt(i), 16);
          int low_nibble = Character.digit(s.charAt(i + 1), 16);
          if (high_nibble == -1 || low_nibble == -1) {
              // illegal format
              return null;
          }
          byteArray[j++] = (byte) (((high_nibble << 4) & 0xf0) | (low_nibble & 0x0f));
      }
      return byteArray;
  }
  
  /**
   * Returns Hex output of byte array
   */
  private static String hex(byte[] input) {
      // create a StringBuffer 2x the size of the hash array
      StringBuilder sb = new StringBuilder(input.length * 2);

      // retrieve the byte array data, convert it to hex
      // and add it to the StringBuilder
      for (byte anInput : input) {
          sb.append(HEX_TABLE[(anInput >> 4) & 0xf]);
          sb.append(HEX_TABLE[anInput & 0xf]);
      }
      return sb.toString();
  }
  
  static String SHAhashAllFields(Map<String, String> fields, String secret) {

      // create a list and sort it
      List<String> fieldNames = new ArrayList<>(fields.keySet());
      Collections.sort(fieldNames);

      // create a buffer for the SHA256 input
      StringBuilder buf = new StringBuilder();


      // iterate through the list and add the remaining field values
      Iterator itr = fieldNames.iterator();
      while (itr.hasNext()) {
          String fieldName = (String) itr.next();
          String fieldValue = fields.get(fieldName);
          if ((fieldValue != null) && (fieldValue.length() > 0)) {
              buf.append(fieldName).append("=").append(fieldValue);
              if (itr.hasNext()) {
                  buf.append('&');
              }
          }
      }
      byte[] mac = null;
      try {
          byte[] b = fromHexString(secret, 0, secret.length());
          SecretKey key = new SecretKeySpec(b, "HmacSHA256");
          Mac m = Mac.getInstance("HmacSHA256");
          m.init(key);

          m.update(buf.toString().getBytes("ISO-8859-1"));
          mac = m.doFinal();
      } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
          throw new CustomException("CHECKSUM_GEN_FAILED", "Hash generation failed, gateway redirect URI " +
                  "cannot be generated");
      }
      return hex(mac);

  } // end hashAllFields()
}
