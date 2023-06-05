package org.ksmart.birth.birthcommon.services;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthcommon.model.common.CommonPay;
import org.ksmart.birth.birthcommon.model.common.CommonPayRequest;
import org.ksmart.birth.birthcommon.repoisitory.CommonRepository;
import org.ksmart.birth.common.contract.RequestInfoWrapper;
import org.ksmart.birth.common.repository.ServiceRequestRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.HRMS_UUID;
import static org.ksmart.birth.utils.BirthConstants.STATUS_FOR_PAYMENT;

@Service
@Slf4j
public class CommonService {

    private  final BirthConfiguration config;

    private  final CommonRepository repository;


    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    CommonService(BirthConfiguration config, CommonRepository repository, ServiceRequestRepository serviceRequestRepository) {
        this.config = config;
        this.repository = repository;
        this.serviceRequestRepository = serviceRequestRepository;
    }
    public List<CommonPay> updatePaymentWorkflow(CommonPayRequest request) {
        List<CommonPay> commonPays = request.getCommonPays();
        System.out.println("common req "+commonPays.get(0).getApplicationStatus());
        System.out.println("common req "+commonPays.get(0).getIsPaymentSuccess());
        List<CommonPay> commonPayList = new ArrayList<>();
        for (CommonPay birth : commonPays) {
            if (birth.getApplicationStatus().equals(STATUS_FOR_PAYMENT) && birth.getIsPaymentSuccess()) {
                birth.setAction("INITIATE");
                birth.setApplicationStatus("INITIATED");
                birth.setHasPayment(true);
                birth.setAmount(new BigDecimal(10));
                System.out.println("common req "+birth.getAction());
                commonPayList = repository.updatePaymentDetails(request);
            } else  if(birth.getApplicationStatus().equals(STATUS_FOR_PAYMENT) && !birth.getIsPaymentSuccess()){
                birth.setAction("");
                birth.setApplicationStatus("STATUS_FOR_PAYMENT");
                birth.setHasPayment(true);
                birth.setAmount(new BigDecimal(0));
            }
        }
        return commonPayList;
    }

    public List<NewBirthApplication> searchBirthDetailsCommon(NewBirthDetailRequest request, SearchCriteria criteria) {
        return repository.searchBirthDetailsCommon(request,criteria);
    }

    public List<String> getHRMSUser(String uuid,String tenantId,String role,String ward, String hospital,RequestInfo requestInfo) {
        List<String> userIds;
        StringBuilder url = getHRMSURIUser(uuid, tenantId, role, ward, hospital);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Object res = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
        try {
            userIds = JsonPath.read(res, HRMS_UUID);
        } catch (Exception e) {
            throw new CustomException("PARSING_ERROR", "Failed to parse HRMS response");
        }
        return userIds;
    }
    private StringBuilder getHRMSURIUser(String uuid,String tenantId,String role, String ward, String hospitalId) {

        StringBuilder url = new StringBuilder(config.getHrmsHost());
        url.append(config.getHrmsContextPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&roles=");
        url.append(role);
        if(ward != null) {
            url.append("&wardcodes=");
            url.append(ward);
        }
        if(hospitalId != null) {
            url.append("&hospitalcode=");
            url.append(hospitalId);
        }
        url.append("&uuid=");
        url.append(uuid);
        return url;
    }
}
