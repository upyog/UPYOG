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
public class CommunicationFileManagementConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    @Value("${persister.save.communicationfile.topic}")
    private String saveCommunicationFileTopic;

	@Value("${persister.update.communicationfile.topic}")
	private String updateCommunicationFileTopic;

	@Value("${employee.allowed.search.params}")
	private String allowedEmployeeSearchParams;

}
