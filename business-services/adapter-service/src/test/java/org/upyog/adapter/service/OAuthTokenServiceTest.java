package org.upyog.adapter.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.upyog.adapter.model.OAuthTokenResponse;
import org.upyog.adapter.model.UserInfo;
import org.upyog.adapter.model.UserSearchResponse;

import java.lang.reflect.Field;
import java.util.List;

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
    private RestTemplate restTemplate;

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
        ResponseEntity<OAuthTokenResponse> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OAuthTokenResponse.class)))
                .thenReturn(responseEntity);

        String token = service.getToken();
        assertThat(token).isEqualTo("test-access-token");
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class));

        // Second call should use cache
        String token2 = service.getToken();
        assertThat(token2).isEqualTo("test-access-token");
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class));
    }

    @Test
    @DisplayName("getToken throws when OAuth endpoint returns no access_token")
    void getToken_throwsWhenNoAccessToken() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse(null, 3600L);
        ResponseEntity<OAuthTokenResponse> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class)))
                .thenReturn(responseEntity);

        assertThatThrownBy(() -> service.getToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no access_token in response");
    }

    @Test
    @DisplayName("getToken throws when response body is null")
    void getToken_throwsWhenResponseBodyNull() throws Exception {
        service = createServiceWithMocks();
        ResponseEntity<OAuthTokenResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class)))
                .thenReturn(responseEntity);

        assertThatThrownBy(() -> service.getToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no access_token in response");
    }

    @Test
    @DisplayName("getToken throws when HTTP call fails")
    void getToken_throwsWhenHttpCallFails() throws Exception {
        service = createServiceWithMocks();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class)))
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
        ResponseEntity<OAuthTokenResponse> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class)))
                .thenReturn(responseEntity);

        UserInfo userFromSearch = new UserInfo();
        userFromSearch.setUuid("uuid-from-search");
        UserSearchResponse searchResponse = new UserSearchResponse();
        searchResponse.setUser(List.of(userFromSearch));
        ResponseEntity<UserSearchResponse> searchResponseEntity = new ResponseEntity<>(searchResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), eq(UserSearchResponse.class)))
                .thenReturn(searchResponseEntity);

        // The code always sets cachedUserInfo=null on refresh, so it fetches via search
        UserInfo result = service.getUserInfo();
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo("uuid-from-search");
        verify(restTemplate).postForEntity(anyString(), any(), eq(UserSearchResponse.class));
    }

    @Test
    @DisplayName("getUserInfo fetches from user search when UserRequest is null")
    void getUserInfo_fetchesFromUserSearchWhenNotInToken() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse("token-123", 3600L);
        tokenResponse.setUserRequest(null);
        ResponseEntity<OAuthTokenResponse> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class)))
                .thenReturn(responseEntity);

        UserInfo userFromSearch = new UserInfo();
        userFromSearch.setUuid("uuid-from-search");
        UserSearchResponse searchResponse = new UserSearchResponse();
        searchResponse.setUser(List.of(userFromSearch));
        ResponseEntity<UserSearchResponse> searchResponseEntity = new ResponseEntity<>(searchResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), eq(UserSearchResponse.class)))
                .thenReturn(searchResponseEntity);

        UserInfo result = service.getUserInfo();
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo("uuid-from-search");
        verify(restTemplate).postForEntity(anyString(), any(), eq(UserSearchResponse.class));
    }

    @Test
    @DisplayName("getUserInfo returns null when user search returns empty list")
    void getUserInfo_returnsNullWhenSearchReturnsEmpty() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse("token-123", 3600L);
        tokenResponse.setUserRequest(null);
        ResponseEntity<OAuthTokenResponse> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class)))
                .thenReturn(responseEntity);

        UserSearchResponse searchResponse = new UserSearchResponse();
        searchResponse.setUser(List.of());
        ResponseEntity<UserSearchResponse> searchResponseEntity = new ResponseEntity<>(searchResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), eq(UserSearchResponse.class)))
                .thenReturn(searchResponseEntity);

        UserInfo result = service.getUserInfo();
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getUserInfo returns null when user search call fails")
    void getUserInfo_returnsNullWhenSearchFails() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse tokenResponse = createTokenResponse("token-123", 3600L);
        tokenResponse.setUserRequest(null);
        ResponseEntity<OAuthTokenResponse> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class)))
                .thenReturn(responseEntity);

        when(restTemplate.postForEntity(anyString(), any(), eq(UserSearchResponse.class)))
                .thenThrow(new RuntimeException("Search endpoint unavailable"));

        UserInfo result = service.getUserInfo();
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getToken refreshes expired token")
    void getToken_refreshesExpiredToken() throws Exception {
        service = createServiceWithMocks();
        OAuthTokenResponse firstResponse = createTokenResponse("first-token", 0L);
        ResponseEntity<OAuthTokenResponse> firstEntity = new ResponseEntity<>(firstResponse, HttpStatus.OK);

        OAuthTokenResponse secondResponse = createTokenResponse("second-token", 3600L);
        ResponseEntity<OAuthTokenResponse> secondEntity = new ResponseEntity<>(secondResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class)))
                .thenReturn(firstEntity)
                .thenReturn(secondEntity);

        String firstToken = service.getToken();
        assertThat(firstToken).isEqualTo("first-token");

        String secondToken = service.getToken();
        assertThat(secondToken).isEqualTo("second-token");

        verify(restTemplate, times(2)).exchange(anyString(), eq(HttpMethod.POST), any(), eq(OAuthTokenResponse.class));
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

    private OAuthTokenService createServiceWithMocks() throws Exception {
        OAuthTokenService svc = new OAuthTokenService();
        setField(svc, "restTemplate", restTemplate);
        setField(svc, "oauthHost", "http://localhost:8080");
        setField(svc, "oauthPath", "/user/oauth/token");
        setField(svc, "basicAuthHeader", "Basic dGVzdA==");
        setField(svc, "username", "testUser");
        setField(svc, "password", "testPass");
        setField(svc, "tenantId", "pg");
        setField(svc, "userType", "EMPLOYEE");
        setField(svc, "userHost", "http://localhost:8080");
        setField(svc, "userSearchPath", "/user/_search");
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