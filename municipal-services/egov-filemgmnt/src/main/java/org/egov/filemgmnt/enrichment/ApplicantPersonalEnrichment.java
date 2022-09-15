package org.egov.filemgmnt.enrichment;

import java.util.UUID;

import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.springframework.stereotype.Component;

@Component
public class ApplicantPersonalEnrichment {

    public void enrichApplicantPersonal(ApplicantPersonal applicant) {
        applicant.setId(UUID.randomUUID().toString());
    }
}
