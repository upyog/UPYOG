package org.upyog.adapter.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.adapter.model.OAuthTokenResponse;
import org.upyog.adapter.model.UserInfo;
import org.upyog.adapter.model.UserSearchResponse;
import org.upyog.adapter.config.AdapterProperties;
import org.upyog.adapter.client.UserFeignClient;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OAuthTokenService}.
 */
@ExtendWith(MockitoExtension.class)
class OAuthTokenServiceTest {

    @Mock
    private UserFeignClient userFeignClient;

    private OAuthTokenService service;

    @Test
    @DisplayName("OAuthTokenService is a Spring Service")
    void service_isAnnotated() {
        assertThat(OAuthTokenService.class.getAnnotation(org.springframework.stereotype.Service.class))
                .isNotNull();
    }

    @Test
    @DisplayName("getToken fetches and caches token successfully")
    void getToken_fetchesAndCachesToken() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse("test-access-token", 3600L);

        when(userFeignClient.fetchToken(
                any(URI.class),
                anyString(),
                anyMap()))
                .thenReturn(tokenResponse);

        String token = service.getToken();
        assertThat(token).isEqualTo("test-access-token");
        verify(userFeignClient, times(1)).fetchToken(any(), any(), any());

        // Second call should use cache
        String token2 = service.getToken();
        assertThat(token2).isEqualTo("test-access-token");
        verify(userFeignClient, times(1)).fetchToken(any(), any(), any());
    }

    @Test
    @DisplayName("getToken throws when OAuth endpoint returns no access_token")
    void getToken_throwsWhenNoAccessToken() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse(null, 3600L);

        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenReturn(tokenResponse);

        assertThatThrownBy(() -> service.getToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no access_token in response");
    }

    @Test
    @DisplayName("getToken throws when response body is null")
    void getToken_throwsWhenResponseBodyNull() throws Exception {
        service = createServiceWithMocks();

        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenReturn(null);

        assertThatThrownBy(() -> service.getToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no access_token in response");
    }

    @Test
    @DisplayName("getToken throws when HTTP call fails")
    void getToken_throwsWhenHttpCallFails() throws Exception {
        service = createServiceWithMocks();
        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenThrow(new RuntimeException("Connection timeout"));

        assertThatThrownBy(() -> service.getToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to reach OAuth endpoint");
    }

    @Test
    @DisplayName("getUserInfo fetches from user search even when UserRequest is present in token (code always clears cachedUserInfo)")
    void getUserInfo_withUserRequestPresent_stillFetchesFromSearch() throws Exception {
        service = createServiceWithMocks();
        UserInfo userInfoFromToken = new UserInfo();
        userInfoFromToken.setUuid("uuid-from-token");
        userInfoFromToken.setUserName("testUser");

        OAuthTokenResponse tokenResponse = createTokenResponse("token-123", 3600L);
        tokenResponse.setUserRequest(userInfoFromToken);

        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenReturn(tokenResponse);

        UserInfo userFromSearch = new UserInfo();
        userFromSearch.setUuid("uuid-from-search");
        UserSearchResponse searchResponse = new UserSearchResponse();
        searchResponse.setUser(List.of(userFromSearch));

        when(userFeignClient.searchUser(any(), any()))
                .thenReturn(searchResponse);

        // The code always sets cachedUserInfo=null on refresh, so it fetches via search
        UserInfo result = service.getUserInfo();
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo("uuid-from-search");
        verify(userFeignClient).searchUser(any(), any());
    }

    @Test
    @DisplayName("getUserInfo fetches from user search when UserRequest is null")
    void getUserInfo_fetchesFromUserSearchWhenNotInToken() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse("token-123", 3600L);
        tokenResponse.setUserRequest(null);

        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenReturn(tokenResponse);

        UserInfo userFromSearch = new UserInfo();
        userFromSearch.setUuid("uuid-from-search");
        UserSearchResponse searchResponse = new UserSearchResponse();
        searchResponse.setUser(List.of(userFromSearch));

        when(userFeignClient.searchUser(any(), any()))
                .thenReturn(searchResponse);

        UserInfo result = service.getUserInfo();
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo("uuid-from-search");
        verify(userFeignClient).searchUser(any(), any());
    }

    @Test
    @DisplayName("getUserInfo returns null when user search returns empty list")
    void getUserInfo_returnsNullWhenSearchReturnsEmpty() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse("token-123", 3600L);
        tokenResponse.setUserRequest(null);

        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenReturn(tokenResponse);

        UserSearchResponse searchResponse = new UserSearchResponse();
        searchResponse.setUser(List.of());

        when(userFeignClient.searchUser(any(), any()))
                .thenReturn(searchResponse);

        UserInfo result = service.getUserInfo();
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getUserInfo returns null when user search call fails")
    void getUserInfo_returnsNullWhenSearchFails() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse("token-123", 3600L);
        tokenResponse.setUserRequest(null);

        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenReturn(tokenResponse);

        when(userFeignClient.searchUser(any(), any()))
                .thenThrow(new RuntimeException("Search endpoint unavailable"));

        UserInfo result = service.getUserInfo();
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getToken refreshes expired token")
    void getToken_refreshesExpiredToken() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse firstResponse = createTokenResponse("first-token", 0L);
        OAuthTokenResponse secondResponse = createTokenResponse("second-token", 3600L);

        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenReturn(firstResponse)
                .thenReturn(secondResponse);

        String firstToken = service.getToken();
        assertThat(firstToken).isEqualTo("first-token");

        String secondToken = service.getToken();
        assertThat(secondToken).isEqualTo("second-token");

        verify(userFeignClient, times(2)).fetchToken(any(), any(), any());
    }

    @Test
    @DisplayName("getToken throws when @Value fields not set")
    void getToken_throwsWhenValueFieldsNotSet() {
        OAuthTokenService bareService = new OAuthTokenService();
        assertThatThrownBy(() -> bareService.getToken())
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("getUserInfo throws when no cached token")
    void getUserInfo_throwsWhenNoCache() {
        OAuthTokenService bareService = new OAuthTokenService();
        assertThatThrownBy(() -> bareService.getUserInfo())
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("getToken retries on transient failures and succeeds eventually")
    void getToken_retriesOnTransientFailureAndSucceeds() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse("retry-token", 3600L);

        // Fail first attempt, succeed on second
        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenThrow(new RuntimeException("Transient connection timeout"))
                .thenReturn(tokenResponse);

        String token = service.getToken();
        assertThat(token).isEqualTo("retry-token");
        verify(userFeignClient, times(2)).fetchToken(any(), any(), any());
    }

    @Test
    @DisplayName("getToken retries up to maxAttempts and throws Exception when all attempts fail")
    void getToken_allAttemptsFail_throwsException() throws Exception {
        service = createServiceWithMocks();

        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenThrow(new RuntimeException("Transient 503 Server Error"));

        assertThatThrownBy(() -> service.getToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("after maximum attempts");

        verify(userFeignClient, times(3)).fetchToken(any(), any(), any());
    }

    @Test
    @DisplayName("getToken does not retry on 4xx client errors and fast-fails")
    void getToken_clientError_throwsImmediateException() throws Exception {
        service = createServiceWithMocks();

        feign.FeignException clientEx = mock(feign.FeignException.class);
        when(clientEx.status()).thenReturn(401);
        when(clientEx.getMessage()).thenReturn("Unauthorized");

        when(userFeignClient.fetchToken(any(), any(), any()))
                .thenThrow(clientEx);

        assertThatThrownBy(() -> service.getToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("client error");

        verify(userFeignClient, times(1)).fetchToken(any(), any(), any());
    }

    private OAuthTokenService createServiceWithMocks() throws Exception {
        OAuthTokenService svc = new OAuthTokenService();
        setField(svc, "userFeignClient", userFeignClient);
        
        AdapterProperties mockProps = mock(AdapterProperties.class);
        lenient().when(mockProps.getOauthHost()).thenReturn("http://localhost:8080");
        lenient().when(mockProps.getOauthPath()).thenReturn("/user/oauth/token");
        lenient().when(mockProps.getBasicAuthHeader()).thenReturn("Basic dGVzdA==");
        lenient().when(mockProps.getUsername()).thenReturn("testUser");
        lenient().when(mockProps.getPassword()).thenReturn("testPass");
        lenient().when(mockProps.getTenantId()).thenReturn("pg");
        lenient().when(mockProps.getUserType()).thenReturn("EMPLOYEE");
        lenient().when(mockProps.getUserSearchPath()).thenReturn("/user/_search");
        lenient().when(mockProps.getOauthMaxAttempts()).thenReturn(3);
        lenient().when(mockProps.getOauthBaseDelayMs()).thenReturn(1L);
        lenient().when(mockProps.getOauthMaxDelayMs()).thenReturn(2L);
        
        setField(svc, "adapterProperties", mockProps);
        return svc;
    }

    private OAuthTokenResponse createTokenResponse(String accessToken, long expiresIn) {
        OAuthTokenResponse response = new OAuthTokenResponse();
        response.setAccess_token(accessToken);
        response.setExpires_in(expiresIn);
        response.setToken_type("bearer");
        response.setScope("read");
        return response;
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}