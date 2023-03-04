package org.ksmart.birth.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParentAddress {

    @Size(max = 64)
    @JsonProperty("presentaddressCountry")
    private String presentaddressCountry;

    @Size(max = 64)
    @JsonProperty("presentaddressStateName")
    private String presentaddressStateName;

    @Size(max = 64)
    @JsonProperty("presentInsideKeralaLBName")
    private String presentInsideKeralaLBName;

    @Size(max = 64)
    @JsonProperty("presentInsideKeralaDistrict")
    private String presentInsideKeralaDistrict;

    @Size(max = 64)
    @JsonProperty("presentInsideKeralaTaluk")
    private String presentInsideKeralaTaluk;

    @Size(max = 64)
    @JsonProperty("presentInsideKeralaVillage")
    private String presentInsideKeralaVillage;

    @Size(max = 1000)
    @JsonProperty("presentInsideKeralaLocalityNameEn")
    private String presentInsideKeralaLocalityNameEn;

    @Size(max = 2000)
    @JsonProperty("presentInsideKeralaStreetNameEn")
    private String presentInsideKeralaStreetNameEn;

    @Size(max = 2500)
    @JsonProperty("presentInsideKeralaHouseNameEn")
    private String presentInsideKeralaHouseNameEn;

    @Size(max = 1000)
    @JsonProperty("presentInsideKeralaLocalityNameMl")
    private String presentInsideKeralaLocalityNameMl;

    @Size(max = 2000)
    @JsonProperty("presentInsideKeralaStreetNameMl")
    private String presentInsideKeralaStreetNameMl;

    @Size(max = 2500)
    @JsonProperty("presentInsideKeralaHouseNameMl")
    private String presentInsideKeralaHouseNameMl;

    @Size(max = 10)
    @JsonProperty("presentInsideKeralaPincode")
    private String presentInsideKeralaPincode;

    @Size(max = 64)
    @JsonProperty("presentInsideKeralaPostOffice")
    private String presentInsideKeralaPostOffice;

    @Size(max = 64)
    @JsonProperty("presentWardNo")
    private String presentWardNo;

    @Size(max = 64)
    @JsonProperty("presentOutsideKeralaDistrict")
    private String presentOutsideKeralaDistrict;

    @Size(max = 1000)
    @JsonProperty("presentOutsideKeralaTaluk")
    private String presentOutsideKeralaTalukName;

    @Size(max = 1000)
    @JsonProperty("presentOutsideKeralaVillage")
    private String presentOutsideKeralaVillageName;


    @Size(max = 64)
    @JsonProperty("presentOutsideKeralaCityVilgeEn")
    private String presentOutsideKeralaCityVilgeEn;

    @Size(max = 64)
    @JsonProperty("presentOutsideKeralaPincode")
    private String presentOutsideKeralaPincode;
    @Size(max = 64)
    @JsonProperty("presentOutsideKeralaPostOfficeEn")
    private String presentOutsideKeralaPostOfficeEn;

    @Size(max = 64)
    @JsonProperty("presentOutsideKeralaPostOfficeMl")
    private String presentOutsideKeralaPostOfficeMl;

    @Size(max = 1000)
    @JsonProperty("presentOutsideKeralaLocalityNameEn")
    private String presentOutsideKeralaLocalityNameEn;

    @Size(max = 2000)
    @JsonProperty("presentOutsideKeralaStreetNameEn")
    private String presentOutsideKeralaStreetNameEn;

    @Size(max = 2500)
    @JsonProperty("presentOutsideKeralaHouseNameEn")
    private String presentOutsideKeralaHouseNameEn;

    @Size(max = 1000)
    @JsonProperty("presentOutsideKeralaLocalityNameMl")
    private String presentOutsideKeralaLocalityNameMl;

    @Size(max = 2000)
    @JsonProperty("presentOutsideKeralaStreetNameMl")
    private String presentOutsideKeralaStreetNameMl;


    @Size(max = 2500)
    @JsonProperty("presentOutsideKeralaHouseNameMl")
    private String presentOutsideKeralaHouseNameMl;

    @Size(max = 2500)
    @JsonProperty("presentOutSideIndiaAdressEn")
    private String presentOutSideIndiaAdressEn;


    @Size(max = 2500)
    @JsonProperty("presentOutSideIndiaAdressMl")
    private String presentOutSideIndiaAdressMl;


    @Size(max = 2500)
    @JsonProperty("presentOutSideIndiaAdressEnB")
    private String presentOutSideIndiaAdressEnB;


    @Size(max = 2500)
    @JsonProperty("presentOutSideIndiaAdressMlB")
    private String presentOutSideIndiaAdressMlB;


    @Size(max = 2500)
    @JsonProperty("presentOutSideIndiaProvinceEn")
    private String presentOutSideIndiaProvinceEn;


    @Size(max = 1000)
    @JsonProperty("presentOutSideIndiaLocalityMl")
    private String presentOutSideIndiaLocalityMl;

    @Size(max = 2500)
    @JsonProperty("presentOutSideIndiaProvinceMl")
    private String presentOutSideIndiaProvinceMl;

    @Size(max = 64)
    @JsonProperty("presentOutSideCountry")
    private String presentOutSideCountry;

    @JsonProperty("isPrsentAddress")
    private Boolean isPrsentAddress;

    @Size(max = 1000)
    @JsonProperty("presentOutSideIndiaadrsVillage")
    private String presentOutSideIndiaadrsVillage;

    @Size(max = 64)
    @JsonProperty("presentOutSideIndiaadrsCityTown")
    private String presentOutSideIndiaadrsCityTown;

    ////Db Fields//////

    @JsonProperty("isPrsentAddressInt")
    private Integer isPrsentAddressInt;
    @Size(max = 64)
    @JsonProperty("presentUuid")
    private String presentUuid;

    @Size(max = 64)
    @JsonProperty("countryIdPresent")
    private String countryIdPresent;

    @Size(max = 64)
    @JsonProperty("stateIdPresent")
    private String stateIdPresent;

    @Size(max = 64)
    @JsonProperty("districtIdPresent")
    private String districtIdPresent;

    @Size(max = 64)
    @JsonProperty("pinNoPresent")
    private String pinNoPresent;

    @Size(max = 1000)
    @JsonProperty("localityEnPresent")
    private String localityEnPresent;

    @Size(max = 1000)
    @JsonProperty("localityMlPresent")
    private String localityMlPresent;

    @Size(max = 1000)
    @JsonProperty("streetNameEnPresent")
    private String streetNameEnPresent;

    @Size(max = 1000)
    @JsonProperty("streetNameMlPresent")
    private String streetNameMlPresent;

    @Size(max = 1000)
    @JsonProperty("houseNameNoEnPresent")
    private String houseNameNoEnPresent;

    @Size(max = 1000)
    @JsonProperty("houseNameNoMlPresent")
    private String houseNameNoMlPresent;

    @Size(max = 1000)
    @JsonProperty("villageNamePresent")
    private String villageNamePresent;

    @Size(max = 10)
    @JsonProperty("poNoPresent")
    private String poNoPresent;

////Permanant
    @Size(max = 1000)
    @JsonProperty("permtaddressCountry")
    private String permtaddressCountry;

    @Size(max = 64)
    @JsonProperty("permtaddressStateName")
    private String permtaddressStateName;

    @Size(max = 64)
    @JsonProperty("permntInKeralaAdrLBName")
    private String permntInKeralaAdrLBName;

    @Size(max = 64)
    @JsonProperty("permntInKeralaAdrDistrict")
    private String permntInKeralaAdrDistrict;

    @Size(max = 64)
    @JsonProperty("permntOutsideKeralaCityVilgeEn")
    private String permntOutsideKeralaCityVilgeEn;

    @Size(max = 64)
    @JsonProperty("permntInKeralaAdrTaluk")
    private String permntInKeralaAdrTaluk;

    @Size(max = 64)
    @JsonProperty("permntInKeralaAdrVillage")
    private String permntInKeralaAdrVillage;

    @Size(max = 1000)
    @JsonProperty("permntInKeralaAdrLocalityNameEn")
    private String permntInKeralaAdrLocalityNameEn;

    @Size(max = 2000)
    @JsonProperty("permntInKeralaAdrStreetNameEn")
    private String permntInKeralaAdrStreetNameEn;

    @Size(max = 2500)
    @JsonProperty("permntInKeralaAdrHouseNameEn")
    private String permntInKeralaAdrHouseNameEn;
    @Size(max = 2500)
    @JsonProperty("permntInKeralaAdrLocalityNameMl")
    private String permntInKeralaAdrLocalityNameMl;

    @Size(max = 2500)
    @JsonProperty("permntInKeralaAdrStreetNameMl")
    private String permntInKeralaAdrStreetNameMl;

    @Size(max = 2500)
    @JsonProperty("permntInKeralaAdrHouseNameMl")
    private String permntInKeralaAdrHouseNameMl;

    @Size(max = 64)
    @JsonProperty("permntInKeralaAdrPincode")
    private String permntInKeralaAdrPincode;

    @Size(max = 64)
    @JsonProperty("permntInKeralaAdrPostOffice")
    private String permntInKeralaAdrPostOffice;

    @Size(max = 64)
    @JsonProperty("permntInKeralaWardNo")
    private String permntInKeralaWardNo;

    @Size(max = 64)
    @JsonProperty("permntOutsideKeralaDistrict")
    private String permntOutsideKeralaDistrict;

    @Size(max = 1000)
    @JsonProperty("permntOutsideKeralaTaluk")
    private String permntOutsideKeralaTaluk;

    @Size(max = 1000)
    @JsonProperty("permntOutsideKeralaVillage")
    private String permntOutsideKeralaVillage;

    @Size(max = 64)
    @JsonProperty("permntOutsideKeralaPincode")
    private String permntOutsideKeralaPincode;

    @Size(max = 1000)
    @JsonProperty("permntOutsideKeralaLocalityNameEn")
    private String permntOutsideKeralaLocalityNameEn;

    @Size(max = 2000)
    @JsonProperty("permntOutsideKeralaStreetNameEn")
    private String permntOutsideKeralaStreetNameEn;

    @Size(max = 2500)
    @JsonProperty("permntOutsideKeralaHouseNameEn")
    private String permntOutsideKeralaHouseNameEn;

    @Size(max = 1000)
    @JsonProperty("permntOutsideKeralaLocalityNameMl")
    private String permntOutsideKeralaLocalityNameMl;

    @Size(max = 2000)
    @JsonProperty("permntOutsideKeralaStreetNameMl")
    private String permntOutsideKeralaStreetNameMl;

    @Size(max = 2500)
    @JsonProperty("permntOutsideKeralaHouseNameMl")
    private String permntOutsideKeralaHouseNameMl;

    @Size(max = 64)
    @JsonProperty("permntOutsideKeralaPostOfficeEn")
    private String permntOutsideKeralaPostOfficeEn;

    @Size(max = 64)
    @JsonProperty("permntOutsideKeralaPostOfficeMl")
    private String permntOutsideKeralaPostOfficeMl;

    @Size(max = 2500)
    @JsonProperty("permntOutsideIndiaLineoneEn")
    private String permntOutsideIndiaLineoneEn;

    @Size(max = 2500)
    @JsonProperty("permntOutsideIndiaLineoneMl")
    private String permntOutsideIndiaLineoneMl;

    @Size(max = 2500)
    @JsonProperty("permntOutsideIndiaLinetwoEn")
    private String permntOutsideIndiaLinetwoEn;

    @Size(max = 2500)
    @JsonProperty("permntOutsideIndiaLinetwoMl")
    private String permntOutsideIndiaLinetwoMl;

    @Size(max = 2500)
    @JsonProperty("permntOutsideIndiaprovinceEn")
    private String permntOutsideIndiaprovinceEn;

    @Size(max = 1000)
    @JsonProperty("permntOutsideIndiaVillage")
    private String permntOutsideIndiaVillage;

    @Size(max = 64)
    @JsonProperty("permntOutsideIndiaCityTown")
    private String permntOutsideIndiaCityTown;

    @Size(max = 10)
    @JsonProperty("permanentOutsideIndiaPostCode")
    private String permanentOutsideIndiaPostCode;

    @Size(max = 64)
    @JsonProperty("permntOutsideIndiaCountry")
    private String permntOutsideIndiaCountry;


    ////Db Fields//////

    @Size(max = 64)
    @JsonProperty("permanentUuid")
    private String permanentUuid;
    @Size(max = 64)
    @JsonProperty("countryIdPermanent")
    private String countryIdPermanent;

    @Size(max = 64)
    @JsonProperty("stateIdPermanent")
    private String stateIdPermanent;

    @Size(max = 64)
    @JsonProperty("districtIdPermanent")
    private String districtIdPermanent;

    @Size(max = 64)
    @JsonProperty("pinNoPermanent")
    private String pinNoPermanent;

    @Size(max = 1000)
    @JsonProperty("localityEnPermanent")
    private String localityEnPermanent;

    @Size(max = 1000)
    @JsonProperty("localityMlPermanent")
    private String localityMlPermanent;

    @Size(max = 1000)
    @JsonProperty("streetNameEnPermanent")
    private String streetNameEnPermanent;

    @Size(max = 1000)
    @JsonProperty("streetNameMlPermanent")
    private String streetNameMlPermanent;

    @Size(max = 1000)
    @JsonProperty("houseNameNoEnPermanent")
    private String houseNameNoEnPermanent;

    @Size(max = 1000)
    @JsonProperty("houseNameNoMlPermanent")
    private String houseNameNoMlPermanent;

    @Size(max = 1000)
    @JsonProperty("villageNamePermanent")
    private String villageNamePermanent;

    @Size(max = 10)
    @JsonProperty("poNoPermanent")
    private String poNoPermanent;

    @Size(max = 64)
    @JsonProperty("bioAdoptPermanent")
    private String bioAdoptPermanent;

    @Size(max = 64)
    @JsonProperty("bioAdoptPresent")
    private String bioAdoptPresent;



}































