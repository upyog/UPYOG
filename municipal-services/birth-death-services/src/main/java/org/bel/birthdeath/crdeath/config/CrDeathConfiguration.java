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
   
    @Value("${persister.save.crdeath.topic}")
    private String saveDeathDetailsTopic;
}
