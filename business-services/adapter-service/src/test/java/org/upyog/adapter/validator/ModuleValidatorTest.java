package org.upyog.adapter.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests to verify the ModuleValidator interface contract.
 */
class ModuleValidatorTest {

    @Test
    @DisplayName("ModuleValidator interface has getModule and validate methods")
    void interface_hasExpectedMethods() throws Exception {
        assertThat(ModuleValidator.class.getDeclaredMethod("getModule")).isNotNull();
        assertThat(ModuleValidator.class.getDeclaredMethod("validate", java.util.Map.class)).isNotNull();
    }
}