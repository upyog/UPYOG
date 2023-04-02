package org.ksmart.birth.birthnac.service;


import lombok.extern.slf4j.Slf4j;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ksmart.birth.birthnac.repository.NacRepository;
import org.ksmart.birth.birthnac.validator.NacApplicationValidator;
import org.ksmart.birth.birthnacregistry.model.RegisterNacRequest;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegratorNac;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestServiceForNac {
	
	 private final NacRepository repository;

	 @Autowired
	 RegistryRequestServiceForNac(NacRepository repository ) {
	 
	        this.repository = repository;
	 }
	 
	 public RegisterNacRequest createRegistryRequestNew(NacDetailRequest request) {///Work to get req to Register
	        return repository.searchNacDetailsForRegister(request);
	    }
	       
}
