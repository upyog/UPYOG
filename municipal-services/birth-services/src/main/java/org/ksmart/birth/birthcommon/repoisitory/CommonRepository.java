package org.ksmart.birth.birthcommon.repoisitory;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthcommon.model.common.CommonPay;
import org.ksmart.birth.birthcommon.model.common.CommonPayRequest;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class CommonRepository {
    private final BndProducer producer;

    private final BirthConfiguration birthDeathConfiguration;
    @Autowired
    CommonRepository(BndProducer producer, BirthConfiguration birthDeathConfiguration){
        this.producer = producer;
        this.birthDeathConfiguration = birthDeathConfiguration;
    }
    public List<CommonPay> updatePaymentDetails(CommonPayRequest request) {
        producer.push(birthDeathConfiguration.getUpdateBirthPaymentTopic(), request);
        return request.getCommonPays();
    }
}
