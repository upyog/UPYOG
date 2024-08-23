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
       
       criteria.setUuid(request.getRequestInfo().getUserInfo().getUuid());
       if("machine".equalsIgnoreCase(request.getType())) {
          criteria.setMachineId(request.getDetailId());
       }
       if("course".equalsIgnoreCase(request.getType())) {
          criteria.setCourseId(request.getDetailId());
       }
       criteria.setSchemeId(request.getSchemeId());
       criteria.setState(request.getAction());

       criteria.setUuid(request.getRequestInfo().getUserInfo().getUuid());
       criteria.setPreviousState(request.getPreviousState());

       List<String> previousStateList = 
             repository.getPreviousStatesByActionAndTenant(request.getAction().toUpperCase(), request.getRequestInfo().getUserInfo().getTenantId());
       if(previousStateList.size()>1){
         if("machine".equalsIgnoreCase(request.getType()))
             criteria.setPreviousState(previousStateList.get(1)); 
       }else{
         criteria.setPreviousState(previousStateList.get(0));
       }      
  
       List<VerificationDetails> details = repository.getApplicationForVerification(criteria);
   
       return details; 
    }

}
