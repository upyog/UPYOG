package org.upyog.dashboard.common.constants;

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
    @DisplayName("Module contains PGR constant")
    void module_containsPGR() {
        assertThat(Module.valueOf("PGR")).isEqualTo(Module.PGR);
    }

    @Test
    @DisplayName("Module has expected number of constants")
    void module_hasExpectedConstants() {
        assertThat(Module.values()).hasSize(5);
    }

    @Test
    @DisplayName("PT, PGR, CHB, ADV, FINANCE constant names")
    void enum_names() {
        assertThat(Module.PT.name()).isEqualTo("PT");
        assertThat(Module.PGR.name()).isEqualTo("PGR");
        assertThat(Module.CHB.name()).isEqualTo("CHB");
        assertThat(Module.ADV.name()).isEqualTo("ADV");
        assertThat(Module.FINANCE.name()).isEqualTo("FINANCE");
    }
}