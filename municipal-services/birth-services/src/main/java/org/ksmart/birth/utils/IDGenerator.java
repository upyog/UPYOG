package org.ksmart.birth.utils;


import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.newbirth.repository.querybuilder.NewBirthQueryBuilder;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class IDGenerator {

    private final MdmsUtil mdmsUtil;
    private final MdmsTenantService mdmsTenantService;
    private final JdbcTemplate jdbcTemplate;
    private final NewBirthQueryBuilder queryBuilder;


    @Autowired
    public IDGenerator(MdmsUtil mdmsUtil, MdmsTenantService mdmsTenantService,
                       JdbcTemplate jdbcTemplate, NewBirthQueryBuilder queryBuilder) {
        this.mdmsTenantService = mdmsTenantService;
        this.mdmsUtil = mdmsUtil;
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Sets the ApplicationNumber for given KsmartBirthDetailsRequest
     *
     * @param request KsmartBirthDetailsRequest which is to be created
     */
    public String setIDGenerator(NewBirthDetailRequest request, String moduleCode, String idType) {
        int Year = Calendar.getInstance().get(Calendar.YEAR);
        String tenantId = request.getNewBirthDetails().get(0).getTenantId();
        String nextID = getNewID(tenantId, Year, moduleCode, idType);

        // mdms call for tenand idgencode and lbtypecode
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());

        String idgenCode = mdmsTenantService.getTenantIdGenCode(mdmsData,tenantId);
        String lbTypeCode = mdmsTenantService.getTenantLetterCode(mdmsData,tenantId);

        String idGenerated = new StringBuilder().append(idType)
                                                .append("-")
                                                .append(nextID)
                                                .append("-")
                                                .append(String.valueOf(Year))
                                                .append("-")
                                                .append(moduleCode)
                                                .append("-")
                                                .append(lbTypeCode.charAt(0))
                                                .append("-")
                                                .append(idgenCode)
                                                .append("-")
                                                .append(BirthConstants.STATE_CODE).toString();
        return idGenerated;
    }

    public String setIDGeneratorStill(StillBirthDetailRequest request, String moduleCode, String idType) {
        int Year = Calendar.getInstance().get(Calendar.YEAR);
        String tenantId = request.getBirthDetails().get(0).getTenantId();
        String nextID = getNewID(tenantId, Year, moduleCode, idType);

        // mdms call for tenand idgencode and lbtypecode
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());

        String idgenCode = mdmsTenantService.getTenantIdGenCode(mdmsData,tenantId);
        String lbTypeCode = mdmsTenantService.getTenantLetterCode(mdmsData,tenantId);

        String idGenerated = new StringBuilder().append(idType)
                .append("-")
                .append(nextID)
                .append("-")
                .append(String.valueOf(Year))
                .append("-")
                .append(moduleCode)
                .append("-")
                .append(lbTypeCode.charAt(0))
                .append("-")
                .append(idgenCode)
                .append("-")
                .append(BirthConstants.STATE_CODE).toString();
        return idGenerated;
    }

    public String getNewID(String tenantId, int Year, String moduleCode, String idType) {

        List<Object> preparedStmtValues = new ArrayList<>();
        preparedStmtValues.add(idType);
        preparedStmtValues.add(moduleCode);
        preparedStmtValues.add(tenantId);
        preparedStmtValues.add(Year);
        String query = queryBuilder.getNextIDQuery();
        List<Map<String, Object>> nextID = jdbcTemplate.queryForList(query, preparedStmtValues.toArray());
        String finalid = String.valueOf(nextID.get(0).get("fn_next_id"));
        return finalid;
    }
}
