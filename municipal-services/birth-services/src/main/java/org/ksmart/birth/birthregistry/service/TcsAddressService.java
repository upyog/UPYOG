package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.internal.StringUtil;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TcsAddressService {
//    public void trimAddressHouseNamePresent(String houseName, RegisterBirthDetail register, String errorString) {
//        if(!StringUtil.isBlank(houseName)){
//            if(houseName.contains(errorString)){
//                register.getRegisterBirthPresent().setHouseNameMl(houseName.replace(errorString, "").trim().replaceAll(",$", ""));
//            }
//        }
//    }
//
//    public void trimAddressHouseNamePermanent(String houseName, RegisterBirthDetail register, String errorString) {
//        if(!StringUtil.isBlank(houseName)){
//            if(houseName.contains(errorString)){
//                register.getRegisterBirthPresent().setHouseNameMl(houseName.replace(errorString, "").trim().replaceAll(",$", ""));
//            }
//        }
//    }

    public void getAddressInsideCountryPresentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPresent().getHouseNameMl()) ? "" : register.getRegisterBirthPresent().getHouseNameMl()).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "രേഖപ്പെടുത്തിയിട്ടില്ല";
        }
        registerCert.setPresentAddDetailsMl(address);
    }

    public void getAddressInsideCountryPermanentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPermanent().getHouseNameMl()) ? "" : register.getRegisterBirthPermanent().getHouseNameMl()).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "രേഖപ്പെടുത്തിയിട്ടില്ല";
        }
        registerCert.setPermenantAddDetailsMl(address);
    }
}
