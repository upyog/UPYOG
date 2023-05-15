package org.ksmart.birth.birthcommon.services;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthcommon.model.WorkFlowCheck;
import org.ksmart.birth.birthcommon.model.common.CommonPay;
import org.ksmart.birth.birthcommon.model.common.CommonPayRequest;
import org.ksmart.birth.birthcommon.repoisitory.CommonRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.STATUS_FOR_PAYMENT;

@Service
@Slf4j
public class CommonService {

    private  final BirthConfiguration config;

    private  final CommonRepository repository;

    @Autowired
    CommonService(BirthConfiguration config, CommonRepository repository) {
        this.config = config;
        this.repository = repository;
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
}
