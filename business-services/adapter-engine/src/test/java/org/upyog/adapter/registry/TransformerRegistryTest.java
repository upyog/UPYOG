package org.upyog.adapter.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.transformer.ModuleTransformer;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link TransformerRegistry}.
 */
class TransformerRegistryTest {

    private TransformerRegistry emptyRegistry;

    @BeforeEach
    void setUp() {
        emptyRegistry = new TransformerRegistry(Collections.emptyList());
    }

    @Test
    @DisplayName("Throws IllegalArgumentException for unregistered module")
    void get_forUnregisteredModule_throwsException() {
        assertThatThrownBy(() -> emptyRegistry.get(Module.PT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No transformer found for module");
    }

    @Test
    @DisplayName("Registry with transformers returns correct transformer")
    void registry_withTransformers_returnsCorrectTransformer() {
        ModuleTransformer<DashboardData> ptTransformer = new ModuleTransformer<>() {
            @Override
            public Module getModule() {
                return Module.PT;
            }

            @Override
            public DashboardPayload transform(DashboardData rawData) {
                return null;
            }
        };

        TransformerRegistry populatedRegistry = new TransformerRegistry(List.of(ptTransformer));
        ModuleTransformer<DashboardData> retrieved = populatedRegistry.get(Module.PT);

        assertThat(retrieved).isSameAs(ptTransformer);
    }

    @Test
    @DisplayName("Registry registers all transformers from list")
    void registry_registersAllTransformers() {
        ModuleTransformer<DashboardData> ptTransformer = new ModuleTransformer<>() {
            @Override
            public Module getModule() {
                return Module.PT;
            }

            @Override
            public DashboardPayload transform(DashboardData rawData) {
                return null;
            }
        };

        TransformerRegistry populatedRegistry = new TransformerRegistry(List.of(ptTransformer));
        assertThat(populatedRegistry.get(Module.PT)).isNotNull();
    }
}