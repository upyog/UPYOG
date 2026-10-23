package org.egov.ewst.util;

import org.egov.ewst.config.EwasteConfiguration;
import org.egov.ewst.repository.ServiceRequestRepository;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EwasteUtil extends CommonUtils {

	public EwasteUtil(ObjectMapper mapper, EwasteConfiguration configs, ServiceRequestRepository restRepo) {
		super(mapper, configs, restRepo);
	}

}
