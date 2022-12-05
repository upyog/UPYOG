package org.bel.birthdeath.crdeath.repository.querybuilder;


import java.util.List;

import javax.validation.constraints.NotNull;

import org.bel.birthdeath.crdeath.web.models.CrDeathSearchCriteria;
import org.springframework.stereotype.Component;

/**
     * Creates CrDeathService
     * Rakhi S IKM
     * on  05/12/2022
     */
@Component
public class CrDeathQueryBuilder extends BaseQueryBuilder {
    
    private static final String QUERY = new StringBuilder().append("SELECT   id, tenantid, dateofdeath, deceased_title")
                                                           .append("  , deceased_firstname_en, deceased_firstname_ml, deceased_middlename_en, deceased_middlename_ml, deceased_lastname_en, deceased_lastname_ml")
                                                           .append("  , deceased_aadhar_number, deceased_gender, registration_no, deseased_passportno, application_no, file_no, ack_no")
                                                           .append("  , created_by, createdtime, lastmodifiedby,lastmodifiedtime")  
                                                           .append(" FROM eg_death_dtls") 
                                                           .toString();
                                                        
    public String getDeathSearchQuery(@NotNull CrDeathSearchCriteria criteria,
                                                           @NotNull List<Object> preparedStmtValues, Boolean isCount) {
         
         StringBuilder query = new StringBuilder(QUERY);
    
         System.out.println("RAkhiPreparedStmt"+preparedStmtValues);
         System.out.println("idCheck"+criteria.getId());
         addFilter("id", criteria.getId(), query, preparedStmtValues);
         addFilter("tenantid", criteria.getTenantId(), query, preparedStmtValues);
         addFilter("deceased_aadhar_number", criteria.getAadhaarNo(), query, preparedStmtValues);                                                   
         return query.toString();
       }                                                  
    
}
