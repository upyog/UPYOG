package org.egov.user.domain.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.encryption.EncryptionService;
import org.egov.encryption.audit.AuditService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EncryptionDecryptionUtil {
    private EncryptionService encryptionService;
    @Autowired
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value(("${state.level.tenant.id}"))
    private String stateLevelTenantId;

    @Value(("${decryption.abac.enabled}"))
    private boolean abacEnabled;

    @Autowired
    public EncryptionDecryptionUtil(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public <T> T encryptObject(Object objectToEncrypt, String key, Class<T> classType) {
        if (objectToEncrypt == null) {
            return null;
        }
        try {
            Object encryptedObject = encryptionService.encryptJson(objectToEncrypt, key, stateLevelTenantId);
            return objectMapper.convertValue(encryptedObject, classType);
        } catch (Exception e) {
            log.error("Error occurred during encryption", e);
            throw new CustomException("ENCRYPTION_ERROR", "Error occurred during encryption: " + e.getMessage());
        }
    }

    public <E, P> P decryptObject(Object objectToDecrypt, String key, Class<E> classType, RequestInfo requestInfo) {
        if (objectToDecrypt == null) {
            return null;
        }
        
        try {
            // Get the appropriate key for decryption based on user context
            if (key == null && requestInfo != null && requestInfo.getUserInfo() != null) {
                Map<String, String> keyPurposeMap = getKeyToDecrypt(objectToDecrypt, requestInfo.getUserInfo());
                key = keyPurposeMap.get("key");
                
                // Log decryption attempt for audit - convert object to JsonNode
                try {
                    auditService.audit(objectMapper.valueToTree(objectToDecrypt), key, keyPurposeMap.get("purpose"), requestInfo);
                } catch (Exception auditException) {
                    log.warn("Failed to audit decryption attempt", auditException);
                }
            }
            
            // If key is still null, use default
            if (key == null) {
                key = "User";
            }
            
            Object decryptedObject = encryptionService.decryptJson(requestInfo, objectToDecrypt, key, stateLevelTenantId, classType);
            return (P) decryptedObject;
            
        } catch (Exception e) {
            log.error("Error occurred during decryption", e);
            throw new CustomException("DECRYPTION_ERROR", "Error occurred during decryption: " + e.getMessage());
        }
    }

    public boolean isUserDecryptingForSelf(Object objectToDecrypt, User userInfo) {
        org.egov.user.domain.model.User userToDecrypt = null;
        if (objectToDecrypt instanceof List) {
            if (((List) objectToDecrypt).isEmpty())
                return false;
            if (((List) objectToDecrypt).size() > 1)
                return false;
            userToDecrypt = (org.egov.user.domain.model.User) ((List) objectToDecrypt).get(0);
        } else {
            throw new CustomException("DECRYPTION_NOTLIST_ERROR", objectToDecrypt + " is not of type List of User");
        }

        if ((userToDecrypt.getUuid() != null) && userToDecrypt.getUuid().equalsIgnoreCase(userInfo.getUuid()))
            return true;
        else
            return false;
    }

    private boolean isDecryptionForIndividualUser(Object objectToDecrypt) {
        if (((List) objectToDecrypt).size() == 1)
            return true;
        else
            return false;
    }

    public Map<String,String> getKeyToDecrypt(Object objectToDecrypt, User userInfo) {
        Map<String,String> keyPurposeMap = new HashMap<>();

        if (!abacEnabled){
            keyPurposeMap.put("key","UserSelf");
            keyPurposeMap.put("purpose","AbacDisabled");
        }


        else if (isUserDecryptingForSelf(objectToDecrypt, userInfo)){
            keyPurposeMap.put("key","UserSelf");
            keyPurposeMap.put("purpose","Self");
        }


        else if (isDecryptionForIndividualUser(objectToDecrypt)){
            keyPurposeMap.put("key","User");
            keyPurposeMap.put("purpose","SingleSearchResult");
        }

        else{
            keyPurposeMap.put("key","User");
            keyPurposeMap.put("purpose","BulkSearchResult");
        }

        return keyPurposeMap;
    }

    private User getEncrichedandCopiedUserInfo(User userInfo) {
        List<Role> newRoleList = new ArrayList<>();
        if (userInfo.getRoles() != null) {
            for (Role role : userInfo.getRoles()) {
                Role newRole = Role.builder().code(role.getCode()).name(role.getName()).id(role.getId()).build();
                newRoleList.add(newRole);
            }
        }

        if (newRoleList.stream().filter(role -> (role.getCode() != null) && (userInfo.getType() != null) && role.getCode().equalsIgnoreCase(userInfo.getType())).count() == 0) {
            Role roleFromtype = Role.builder().code(userInfo.getType()).name(userInfo.getType()).build();
            newRoleList.add(roleFromtype);
        }

        User newuserInfo = User.builder().id(userInfo.getId()).userName(userInfo.getUserName()).name(userInfo.getName())
                .type(userInfo.getType()).mobileNumber(userInfo.getMobileNumber()).emailId(userInfo.getEmailId())
                .roles(newRoleList).tenantId(userInfo.getTenantId()).uuid(userInfo.getUuid()).build();
        return newuserInfo;
    }

}
