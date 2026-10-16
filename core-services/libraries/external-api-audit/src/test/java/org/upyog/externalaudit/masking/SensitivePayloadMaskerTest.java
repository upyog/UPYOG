package org.upyog.externalaudit.masking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.upyog.externalaudit.config.ExternalApiAuditProperties;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SensitivePayloadMaskerTest {

    private SensitivePayloadMasker masker;

    @BeforeEach
    void setUp() {
        masker = new SensitivePayloadMasker(new ObjectMapper(), new ExternalApiAuditProperties());
    }

    @Test
    void shouldMaskPasswordAndTokens() {
        Map<String, Object> payload = Map.of(
                "UserName", "UMANG_API",
                "Password", "secret",
                "AccessToken", "token-value",
                "RefreshToken", "refresh-value");

        @SuppressWarnings("unchecked")
        Map<String, Object> masked = (Map<String, Object>) masker.mask(payload);

        assertEquals("UMANG_API", masked.get("UserName"));
        assertEquals(ExternalApiAuditConstants.MASKED_VALUE, masked.get("Password"));
        assertEquals(ExternalApiAuditConstants.MASKED_VALUE, masked.get("AccessToken"));
        assertEquals(ExternalApiAuditConstants.MASKED_VALUE, masked.get("RefreshToken"));
    }

    @Test
    void shouldMaskBankAccountFields() {
        Map<String, Object> payload = Map.of(
                "VoucherNumber", "V1",
                "ULBBankAccountNumber", "123456",
                "BeneficiaryAccountNumber", "987654");

        @SuppressWarnings("unchecked")
        Map<String, Object> masked = (Map<String, Object>) masker.mask(payload);

        assertEquals("V1", masked.get("VoucherNumber"));
        assertEquals(ExternalApiAuditConstants.MASKED_VALUE, masked.get("ULBBankAccountNumber"));
        assertEquals(ExternalApiAuditConstants.MASKED_VALUE, masked.get("BeneficiaryAccountNumber"));
        assertNotEquals("123456", masked.get("ULBBankAccountNumber"));
    }
}
