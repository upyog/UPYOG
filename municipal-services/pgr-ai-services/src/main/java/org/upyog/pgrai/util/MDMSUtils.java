package org.upyog.pgrai.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.upyog.pgrai.config.PGRConfiguration;
import org.upyog.pgrai.repository.ServiceRequestRepository;
import org.upyog.pgrai.web.models.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.upyog.pgrai.util.PGRConstants.MDMS_MODULE_NAME;
import static org.upyog.pgrai.util.PGRConstants.MDMS_SERVICEDEF;

/**
 * Utility class for interacting with the MDMS service.
 * Provides methods to fetch PGR master data and construct MDMS search requests.
 */
@Component
public class MDMSUtils {

    private PGRConfiguration config;

    private ServiceRequestRepository serviceRequestRepository;

    /**
     * Constructor for `MDMSUtils`.
     *
     * @param config                  Configuration object for PGR.
     * @param serviceRequestRepository Repository for making service requests.
     */
    @Autowired
    public MDMSUtils(PGRConfiguration config, ServiceRequestRepository serviceRequestRepository) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    /**
     * Calls the MDMS service to fetch PGR master data.
     *
     * @param request The service request containing tenant and request information.
     * @return The result of the MDMS call.
     */
    public Object mDMSCall(ServiceRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getService().getTenantId();
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
        return result;
    }

    /**
     * Constructs the MDMS search criteria based on the tenant ID.
     *
     * @param requestInfo The request information.
     * @param tenantId    The tenant ID for which the MDMS data is fetched.
     * @return The MDMS criteria request object.
     */
    public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
        List<ModuleDetail> pgrModuleRequest = getPGRModuleRequest();

        List<ModuleDetail> moduleDetails = new LinkedList<>();
        moduleDetails.addAll(pgrModuleRequest);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
                .build();

        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo).build();
        return mdmsCriteriaReq;
    }

    /**
     * Creates the request to search for service definitions from MDMS.
     *
     * @return A list of module details for the PGR module.
     */
    private List<ModuleDetail> getPGRModuleRequest() {

        // Master details for the PGR module
        List<MasterDetail> pgrMasterDetails = new ArrayList<>();

        // Filter to only get active service definitions
        final String filterCode = "$.[?(@.active==true)]";

        pgrMasterDetails.add(MasterDetail.builder().name(MDMS_SERVICEDEF).filter(filterCode).build());

        ModuleDetail pgrModuleDtls = ModuleDetail.builder().masterDetails(pgrMasterDetails)
                .moduleName(MDMS_MODULE_NAME).build();

        return Collections.singletonList(pgrModuleDtls);
    }

    /**
     * Returns the URL for the MDMS search endpoint.
     *
     * @return The constructed MDMS search URL.
     */
    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
    }
}