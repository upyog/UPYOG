package org.upyog.adapter.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link OAuthTokenResponse}.
 */
class OAuthTokenResponseTest {

    @Test
    @DisplayName("Deserializes snake_case JSON fields")
    void deserializes_snakeCaseFields() throws Exception {
        String json = "{"
                + "\"access_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9\","
                + "\"token_type\": \"bearer\","
                + "\"refresh_token\": \"refresh-xyz\","
                + "\"expires_in\": 3599,"
                + "\"scope\": \"read\""
                + "}";

        ObjectMapper mapper = new ObjectMapper();
        OAuthTokenResponse response = mapper.readValue(json, OAuthTokenResponse.class);

        assertThat(response.getAccessToken()).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        assertThat(response.getToken_type()).isEqualTo("bearer");
        assertThat(response.getRefresh_token()).isEqualTo("refresh-xyz");
        assertThat(response.getExpiresIn()).isEqualTo(3599L);
        assertThat(response.getScope()).isEqualTo("read");
    }

    @Test
    @DisplayName("Ignores unknown JSON properties")
    void ignores_unknownProperties() throws Exception {
        String json = "{"
                + "\"access_token\": \"abc\","
                + "\"unknown_field\": \"should be ignored\","
                + "\"expires_in\": 3600"
                + "}";

        ObjectMapper mapper = new ObjectMapper();
        OAuthTokenResponse response = mapper.readValue(json, OAuthTokenResponse.class);

        assertThat(response.getAccessToken()).isEqualTo("abc");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
    }

    @Test
    @DisplayName("Access token can be null")
    void accessToken_canBeNull() {
        OAuthTokenResponse response = new OAuthTokenResponse();
        assertThat(response.getAccessToken()).isNull();
    }
}