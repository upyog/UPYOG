package digit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.AadharUser;
import digit.repository.AadharRepository;
import digit.web.models.SchemeApplicationRequest;

@Service
public class AadharService {
    private final AadharRepository addAadharRepository;

    @Autowired
    public AadharService(AadharRepository addAadharRepository) {
        this.addAadharRepository = addAadharRepository;
    }


    public AadharUser getAadharUserByApplication(SchemeApplicationRequest schemeApplicationRequest) {
       
        AadharUser aadharUser = new AadharUser();
 /*        aadharUser.setAadharRef(schemeApplicationRequest.getAadharRef()); 
//       aadharUser.setUuid(schemeApplicationRequest.getUdid()); 
         aadharUser.setAadharFatherName(schemeApplicationRequest.getAadharfatherName()); 
         aadharUser.setAadharName(schemeApplicationRequest.getAadharname()); 
         aadharUser.setAadharDob(schemeApplicationRequest.getAadhardob()); 
         aadharUser.setAadharMobile(schemeApplicationRequest.getAadharmobile()); 
        //  aadharUser.setCreatedOn(schemeApplicationRequest.getCreatedOn());; 
        //  aadharUser.setModifiedOn(schemeApplicationRequest.getModifiedOn()); 
        //  aadharUser.setCreatedBy(schemeApplicationRequest.getCreatedBy()); 
        //  aadharUser.setModifiedBy(schemeApplicationRequest.getModifiedby()); 
    */   return addAadharRepository.save(aadharUser);
    }


}
