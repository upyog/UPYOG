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
public class FilemgmntConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    @Value("${persister.save.applicantpersonal.topic}")
    private String saveApplicantPersonalTopic;

    @Value("${persister.update.applicantpersonal.topic}")
    private String updateApplicantPersonalTopic;
}
