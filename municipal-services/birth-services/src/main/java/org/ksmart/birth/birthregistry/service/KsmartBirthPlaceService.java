package org.ksmart.birth.birthregistry.service;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.ksmart.birth.utils.BirthConstants.*;
import static org.ksmart.birth.utils.BirthConstants.BIRTH_PLACE_OTHERS_COUNTRY;

@Service
public class KsmartBirthPlaceService {
    private final MdmsLocationService mdmsLocationService;
    private final MdmsTenantService mdmsTenantService;

    @Autowired
    KsmartBirthPlaceService(MdmsLocationService mdmsLocationService, MdmsTenantService mdmsTenantService) {
        this.mdmsLocationService = mdmsLocationService;
        this.mdmsTenantService = mdmsTenantService;
    }

    public void setBirthPlaceDetails(RegisterBirthDetail registerMain, RegisterCertificateData register, Object  mdmsData) {
        if (register.getBirthPlaceId().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn =new StringBuilder().append(mdmsLocationService.getHospitalNameEn(mdmsData, register.getBirthPlaceHospitalId()) )
                    .append(",")
                    .append(mdmsLocationService.getHospitalAddressEn(mdmsData, register.getBirthPlaceHospitalId())==null
                            ?"":mdmsLocationService.getHospitalAddressEn(mdmsData, register.getBirthPlaceHospitalId())).toString();
            String placeMl =new StringBuilder().append(mdmsLocationService.getHospitalNameMl(mdmsData, register.getBirthPlaceHospitalId()) )
                    .append(",")
                    .append(mdmsLocationService.getHospitalAddressMl(mdmsData, register.getBirthPlaceHospitalId())==null
                            ?"":mdmsLocationService.getHospitalAddressMl(mdmsData, register.getBirthPlaceHospitalId())).toString();

            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn =new StringBuilder().append(mdmsLocationService.getInstitutionNameEn(mdmsData, register.getBirthPlaceInstitutionId()) )
                    .append(",")
                    .append(mdmsLocationService.getInstitutionTypeEn(mdmsData, register.getBirthPlaceInstitutionlTypeId())==null
                            ?"":mdmsLocationService.getInstitutionTypeEn(mdmsData, register.getBirthPlaceInstitutionlTypeId()))
                    .append(",")
                    .append(mdmsLocationService.getInstitutionAddressEn(mdmsData, register.getBirthPlaceInstitutionId())==null
                            ?"":mdmsLocationService.getInstitutionAddressEn(mdmsData, register.getBirthPlaceInstitutionId())).toString();

            String placeMl =new StringBuilder().append(mdmsLocationService.getInstitutionNameMl(mdmsData, register.getBirthPlaceInstitutionId()) )
                    .append(",")
                    .append(mdmsLocationService.getInstitutionTypeMl(mdmsData, register.getBirthPlaceInstitutionlTypeId())==null
                            ?"":mdmsLocationService.getInstitutionTypeMl(mdmsData, register.getBirthPlaceInstitutionlTypeId()))
                    .append(",")
                    .append(mdmsLocationService.getInstitutionAddressMl(mdmsData, register.getBirthPlaceInstitutionId())==null
                            ?"":mdmsLocationService.getInstitutionAddressMl(mdmsData, register.getBirthPlaceInstitutionId())).toString();

            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_HOME)) {
            String placeEn = new StringBuilder().append(registerMain.getRegisterBirthPlace().getHouseNameEn() == null ? "" : registerMain.getRegisterBirthPlace().getHouseNameEn())
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoLocalityEn() == null ? "" : registerMain.getRegisterBirthPlace().getHoLocalityEn())
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoStreetEn() == null ? "" : registerMain.getRegisterBirthPlace().getHoStreetEn())
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoPoId() == null ? "" : mdmsTenantService.getPostOfficeNameEn(mdmsData,registerMain.getRegisterBirthPlace().getHoPoId()))
                    .append("-")
                    .append(registerMain.getRegisterBirthPlace().getHoPoId() == null ? "" : mdmsTenantService.getPostOfficePinCode(mdmsData,registerMain.getRegisterBirthPlace().getHoPoId()))
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoDistrictId() == null ? "" : mdmsTenantService.getDistrictNameEn(mdmsData, registerMain.getRegisterBirthPlace().getHoDistrictId()))
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoStateId() == null ? "" : mdmsTenantService.getStateNameEn(mdmsData, registerMain.getRegisterBirthPlace().getHoStateId()))
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoCountryId() == null ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, registerMain.getRegisterBirthPlace().getHoCountryId())).toString();
            String placeMl =new StringBuilder().append(registerMain.getRegisterBirthPlace().getHouseNameMl() == null ? "" : registerMain.getRegisterBirthPlace().getHouseNameMl())
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoLocalityMl() == null ? "" : registerMain.getRegisterBirthPlace().getHoLocalityMl())
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoStreetMl() == null ? "" : registerMain.getRegisterBirthPlace().getHoStreetMl())
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoPoId() == null ? "" : mdmsTenantService.getPostOfficeNameMl(mdmsData,registerMain.getRegisterBirthPlace().getHoPoId()))
                    .append("-")
                    .append(registerMain.getRegisterBirthPlace().getHoPoId() == null ? "" : mdmsTenantService.getPostOfficePinCode(mdmsData,registerMain.getRegisterBirthPlace().getHoPoId()))
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoDistrictId() == null ? "" : mdmsTenantService.getDistrictNameMl(mdmsData, registerMain.getRegisterBirthPlace().getHoDistrictId()))
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoStateId() == null ? "" : mdmsTenantService.getStateNameMl(mdmsData, registerMain.getRegisterBirthPlace().getHoStateId()))
                    .append(",")
                    .append(registerMain.getRegisterBirthPlace().getHoCountryId() == null ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, registerMain.getRegisterBirthPlace().getHoCountryId())).toString();
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        }else if (register.getBirthPlaceId().contains(BIRTH_PLACE_VEHICLE)) {
            String placeEn =new StringBuilder().append("On the way to ")
                    .append(mdmsLocationService.getHospitalNameEn(mdmsData, registerMain.getRegisterBirthPlace().getVehicleHospitalid())==null
                            ?"":mdmsLocationService.getHospitalNameEn(mdmsData, register.getBirthPlaceHospitalId()))
                    .append("(Vechicle No ")
                    .append(registerMain.getRegisterBirthPlace().getVehicleRegistrationNo() == null?"":registerMain.getRegisterBirthPlace().getVehicleRegistrationNo())
                    .append(")").toString();
            String placeMl =new StringBuilder()
                    .append(registerMain.getRegisterBirthPlace().getVehicleRegistrationNo() == null?"":registerMain.getRegisterBirthPlace().getVehicleRegistrationNo())
                    .append(" ആം നമ്പര്‍ വാഹനത്തില്‍ ")
                    .append(mdmsLocationService.getHospitalNameMl(mdmsData, registerMain.getRegisterBirthPlace().getVehicleHospitalid())==null
                            ?" ആശുപത്രി":mdmsLocationService.getHospitalNameMl(mdmsData, register.getBirthPlaceHospitalId()))
                    .append("യിലേക്ക് കൊണ്ടുപോകുന്ന മാര്‍ഗ്ഗമദ്ധ്യേ").toString();
        }else if (register.getBirthPlaceId().contains(BIRTH_PLACE_PUBLIC)) {
            String placeEn =new StringBuilder().append("").toString();
            String placeMl =new StringBuilder().append("").toString();
        }else if (register.getBirthPlaceId().contains(BIRTH_PLACE_OTHERS_COUNTRY)) {
            String placeEn =new StringBuilder().append("").toString();
            String placeMl =new StringBuilder().append("").toString();
        }
        else {
        }
    }

}
