package org.egov.pg.utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Utils {

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

    /**
     * Serializes an object into a JSON string using Jackson ObjectMapper.
     */
    public static String convertObjectToString(ObjectMapper mapper, Object options) throws Exception {
        return mapper.writeValueAsString(options);
    }

}
