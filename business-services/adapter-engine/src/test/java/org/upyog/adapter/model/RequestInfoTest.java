package org.upyog.adapter.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RequestInfo}.
 */
class RequestInfoTest {

    @Test
    @DisplayName("Builder creates RequestInfo with all fields")
    void builder_createsFullRequestInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUuid("user-uuid");

        RequestInfo requestInfo = RequestInfo.builder()
                .apiId("Rainmaker")
                .ver(null)
                .ts(null)
                .action(null)
                .did(null)
                .key(null)
                .msgId("1700000000000|en_IN")
                .authToken("bearer-token")
                .userInfo(userInfo)
                .build();

        assertThat(requestInfo.getApiId()).isEqualTo("Rainmaker");
        assertThat(requestInfo.getMsgId()).isEqualTo("1700000000000|en_IN");
        assertThat(requestInfo.getAuthToken()).isEqualTo("bearer-token");
        assertThat(requestInfo.getUserInfo().getUuid()).isEqualTo("user-uuid");
    }

    @Test
    @DisplayName("Optional fields default to null")
    void optionalFields_defaultToNull() {
        RequestInfo requestInfo = RequestInfo.builder().apiId("test").build();

        assertThat(requestInfo.getVer()).isNull();
        assertThat(requestInfo.getTs()).isNull();
        assertThat(requestInfo.getAction()).isNull();
        assertThat(requestInfo.getDid()).isNull();
        assertThat(requestInfo.getKey()).isNull();
    }
}