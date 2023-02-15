package org.ksmart.birth.ksmartbirthapplication.enrichment;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.ksmart.birth.birthapplication.enrichment.BaseEnrichment;
import org.ksmart.birth.birthapplication.repository.querybuilder.BirthApplicationQueryBuilder;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KsmartBirthEnrichment implements BaseEnrichment {
    @Autowired
    BirthConfiguration config;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BirthApplicationQueryBuilder birthApplicationQueryBuilder;

    @Autowired
    IDGenerator generator;

    public void enrichCreate(KsmartBirthDetailsRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);
        request.getKsmartBirthDetails().forEach(birth -> {

            birth.setId(UUID.randomUUID().toString());

            birth.setAuditDetails(auditDetails);

            birth.setBirthPlaceUuid(UUID.randomUUID().toString());

            birth.getParentsDetails().setFatherUuid(UUID.randomUUID().toString());

            birth.getParentsDetails().setMotherUuid(UUID.randomUUID().toString());

//            birth.get().setId(UUID.randomUUID().toString());
//
//            birth.getBirthPresentAddress().setId(UUID.randomUUID().toString());

            birth.setBirthStatisticsUuid(UUID.randomUUID().toString());

            birth.setBirthInitiatorUuid(UUID.randomUUID().toString());
            birth.setAddressUuid(UUID.randomUUID().toString());

        });
        setApplicationNumbers(request);
        setFileNumbers(request);
        setRegistrationNumber(request);
    }

    public void enrichUpdate(KsmartBirthDetailsRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);
        request.getKsmartBirthDetails()
                .forEach(birth -> birth.setAuditDetails(auditDetails));
    }

    private void setApplicationNumbers(KsmartBirthDetailsRequest request) {
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        String id = generator.setIDGenerator(request, BirthConstants.FUN_MODULE_NEW,BirthConstants.APP_NUMBER_CAPTION);
        request.getKsmartBirthDetails()
                .forEach(birth -> {
                    birth.setApplicationNo(id);
                    birth.setApplicationType("ACTIVE");
                    birth.setDateOfReport(currentTime);
                });
    }

    private void setFileNumbers(KsmartBirthDetailsRequest request) {
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        String id = generator.setIDGenerator(request, BirthConstants.FUN_MODULE_NEW,BirthConstants.FILE_NUMBER_CAPTION);
        request.getKsmartBirthDetails()
                .forEach(birth -> {
                    birth.setFileNumber(id);
                    birth.setFileDate(currentTime);
                });
    }

    private void setRegistrationNumber(KsmartBirthDetailsRequest request) {
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        String id = generator.setIDGenerator(request, BirthConstants.FUN_MODULE_NEW,BirthConstants.REGY_NUMBER_CAPTION);
        request.getKsmartBirthDetails()
                .forEach(birth -> {
                    birth.setRegistrationNo(id);
                    birth.setRegistrationDate(currentTime);
                });
    }
}
