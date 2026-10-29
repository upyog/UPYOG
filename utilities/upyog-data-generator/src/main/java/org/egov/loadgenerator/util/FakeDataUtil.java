package org.egov.loadgenerator.util;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility component responsible for generating randomized test data
 * used by the Load Generator framework.
 *
 * <p>This class provides helper methods for generating realistic
 * names, addresses, mobile numbers, email addresses, geographical
 * coordinates, timestamps, UUIDs, and other randomized values
 * required by module-specific payload generators.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Generate realistic fake user information.</li>
 *   <li>Provide random addresses and location details.</li>
 *   <li>Generate unique identifiers and timestamps.</li>
 *   <li>Generate random numeric values within configurable ranges.</li>
 *   <li>Select random values from predefined collections.</li>
 * </ul>
 *
 * <h3>Implementation Notes</h3>
 * <p>A {@link ThreadLocal} instance of {@link Faker} is used to ensure
 * thread-safe random data generation during concurrent load testing.
 *
 * @see Faker
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
     *
     * @return a randomly generated mobile number
     */
    public String randomMobile() {
        return MOBILE_PREFIXES[ThreadLocalRandom.current().nextInt(MOBILE_PREFIXES.length)]
                + String.format("%08d", ThreadLocalRandom.current().nextInt(100000000));
    }

    /**
     * Generates a random full name.
     *
     * @return a randomly generated full name
     */
    public String randomName() {
        return faker.get().name().fullName();
    }

    /**
     * Generates a random first name.
     *
     * @return a randomly generated first name
     */
    public String randomFirstName() {
        return faker.get().name().firstName();
    }

    /**
     * Generates a random last name.
     *
     * @return a randomly generated last name
     */
    public String randomLastName() {
        return faker.get().name().lastName();
    }

    /**
     * Generates a random email address.
     *
     * @return a randomly generated email address
     */
    public String randomEmail() {
        return faker.get().internet().emailAddress();
    }

    /**
     * Generates a random street address.
     *
     * @return a randomly generated address
     */
    public String randomAddress() {
        return faker.get().address().streetAddress();
    }

    /**
     * Generates a random city name.
     *
     * @return a randomly generated city name
     */
    public String randomCity() {
        return faker.get().address().city();
    }

    /**
     * Generates a random six-digit Indian PIN code.
     *
     * @return a randomly generated PIN code
     */
    public String randomPincode() {
        return String.format("%06d", 100000 + ThreadLocalRandom.current().nextInt(900000));
    }

    /**
     * Generates a random building or door number.
     *
     * @return a randomly generated building number
     */
    public String randomDoorNo() {
        return faker.get().address().buildingNumber();
    }

    /**
     * Generates a random latitude within India's approximate geographical bounds.
     *
     * @return a random latitude
     */
    public double randomLatitude() {
        return 8.0 + (ThreadLocalRandom.current().nextDouble() * 29.0);
    }

    /**
     * Generates a random longitude within India's approximate geographical bounds.
     *
     * @return a random longitude
     */
    public double randomLongitude() {
        return 68.0 + (ThreadLocalRandom.current().nextDouble() * 29.0);
    }

    /**
     * Generates a universally unique identifier (UUID).
     *
     * @return a randomly generated UUID
     */
    public String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns the current system time in milliseconds since the Unix epoch.
     *
     * @return the current timestamp in milliseconds
     */
    public long currentEpoch() {
        return System.currentTimeMillis();
    }

    /**
     * Generates a random integer within the specified range.
     *
     * @param min the inclusive lower bound
     * @param max the exclusive upper bound
     * @return a randomly generated integer
     */
    public int randomInt(int min, int max) {
        return min + ThreadLocalRandom.current().nextInt(max - min);
    }

    /**
     * Returns a randomly selected element from the specified array.
     *
     * @param array the source array
     * @param <T> the element type
     * @return a randomly selected element
     */
    public <T> T randomFrom(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }
}
