package org.ksmart.birth.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class EncryptionUtil {

    private final EncryptionService encryptionService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value(("${egov.state.level.tenant.id}"))
    private String stateLevelTenantId;

    @Value(("${decryption.abac.enabled}"))
    private boolean abacEnabled;

    public EncryptionUtil(final EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public <T> T encryptObject(final Object objectToEncrypt, final String key, final Class<T> classType) {
        if (objectToEncrypt == null) {
            return null;
        }

        try {
            final T encryptedObject = encryptionService.encryptJson(objectToEncrypt,
                                                                    key,
                                                                    stateLevelTenantId,
                                                                    classType);
            if (encryptedObject == null) {
                throw new CustomException("ENCRYPTION_NULL_ERROR", "Null object found on performing encryption");
            }

            if (log.isDebugEnabled()) {
                log.debug("Encryptor response: \n{}", BirthUtils.toJson(encryptedObject));
            }

            return encryptedObject;
        } catch (IOException | HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            log.error("Error occurred while encrypting", e);
            throw new CustomException("ENCRYPTION_ERROR", "Error occurred in encryption process");
        } catch (Exception e) {
            log.error("Unknown error occurred while encrypting", e);
            throw new CustomException("UNKNOWN_ERROR", "Unknown error occurred in encryption process");
        }
    }

    @SuppressWarnings("unchecked")
    public <E, P> P decryptObject(final Object objectToDecrypt, final String key, final Class<E> classType, // NOPMD
                                  final RequestInfo requestInfo) {
        if (objectToDecrypt == null) {
            return null;
        }

        Object decryptObject = objectToDecrypt;

        RequestInfo request = requestInfo;
        try {
            if (request == null || request.getUserInfo() == null) {
                request = RequestInfo.builder()
                                     .userInfo(User.builder()
                                                   .uuid("no uuid")
                                                   .type("EMPLOYEE")
                                                   .build())
                                     .build();
            }

            boolean objectIsList = false;
            if (!(decryptObject instanceof List)) {
                objectIsList = true;
                decryptObject = Collections.singletonList(decryptObject);
            }

            final User encrichedUserInfo = getEncrichedandCopiedUserInfo(request.getUserInfo());
            P decryptedObject = (P) encryptionService.decryptJson(decryptObject, key, encrichedUserInfo, classType);
            if (decryptedObject == null) {
                throw new CustomException("DECRYPTION_NULL_ERROR", "Null object found on performing decryption");
            }
//            auditTheDecryptRequest(key, encrichedUserInfo);
            if (objectIsList) {
                decryptedObject = (P) ((List<E>) decryptedObject).get(0);
            }

            if (log.isDebugEnabled()) {
                log.debug("Decryptor response: \n{}", BirthUtils.toJson(decryptedObject));
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

    public void auditTheDecryptRequest(final String key, final User userInfo) {
        final String purpose = "FMDetail"; // NOPMD

        final ObjectNode abacParams = objectMapper.createObjectNode();
        abacParams.set("key", TextNode.valueOf(key));

        final List<String> decryptedUserUuid = Arrays.asList(userInfo.getUuid());

        final ObjectNode auditData = objectMapper.createObjectNode();
        auditData.set("entityType", TextNode.valueOf(User.class.getName()));
        auditData.set("decryptedEntityIds", objectMapper.valueToTree(decryptedUserUuid));
        auditService.audit(userInfo.getUuid(), System.currentTimeMillis(), purpose, abacParams, auditData);
    }

    private User getEncrichedandCopiedUserInfo(final User userInfo) {
        final List<Role> newRoleList = new ArrayList<>();
        if (userInfo.getRoles() != null) {
            for (final Role role : userInfo.getRoles()) {
                final Role newRole = Role.builder()
                                         .code(role.getCode())
                                         .name(role.getName())
                                         .id(role.getId())
                                         .build();
                newRoleList.add(newRole);
            }
        }

        if (newRoleList.stream()
                       .filter(role -> role.getCode() != null && userInfo.getType() != null && role.getCode()
                                                                                                   .equalsIgnoreCase(userInfo.getType()))
                       .count() == 0) {
            final Role roleFromtype = Role.builder()
                                          .code(userInfo.getType())
                                          .name(userInfo.getType())
                                          .build();
            newRoleList.add(roleFromtype);
        }
        return User.builder()
                   .id(userInfo.getId())
                   .userName(userInfo.getUserName())
                   .name(userInfo.getName())
                   .type(userInfo.getType())
                   .mobileNumber(userInfo.getMobileNumber())
                   .emailId(userInfo.getEmailId())
                   .roles(newRoleList)
                   .tenantId(userInfo.getTenantId())
                   .uuid(userInfo.getUuid())
                   .build();
    }
}
