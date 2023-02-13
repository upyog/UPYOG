
package org.ksmart.birth.ksmartbirthapplication.model.newbirth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;

//public class KsmartBirthParentAddress {
//
////    @Size(max = 64)
////    @JsonProperty("presentAddresscountry")
////    private String presentCountryid;
////
////    @Size(max = 64)
////    @JsonProperty("presentAddressstatename")
////    private String presentStateid;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralalbname")
////    private String presentInTenantid;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeraladistrict")
////    private String presentInDistrictid;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralataluk")
////    private String presentInTalukName;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralavillage")
////    private String presentInVillageName;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralalocalitynameen")
////    private String presentInMainPlaceEn;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralastreetnameen")
////    private String presentInStreetLocalityAreaEn;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralahousenameen")
////    private String presentInHousenameEn;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralalocalitynameml")
////    private String presentInMainPlaceMl;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralastreetnameml")
////    private String presentInStreetLocalityAreaMl;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralahousenameml")
////    private String presentInHousenameMl;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralapincode")
////    private String presentInPinno;
////
////    @Size(max = 64)
////    @JsonProperty("presentInsidekeralapostoffice")
////    private String presentInPoid;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeraladistrict")
////    private String presentDistrictid;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralataluk")
////    private String presentTalukName;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralavillage")
////    private String presentVillageName;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralacityvilgeen")
////    private String present;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralacityvilgeml")
////    private String present;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralapincode")
////    private String presentPinno;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralapostoffice")
////    private String presentPoid;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralalocalitynameen")
////    private String present;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralastreetnameen")
////    private String presentStreetLocalityAreaEn;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralahousenameen")
////    private String presentHousenameEn;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralalocalitynameml")
////    private String presentMainPlaceMl;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralastreetnameml")
////    private String presentStreetLocalityAreaMl;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidekeralahousenameml")
////    private String presentHousenameMl;
////
////    @Size(max = 64)
////    @JsonProperty("presentAdrscitytown")
////    private String present;
////
////    @Size(max = 64)
////    @JsonProperty("presentOutsidecountry")
////    private String presentCountryid;
////
////    @Size(max = 64)
////    @JsonProperty("presentProvinceen")
////    private String presentOtStateRegionProvinceEn;
////
////    @Size(max = 64)
////    @JsonProperty("presentPostcode")
////    private String presentOtZipcode;
////
////    @Size(max = 64)
////    @JsonProperty("presentIscitytownvillage")
////    private String present;
////
////    @Size(max = 64)
////    @JsonProperty("presentAddresslinetwoen")
////    private String presentOtAddress2En;
////
////    @Size(max = 64)
////    @JsonProperty("presentAddresslinetwoml")
////    private String presentOtAddress2Ml;
////
////    @Size(max = 64)
////    @JsonProperty("presentAddresslineoneen")
////    private String presentOtAddress1En;
////
////    @Size(max = 64)
////    @JsonProperty("presentAddresslineoneml")
////    private String presentOtAddress1Ml;
////
////    @Size(max = 64)
////    @JsonProperty("permanantAddresscountry")
////    private String permanantCountryid;
////
////    @Size(max = 64)
////    @JsonProperty("permanantAddressstatename")
////    private String permanantStateid;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralalbname")
////    private String permanantTenantid;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeraladistrict")
////    private String permanantDistrictid;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralataluk")
////    private String permanantTalukName;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralavillage")
////    private String permanantVillageName;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralalocalitynameen")
////    private String permanantMainPlaceEn;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralastreetnameen")
////    private String permanantStreetLocalityAreaEn;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralahousenameen")
////    private String permanantHousenameEn;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralalocalitynameml")
////    private String permanantMainPlaceMl;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralastreetnameml")
////    private String permanantStreetLocalityAreaMl;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralahousenameml")
////    private String permanantHousenameMl;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralapincode")
////    private String permanantPinno;
////
////    @Size(max = 64)
////    @JsonProperty("permanantInsidekeralapostoffice")
////    private String permanantPoid;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeraladistrict")
////    private String permanantDistrictid;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralataluk")
////    private String permanantTalukName;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralavillage")
////    private String permanantVillageName;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralacityvilgeen")
////    private String permanant;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralacityvilgeml")
////    private String permanant;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralapincode")
////    private String permanantPinno;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralapostoffice")
////    private String permanantPoid;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralalocalitynameen")
////    private String permanant;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralastreetnameen")
////    private String permanantStreetLocalityAreaEn;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralahousenameen")
////    private String permanantHousenameEn;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralalocalitynameml")
////    private String permanantMainPlaceMl;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralastreetnameml")
////    private String permanantStreetLocalityAreaMl;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidekeralahousenameml")
////    private String permanantHousenameMl;
////
////    @Size(max = 64)
////    @JsonProperty("permanantAdrscitytown")
////    private String permanant;
////
////    @Size(max = 64)
////    @JsonProperty("permanantOutsidecountry")
////    private String permanantCountryid;
////
////    @Size(max = 64)
////    @JsonProperty("permanantProvinceen")
////    private String permanantOtStateRegionProvinceEn;
////
////    @Size(max = 64)
////    @JsonProperty("permanantPostcode")
////    private String permanantOtZipcode;
////
////    @Size(max = 64)
////    @JsonProperty("permanantIscitytownvillage")
////    private String permanant;
////
////    @Size(max = 64)
////    @JsonProperty("permanantAddresslinetwoen")
////    private String permanantOtAddress2En;
////
////    @Size(max = 64)
////    @JsonProperty("permanantAddresslinetwoml")
////    private String permanantOtAddress2Ml;
////
////    @Size(max = 64)
////    @JsonProperty("permanantAddresslineoneen")
////    private String permanantOtAddress1En;
////
////    @Size(max = 64)
////    @JsonProperty("permanantAddresslineoneml")
////    private String permanantOtAddress1Ml;
//
//}

