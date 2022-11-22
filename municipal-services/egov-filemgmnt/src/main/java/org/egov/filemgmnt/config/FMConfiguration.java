package org.egov.filemgmnt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Component
public class FMConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    @Value("${persister.save.applicantpersonal.topic}")
    private String saveApplicantPersonalTopic;

    @Value("${persister.update.applicantpersonal.topic}")
    private String updateApplicantPersonalTopic;

    @Value("${persister.save.servicedetails.topic}")
    private String saveServiceDetailsTopic;

    @Value("${egov.idgen.fm.fileCode.name}")
    private String filemgmntFileCodeName;

    @Value("${egov.idgen.fm.fileCode.format}")
    private String filemgmntFileCodeFormat;

    @Value("${citizen.allowed.search.params}")
    private String allowedCitizenSearchParams;

    @Value("${employee.allowed.search.params}")
    private String allowedEmployeeSearchParams;

    @Value("${workflow.context.path}")
    private String wfHost;

    @Value("${workflow.transition.path}")
    private String wfTransitionPath;

    // communication file config
    @Value("${persister.save.communicationfile.topic}")
    private String saveCommunicationFileTopic;

    @Value("${persister.update.communicationfile.topic}")
    private String updateCommunicationFileTopic;
}
