package org.egov.finance.voucher.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExilPrecision {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(ExilPrecision.class);

	    public static double convertToDouble(final double dNum, final int precision)
	    {
	        // precision++;
	        if (Double.isNaN(dNum))
	            return 0;
	        final int afterPoint = (int) Math.pow(10, precision);
	        String fraction = "0.";
	        for (int i = 0; i < precision; i++)
	            fraction += "0";
	        fraction += "5";
	        final double adjustFraction = Double.parseDouble(fraction);
	        final double retNum = Math.floor((dNum + adjustFraction) * afterPoint) / afterPoint;
	        return retNum;
	    }

	    public static String convertToString(final double dNum, final int precision)
	    {
	        // precision++;
	        if (Double.isNaN(dNum))
	            return "0";
	        final int afterPoint = (int) Math.pow(10, precision);
	        String fraction = "0.";
	        for (int i = 0; i < precision; i++)
	            fraction += "0";
	        fraction += "5";
	        final double adjustFraction = Double.parseDouble(fraction);
	        final double retNum = Math.floor((dNum + adjustFraction) * afterPoint) / afterPoint;
	        return String.valueOf(retNum);
	    }

	    public static double convertToDouble(final String sNum, final int precision)
	    {
	        // precision++;
	        double dNum = 0;
	        try {
	            dNum = Double.parseDouble(sNum);
	        } catch (final NumberFormatException e) {
	            LOGGER.error("There is error " + e.getMessage());
	            return 0;
	        }
	        final int afterPoint = (int) Math.pow(10, precision);
	        String fraction = "0.";
	        for (int i = 0; i < precision; i++)
	            fraction += "0";
	        fraction += "5";
	        final double adjustFraction = Double.parseDouble(fraction);
	        final double retNum = Math.floor((dNum + adjustFraction) * afterPoint) / afterPoint;
	        return retNum;
	    }

	    public static String convertToString(final String sNum, final int precision)
	    {
	        // precision++;
	        double dNum = 0;
	        dNum = Double.parseDouble(sNum);
	        final int afterPoint = (int) Math.pow(10, precision);
	        String fraction = "0.";
	        for (int i = 0; i < precision; i++)
	            fraction += "0";
	        fraction += "5";
	        final double adjustFraction = Double.parseDouble(fraction);
	        final double retNum = Math.floor((dNum + adjustFraction) * afterPoint) / afterPoint;
	        return String.valueOf(retNum);
	    }

	    public static void main(final String[] args)
	    {

	        if (LOGGER.isDebugEnabled())
	            LOGGER.debug(ExilPrecision.convertToString("18.245287987", 2));

	    }

}
