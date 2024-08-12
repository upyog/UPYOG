package digit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.VerificationDetails;
import digit.repository.SchemeApplicationRepository;
import digit.web.models.SchemeApplicationSearchCriteria;
import digit.web.models.VerifierRequest;

@Service
public class VerifierService {
    
    @Autowired
    private SchemeApplicationRepository repository;

    @Autowired
    private SchemeApplicationSearchCriteria criteria;


    public List<VerificationDetails> getApplicationDetails(VerifierRequest request){

       criteria.setMachineId(request.getMachineId());
       criteria.setCourseId(request.getCourseId());
       criteria.setSchemeId(request.getSchemeId());

       List<VerificationDetails> details = repository.getApplicationForVerification(criteria);

       return details; 
    }

}
