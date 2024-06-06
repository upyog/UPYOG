import { FormComposer, Loader,Modal ,Card , CardHeader, StatusTable,Row } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../config/Create/config";

const NewApplication = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const tenants = Digit.Hooks.pt.useTenants();
  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);
  const defaultValues = { };
  const history = useHistory();
  // delete
  // const [_formData, setFormData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", { });
  const { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(Digit.ULBService.getStateId(), "PropertyTax", "CommonFieldsConfig");
  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);

  const onFormValueChange = (setValue, formData, formState) => {
    console.log("formData, formState",formData, formState)
    setSubmitValve(!Object.keys(formState.errors).length);
    if (Object.keys(formState.errors).length === 1 && formState.errors?.units?.message === "arv") {
      setSubmitValve(!formData?.units.some((unit) => unit.occupancyType === "RENTED" && !unit.arv));
    }
    if (formData?.ownershipCategory?.code?.includes("MULTIPLEOWNERS") && formData?.owners?.length < 2) {
      setSubmitValve(false);
    }
    let pincode = formData?.address?.pincode;
    if (pincode) {
      if (!Digit.Utils.getPattern("Pincode").test(pincode)) setSubmitValve(false);
      const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item.toString() === pincode));
      if (!foundValue) {
        setSubmitValve(false);
      }
    }
  };

  const onSubmit = (data) => {
    const formData = {
      tenantId,
      address: {
        ...data?.address,
        city: data?.address?.city?.name,
        locality: { code: data?.address?.locality?.code, area: data?.address?.locality?.area },
      },
      usageCategory: data?.usageCategoryMajor.code,
      usageCategoryMajor: data?.usageCategoryMajor?.code.split(".")[0],
      usageCategoryMinor: data?.usageCategoryMajor?.code.split(".")[1] || null,
      landArea: Number(data?.landarea),
      superBuiltUpArea: Number(data?.landarea),
      propertyType: data?.PropertyType?.code,
      noOfFloors: Number(data?.noOfFloors),
      ownershipCategory: data?.ownershipCategory?.code,
      additionalDetails:{
      //RentedMonths: data?.units[0]?.RentedMonths,
      //NonRentedMonthsUsage: data?.units[0]?.NonRentedMonthsUsage,
       ageOfProperty:data?.propertyStructureDetails?.ageOfProperty,
       structureType:data?.propertyStructureDetails?.structureType,
      electricity:data?.electricity,
      uid:data?.uid
      },
      owners: data?.owners.map((owner,index) => {
        let {
          name,
          mobileNumber,
          designation,
          altContactNumber,
          emailId,
          correspondenceAddress,
          isCorrespondenceAddress,
          ownerType,
          fatherOrHusbandName,
        } = owner;
        let __owner;

        if (!data?.ownershipCategory?.code.includes("INDIVIDUAL")) {
          __owner = { name, mobileNumber, designation, altContactNumber, emailId, correspondenceAddress, isCorrespondenceAddress, ownerType };
        } else {
          __owner = {
            name,
            mobileNumber,
            correspondenceAddress,
            permanentAddress: data?.address?.locality?.name,
            relationship: owner?.relationship.code,
            fatherOrHusbandName,
            gender: owner?.gender.code,
            emailId,
          };
        }

        if (!__owner?.correspondenceAddress) __owner.correspondenceAddress = "";

        const _owner = {
          ...__owner,
          ownerType: owner?.ownerType?.code,
        };
        if (_owner.ownerType !== "NONE") {
          const { documentType, documentUid } = owner?.documents;
          _owner.documents = [
            { documentUid: documentUid, documentType: documentType.code, fileStoreId: documentUid },
            data?.documents?.documents?.find((e) => e.documentType?.includes("OWNER.IDENTITYPROOF")),
          ];
        } else {
          _owner.documents = [data?.documents?.documents?.find((e) => e.documentType?.includes("OWNER.IDENTITYPROOF"))];
        }
        return _owner;
      }),

      channel: "CFC_COUNTER", // required
      creationReason: "CREATE", // required
      source: "MUNICIPAL_RECORDS", // required
      units: data?.PropertyType?.code !== "VACANT" ? data?.units : [],
      documents: data?.documents?.documents,
      applicationStatus: "CREATE",
    };

    if (!data?.ownershipCategory?.code.includes("INDIVIDUAL")) {
      formData.institution = {
        name: data.owners?.[0].institution.name,
        type:data.owners?.[0].institution?.type?.code,
        designation: data.owners?.[0].designation,        
        nameOfAuthorizedPerson: data.owners?.[0].name,
        tenantId: Digit.ULBService.getCurrentTenantId(),
      };
    }

    history.replace("/digit-ui/employee/pt/response", { Property: formData }); //current wala

  };
  if (isLoading) {
    return <Loader />;
  }

  /* use newConfig instead of commonFields for local development in case needed */

  const CloseBtn = (props) => {
    return (
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };
  const closeModal =() =>{
    setShowToast(false)
  }
  const setModal=()=>{
      setShowToast(false)   
      history.replace("/digit-ui/employee/pt/response", { Property: formData })
    }
let conf =[
  {
      "head": "ES_NEW_APPLICATION_LOCATION_DETAILS",
      "body": [
          {
              "route": "map",
              "component": "PTSelectGeolocation",
              "nextStep": "pincode",
              "hideInEmployee": true,
              "key": "address",
              "texts": {
                  "header": "PT_GEOLOCATON_HEADER",
                  "cardText": "PT_GEOLOCATION_TEXT",
                  "nextText": "PT_COMMON_NEXT",
                  "skipAndContinueText": "CORE_COMMON_SKIP_CONTINUE"
              }
          },
          {
              "route": "pincode",
              "component": "PTSelectPincode",
              "texts": {
                  "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
                  "header": "PT_PINCODE_LABEL",
                  "cardText": "PT_PINCODE_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT",
                  "skipText": "CORE_COMMON_SKIP_CONTINUE"
              },
              "withoutLabel": true,
              "key": "address",
              "nextStep": "address",
              "type": "component"
          },
          {
              "route": "address",
              "component": "PTSelectAddress",
              "withoutLabel": true,
              "texts": {
                  "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
                  "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
                  "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_CITY_MOHALLA_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "address",
              "nextStep": "street",
              "isMandatory": true,
              "type": "component"
          },
          {
              "type": "component",
              "route": "street",
              "component": "PTSelectStreet",
              "key": "address",
              "withoutLabel": true,
              "texts": {
                  "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
                  "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
                  "cardText": "PT_STREET_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "nextStep": "landmark"
          },
          {
              "type": "component",
              "route": "landmark",
              "component": "SelectLandmark",
              "withoutLabel": true,
              "texts": {
                  "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
                  "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TITLE",
                  "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT",
                  "skipText": "CORE_COMMON_SKIP_CONTINUE"
              },
              "key": "address",
              "nextStep": "proof",
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "proof",
              "component": "Proof",
              "withoutLabel": true,
              "texts": {
                  "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
                  "header": "PT_PROOF_OF_ADDRESS_HEADER",
                  "cardText": "",
                  "nextText": "PT_COMMONS_NEXT",
                  "submitBarLabel": "PT_COMMONS_NEXT"
              },
              "key": "address",
              "nextStep": "owner-ship-details@0",
              "hideInEmployee": true
          }
      ]
  },
  {
      "head": "ES_NEW_APPLICATION_PROPERTY_ASSESSMENT",
      "body": [
          {
              "route": "info",
              "component": "PropertyTax",
              "nextStep": "isResidential",
              "hideInEmployee": true,
              "key": "Documents"
          },
          {
              "type": "component",
              "route": "isResidential",
              "isMandatory": true,
              "component": "IsResidential",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_PROPERTY_DETAILS_RESIDENTIAL_PROPERTY_HEADER",
                  "cardText": "PT_PROPERTY_DETAILS_RESIDENTIAL_PROPERTY_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "isResdential",
              "withoutLabel": true,
              "hideInEmployee": true,
              "nextStep": {
                  "PT_COMMON_YES": "property-type",
                  "PT_COMMON_NO": "property-usage-type"
              }
          },
          {
              "type": "component",
              "route": "property-usage-type",
              "isMandatory": true,
              "component": "PropertyUsageType",
              "texts": {
                  "headerCaption": "PT_ASSESMENT_INFO_USAGE_TYPE",
                  "header": "PT_PROPERTY_DETAILS_USAGE_TYPE_HEADER",
                  "cardText": "PT_PROPERTY_DETAILS_USAGE_TYPE_TEXT",
                  "submitBarLabel": "PT_COMMONS_NEXT"
              },
              "nextStep": "property-type",
              "key": "usageCategoryMajor",
              "withoutLabel": true
          },
          {
              "type": "component",
              "isMandatory": true,
              "component": "ProvideSubUsageType",
              "key": "usageCategoryMinor",
              "withoutLabel": true
          },
          {
              "type": "component",
              "route": "provide-sub-usage-type",
              "isMandatory": true,
              "component": "ProvideSubUsageType",
              "texts": {
                  "headerCaption": "PT_ASSESMENT_INFO_USAGE_TYPE",
                  "header": "PT_ASSESSMENT_FLOW_SUBUSAGE_HEADER",
                  "cardText": "PT_ASSESSMENT_FLOW_SUBUSAGE_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "units",
              "withoutLabel": true,
              "nextStep": {
                  "yes": "is-any-part-of-this-floor-unoccupied",
                  "no": "provide-sub-usage-type-of-rented-area"
              },
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "property-type",
              "isMandatory": true,
              "component": "PropertyType",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_ASSESMENT1_PROPERTY_TYPE",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMONS_NEXT"
              },
              "nextStep": {
                  "COMMON_PROPTYPE_BUILTUP_INDEPENDENTPROPERTY": "number-of-floors",
                  "COMMON_PROPTYPE_BUILTUP_SHAREDPROPERTY": "provide-floor-no",
                  "COMMON_PROPTYPE_VACANT": "area"
              },
              "key": "PropertyType",
              "withoutLabel": true
          },
          {
              "type": "component",
              "isMandatory": true,
              "component": "Area",
              "key": "landarea",
              "withoutLabel": true
          },
          {
              "type": "component",
              "isMandatory": true,
              "component": "Electricity",
              "key": "electricity",
              "withoutLabel": true
          },
          {
              "type": "component",
              "isMandatory": true,
              "component": "UID",
              "key": "uid",
              "withoutLabel": true
          },
                      {
              "type": "component",
              "isMandatory": true,
              "component": "PropertyStructureDetails",
              "key": "propertyStructureDetails",
              "withoutLabel": true
          },
          {
              "type": "component",
              "route": "electricity",
              "isMandatory": true,
              "component": "Electricity",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_ASSESSMENT_FLOW_AREA_HEADER",
                  "cardText": "PT_SELFOCCUPIED_AREA",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "units",
              "withoutLabel": true,
              "nextStep": {
                  "yes": "rental-details",
                  "no": "provide-sub-usage-type",
                  "vacant": "map"
              },
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "uid",
              "isMandatory": true,
              "component": "UID",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_ASSESSMENT_FLOW_AREA_HEADER",
                  "cardText": "PT_SELFOCCUPIED_AREA",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "units",
              "withoutLabel": true,
              "nextStep": {
                  "yes": "rental-details",
                  "no": "provide-sub-usage-type",
                  "vacant": "map"
              },
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "area",
              "isMandatory": true,
              "component": "Area",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_ASSESSMENT_FLOW_AREA_HEADER",
                  "cardText": "PT_SELFOCCUPIED_AREA",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "units",
              "withoutLabel": true,
              "nextStep": {
                  "yes": "rental-details",
                  "no": "provide-sub-usage-type",
                  "vacant": "map"
              },
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "number-of-floors",
              "isMandatory": true,
              "component": "PropertyFloorDetails",
              "texts": {
                  "headerCaption": "",
                  "header": "BPA_SCRUTINY_DETAILS_NUMBER_OF_FLOORS_LABEL",
                  "cardText": "PT_PROPERTY_DETAILS_NO_OF_FLOORS_TEXT",
                  "submitBarLabel": "PT_COMMONS_NEXT"
              },
              "nextStep": "number-of-basements@0",
              "key": "noOfFloors",
              "withoutLabel": true
          },
          {
              "type": "component",
              "component": "Units",
              "key": "units",
              "withoutLabel": true
          },
          {
              "type": "component",
              "route": "provide-floor-no",
              "isMandatory": true,
              "component": "ProvideFloorNo",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_FLOOR_NUMBER_HEADER",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMONS_NEXT"
              },
              "nextStep": "floordetails",
              "key": "Floorno",
              "withoutLabel": true,
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "is-this-floor-self-occupied",
              "isMandatory": true,
              "component": "IsThisFloorSelfOccupied",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_ASSESSMENT_FLOW_FLOOR_OCC_HEADER",
                  "cardText": "PT_ASSESSMENT_FLOW_FLOOR_OCC_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "units",
              "withoutLabel": true,
              "nextStep": {
                  "PT_YES_IT_IS_SELFOCCUPIED": "provide-sub-usage-type",
                  "PT_YES_IT_IS_SELFOCCUPIED1": "is-any-part-of-this-floor-unoccupied",
                  "PT_PARTIALLY_RENTED_OUT": "area",
                  "PT_PARTIALLY_RENTED_OUT1": "area",
                  "PT_FULLY_RENTED_OUT": "provide-sub-usage-type-of-rented-area",
                  "PT_FULLY_RENTED_OUT1": "rental-details"
              },
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "number-of-basements@0",
              "isMandatory": true,
              "component": "PropertyBasementDetails",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_PROPERTY_DETAILS_NO_OF_BASEMENTS_HEADER",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMONS_NEXT"
              },
              "nextStep": {
                  "PT_NO_BASEMENT_OPTION": "floordetails",
                  "PT_ONE_BASEMENT_OPTION": "floordetails",
                  "PT_TWO_BASEMENT_OPTION": "floordetails"
              },
              "key": "noOofBasements",
              "withoutLabel": true,
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "floordetails",
              "isMandatory": true,
              "component": "GroundFloorDetails",
              "texts": {
                  "headerCaption": "",
                  "cardText": "PT_PROPERTY_DETAILS_FLOOR_DETAILS_TEXT",
                  "submitBarLabel": "Next"
              },
              "nextStep": "is-this-floor-self-occupied",
              "key": "units",
              "withoutLabel": true,
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "rental-details",
              "isMandatory": true,
              "component": "RentalDetails",
              "texts": {
                  "header": "PT_ASSESSMENT_FLOW_RENTAL_DETAIL_HEADER",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "units",
              "withoutLabel": true,
              "nextStep": "is-any-part-of-this-floor-unoccupied",
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "provide-sub-usage-type-of-rented-area",
              "isMandatory": true,
              "component": "ProvideSubUsageTypeOfRentedArea",
              "texts": {
                  "headerCaption": "PT_ASSESMENT_INFO_USAGE_TYPE",
                  "header": "PT_ASSESSMENT_FLOW_RENT_SUB_USAGE_HEADER",
                  "cardText": "PT_ASSESSMENT_FLOW_SUBUSAGE_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "units",
              "withoutLabel": true,
              "nextStep": "rental-details",
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "is-any-part-of-this-floor-unoccupied",
              "isMandatory": true,
              "component": "IsAnyPartOfThisFloorUnOccupied",
              "texts": {
                  "header": "PT_ASSESSMENT_FLOW_ISUNOCCUPIED_HEADER",
                  "cardText": "PT_ASSESSMENT_FLOW_ISUNOCCUPIED_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "units",
              "withoutLabel": true,
              "nextStep": {
                  "PT_COMMON_NO": "map",
                  "PT_COMMON_YES": "un-occupied-area"
              },
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "un-occupied-area",
              "isMandatory": true,
              "component": "UnOccupiedArea",
              "texts": {
                  "header": "PT_ASSESSMENT_FLOW_UNOCCUPIED_AREA_HEADER",
                  "cardText": "PT_ASSESSMENT_FLOW_UNOCCUPIED_AREA_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT",
                  "skipText": ""
              },
              "key": "units",
              "withoutLabel": true,
              "nextStep": "map",
              "hideInEmployee": true
          }
      ]
  },
  {
      "head": "ES_NEW_APPLICATION_OWNERSHIP_DETAILS",
      "body": [
          {
              "type": "component",
              "route": "owner-ship-details@0",
              "isMandatory": true,
              "component": "SelectOwnerShipDetails",
              "texts": {
                  "headerCaption": "PT_PROPERTIES_OWNERSHIP",
                  "header": "PT_PROVIDE_OWNERSHIP_DETAILS",
                  "cardText": "PT_PROVIDE_OWNERSHI_DETAILS_SUB_TEXT",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "ownershipCategory",
              "withoutLabel": true,
              "nextStep": {
                  "INSTITUTIONALPRIVATE": "inistitution-details",
                  "INSTITUTIONALGOVERNMENT": "inistitution-details",
                  "INDIVIDUAL.SINGLEOWNER": "owner-details",
                  "INDIVIDUAL.MULTIPLEOWNERS": "owner-details"
              }
          },
          {
              "isMandatory": true,
              "type": "component",
              "route": "owner-details",
              "key": "owners",
              "component": "SelectOwnerDetails",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_OWNERSHIP_INFO_SUB_HEADER",
                  "cardText": "PT_FORM3_HEADER_MESSAGE",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "withoutLabel": true,
              "nextStep": "special-owner-category",
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "special-owner-category",
              "isMandatory": true,
              "component": "SelectSpecialOwnerCategoryType",
              "texts": {
                  "headerCaption": "PT_OWNERS_DETAILS",
                  "header": "PT_SPECIAL_OWNER_CATEGORY",
                  "cardText": "PT_FORM3_HEADER_MESSAGE",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "owners",
              "withoutLabel": true,
              "nextStep": "owner-address",
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "owner-address",
              "isMandatory": true,
              "component": "SelectOwnerAddress",
              "texts": {
                  "headerCaption": "PT_OWNERS_DETAILS",
                  "header": "PT_OWNERS_ADDRESS",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "owners",
              "withoutLabel": true,
              "nextStep": "special-owner-category-proof",
              "hideInEmployee": true
          },
          {
              "type": "component",
              "component": "SelectAltContactNumber",
              "key": "owners",
              "withoutLabel": true,
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "special-owner-category-proof",
              "isMandatory": true,
              "component": "SelectSpecialProofIdentity",
              "texts": {
                  "headerCaption": "PT_OWNERS_DETAILS",
                  "header": "PT_SPECIAL_OWNER_CATEGORY_PROOF_HEADER",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "owners",
              "withoutLabel": true,
              "nextStep": "proof-of-identity",
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "proof-of-identity",
              "isMandatory": true,
              "component": "SelectProofIdentity",
              "texts": {
                  "headerCaption": "PT_OWNERS_DETAILS",
                  "header": "PT_PROOF_IDENTITY_HEADER",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMON_NEXT",
                  "addMultipleText": "PT_COMMON_ADD_APPLICANT_LABEL"
              },
              "key": "owners",
              "withoutLabel": true,
              "nextStep": null,
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "inistitution-details",
              "isMandatory": true,
              "component": "SelectInistitutionOwnerDetails",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_INSTITUTION_DETAILS_HEADER",
                  "cardText": "PT_FORM3_HEADER_MESSAGE",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "owners",
              "withoutLabel": true,
              "nextStep": "institutional-owner-address",
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "institutional-owner-address",
              "isMandatory": true,
              "component": "SelectOwnerAddress",
              "texts": {
                  "headerCaption": "PT_OWNERS_DETAILS",
                  "header": "PT_OWNERS_ADDRESS",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "owners",
              "withoutLabel": true,
              "nextStep": "institutional-proof-of-identity",
              "hideInEmployee": true
          },
          {
              "type": "component",
              "route": "institutional-proof-of-identity",
              "isMandatory": true,
              "component": "SelectProofIdentity",
              "texts": {
                  "headerCaption": "PT_OWNERS_DETAILS",
                  "header": "PT_PROOF_IDENTITY_HEADER",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMON_NEXT"
              },
              "key": "owners",
              "withoutLabel": true,
              "nextStep": null,
              "hideInEmployee": true
          },
          {
              "type": "component",
              "component": "PTEmployeeOwnershipDetails",
              "key": "owners",
              "withoutLabel": true,
              "hideInCitizen": true
          }
      ]
  },
  {
      "head": "ES_NEW_APPLICATION_DOCUMENTS_REQUIRED",
      "body": [
          {
              "component": "SelectDocuments",
              "withoutLabel": true,
              "key": "documents",
              "type": "component"
          }
      ]
  }
]
  return (
    <FormComposer
      heading={t("ES_TITLE_NEW_PROPERTY_APPLICATION")}
      isDisabled={!canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={conf.map((config) => {   
        return {
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
        };
      })}
      fieldStyle={{ marginRight: 0 }}
      onSubmit={onSubmit}
      defaultValues={defaultValues}
      onFormValueChange={onFormValueChange}
    />
  );
};

export default NewApplication;