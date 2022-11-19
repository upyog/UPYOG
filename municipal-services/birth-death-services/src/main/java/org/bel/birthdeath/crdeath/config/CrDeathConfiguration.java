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
}
