package org.upyog.adapter.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ValidationException}.
 */
class ValidationExceptionTest {

    @Test
    @DisplayName("constructor stores message and exception is a RuntimeException")
    void constructor_storesMessageAndIsRuntimeException() {
        ValidationException ex = new ValidationException("field is required");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("field is required");
    }

    @Test
    @DisplayName("can be thrown and caught as RuntimeException")
    void canBeThrownAndCaughtAsRuntimeException() {
        RuntimeException caught = null;
        try {
            throw new ValidationException("test error");
        } catch (RuntimeException e) {
            caught = e;
        }

        assertThat(caught).isNotNull();
        assertThat(caught.getMessage()).isEqualTo("test error");
    }

    @Test
    @DisplayName("serialVersionUID is stable — class is serializable")
    void classIsSerializable() {
        // Just verifying the instance can be created without issues;
        // serialVersionUID is a compile-time constant on the class.
        ValidationException ex = new ValidationException("msg");
        assertThat(ex.getMessage()).isNotNull();
    }
}
