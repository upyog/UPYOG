package org.upyog.dashboard.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.common.constants.Module;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link ValidatorRegistry}.
 */
class ValidatorRegistryTest {

    private ValidatorRegistry registry;

    @BeforeEach
    void setUp() {
        // Use an empty list to test the registry behavior
        registry = new ValidatorRegistry(Collections.emptyList());
    }

    @Test
    @DisplayName("Throws IllegalArgumentException for unregistered module")
    void get_forUnregisteredModule_throwsException() {
        assertThatThrownBy(() -> registry.get(Module.PT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Validator not found for module");
    }

    @Test
    @DisplayName("Registry with validators returns correct validator")
    void registry_withValidators_returnsCorrectValidator() {
        ModuleValidator ptValidator = new ModuleValidator() {
            @Override
            public Module getModule() {
                return Module.PT;
            }

            @Override
            public void validate(java.util.Map<String, Object> metrics) {
                // no-op
            }
        };

        ValidatorRegistry populatedRegistry = new ValidatorRegistry(List.of(ptValidator));

        ModuleValidator retrieved = populatedRegistry.get(Module.PT);
        assertThat(retrieved).isSameAs(ptValidator);
    }

    @Test
    @DisplayName("Registry populates validators from list")
    void registry_populatesValidatorsFromList() {
        ModuleValidator ptValidator = new ModuleValidator() {
            @Override
            public Module getModule() {
                return Module.PT;
            }

            @Override
            public void validate(java.util.Map<String, Object> metrics) {
            }
        };

        ValidatorRegistry populatedRegistry = new ValidatorRegistry(List.of(ptValidator));
        assertThat(populatedRegistry.get(Module.PT)).isNotNull();
    }
}