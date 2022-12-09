package org.bel.birthdeath.crdeath.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Component
public class CrDeathConfiguration {
   
      //Persister Config
    @Value("${persister.save.crdeath.topic}")
    private String saveDeathDetailsTopic;

    
    //MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    //IDGen

    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    
    @Value("${egov.idgen.deathapplfilecode.name}")
    private String deathApplnFileCodeName;

    @Value("${egov.idgen.deathapplfilecode.format}")
    private String deathApplnFileCodeFormat;


    @Value("${egov.idgen.deathackno.name}")
    private String deathAckName;

    @Value("${egov.idgen.deathackno.format}")
    private String deathACKFormat;



}
