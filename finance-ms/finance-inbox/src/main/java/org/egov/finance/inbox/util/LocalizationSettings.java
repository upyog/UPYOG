

package org.egov.finance.inbox.util;



import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.System.getProperty;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

public final class LocalizationSettings {

    public static final String DEFAULT_TIME_ZONE_KEY = "default.time.zone";
    public static final String DEFAULT_COUNTRY_CODE_KEY = "default.country.code";
    public static final String DEFAULT_CURRENCY_CODE_KEY = "default.currency.code";
    public static final String DEFAULT_CURRENCY_NAME_KEY = "default.currency.name";
    public static final String DEFAULT_CURRENCY_NAME_PLURAL_KEY = "default.currency.name.plural";
    public static final String DEFAULT_CURRENCY_UNIT_NAME_KEY = "default.currency.unit.name";
    public static final String DEFAULT_CURRENCY_UNIT_NAME_PLURAL_KEY = "default.currency.unit.name.plural";
    public static final String DEFAULT_CURRENCY_NAME_SHORT_KEY = "default.currency.name.short";
    public static final String DEFAULT_CURRENCY_SYMBOL_UTF8_KEY = "default.currency.symbol.utf-8";
    public static final String DEFAULT_CURRENCY_SYMBOL_HEX_KEY = "default.currency.symbol.hex";
    public static final String DEFAULT_LOCALE_KEY = "default.locale";
    public static final String DEFAULT_ENCODING_KEY = "default.encoding";
    public static final String DEFAULT_DATE_PATTERN_KEY = "default.date.pattern";
    public static final String DEFAULT_DATE_TIME_PATTERN_KEY = "default.date.time.pattern";

    public static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";
    public static final String DEFAULT_DATE_TIME_PATTERN = "dd/MM/yyyy hh:mm a";
    public static final String DEFAULT_TIME_ZONE = "IST";
    public static final String DEFAULT_COUNTRY_CODE = "91";
    public static final String DEFAULT_CURRENCY_CODE = "INR";
    public static final String DEFAULT_CURRENCY_NAME = "Rupee";
    public static final String DEFAULT_CURRENCY_NAME_PLURAL = "Rupees";
    public static final String DEFAULT_CURRENCY_UNIT_NAME = "Paisa";
    public static final String DEFAULT_CURRENCY_UNIT_NAME_PLURAL = "Paise";
    public static final String DEFAULT_CURRENCY_NAME_SHORT = "Rs.";
    public static final String DEFAULT_CURRENCY_SYMBOL_UTF8 = "\u20B9";
    public static final String DEFAULT_CURRENCY_SYMBOL_HEX = "&#x20b9;";
    public static final String DEFAULT_LOCALE = "en_IN";
    public static final String DEFAULT_ENCODING = "UTF-8";


    private LocalizationSettings() {
        //only static methods
    }

	/*
	 * public static DateTimeZone jodaTimeZone() { return
	 * DateTimeZone.forTimeZone(timeZone()); }
	 */
    
	/*
	 * public static ZoneId javaTimeZone() { return timeZone().toZoneId(); }
	 */ 
    

    public static TimeZone timeZone() {
        return TimeZone.getTimeZone(defaultIfBlank(getProperty(DEFAULT_TIME_ZONE_KEY), DEFAULT_TIME_ZONE));
    }

    public static String countryCode() {
        return defaultIfBlank(getProperty(DEFAULT_COUNTRY_CODE_KEY), DEFAULT_COUNTRY_CODE);
    }

    public static String currencyCode() {
        return defaultIfBlank(getProperty(DEFAULT_CURRENCY_CODE_KEY), DEFAULT_CURRENCY_CODE);
    }

    public static String currencyName() {
        return defaultIfBlank(getProperty(DEFAULT_CURRENCY_NAME_KEY), DEFAULT_CURRENCY_NAME);
    }

    public static String currencyNamePlural() {
        return defaultIfBlank(getProperty(DEFAULT_CURRENCY_NAME_PLURAL_KEY), DEFAULT_CURRENCY_NAME_PLURAL);
    }

    public static String currencyUnitName() {
        return defaultIfBlank(getProperty(DEFAULT_CURRENCY_UNIT_NAME_KEY), DEFAULT_CURRENCY_UNIT_NAME);
    }

    public static String currencyUnitNamePlural() {
        return defaultIfBlank(getProperty(DEFAULT_CURRENCY_UNIT_NAME_PLURAL_KEY), DEFAULT_CURRENCY_UNIT_NAME_PLURAL);
    }

    public static String currencyNameShort() {
        return defaultIfBlank(getProperty(DEFAULT_CURRENCY_NAME_SHORT_KEY), DEFAULT_CURRENCY_NAME_SHORT);
    }

    public static String currencySymbolUtf8() {
        return defaultIfBlank(getProperty(DEFAULT_CURRENCY_SYMBOL_UTF8_KEY), DEFAULT_CURRENCY_SYMBOL_UTF8);
    }

    public static String currencySymbolHex() {
        return defaultIfBlank(getProperty(DEFAULT_CURRENCY_SYMBOL_HEX_KEY), DEFAULT_CURRENCY_SYMBOL_HEX);
    }

    public static Locale locale() {
        return Locale.forLanguageTag(defaultIfBlank(getProperty(DEFAULT_LOCALE_KEY), DEFAULT_LOCALE));
    }

    public static Charset encoding() {
        return Charset.forName(defaultIfBlank(getProperty(DEFAULT_ENCODING_KEY), DEFAULT_ENCODING));
    }

    public static String datePattern() {
        return defaultIfBlank(getProperty(DEFAULT_DATE_PATTERN_KEY), DEFAULT_DATE_PATTERN);
    }

    public static String dateTimePattern() {
        return defaultIfBlank(getProperty(DEFAULT_DATE_TIME_PATTERN_KEY), DEFAULT_DATE_TIME_PATTERN);
    }
}
