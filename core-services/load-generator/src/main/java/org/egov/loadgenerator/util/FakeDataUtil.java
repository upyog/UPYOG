package org.egov.loadgenerator.util;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;


import java.util.UUID;

/**
 * Utility class for generating random test data
 * shared across all load generator modules.
 */
@Component
public class FakeDataUtil {

    private static final ThreadLocal<Faker> faker =
        ThreadLocal.withInitial(Faker::new);

    // Supported mobile number prefixes
    private static final String[] MOBILE_PREFIXES = {
            "98", "97", "96", "95", "94", "93", "91", "90", "89", "88"
    };

    /**
     * Generates a random 10-digit mobile number.
     */
    public String randomMobile() {
        return MOBILE_PREFIXES[ThreadLocalRandom.current().nextInt(MOBILE_PREFIXES.length)]
                + String.format("%08d", ThreadLocalRandom.current().nextInt(100000000));
    }

    /** Returns a random full name. */
    public String randomName() {
        return faker.get().name().fullName();
    }

    /** Returns a random first name. */
    public String randomFirstName() {
        return faker.get().name().firstName();
    }

    /** Returns a random last name. */
    public String randomLastName() {
        return faker.get().name().lastName();
    }

    /** Returns a random email address. */
    public String randomEmail() {
        return faker.get().internet().emailAddress();
    }

    /** Returns a random street address. */
    public String randomAddress() {
        return faker.get().address().streetAddress();
    }

    /** Returns a random city name. */
    public String randomCity() {
        return faker.get().address().city();
    }

    /** Generates a random 6-digit Indian PIN code. */
    public String randomPincode() {
        return String.format("%06d", 100000 + ThreadLocalRandom.current().nextInt(900000));
    }

    /** Returns a random building or door number. */
    public String randomDoorNo() {
        return faker.get().address().buildingNumber();
    }

    /**
     * Generates a random latitude within India's approximate bounds.
     */
    public double randomLatitude() {
        return 8.0 + (ThreadLocalRandom.current().nextDouble() * 29.0);
    }

    /**
     * Generates a random longitude within India's approximate bounds.
     */
    public double randomLongitude() {
        return 68.0 + (ThreadLocalRandom.current().nextDouble() * 29.0);
    }

    /** Generates a random UUID. */
    public String uuid() {
        return UUID.randomUUID().toString();
    }

    /** Returns the current timestamp in milliseconds. */
    public long currentEpoch() {
        return System.currentTimeMillis();
    }

    /**
     * Generates a random integer between the given bounds.
     */
    public int randomInt(int min, int max) {
        return min + ThreadLocalRandom.current().nextInt(max - min);
    }

    /**
     * Returns a random element from the given array.
     */
    public <T> T randomFrom(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }
}
