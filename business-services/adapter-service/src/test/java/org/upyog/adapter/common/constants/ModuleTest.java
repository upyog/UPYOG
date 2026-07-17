package org.upyog.adapter.common.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Module} enum.
 */
class ModuleTest {

    @Test
    @DisplayName("Module contains PT constant")
    void module_containsPT() {
        assertThat(Module.valueOf("PT")).isEqualTo(Module.PT);
    }

    @Test
    @DisplayName("Module has exactly one constant")
    void module_hasExactlyOneConstant() {
        assertThat(Module.values()).hasSize(1);
    }

    @Test
    @DisplayName("PT constant name is PT")
    void pt_name() {
        assertThat(Module.PT.name()).isEqualTo("PT");
    }
}