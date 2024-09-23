package org.upyog.chb.util;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.encryption.EncryptionService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.upyog.chb.constants.CommunityHallBookingConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EncryptionDecryptionUtil {

    private EncryptionService encryptionService;

    @Value(("${state.level.tenant.id}"))
    private String stateLevelTenantId;

    @Value(("${chb.decryption.abac.enabled}"))
    private boolean abacEnabled;

    public EncryptionDecryptionUtil(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public <T> T encryptObject(Object objectToEncrypt, String key, Class<T> classType) {
        try {
            if (objectToEncrypt == null) {
                return null;
            }
            T encryptedObject = encryptionService.encryptJson(objectToEncrypt, key, stateLevelTenantId, classType);
            if (encryptedObject == null) {
                throw new CustomException("ENCRYPTION_NULL_ERROR", "Null object found on performing encryption");
            }
            return encryptedObject;
        } catch (Exception e) {
            log.error("Unknown Error occurred while encrypting", e);
            throw new CustomException("UNKNOWN_ERROR", "Unknown error occurred in encryption process");
        }
    }

    public <E, P> P decryptObject(Object objectToDecrypt, String key, Class<E> classType, RequestInfo requestInfo) {

        try {
            boolean objectToDecryptNotList = false;
            if (objectToDecrypt == null) {
                return null;
            } else if (requestInfo == null || requestInfo.getUserInfo() == null) {
                User userInfo = User.builder().uuid("no uuid").type("EMPLOYEE").build();
                requestInfo = RequestInfo.builder().userInfo(userInfo).build();
            }
            if (!(objectToDecrypt instanceof List)) {
                objectToDecryptNotList = true;
                objectToDecrypt = Collections.singletonList(objectToDecrypt);
            }

            Map<String, String> keyPurposeMap = getKeyToDecrypt(objectToDecrypt, key);
            String purpose = keyPurposeMap.get("purpose");

            if (key.equalsIgnoreCase(CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_ENCRYPTION_KEY))
                key = keyPurposeMap.get("key");

            P decryptedObject = (P) encryptionService.decryptJson(requestInfo, objectToDecrypt, key, purpose, classType);
            if (decryptedObject == null) {
                throw new CustomException("DECRYPTION_NULL_ERROR", "Null object found on performing decryption");
            }

            if (objectToDecryptNotList) {
                decryptedObject = (P) ((List<E>) decryptedObject).get(0);
            }
            return decryptedObject;
        } catch (IOException | HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            log.error("Error occurred while decrypting", e);
            throw new CustomException("DECRYPTION_SERVICE_ERROR", "Error occurred in decryption process");
        } catch (Exception e) {
            log.error("Unknown Error occurred while decrypting", e);
            throw new CustomException("UNKNOWN_ERROR", "Unknown error occurred in decryption process");
        }
    }

    public Map<String, String> getKeyToDecrypt(Object objectToDecrypt, String key) {
        Map<String, String> keyPurposeMap = new HashMap<>();

        if (!abacEnabled) {
			if (key.equals(CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_ENCRYPTION_KEY)/* || key == null */) {
                keyPurposeMap.put("key", CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY);
                keyPurposeMap.put("purpose", CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_PURPOSE);
            } 
        } else {
            if (key.equals(CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_ENCRYPTION_KEY) || key == null) {
                keyPurposeMap.put("key", CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_ENCRYPTION_KEY);
                keyPurposeMap.put("purpose", "CHBBookingSearch");
            }
        }

        return keyPurposeMap;
    }
}
