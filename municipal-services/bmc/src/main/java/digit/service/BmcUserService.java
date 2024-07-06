package digit.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.repository.BmcUserRepository;
import digit.web.models.BmcUser;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;

@Service
public class BmcUserService {

    @Autowired
	private BmcUserRepository bmcUserRepository;
    private static final Logger log = LoggerFactory.getLogger(BmcUserService.class);
	public BmcUser saveUserData (SchemeApplicationRequest schemeApplicationRequest) {
	            RequestInfo requestInfo = schemeApplicationRequest.getRequestInfo();
	            List<SchemeApplication> schemeApplication = schemeApplicationRequest.getSchemeApplications();

	            

	            BmcUser bmcUser = new BmcUser();
	            bmcUser.setId(schemeApplication.get(0).getUserId());
	            bmcUser.setTenantId(requestInfo.getUserInfo().getTenantId());
	            bmcUser.setUsername(requestInfo.getUserInfo().getUserName());

	            log.info("Saving BMC User with ID: {}", schemeApplication.get(0).getUserId());

	            return bmcUserRepository.save(bmcUser);
	        
	    }

}
