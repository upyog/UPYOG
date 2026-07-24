package org.upyog.adapter.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Roles}.
 */
class RolesTest {

    @Test
    @DisplayName("Deserializes from JSON")
    void deserializes_fromJson() throws Exception {
        String json = "{"
                + "\"name\": \"System User\","
                + "\"code\": \"NATIONAL_DASHBOARD_ADMIN\","
                + "\"tenantId\": \"pg\""
                + "}";

        ObjectMapper mapper = new ObjectMapper();
        Roles roles = mapper.readValue(json, Roles.class);

        assertThat(roles.getName()).isEqualTo("System User");
        assertThat(roles.getCode()).isEqualTo("NATIONAL_DASHBOARD_ADMIN");
        assertThat(roles.getTenantId()).isEqualTo("pg");
    }

    @Test
    @DisplayName("Setters work correctly")
    void setters_workCorrectly() {
        Roles roles = new Roles();
        roles.setName("Admin");
        roles.setCode("ADMIN");
        roles.setTenantId("pb");

        assertThat(roles.getName()).isEqualTo("Admin");
        assertThat(roles.getCode()).isEqualTo("ADMIN");
        assertThat(roles.getTenantId()).isEqualTo("pb");
    }
}