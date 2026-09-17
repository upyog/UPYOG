package org.upyog.dashboard.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardPayload;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link ModuleTransformer} interface contract.
 */
class ModuleTransformerTest {

    @Test
    @DisplayName("ModuleTransformer interface has getModule and transform methods")
    void interface_hasExpectedMethods() throws Exception {
        assertThat(ModuleTransformer.class.getDeclaredMethod("getModule")).isNotNull();
        assertThat(ModuleTransformer.class.getDeclaredMethod("transform", Object.class)).isNotNull();
    }

    @Test
    @DisplayName("getModule returns Module type")
    void getModule_returnsModule() throws Exception {
        assertThat(ModuleTransformer.class.getDeclaredMethod("getModule")
                .getReturnType()).isEqualTo(Module.class);
    }

    @Test
    @DisplayName("transform returns DashboardPayload type")
    void transform_returnsDashboardPayload() throws Exception {
        assertThat(ModuleTransformer.class.getDeclaredMethod("transform", Object.class)
                .getReturnType()).isEqualTo(DashboardPayload.class);
    }
}