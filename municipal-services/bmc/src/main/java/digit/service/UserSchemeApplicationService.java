package digit.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.UserSchemeApplication;
import digit.repository.UserSchemeApplicationRepository;
import digit.repository.UserSchemeCitizenRepository;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;


@Service
public class UserSchemeApplicationService {

    private final UserSchemeApplicationRepository userSchemeApplicationRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserSchemeApplicationService.class);
    
    private final UserSchemeCitizenRepository userschemecitizenRepository;

	@Autowired
    public UserSchemeApplicationService(UserSchemeApplicationRepository userSchemeApplicationRepository,UserSchemeCitizenRepository userschemecitizenRepository) {
        this.userSchemeApplicationRepository = userSchemeApplicationRepository;
        this.userschemecitizenRepository = userschemecitizenRepository;
    }
	 public UserSchemeApplication saveDatatoDatabase(SchemeApplicationRequest schemeApplicationRequest) {
	        if (schemeApplicationRequest == null || schemeApplicationRequest.getSchemeApplications() == null || schemeApplicationRequest.getSchemeApplications().isEmpty()) {
	            throw new IllegalArgumentException("SchemeApplications list is empty or null");
	        }

	        SchemeApplication schemeApplication = schemeApplicationRequest.getSchemeApplications().get(0);

	        Optional<UserSchemeApplication> existingApplication = userSchemeApplicationRepository.findByApplicationNumber(schemeApplication.getApplicationNumber());
	        if (existingApplication.isPresent()) {
	            throw new IllegalArgumentException("This User with application number " + schemeApplication.getApplicationNumber() + " already exists");
	        }

	        UserSchemeApplication userSchemeApplication = new UserSchemeApplication();
	        userSchemeApplication.setApplicationNumber(schemeApplication.getApplicationNumber());
	        userSchemeApplication.setApplicationStatus(schemeApplication.getApplicationStatus());
	        userSchemeApplication.setFinalApproval(schemeApplication.getFinalApproval());
	        userSchemeApplication.setFirstApprovalStatus(schemeApplication.getFirstApprovalStatus());
	        userSchemeApplication.setVerificationStatus(schemeApplication.getVerificationStatus());
	        userSchemeApplication.setTenantId(schemeApplication.getTenantId());
	        userSchemeApplication.setUserId(schemeApplication.getUserId());
	        userSchemeApplication.setSubmitted(schemeApplication.getSubmitted());
	        userSchemeApplication.setRandomSelection(schemeApplication.getRandomSelection());
	        userSchemeApplication.setOptedId(schemeApplication.getOptedId());

	        logger.info("Saving UserSchemeApplication: {}", userSchemeApplication);
	        return userSchemeApplicationRepository.save(userSchemeApplication);
	    }
	 
	 public List<UserSchemeApplication> getfirstApprovalCitizen(SchemeApplicationRequest schemeApplicationRequest) {
		 return userschemecitizenRepository.getApprovedUserSchemes();
	 }


}
