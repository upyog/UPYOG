package org.ksmart.birth.birthregistry.service;

import org.apache.commons.text.CaseUtils;
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

    public void setBirthPlaceDetails(RegisterBirthDetail registerMain, RegisterCertificateData register, Object mdmsDataLoc, Object mdmsData) {

        if (register.getBirthPlaceId().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn = new StringBuilder().append(mdmsLocationService.getHospitalNameEn(mdmsDataLoc, register.getBirthPlaceHospitalId()))
                    .append(",")
                    .append(mdmsLocationService.getHospitalAddressEn(mdmsDataLoc, register.getBirthPlaceHospitalId()) == null
                            ? "" : mdmsLocationService.getHospitalAddressEn(mdmsDataLoc, register.getBirthPlaceHospitalId())).toString();
            String placeMl = new StringBuilder().append(mdmsLocationService.getHospitalNameMl(mdmsDataLoc, register.getBirthPlaceHospitalId()))
                    .append(",")
                    .append(mdmsLocationService.getHospitalAddressMl(mdmsDataLoc, register.getBirthPlaceHospitalId()) == null
                            ? "" : mdmsLocationService.getHospitalAddressMl(mdmsDataLoc, register.getBirthPlaceHospitalId())).toString();

            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn = "";
            String placeMl = "";
            if (register.getAckNo().startsWith(TCS_CODE_1, 0) || register.getAckNo().startsWith(TCS_CODE_2, 0)) {
                if (register.getBirthPlaceHospitalId().contains(TCS_ERROR_HOSP)) {
                    placeEn = new StringBuilder().append(mdmsLocationService.getHospitalNameEn(mdmsDataLoc, register.getBirthPlaceHospitalId()))
                            .append(",")
                            .append(mdmsLocationService.getHospitalAddressEn(mdmsDataLoc, register.getBirthPlaceHospitalId()) == null
                                    ? "" : mdmsLocationService.getHospitalAddressEn(mdmsDataLoc, register.getBirthPlaceHospitalId())).toString();
                    placeMl = new StringBuilder().append(mdmsLocationService.getHospitalNameMl(mdmsDataLoc, register.getBirthPlaceHospitalId()))
                            .append(",")
                            .append(mdmsLocationService.getHospitalAddressMl(mdmsDataLoc, register.getBirthPlaceHospitalId()) == null
                                    ? "" : mdmsLocationService.getHospitalAddressMl(mdmsDataLoc, register.getBirthPlaceHospitalId())).toString();
                }
            } else {
                placeEn = new StringBuilder().append(mdmsLocationService.getInstitutionNameEn(mdmsDataLoc, register.getBirthPlaceInstitutionId()) + ",")
                        .append(mdmsLocationService.getInstitutionTypeEn(mdmsData, register.getBirthPlaceInstitutionlTypeId()) == null
                                ? "" : mdmsLocationService.getInstitutionTypeEn(mdmsData, register.getBirthPlaceInstitutionlTypeId()) + ",")
                        .append(mdmsLocationService.getInstitutionAddressEn(mdmsDataLoc, register.getBirthPlaceInstitutionId()) == null
                                ? "" : mdmsLocationService.getInstitutionAddressEn(mdmsDataLoc, register.getBirthPlaceInstitutionId())).toString();

                placeMl = new StringBuilder().append(mdmsLocationService.getInstitutionNameMl(mdmsDataLoc, register.getBirthPlaceInstitutionId()) + ",")
                        .append(mdmsLocationService.getInstitutionTypeMl(mdmsData, register.getBirthPlaceInstitutionlTypeId()) == null
                                ? "" : mdmsLocationService.getInstitutionTypeMl(mdmsData, register.getBirthPlaceInstitutionlTypeId()) + ",")
                        .append(mdmsLocationService.getInstitutionAddressMl(mdmsDataLoc, register.getBirthPlaceInstitutionId()) == null
                                ? "" : mdmsLocationService.getInstitutionAddressMl(mdmsDataLoc, register.getBirthPlaceInstitutionId())).toString();
            }
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_HOME)) {
            String placeEn = new StringBuilder().append(registerMain.getRegisterBirthPlace().getHouseNameEn() == null ? "" : CaseUtils.toCamelCase(registerMain.getRegisterBirthPlace().getHouseNameEn(), true) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoLocalityEn() == null ? "" : CaseUtils.toCamelCase(registerMain.getRegisterBirthPlace().getHoLocalityEn(), true) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoStreetEn() == null ? "" : CaseUtils.toCamelCase(registerMain.getRegisterBirthPlace().getHoStreetEn(), true) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoPoId() == null ? "" : mdmsTenantService.getPostOfficeNameEn(mdmsData, registerMain.getRegisterBirthPlace().getHoPoId()) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoPoId() == null ? "" : mdmsTenantService.getPostOfficePinCode(mdmsData, registerMain.getRegisterBirthPlace().getHoPoId()) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoDistrictId() == null ? "" : mdmsTenantService.getDistrictNameEn(mdmsData, registerMain.getRegisterBirthPlace().getHoDistrictId()) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoStateId() == null ? "" : mdmsTenantService.getStateNameEn(mdmsData, registerMain.getRegisterBirthPlace().getHoStateId()) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoCountryId() == null ? "" : mdmsTenantService.getCountryNameEn(mdmsData, registerMain.getRegisterBirthPlace().getHoCountryId())).toString();
            String placeMl = new StringBuilder().append(registerMain.getRegisterBirthPlace().getHouseNameMl() == null ? "" : registerMain.getRegisterBirthPlace().getHouseNameMl() + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoLocalityMl() == null ? "" : registerMain.getRegisterBirthPlace().getHoLocalityMl() + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoStreetMl() == null ? "" : registerMain.getRegisterBirthPlace().getHoStreetMl() + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoPoId() == null ? "" : mdmsTenantService.getPostOfficeNameMl(mdmsData, registerMain.getRegisterBirthPlace().getHoPoId()) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoPoId() == null ? "" : mdmsTenantService.getPostOfficePinCode(mdmsData, registerMain.getRegisterBirthPlace().getHoPoId()) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoDistrictId() == null ? "" : mdmsTenantService.getDistrictNameMl(mdmsData, registerMain.getRegisterBirthPlace().getHoDistrictId()) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoStateId() == null ? "" : mdmsTenantService.getStateNameMl(mdmsData, registerMain.getRegisterBirthPlace().getHoStateId()) + ",")
                    .append(registerMain.getRegisterBirthPlace().getHoCountryId() == null ? "" : mdmsTenantService.getCountryNameMl(mdmsData, registerMain.getRegisterBirthPlace().getHoCountryId())).toString();
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_VEHICLE)) {
            System.out.println(registerMain.getRegisterBirthPlace().getVehicleHospitalid());
            String placeEn = new StringBuilder().append("On the way to ")
                    .append(mdmsLocationService.getHospitalNameEn(mdmsData, registerMain.getRegisterBirthPlace().getVehicleHospitalid()) == null
                            ? "" : mdmsLocationService.getHospitalNameEn(mdmsData, register.getBirthPlaceHospitalId()))
                    .append("(Vechicle No ")
                    .append(registerMain.getRegisterBirthPlace().getVehicleRegistrationNo() == null ? "" : registerMain.getRegisterBirthPlace().getVehicleRegistrationNo())
                    .append(")").toString();
            String placeMl = new StringBuilder()
                    .append(registerMain.getRegisterBirthPlace().getVehicleRegistrationNo() == null ? "" : registerMain.getRegisterBirthPlace().getVehicleRegistrationNo())
                    .append(" ആം നമ്പര്‍ വാഹനത്തില്‍ ")
                    .append(mdmsLocationService.getHospitalNameMl(mdmsData, registerMain.getRegisterBirthPlace().getVehicleHospitalid()) == null
                            ? " ആശുപത്രി" : mdmsLocationService.getHospitalNameMl(mdmsData, register.getBirthPlaceHospitalId()))
                    .append("യിലേക്ക് കൊണ്ടുപോകുന്ന മാര്‍ഗ്ഗമദ്ധ്യേ").toString();
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_PUBLIC)) {
            String placeEn = new StringBuilder().append(registerMain.getRegisterBirthPlace().getPublicLocalityEn())
                    .append(",").append(registerMain.getRegisterBirthPlace().getPublicStreetEn()).toString();
            String placeMl = new StringBuilder().append(registerMain.getRegisterBirthPlace().getPublicLocalityMl())
                    .append(",").append(registerMain.getRegisterBirthPlace().getPublicStreetMl()).toString();
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_OTHERS_COUNTRY)) {
            String placeEn = new StringBuilder().append(registerMain.getRegisterBirthPlace().getOtherBirthAddress1En() == null ? "" : CaseUtils.toCamelCase(registerMain.getRegisterBirthPlace().getOtherBirthAddress1En(), true) + ",")
                    .append(registerMain.getRegisterBirthPlace().getOtherBirthAddress2En() == null ? "" : CaseUtils.toCamelCase(registerMain.getRegisterBirthPlace().getOtherBirthAddress2En(), true) + ",")
                    .append(registerMain.getRegisterBirthPlace().getOtherBirthProvinceEn() == null ? "" : CaseUtils.toCamelCase(registerMain.getRegisterBirthPlace().getOtherBirthProvinceEn(), true) + ",")
                    .append(registerMain.getRegisterBirthPlace().getOtherBirthCountry() == null ? "" : mdmsTenantService.getCountryNameEn(mdmsData, registerMain.getRegisterBirthPlace().getOtherBirthCountry()))
                    .toString();
            String placeMl = new StringBuilder().append(registerMain.getRegisterBirthPlace().getOtherBirthAddress1Ml() == null ? "" : registerMain.getRegisterBirthPlace().getOtherBirthAddress1Ml() + ",")
                    .append(registerMain.getRegisterBirthPlace().getOtherBirthAddress2Ml() == null ? "" : registerMain.getRegisterBirthPlace().getOtherBirthAddress2Ml() + ",")
                    .append(registerMain.getRegisterBirthPlace().getOtherBirthProvinceMl() == null ? "" : registerMain.getRegisterBirthPlace().getOtherBirthProvinceMl() + ",")
                    .append(registerMain.getRegisterBirthPlace().getOtherBirthCountry() == null ? "" : mdmsTenantService.getCountryNameMl(mdmsData, registerMain.getRegisterBirthPlace().getOtherBirthCountry()))
                    .toString();
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        }
        else if(register.getBirthPlaceId().contains(BIRTH_PLACE_OTHERS_MIGRATION)){
            String placeEn = new StringBuilder().append(registerMain.getRegisterBirthPlace().getOthDetailsEn()).toString();
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl("Not Recorded");
        } else{
        }
    }
}


