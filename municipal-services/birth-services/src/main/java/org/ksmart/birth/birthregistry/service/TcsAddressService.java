package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.jsoup.internal.StringUtil;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TcsAddressService {
    public void trimAddressHouseNamePresent(String houseName, RegisterBirthDetail register, String errorString) {
        if(!StringUtil.isBlank(houseName)){
            if(houseName.contains(errorString)){
                register.getRegisterBirthPresent().setHouseNameMl(houseName.replace(errorString, "").trim().replaceAll(",$", ""));
            }
        }
    }

    public void trimAddressHouseNamePermanent(String houseName, RegisterBirthDetail register, String errorString) {
        if(!StringUtil.isBlank(houseName)){
            if(houseName.contains(errorString)){
                register.getRegisterBirthPermanent().setHouseNameMl(houseName.replace(errorString, "").trim().replaceAll(",$", ""));
            }
        }
    }
}
