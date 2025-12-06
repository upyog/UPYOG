package org.upyog.service;

<<<<<<< HEAD
import jakarta.validation.Valid;

=======
import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
>>>>>>> master-LTS
import org.upyog.web.models.CommonDetails;
import org.upyog.web.models.ModuleSearchRequest;

public interface CommonService {
    CommonDetails getApplicationCommonDetails(@Valid ModuleSearchRequest request);
}

