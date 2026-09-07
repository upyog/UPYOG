package org.upyog.dashboard.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserSearchResponse}.
 */
class UserSearchResponseTest {

    @Test
    @DisplayName("Deserializes from JSON with user list")
    void deserializes_fromJsonWithUserList() throws Exception {
        String json = "{"
                + "\"user\": ["
                + "  {"
                + "    \"uuid\": \"user-uuid-123\","
                + "    \"userName\": \"NDS1\","
                + "    \"type\": \"EMPLOYEE\","
                + "    \"tenantId\": \"pg\""
                + "  }"
                + "]"
                + "}";

        ObjectMapper mapper = new ObjectMapper();
        UserSearchResponse response = mapper.readValue(json, UserSearchResponse.class);

        assertThat(response.getUser()).hasSize(1);
        assertThat(response.getUser().get(0).getUuid()).isEqualTo("user-uuid-123");
        assertThat(response.getUser().get(0).getUserName()).isEqualTo("NDS1");
    }

    @Test
    @DisplayName("Ignores unknown JSON properties")
    void ignores_unknownProperties() throws Exception {
        String json = "{\"user\": [{\"uuid\": \"abc\"}], \"unknownField\": \"ignored\"}";

        ObjectMapper mapper = new ObjectMapper();
        UserSearchResponse response = mapper.readValue(json, UserSearchResponse.class);

        assertThat(response.getUser()).hasSize(1);
    }

    @Test
    @DisplayName("User can be null when not present in JSON")
    void user_canBeNull() throws Exception {
        String json = "{}";

        ObjectMapper mapper = new ObjectMapper();
        UserSearchResponse response = mapper.readValue(json, UserSearchResponse.class);

        assertThat(response.getUser()).isNull();
    }
}