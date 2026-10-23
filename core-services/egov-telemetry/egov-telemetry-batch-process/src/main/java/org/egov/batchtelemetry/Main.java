package org.egov.batchtelemetry;

import org.egov.batchtelemetry.application.BatchApplication;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Batch entrypoint: derives {@code startTime}/{@code endTime} for {@link BatchApplication#executeBatch}.
 * <p>
 * Java 17 upgrade: date handling uses {@link java.time} only (replaces Joda) — no {@code joda-time} on the classpath.
 */
public class Main {

    /** Parses single-arg {@code yyyy-MM-dd} batch day (same pattern as before the java.time migration). */
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) throws Exception {

        BatchApplication batchApplication = new BatchApplication();

        Long startTime = null, endTime = null;

        // JVM default zone — matches prior Joda LocalDate/DateTime "system" behaviour for start-of-day millis.
        ZoneId zone = ZoneId.systemDefault();

        if (args.length == 0) {
            // No args: window ends at start of today, starts one day earlier (same logic as old Joda path).
            LocalDate date = LocalDate.now(zone);
            endTime = date.atStartOfDay(zone).toInstant().toEpochMilli();
            startTime = endTime - TimeUnit.DAYS.toMillis(1);
        } else if (args.length == 1) {
            // One arg: end = start of that calendar day in default zone; window length still one day.
            LocalDate parsed = LocalDate.parse(args[0], DAY_FORMAT);
            endTime = parsed.atStartOfDay(zone).toInstant().toEpochMilli();
            startTime = endTime - TimeUnit.DAYS.toMillis(1);
        } else if (args.length == 2) {
            // Two args: explicit epoch range (unchanged).
            startTime = Long.parseLong(args[0]);
            endTime = Long.parseLong(args[1]);
        }

        batchApplication.executeBatch(startTime, endTime);

    }

}
