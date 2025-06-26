package org.egov.finance.inbox.util;

import static org.egov.finance.inbox.util.LocalizationSettings.datePattern;
import static org.egov.finance.inbox.util.LocalizationSettings.dateTimePattern;
import static org.egov.finance.inbox.util.LocalizationSettings.timeZone;
import static org.egov.finance.inbox.util.LocalizationSettings.locale;
import static org.egov.finance.inbox.util.NumberToWordConverter.numberToWords;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.egov.finance.inbox.exception.InboxServiceException;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;


public final class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static final String DEFAULT_YEAR_PATTERN = "yyyy";
    private static final String FILE_NAME_DATE_PATTERN = "yyyyMMddhhmm";
    private static final Map<String, DateTimeFormatter> DATE_FORMATTER_HOLDER = new ConcurrentHashMap<>(3);

    private static final String[] DATE_IN_WORDS = {
            "First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth", "Eleventh",
            "Twelfth", "Thirteenth", "Fourteenth", "Fifteenth", "Sixteenth", "Seventeenth", "Eighteenth", "Nineteenth",
            "Twentieth", "Twenty first", "Twenty second", "Twenty third", "Twenty fourth", "Twenty fifth", "Twenty sixth",
            "Twenty seventh", "Twenty eighth", "Twenty ninth", "Thirtieth", "Thirty first"
    };

    private static final Map<Integer, String> MONTH_SHORT_NAMES = new ImmutableMap.Builder<Integer, String>()
            .put(1, "Jan").put(2, "Feb").put(3, "Mar")
            .put(4, "Apr").put(5, "May").put(6, "Jun")
            .put(7, "Jul").put(8, "Aug").put(9, "Sep")
            .put(10, "Oct").put(11, "Nov").put(12, "Dec").build();

    private static final Map<Integer, String> MONTH_FULL_NAMES = new ImmutableMap.Builder<Integer, String>()
            .put(1, "January").put(2, "February").put(3, "March")
            .put(4, "April").put(5, "May").put(6, "June")
            .put(7, "July").put(8, "August").put(9, "September")
            .put(10, "October").put(11, "November").put(12, "December").build();

    private static final Map<Integer, String> FIN_MONTH_NAMES = new ImmutableMap.Builder<Integer, String>()
            .put(1, "April").put(2, "May").put(3, "June")
            .put(4, "July").put(5, "August").put(6, "September")
            .put(7, "October").put(8, "November").put(9, "December")
            .put(10, "January").put(11, "February").put(12, "March").build();

    private DateUtils() {}

    public static String currentYear() {
        return String.valueOf(LocalDate.now().getYear());
    }

    public static String toYearFormat(LocalDate date) {
        return String.valueOf(date.getYear());
    }

    public static String toYearFormat(Date date) {
        return toYearFormat(convertToLocalDate(date));
    }

    public static String currentDateToDefaultDateFormat() {
        return toDefaultDateFormat(LocalDate.now());
    }

    public static String currentDateToGivenFormat(String format) {
        return getFormattedDate(now(), format);
    }

    public static String toDefaultDateFormat(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(datePattern()).withLocale(locale()));
    }

    public static String toDefaultDateFormat(Date date) {
        return toDefaultDateFormat(convertToLocalDate(date));
    }

    public static Date toDateUsingDefaultPattern(String date) {
        return Date.from(LocalDate.parse(date, DateTimeFormatter.ofPattern(datePattern()).withLocale(locale()))
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String currentDateToFileNameFormat() {
        return ZonedDateTime.now(ZoneId.of(timeZone().getID())).format(DateTimeFormatter.ofPattern(FILE_NAME_DATE_PATTERN));
    }

    public static Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date startOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static int noOfMonthsBetween(Date startDate, Date endDate) {
        return (int) ChronoUnit.MONTHS.between(convertToLocalDate(startDate), convertToLocalDate(endDate));
    }

    public static int daysBetween(Date startDate, Date endDate) {
        return (int) ChronoUnit.DAYS.between(convertToLocalDate(startDate), convertToLocalDate(endDate));
    }

    public static int noOfYearsBetween(Date startDate, Date endDate) {
        return (int) ChronoUnit.YEARS.between(convertToLocalDate(startDate), convertToLocalDate(endDate));
    }

    public static Date add(Date inputDate, int addType, int addAmount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputDate);
        calendar.add(addType, addAmount);
        return calendar.getTime();
    }

    public static boolean compareDates(Date firstDate, Date secondDate) {
        return firstDate == null || secondDate == null || !firstDate.before(secondDate);
    }

    public static Map<Integer, String> getAllMonths() {
        return MONTH_SHORT_NAMES;
    }

    public static Map<Integer, String> getAllMonthsWithFullNames() {
        return MONTH_FULL_NAMES;
    }

    public static Date getDate(String date, String pattern) {
        try {
            return StringUtils.isNotBlank(date) && StringUtils.isNotBlank(pattern) ? getDateFormatter(pattern).parse(date) : null;
        } catch (ParseException e) {
            throw new InboxServiceException(Map.of("INVALID_DATE","INVALID_DATE"));
        }
    }

    public static Date getDate(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        return calendar.getTime();
    }

    public static String getDefaultFormattedDate(Date date) {
        return toDefaultDateFormat(date);
    }

    public static String getFormattedDate(Date date, String pattern) {
        return convertToLocalDate(date).format(DateTimeFormatter.ofPattern(pattern).withLocale(locale()));
    }

    public static Date now() {
        return new Date();
    }

    public static Date today() {
        Calendar calendar = Calendar.getInstance();
        return getDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Date tomorrow() {
        return add(new Date(), Calendar.DAY_OF_MONTH, 1);
    }

    public static String convertToWords(Date dateToConvert) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToConvert);
        StringBuilder dateInWord = new StringBuilder();
        dateInWord.append(DATE_IN_WORDS[cal.get(Calendar.DATE) - 1]).append(' ');
        dateInWord.append(MONTH_FULL_NAMES.get(cal.get(Calendar.MONTH) + 1)).append(' ');
        dateInWord.append(numberToWords(BigDecimal.valueOf(cal.get(Calendar.YEAR)), false, false));
        return dateInWord.toString();
    }

    public static boolean between(Date date, Date fromDate, Date toDate) {
        return (date.after(fromDate) || date.equals(fromDate)) && (date.before(toDate) || date.equals(toDate));
    }

    public static Map<Integer, String> getAllFinancialYearMonthsWithFullNames() {
        return FIN_MONTH_NAMES;
    }

    public static SimpleDateFormat getDateFormatter(String pattern) {
        return new SimpleDateFormat(pattern, locale());
    }

    public static DateTimeFormatter defaultDateFormatter() {
        return DateTimeFormatter.ofPattern(datePattern()).withLocale(locale());
    }

    private static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    public static String toDefaultDateTimeFormat(Date date) {
        if (date == null) return null;

        Instant instant = date.toInstant();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern())
                                                       .withZone(timeZone().toZoneId());
        return formatter.format(instant);
    }
} 
