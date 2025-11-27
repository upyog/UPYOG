package org.egov.ndc.config;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResponseInfoFactoryTest {

    private ResponseInfoFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ResponseInfoFactory();
    }

    @Test
    void testCreateResponseInfoWithValidRequestInfoAndSuccessTrue() {
        RequestInfo requestInfo = RequestInfo.builder()
                .apiId("api-id")
                .ver("1.0")
                .ts(123456789L)
                .msgId("msg-id")
                .build();

        ResponseInfo response = factory.createResponseInfoFromRequestInfo(requestInfo, true);

        assertEquals("api-id", response.getApiId());
        assertEquals("1.0", response.getVer());
        assertEquals(123456789L, response.getTs());
        assertEquals("msg-id", response.getMsgId());
        assertEquals("uief87324", response.getResMsgId());
        assertEquals("successful", response.getStatus());
    }

    @Test
    void testCreateResponseInfoWithValidRequestInfoAndSuccessFalse() {
        RequestInfo requestInfo = RequestInfo.builder()
                .apiId("api-id")
                .ver("1.0")
                .ts(987654321L)
                .msgId("msg-id")
                .build();

        ResponseInfo response = factory.createResponseInfoFromRequestInfo(requestInfo, false);

        assertEquals("failed", response.getStatus());
    }

    @Test
    void testCreateResponseInfoWithNullRequestInfo() {
        ResponseInfo response = factory.createResponseInfoFromRequestInfo(null, true);

        assertEquals("", response.getApiId());
        assertEquals("", response.getVer());
        assertNull(response.getTs());
        assertEquals("", response.getMsgId());
        assertEquals("uief87324", response.getResMsgId());
        assertEquals("successful", response.getStatus());
    }


}