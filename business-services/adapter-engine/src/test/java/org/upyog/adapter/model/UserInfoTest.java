package org.upyog.adapter.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserInfo}.
 */
class UserInfoTest {

    @Test
    @DisplayName("Deserializes from JSON with all fields")
    void deserializes_fromJson() throws Exception {
        String json = "{"
                + "\"id\": \"1\","
                + "\"uuid\": \"user-uuid-123\","
                + "\"userName\": \"NDS1\","
                + "\"name\": \"National Dashboard System User\","
                + "\"mobileNumber\": \"9999999999\","
                + "\"emailId\": \"nds@test.com\","
                + "\"locale\": \"en_IN\","
                + "\"type\": \"EMPLOYEE\","
                + "\"active\": true,"
                + "\"tenantId\": \"pg\","
                + "\"roles\": [{\"code\": \"ADMIN\", \"name\": \"Admin\", \"tenantId\": \"pg\"}]"
                + "}";

        ObjectMapper mapper = new ObjectMapper();
        UserInfo userInfo = mapper.readValue(json, UserInfo.class);

        assertThat(userInfo.getId()).isEqualTo("1");
        assertThat(userInfo.getUuid()).isEqualTo("user-uuid-123");
        assertThat(userInfo.getUserName()).isEqualTo("NDS1");
        assertThat(userInfo.getName()).isEqualTo("National Dashboard System User");
        assertThat(userInfo.getMobileNumber()).isEqualTo("9999999999");
        assertThat(userInfo.getEmailId()).isEqualTo("nds@test.com");
        assertThat(userInfo.getLocale()).isEqualTo("en_IN");
        assertThat(userInfo.getType()).isEqualTo("EMPLOYEE");
        assertThat(userInfo.getActive()).isTrue();
        assertThat(userInfo.getTenantId()).isEqualTo("pg");
        assertThat(userInfo.getRoles()).hasSize(1);
    }

    @Test
    @DisplayName("JSON with active=false deserializes correctly")
    void deserializes_inactiveUser() throws Exception {
        String json = "{\"active\": false, \"tenantId\": \"pb\"}";

        ObjectMapper mapper = new ObjectMapper();
        UserInfo userInfo = mapper.readValue(json, UserInfo.class);

        assertThat(userInfo.getActive()).isFalse();
        assertThat(userInfo.getTenantId()).isEqualTo("pb");
        assertThat(userInfo.getRoles()).isNull();
    }
}