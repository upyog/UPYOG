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

    private boolean isEncryptedFormat(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        // Check if value follows the encrypted format: keyId|ciphertext
        String[] parts = value.split("\\|");
        if (parts.length != 2) {
            return false;
        }
        try {
            Integer.parseInt(parts[0].trim());
            return !parts[1].trim().isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isObjectEncrypted(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            if (list.isEmpty()) {
                return false;
            }
            // Check first user object for encryption indicators
            Object firstUser = list.get(0);
            return isUserObjectEncrypted(firstUser);
        } else {
            return isUserObjectEncrypted(obj);
        }
    }

    private boolean isUserObjectEncrypted(Object userObj) {
        if (userObj == null) {
            return false;
        }

        try {
            // Convert to JSON and check common encrypted fields
            com.fasterxml.jackson.databind.JsonNode userNode = objectMapper.valueToTree(userObj);

            // Check commonly encrypted fields like mobileNumber, emailId, name, aadhaarNumber
            String[] fieldsToCheck = {"mobileNumber", "emailId", "name", "aadhaarNumber", "pan"};

            for (String field : fieldsToCheck) {
                if (userNode.has(field) && userNode.get(field) != null && !userNode.get(field).isNull()) {
                    String value = userNode.get(field).asText();
                    if (!value.isEmpty() && isEncryptedFormat(value)) {
                        log.debug("Found encrypted field '{}' with value starting with: {}", field,
                                value.length() > 10 ? value.substring(0, 10) + "..." : value);
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            log.warn("Error checking if user object is encrypted", e);
            return false;
        }
    }

    public <E, P> P decryptObject(Object objectToDecrypt, String key, Class<E> classType, RequestInfo requestInfo) {
        if (objectToDecrypt == null) {
            return null;
        }

        // PERMANENT FIX: Preserve roles before any processing (encrypted or unencrypted)
        Map<String, Set<org.egov.user.domain.model.Role>> userRolesMap = preserveRolesBeforeDecryption(objectToDecrypt);

        // First check if the object actually contains encrypted data
        if (!isObjectEncrypted(objectToDecrypt)) {
            log.info("Object does not contain encrypted data, returning as-is with role preservation");
            // PERMANENT FIX: Restore roles even when skipping decryption
            P result = (P) objectToDecrypt;
            restoreRolesAfterDecryption(result, userRolesMap);
            return result;
        }

        try {
            // CRITICAL FIX: Track if we need to unwrap after decryption (mirroring origin/master behavior)
            boolean objectToDecryptNotList = false;

            // CRITICAL FIX: Wrap single objects in List before passing to encryption service
            // This ensures consistent ABAC policy evaluation (same as origin/master branch)
            // The encryption service handles Lists differently than single objects for ABAC
            if (!(objectToDecrypt instanceof List)) {
                objectToDecryptNotList = true;
                objectToDecrypt = Collections.singletonList(objectToDecrypt);
                log.debug("Wrapped single object in List for encryption service");
            }

            // Get the appropriate key and purpose for decryption based on user context
            String purpose = null;
            if (requestInfo != null && requestInfo.getUserInfo() != null) {
                Map<String, String> keyPurposeMap = getKeyToDecrypt(objectToDecrypt, requestInfo.getUserInfo());
                purpose = keyPurposeMap.get("purpose");

                if (key == null) {
                    key = keyPurposeMap.get("key");
                }

                // Log decryption attempt for audit - convert object to JsonNode
                try {
                    auditService.audit(objectMapper.valueToTree(objectToDecrypt), key, purpose, requestInfo);
                } catch (Exception auditException) {
                    log.warn("Failed to audit decryption attempt", auditException);
                }
            }

            // If key is still null, use default
            if (key == null) {
                key = "User";
            }

            // If purpose is still null, use default based on ABAC setting
            if (purpose == null) {
                purpose = abacEnabled ? "BulkSearchResult" : "AbacDisabled";
            }

            // Create a safe RequestInfo for encryption service to avoid null user info issues
            RequestInfo safeRequestInfo = requestInfo;

            // FIX: Handle both null requestInfo AND null userInfo (matching master branch behavior)
            // Changed from && to || to handle case when requestInfo itself is null
            if (requestInfo == null || requestInfo.getUserInfo() == null) {
                // Create default user info when missing (same as master branch)
                User userInfo = User.builder()
                    .uuid("no uuid")
                    .type("EMPLOYEE")
                    .roles(new ArrayList<>())
                    .build();

                // Create RequestInfo with default user
                safeRequestInfo = RequestInfo.builder()
                    .apiId(requestInfo != null ? requestInfo.getApiId() : "user-service")
                    .ver(requestInfo != null ? requestInfo.getVer() : null)
                    .ts(requestInfo != null ? requestInfo.getTs() : null)
                    .action(requestInfo != null ? requestInfo.getAction() : null)
                    .did(requestInfo != null ? requestInfo.getDid() : null)
                    .key(requestInfo != null ? requestInfo.getKey() : null)
                    .msgId(requestInfo != null ? requestInfo.getMsgId() : null)
                    .authToken(requestInfo != null ? requestInfo.getAuthToken() : null)
                    .correlationId(requestInfo != null ? requestInfo.getCorrelationId() : null)
                    .userInfo(userInfo)
                    .build();
            }

            // CRITICAL FIX: Enrich userInfo with roles before decryption (same as master branch)
            // This ensures proper role-based access control during decryption
            final User enrichedUserInfo = getEncrichedandCopiedUserInfo(safeRequestInfo.getUserInfo());
            safeRequestInfo.setUserInfo(enrichedUserInfo);

            // CRITICAL FIX: Pass List to encryption service for consistent ABAC evaluation
            // The encryptionService.decryptJson signature is: (RequestInfo, Object, String model, String purpose, Class)
            Object decryptedObject = encryptionService.decryptJson(safeRequestInfo, objectToDecrypt, key, purpose, classType);

            // CRITICAL FIX: Unwrap if we wrapped it earlier (restore original object type)
            if (objectToDecryptNotList && decryptedObject instanceof List) {
                List<?> decryptedList = (List<?>) decryptedObject;
                if (!decryptedList.isEmpty()) {
                    decryptedObject = decryptedList.get(0);
                    log.debug("Unwrapped single object from List after decryption");
                }
            }

            // PERMANENT FIX: Restore roles after decryption
            restoreRolesAfterDecryption(decryptedObject, userRolesMap);
            return (P) decryptedObject;

        } catch (Exception e) {
            log.error("Error occurred during decryption", e);
            throw new CustomException("DECRYPTION_ERROR", "Error occurred during decryption: " + e.getMessage());
        }
    }

    public boolean isUserDecryptingForSelf(Object objectToDecrypt, User userInfo) {
        org.egov.user.domain.model.User userToDecrypt = null;
        if (objectToDecrypt instanceof List) {
            List<?> listToDecrypt = (List<?>) objectToDecrypt;
            if (listToDecrypt == null || listToDecrypt.isEmpty())
                return false;
            if (listToDecrypt.size() > 1)
                return false;
            userToDecrypt = (org.egov.user.domain.model.User) listToDecrypt.get(0);
        } else if (objectToDecrypt instanceof org.egov.user.domain.model.User) {
            // FIX: Handle single User object
            userToDecrypt = (org.egov.user.domain.model.User) objectToDecrypt;
        } else {
            // Not a User or List of Users (could be Address, etc.)
            return false;
        }

        if ((userToDecrypt != null && userToDecrypt.getUuid() != null) && userInfo != null && userToDecrypt.getUuid().equalsIgnoreCase(userInfo.getUuid()))
            return true;
        else
            return false;
    }

    private boolean isDecryptionForIndividualUser(Object objectToDecrypt) {
        if (objectToDecrypt instanceof List) {
            List<?> listToDecrypt = (List<?>) objectToDecrypt;
            if (listToDecrypt != null && listToDecrypt.size() == 1)
                return true;
        } else if (objectToDecrypt instanceof org.egov.user.domain.model.User) {
            // FIX: Single User object is also individual user decryption
            return true;
        }
        return false;
    }

    public Map<String,String> getKeyToDecrypt(Object objectToDecrypt, User userInfo) {
        Map<String,String> keyPurposeMap = new HashMap<>();

        // FIX: Handle both single objects and Lists
        Object firstObject;
        if (objectToDecrypt instanceof List) {
            List<?> listToDecrypt = (List<?>) objectToDecrypt;
            if (listToDecrypt.isEmpty()) {
                // Default for empty list
                keyPurposeMap.put("key", "User");
                keyPurposeMap.put("purpose", "BulkSearchResult");
                return keyPurposeMap;
            }
            firstObject = listToDecrypt.get(0);
        } else {
            // Single object (not a list) - could be User or Address
            firstObject = objectToDecrypt;
        }

        if (!abacEnabled){
            keyPurposeMap.put("key","UserSelf");
            keyPurposeMap.put("purpose","AbacDisabled");
        }

        // Handle Address objects
        else if (firstObject instanceof org.egov.user.domain.model.Address) {
            keyPurposeMap.put("key", "Address");
            keyPurposeMap.put("purpose", "Address");
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

    /**
     * PERMANENT FIX: Preserve user roles before decryption to prevent role loss
     */
    private Map<String, Set<org.egov.user.domain.model.Role>> preserveRolesBeforeDecryption(Object objectToDecrypt) {
        Map<String, Set<org.egov.user.domain.model.Role>> userRolesMap = new HashMap<>();

        if (objectToDecrypt == null) {
            return userRolesMap;
        }

        try {
            if (objectToDecrypt instanceof List) {
                List<?> list = (List<?>) objectToDecrypt;
                for (Object item : list) {
                    if (item instanceof org.egov.user.domain.model.User) {
                        org.egov.user.domain.model.User user = (org.egov.user.domain.model.User) item;
                        if (user.getUuid() != null) {
                            // CRITICAL FIX: Always preserve roles, even if empty - prevents null roles
                            Set<org.egov.user.domain.model.Role> roles = user.getRoles() != null ?
                                new HashSet<>(user.getRoles()) : new HashSet<>();
                            userRolesMap.put(user.getUuid(), roles);
                            log.debug("ROLE PRESERVATION - Stored {} roles for user {} before decryption",
                                     roles.size(), user.getUuid());
                        }
                    }
                }
            } else if (objectToDecrypt instanceof org.egov.user.domain.model.User) {
                org.egov.user.domain.model.User user = (org.egov.user.domain.model.User) objectToDecrypt;
                if (user.getUuid() != null) {
                    // CRITICAL FIX: Always preserve roles, even if empty - prevents null roles
                    Set<org.egov.user.domain.model.Role> roles = user.getRoles() != null ?
                        new HashSet<>(user.getRoles()) : new HashSet<>();
                    userRolesMap.put(user.getUuid(), roles);
                    log.debug("ROLE PRESERVATION - Stored {} roles for user {} before decryption",
                             roles.size(), user.getUuid());
                }
            }
        } catch (Exception e) {
            log.warn("Error preserving roles before decryption", e);
        }

        return userRolesMap;
    }

    /**
     * PERMANENT FIX: Restore user roles after decryption to prevent role loss
     */
    private void restoreRolesAfterDecryption(Object decryptedObject, Map<String, Set<org.egov.user.domain.model.Role>> userRolesMap) {
        if (decryptedObject == null) {
            return;
        }

        // CRITICAL FIX: Don't exit early if userRolesMap is empty - we still need to ensure roles are not null

        try {
            if (decryptedObject instanceof List) {
                List<?> list = (List<?>) decryptedObject;
                for (Object item : list) {
                    if (item instanceof org.egov.user.domain.model.User) {
                        org.egov.user.domain.model.User user = (org.egov.user.domain.model.User) item;
                        if (user.getUuid() != null) {
                            if (userRolesMap.containsKey(user.getUuid())) {
                                Set<org.egov.user.domain.model.Role> preservedRoles = userRolesMap.get(user.getUuid());
                                user.setRoles(preservedRoles);
                                log.info("ROLE RESTORATION - Restored {} roles for user {} after decryption",
                                       preservedRoles.size(), user.getUuid());
                            } else {
                                // CRITICAL FIX: Ensure roles is never null, even when not preserved
                                if (user.getRoles() == null) {
                                    user.setRoles(new HashSet<>());
                                    log.debug("ROLE INITIALIZATION - Set empty roles for user {} (no roles to preserve)",
                                            user.getUuid());
                                }
                            }
                        }
                    }
                }
            } else if (decryptedObject instanceof org.egov.user.domain.model.User) {
                org.egov.user.domain.model.User user = (org.egov.user.domain.model.User) decryptedObject;
                if (user.getUuid() != null) {
                    if (userRolesMap.containsKey(user.getUuid())) {
                        Set<org.egov.user.domain.model.Role> preservedRoles = userRolesMap.get(user.getUuid());
                        user.setRoles(preservedRoles);
                        log.info("ROLE RESTORATION - Restored {} roles for user {} after decryption",
                               preservedRoles.size(), user.getUuid());
                    } else {
                        // CRITICAL FIX: Ensure roles is never null, even when not preserved
                        if (user.getRoles() == null) {
                            user.setRoles(new HashSet<>());
                            log.debug("ROLE INITIALIZATION - Set empty roles for user {} (no roles to preserve)",
                                    user.getUuid());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error restoring roles after decryption", e);
        }
    }

}
