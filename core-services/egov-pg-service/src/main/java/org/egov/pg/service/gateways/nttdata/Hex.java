package org.egov.pg.service.gateways.nttdata;



import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Hex
{
  private static String apiName = "OTS_AIPAY_";

  long systemMilliseconds = System.currentTimeMillis();
  public static final String DEFAULT_CHARSET_NAME = "UTF-8";
  private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  private final String charsetName;

  public static byte[] decodeHex(char[] data)
  {
    int len = data.length;

    byte[] out = new byte[len >> 1];

    int i = 0;
    for (int j = 0; j < len; ++i) {
      int f = toDigit(data[j]) << 4;
      ++j;
      f |= toDigit(data[j]);
      ++j;
      out[i] = (byte)(f & 0xFF);
    }

    return out;
  }

  public static char[] encodeHex(byte[] data)
  {
    return encodeHex(data, true);
  }

  public static char[] encodeHex(byte[] data, boolean toLowerCase)
  {
    return encodeHex(data, (toLowerCase) ? DIGITS_LOWER : DIGITS_UPPER);
  }

  protected static char[] encodeHex(byte[] data, char[] toDigits)
  {
    int l = data.length;
    char[] out = new char[l << 1];

    int i = 0; for (int j = 0; i < l; ++i) {
      out[(j++)] = toDigits[((0xF0 & data[i]) >>> 4)];
      out[(j++)] = toDigits[(0xF & data[i])];
    }
    return out;
  }

  public static String encodeHexString(byte[] data)
  {
    return new String(encodeHex(data));
  }

  protected static int toDigit(char ch)
  {
    return Character.digit(ch, 16);
  }

  public Hex()
  {
    this.charsetName = "UTF-8";
  }

  public Hex(String csName)
  {
    this.charsetName = csName;
  }

  public byte[] decode(byte[] array)
  {
    byte[] str = null;
    try
    {
      str = decodeHex(new String(array, getCharsetName()).toCharArray());
      return str;
    } catch (UnsupportedEncodingException e) {
      System.out.println(apiName + this.systemMilliseconds + ": EXCEPTION IN DECODING ::" + e.getMessage());
    }
    return str;
  }

  public Object decode(Object object)
  {
    char[] charArray = null;
    try
    {
      charArray = (object instanceof String) ? ((String)object).toCharArray() : (char[])object;
      return decodeHex(charArray);
    } catch (ClassCastException e) {
      System.out.println(apiName + this.systemMilliseconds + ": EXCEPTION IN DECODING ::" + e.getMessage());
    }
    return charArray;
  }

  public Object encode(Object object) throws UnsupportedEncodingException
  {
    byte[] byteArray = null;
    try
    {
      byteArray = (object instanceof String) ? ((String)object).getBytes(getCharsetName()) : (byte[])object;
      return encodeHex(byteArray);
    }
    catch (ClassCastException e) {
      System.out.println(apiName + this.systemMilliseconds + ": EXCEPTION IN ENCODING ::" + e.getMessage());
    }

    return byteArray;
  }

  public String getCharsetName()
  {
    return this.charsetName;
  }

  public String toString()
  {
    return super.toString() + "[charsetName=" + this.charsetName + "]";
  }
}
