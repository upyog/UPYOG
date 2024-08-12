package digit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.UserOtherDetails;
import digit.repository.UserOtherDetailsRepository;
import digit.web.models.SchemeApplicationRequest;

@Service
public class UserOtherDetailsService {
    @Autowired
    private UserOtherDetailsRepository userOtherDetailsRepository;

    public UserOtherDetails getbyUserOtherDetailsByApplication(SchemeApplicationRequest schemeApplicationRequest) {

        UserOtherDetails userOtherDetails = new UserOtherDetails();
         userOtherDetails.setUserId(schemeApplicationRequest.getUserId());
         userOtherDetails.setTenantId(schemeApplicationRequest.getTenantId());
//       userOtherDetails.setReligion(schemeApplicationRequest.getReligion()); 
         userOtherDetails.setDivyangCardId(schemeApplicationRequest.getDivyangCardId());
         userOtherDetails.setDivyangPercent(schemeApplicationRequest.getDivyangPercent());
         userOtherDetails.setTransgenderId(schemeApplicationRequest.getTransgenderId());
         userOtherDetails.setRationCardCategory(schemeApplicationRequest.getRationCardCategory());
         userOtherDetails.setEducationLevel(schemeApplicationRequest.getEducationLevel());
         userOtherDetails.setUdid(schemeApplicationRequest.getUdid());

        return userOtherDetailsRepository.save(userOtherDetails);
        
    }

}
