package org.egov.wf.util;

import org.egov.tracer.model.CustomException;
import org.egov.wf.repository.BusinessServiceRepository;
import org.egov.wf.web.models.BusinessService;
import org.egov.wf.web.models.BusinessServiceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class BusinessUtil {

    private BusinessServiceRepository businessServiceRepository;

    @Autowired
    public BusinessUtil(BusinessServiceRepository businessServiceRepository) {
        this.businessServiceRepository = businessServiceRepository;
    }

    /**
     * Searches for businessService for the given list of processStateAndActions
     * @param tenantId The tenantId of the BusinessService
     * @param businessService The businessService code of the businessService
     * @return BusinessService
     */
    public BusinessService getBusinessService(String tenantId,String businessService){
        BusinessServiceSearchCriteria criteria = new BusinessServiceSearchCriteria();
        criteria.setTenantId(tenantId);
        criteria.setBusinessServices(Collections.singletonList(businessService));
        List<BusinessService> businessServices = businessServiceRepository.getBusinessServices(criteria);
        log.info("#### businessServices.size(): "+businessServices.size());
        if(CollectionUtils.isEmpty(businessServices))
            throw new CustomException("INVALID REQUEST","No BusinessService found for businessService: "+criteria.getBusinessServices());
        return businessServices.get(0);
    }

}