public class KsmartBirthParentAddress {

//    @Size(max = 64)
//    @JsonProperty("presentAddresscountry")
//    private String presentCountryid;
//
//    @Size(max = 64)
//    @JsonProperty("presentAddressstatename")
//    private String presentStateid;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralalbname")
//    private String presentInTenantid;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeraladistrict")
//    private String presentInDistrictid;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralataluk")
//    private String presentInTalukName;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralavillage")
//    private String presentInVillageName;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralalocalitynameen")
//    private String presentInMainPlaceEn;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralastreetnameen")
//    private String presentInStreetLocalityAreaEn;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralahousenameen")
//    private String presentInHousenameEn;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralalocalitynameml")
//    private String presentInMainPlaceMl;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralastreetnameml")
//    private String presentInStreetLocalityAreaMl;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralahousenameml")
//    private String presentInHousenameMl;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralapincode")
//    private String presentInPinno;
//
//    @Size(max = 64)
//    @JsonProperty("presentInsidekeralapostoffice")
//    private String presentInPoid;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeraladistrict")
//    private String presentDistrictid;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralataluk")
//    private String presentTalukName;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralavillage")
//    private String presentVillageName;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralacityvilgeen")
//    private String present;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralacityvilgeml")
//    private String present;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralapincode")
//    private String presentPinno;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralapostoffice")
//    private String presentPoid;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralalocalitynameen")
//    private String present;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralastreetnameen")
//    private String presentStreetLocalityAreaEn;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralahousenameen")
//    private String presentHousenameEn;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralalocalitynameml")
//    private String presentMainPlaceMl;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralastreetnameml")
//    private String presentStreetLocalityAreaMl;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidekeralahousenameml")
//    private String presentHousenameMl;
//
//    @Size(max = 64)
//    @JsonProperty("presentAdrscitytown")
//    private String present;
//
//    @Size(max = 64)
//    @JsonProperty("presentOutsidecountry")
//    private String presentCountryid;
//
//    @Size(max = 64)
//    @JsonProperty("presentProvinceen")
//    private String presentOtStateRegionProvinceEn;
//
//    @Size(max = 64)
//    @JsonProperty("presentPostcode")
//    private String presentOtZipcode;
//
//    @Size(max = 64)
//    @JsonProperty("presentIscitytownvillage")
//    private String present;
//
//    @Size(max = 64)
//    @JsonProperty("presentAddresslinetwoen")
//    private String presentOtAddress2En;
//
//    @Size(max = 64)
//    @JsonProperty("presentAddresslinetwoml")
//    private String presentOtAddress2Ml;
//
//    @Size(max = 64)
//    @JsonProperty("presentAddresslineoneen")
//    private String presentOtAddress1En;
//
//    @Size(max = 64)
//    @JsonProperty("presentAddresslineoneml")
//    private String presentOtAddress1Ml;
//
//    @Size(max = 64)
//    @JsonProperty("permanantAddresscountry")
//    private String permanantCountryid;
//
//    @Size(max = 64)
//    @JsonProperty("permanantAddressstatename")
//    private String permanantStateid;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralalbname")
//    private String permanantTenantid;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeraladistrict")
//    private String permanantDistrictid;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralataluk")
//    private String permanantTalukName;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralavillage")
//    private String permanantVillageName;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralalocalitynameen")
//    private String permanantMainPlaceEn;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralastreetnameen")
//    private String permanantStreetLocalityAreaEn;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralahousenameen")
//    private String permanantHousenameEn;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralalocalitynameml")
//    private String permanantMainPlaceMl;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralastreetnameml")
//    private String permanantStreetLocalityAreaMl;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralahousenameml")
//    private String permanantHousenameMl;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralapincode")
//    private String permanantPinno;
//
//    @Size(max = 64)
//    @JsonProperty("permanantInsidekeralapostoffice")
//    private String permanantPoid;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeraladistrict")
//    private String permanantDistrictid;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralataluk")
//    private String permanantTalukName;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralavillage")
//    private String permanantVillageName;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralacityvilgeen")
//    private String permanant;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralacityvilgeml")
//    private String permanant;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralapincode")
//    private String permanantPinno;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralapostoffice")
//    private String permanantPoid;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralalocalitynameen")
//    private String permanant;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralastreetnameen")
//    private String permanantStreetLocalityAreaEn;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralahousenameen")
//    private String permanantHousenameEn;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralalocalitynameml")
//    private String permanantMainPlaceMl;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralastreetnameml")
//    private String permanantStreetLocalityAreaMl;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidekeralahousenameml")
//    private String permanantHousenameMl;
//
//    @Size(max = 64)
//    @JsonProperty("permanantAdrscitytown")
//    private String permanant;
//
//    @Size(max = 64)
//    @JsonProperty("permanantOutsidecountry")
//    private String permanantCountryid;
//
//    @Size(max = 64)
//    @JsonProperty("permanantProvinceen")
//    private String permanantOtStateRegionProvinceEn;
//
//    @Size(max = 64)
//    @JsonProperty("permanantPostcode")
//    private String permanantOtZipcode;
//
//    @Size(max = 64)
//    @JsonProperty("permanantIscitytownvillage")
//    private String permanant;
//
//    @Size(max = 64)
//    @JsonProperty("permanantAddresslinetwoen")
//    private String permanantOtAddress2En;
//
//    @Size(max = 64)
//    @JsonProperty("permanantAddresslinetwoml")
//    private String permanantOtAddress2Ml;
//
//    @Size(max = 64)
//    @JsonProperty("permanantAddresslineoneen")
//    private String permanantOtAddress1En;
//
//    @Size(max = 64)
//    @JsonProperty("permanantAddresslineoneml")
//    private String permanantOtAddress1Ml;

}