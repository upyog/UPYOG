import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { FormComposer, Header, Loader } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const isConventionalSpecticTank = (tankDimension) => tankDimension === "lbd";

const EditForm = ({ tenantId, applicationData, channelMenu, vehicleMenu, sanitationMenu }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(false);
  const stateId = Digit.ULBService.getStateId();
  const { data: commonFields, isLoading } = Digit.Hooks.fsm.useMDMS(stateId, "FSM", "CommonFieldsConfig");
  const { data: preFields, isLoading: isApplicantConfigLoading } = Digit.Hooks.fsm.useMDMS(stateId, "FSM", "PreFieldsConfig");
  const { data: postFields, isLoading: isTripConfigLoading } = Digit.Hooks.fsm.useMDMS(stateId, "FSM", "PostFieldsConfig");
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("FSM_MUTATION_HAPPENED", false);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("FSM_ERROR_DATA", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("FSM_MUTATION_SUCCESS_DATA", false);

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
  }, []);

  var defaultValues = {
    channel: channelMenu.filter((channel) => channel.code === applicationData.source)[0],
    applicationData: {
      applicantName: applicationData.citizen.name,
      mobileNumber: applicationData.citizen.mobileNumber,
      applicantGender: applicationData.citizen.gender,
    },
    tripData: {
      noOfTrips: applicationData.noOfTrips,
      amountPerTrip: applicationData.additionalDetails.tripAmount !== "null" ? applicationData.additionalDetails.tripAmount : "",
      amount:
        applicationData.additionalDetails.tripAmount !== "null"
          ? applicationData.noOfTrips * applicationData.additionalDetails.tripAmount
          : undefined,
      vehicleType: { capacity: applicationData?.vehicleCapacity },
      vehicleCapacity: applicationData?.vehicleCapacity,
      distancefromroad:applicationData?.additionalDetails?.distancefromroad,
      roadWidth:applicationData?.additionalDetails?.roadWidth
    },
    propertyType: applicationData.propertyUsage.split(".")[0],
    propertyID: applicationData?.additionalDetails?.propertyID,
    subtype: applicationData.propertyUsage,
    address: {
      pincode: applicationData.address.pincode,
      locality: {
        ...applicationData.address.locality,
        i18nkey: `${applicationData.tenantId.toUpperCase().split(".").join("_")}_REVENUE_${applicationData?.address?.locality?.code}`,
      },
      slum: applicationData.address.slumName,
      street: applicationData.address.street,
      doorNo: applicationData.address.doorNo,
      landmark: applicationData.address.landmark,
    },
    pitType: sanitationMenu.filter((type) => type.code === applicationData.sanitationtype)[0],
    pitDetail: applicationData.pitDetail,
    paymentPreference: applicationData.paymentPreference,
    advanceAmount: applicationData.advanceAmount,
  };

  if (
    (applicationData && applicationData?.address?.additionalDetails?.boundaryType === "Village") ||
    applicationData?.address?.additionalDetails?.boundaryType === "GP"
  ) {
    defaultValues.address = {
      ...defaultValues.address,
      propertyLocation: {
        active: true,
        code: "FROM_GRAM_PANCHAYAT",
        i18nKey: "FROM_GRAM_PANCHAYAT",
        name: "From Gram Panchayat",
      },
      additionalDetails: {
        boundaryType: applicationData?.address?.additionalDetails?.boundaryType,
        gramPanchayat: applicationData?.address?.additionalDetails?.gramPanchayat,
        village: applicationData?.address?.additionalDetails?.village,
        newGp: applicationData?.address?.additionalDetails?.newGramPanchayat,
      },
    };
  } else if (applicationData && applicationData?.address?.additionalDetails?.boundaryType === "Locality") {
    defaultValues.address = {
      ...defaultValues.address,
      propertyLocation: {
        active: true,
        code: "WITHIN_ULB_LIMITS",
        i18nKey: "WITHIN_ULB_LIMITS",
        name: "Witnin ULB Limits",
      },
      additionalDetails: {
        boundaryType: applicationData?.address?.additionalDetails?.boundaryType,
        newLocality: applicationData?.address?.additionalDetails?.newLocality,
      },
    };
  }

  const onFormValueChange = (setValue, formData) => {
    if (
      formData?.propertyType &&
      formData?.subtype &&
      (formData?.address?.locality?.code ||
        (formData?.address?.propertyLocation?.code === "FROM_GRAM_PANCHAYAT" &&
          (formData?.address?.gramPanchayat?.code || formData?.address?.additionalDetails?.gramPanchayat?.code))) &&
      formData?.tripData?.vehicleType &&
      (formData?.tripData?.amountPerTrip || formData?.tripData?.amountPerTrip === 0)
    ) {
      setSubmitValve(true);
      const pitDetailValues = formData?.pitDetail ? Object.values(formData?.pitDetail).filter((value) => value > 0) : null;
      // let min = Digit.SessionStorage.get("advance_amount");
      if (formData?.pitType) {
        if (pitDetailValues === null || pitDetailValues?.length === 0) {
          setSubmitValve(true);
        } else if (isConventionalSpecticTank(formData?.pitType?.dimension) && pitDetailValues?.length >= 3) {
          setSubmitValve(true);
        } else if (!isConventionalSpecticTank(formData?.pitType?.dimension) && pitDetailValues?.length >= 2) {
          setSubmitValve(true);
        } else setSubmitValve(false);
      }
      /*if (formData?.tripData?.amountPerTrip !== 0 && (formData?.advancepaymentPreference?.advanceAmount > formData?.tripData?.amount || formData?.advancepaymentPreference?.advanceAmount < min)) {
        setSubmitValve(false);
      }
      if (applicationData?.advanceAmount > 0 && formData?.advancepaymentPreference?.advanceAmount <= 0) {
        setSubmitValve(false);
      } */
    } else {
      setSubmitValve(false);
    }
  };

  // useEffect(() => {
  //   (async () => {

  //   })();
  // }, [propertyType, subType, vehicle]);

  const onSubmit = (data) => {
    const applicationChannel = data.channel;
    const sanitationtype = data?.pitType?.code;
    const pitDimension = data?.pitDetail;
    const applicantName = data.applicationData.applicantName;
    const mobileNumber = data.applicationData.mobileNumber;
    const pincode = data?.address?.pincode;
    const street = data?.address?.street?.trim();
    const doorNo = data?.address?.doorNo?.trim();
    const slum = data?.address?.slum;
    const landmark = data?.address?.landmark?.trim();
    const noOfTrips = data.tripData.noOfTrips;
    const amount = data.tripData.amountPerTrip;
    const cityCode = data?.address?.city?.code;
    const city = data?.address?.city?.name;
    // const state = data?.address?.city?.state;
    const localityCode = data?.address?.locality?.code;
    const localityName = data?.address?.locality?.name;
    const propertyUsage = data?.subtype;
    // const advanceAmount = amount === 0 ? null : data?.advancepaymentPreference?.advanceAmount;
    const { height, length, width, diameter } = pitDimension;

    const advanceAmount =
      amount === 0
        ? null
        : data?.advancepaymentPreference?.advanceAmount
        ? data?.advancepaymentPreference?.advanceAmount
        : applicationData.advanceAmount;
    const totalAmount = amount * noOfTrips;
    const gramPanchayat = data?.address?.gramPanchayat || data?.address?.additionalDetails?.gramPanchayat;
    const village = data?.address?.village || data?.address?.additionalDetails?.village;
    const propertyLocation = data?.address?.propertyLocation?.code;
    const newGp = data?.address?.newGp || data?.address?.additionalDetails?.newGramPanchayat;
    const newVillage = data?.address?.newVillage || data?.address?.additionalDetails?.village;
    const newLocality = data?.address?.newLocality || data?.address?.additionalDetails?.newLocality;


    const formData = {
      ...applicationData,
      sanitationtype: sanitationtype,
      source: applicationChannel.code,
      additionalDetails: {
        ...applicationData.additionalDetails,
        tripAmount: typeof amount === "number" ? JSON.stringify(amount) : amount,
        totalAmount: typeof totalAmount === "number" ? JSON.stringify(totalAmount) : totalAmount,
      },
      propertyUsage,
      vehicleType: data.tripData.vehicleType.type,
      vehicleCapacity: data?.tripData?.vehicleType?.capacity,
      noOfTrips,
      pitDetail: {
        ...applicationData.pitDetail,
        distanceFromRoad: data.distanceFromRoad,
        height,
        length,
        width,
        diameter,
      },
      address: {
        ...applicationData.address,
        tenantId: cityCode,
        landmark,
        doorNo,
        street,
        pincode,
        slumName: slum,
        locality: {
          ...applicationData.address.locality,
          code: propertyLocation === "FROM_GRAM_PANCHAYAT" ? gramPanchayat?.code : localityCode,
          name: propertyLocation === "FROM_GRAM_PANCHAYAT" ? gramPanchayat?.name : localityName,
        },
        geoLocation: {
          ...applicationData.address.geoLocation,
          latitude: data?.address?.latitude ? data?.address?.latitude : applicationData.address.geoLocation.latitude,
          longitude: data?.address?.longitude ? data?.address?.longitude : applicationData.address.geoLocation.longitude,
        },
        additionalDetails: {
          boundaryType: propertyLocation === "FROM_GRAM_PANCHAYAT" ? "GP" : "Locality",
          gramPanchayat: {
            code: gramPanchayat?.code,
            name: gramPanchayat?.name,
          },
          village: village?.code
            ? {
                code: village?.code ? village?.code : "",
                code: village?.code ? village?.code : "",
              }
            : newVillage,
          newGramPanchayat: newGp,
          newLocality: newLocality,
        },
      },
      advanceAmount: typeof advanceAmount === "number" ? JSON.stringify(advanceAmount) : advanceAmount,
    };

    delete formData["responseInfo"];

    window.Digit.SessionStorage.set("propertyType", null);
    window.Digit.SessionStorage.set("subType", null);
    Digit.SessionStorage.set("city_property", null);
    Digit.SessionStorage.set("selected_localities", null);
    Digit.SessionStorage.set("locality_property", null);
    history.replace("/digit-ui/employee/fsm/response", {
      applicationData: formData,
      key: "update",
      action: applicationData?.applicationStatus === "CREATED" ? "SUBMIT" : "SCHEDULE",
    });
  };

  if (isLoading || isTripConfigLoading || isApplicantConfigLoading) {
    return <Loader />;
  }

  const configs = [...preFields, ...commonFields];
  console.log(configs,"configs");
  let conf = [
   
    {
      "head": "ES_NEW_APPLICATION_PROPERTY_DETAILS",
      "body": [
        {
          "label": "ES_NEW_APPLICATION_PROPERTY_ID",
          "isMandatory": true,
          "component": "CPTPropertySearchNSummary",
          "withoutLabel": true,
          "key": "cpt",
          "type": "component",
          "hideInCitizen": true
        },
        {
          "label": "ES_NEW_APPLICATION_PROPERTY_TYPE",
          "isMandatory": true,
          "type": "component",
          "route": "property-type",
          "key": "propertyType",
          "component": "SelectPropertyType",
          "texts": {
            "headerCaption": "",
            "header": "CS_FILE_APPLICATION_PROPERTY_LABEL",
            "cardText": "CS_FILE_APPLICATION_PROPERTY_TEXT",
            "submitBarLabel": "CS_COMMON_NEXT"
          },
          "nextStep": "property-subtype"
        },
        {
          "label": "ES_NEW_APPLICATION_PROPERTY_SUB-TYPE",
          "isMandatory": true,
          "type": "component",
          "route": "property-subtype",
          "key": "subtype",
          "component": "SelectPropertySubtype",
          "texts": {
            "headerCaption": "",
            "header": "CS_FILE_APPLICATION_PROPERTY_SUBTYPE_LABEL",
            "cardText": "CS_FILE_APPLICATION_PROPERTY_SUBTYPE_TEXT",
            "submitBarLabel": "CS_COMMON_NEXT"
          },
          "nextStep": "map"
        }
      ]
    },
    {
      "head": "ES_TITLE_APPLICANT_DETAILS",
      "body": [
        {
          "label": "ES_NEW_APPLICATION_APPLICATION_CHANNEL",
          "isMandatory": true,
          "type": "component",
          "key": "channel",
          "component": "SelectChannel",
          "nextStep": "applicantName"
        },
        {
          "type": "component",
          "key": "applicationData",
          "withoutLabel": true,
          "component": "SelectName"
        }
      ]
    },
    {
      "head": "ES_NEW_APPLICATION_LOCATION_DETAILS",
      "body": [
        {
          "route": "address",
          "component": "FSMSelectAddress",
          "withoutLabel": true,
          "texts": {
              "headerCaption": "CS_FILE_APPLICATION_PROPERTY_LOCATION_LABEL",
              "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
              "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_CITY_MOHALLA_TEXT",
              "submitBarLabel": "CS_COMMON_NEXT"
          },
          "key": "address",
          "nextStep": "locality-gramPanchayat",
          "isMandatory": true,
          "type": "component"
      },
      {
          "route": "locality-gramPanchayat",
          "component": "SelectLocalityOrGramPanchayat",
          "withoutLabel": true,
          "texts": {
              "headerCaption": "CS_FILE_APPLICATION_PROPERTY_LOCATION_LABEL",
              "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
              "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_CITY_MOHALLA_TEXT",
              "submitBarLabel": "CS_COMMON_NEXT"
          },
          "key": "address",
          "nextStep": "select-trip-number",
          "isMandatory": true,
          "type": "component"
      },
      {
          "route": "pincode",
          "component": "FSMSelectPincode",
          "texts": {
              "headerCaption": "",
              "header": "CS_FILE_APPLICATION_PINCODE_LABEL",
              "cardText": "CS_FILE_APPLICATION_PINCODE_TEXT",
              "submitBarLabel": "CS_COMMON_NEXT",
              "skipText": "CORE_COMMON_SKIP_CONTINUE"
          },
          "withoutLabel": true,
          "key": "address",
          "nextStep": "check-slum",
          "type": "component"
      },
        {
          "type": "component",
          "route": "check-slum",
          "isMandatory": true,
          "component": "CheckSlum",
          "texts": {
            "header": "ES_NEW_APPLICATION_SLUM_CHECK",
            "submitBarLabel": "CS_COMMON_NEXT"
          },
          "key": "address",
          "withoutLabel": true,
          "nextStep": "slum-details",
          "hideInEmployee": true
        },
        {
          "type": "component",
          "route": "slum-details",
          "isMandatory": true,
          "component": "SelectSlumName",
          "texts": {
            "header": "CS_NEW_APPLICATION_SLUM_NAME",
            "cardText": "CS_NEW_APPLICATION_SLUM_TEXT",
            "submitBarLabel": "CS_COMMON_NEXT"
          },
          "withoutLabel": true,
          "key": "address",
          "nextStep": "street"
        },
        {
          "type": "component",
          "route": "street",
          "component": "FSMSelectStreet",
          "key": "address",
          "withoutLabel": true,
          "texts": {
            "headerCaption": "CS_FILE_APPLICATION_PROPERTY_LOCATION_LABEL",
            "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
            "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_STREET_DOOR_NO_LABEL",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE"
          },
          "nextStep": "landmark"
        },
        {
          "type": "component",
          "route": "landmark",
          "component": "FSMSelectLandmark",
          "withoutLabel": true,
          "texts": {
            "headerCaption": "CS_FILE_APPLICATION_PROPERTY_LOCATION_LABEL",
            "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TITLE",
            "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TEXT",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE"
          },
          "key": "address",
          "nextStep": "pit-type"
        }
      ]
    },
    {
      "head": "CS_CHECK_PIT_SEPTIC_TANK_DETAILS",
      "body": [
        {
          "label": "ES_NEW_APPLICATION_ROAD_WIDTH",
          "isMandatory": true,
          "type": "component",
          "route": "road-details",
          "key": "roadWidth",
          "hideInEmployee": true,
          "component": "SelectRoadDetails",
          "texts": {
            "header": "CS_FILE_PROPERTY_ROAD_WIDTH",
            "cardText": "CS_FILE_PROPERTY_ROAD_WIDTH_TEXT",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE"
          },
          "nextStep": "tank-size"
        },
        {
          "label": "ES_NEW_APPLICATION_PIT_TYPE",
          "isMandatory": true,
          "type": "component",
          "route": "pit-type",
          "key": "pitType",
          "component": "SelectPitType",
          "texts": {
            "header": "CS_FILE_PROPERTY_PIT_TYPE",
            "cardText": "CS_FILE_PROPERTY_PIT_TYPE_TEXT",
            "submitBarLabel": "CS_COMMON_NEXT"
          },
          "nextStep": "road-details"
        },
        {
          "route": "tank-size",
          "component": "SelectTankSize",
          "isMandatory": false,
          "texts": {
            "headerCaption": "",
            "header": "CS_FILE_APPLICATION_PIT_SEPTIC_TANK_SIZE_TITLE",
            "cardText": "CS_FILE_APPLICATION_PIT_SEPTIC_TANK_SIZE_TEXT",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE"
          },
          "type": "component",
          "key": "pitDetail",
          "nextStep": "select-payment-preference",
          "label": "ES_NEW_APPLICATION_PIT_DIMENSION"
        },
        {
          "type": "component",
          "key": "tripData",
          "withoutLabel": true,
          "component": "SelectTrips"
        }
      ]
    },
    {
      "head": "CS_FILE_ADDITIONAL_DETAILS",
      "hideInEmployee": true,
      "body": [
        {
          "label": "a",
          "isMandatory": true,
          "type": "component",
          "route": "select-trip-number",
          "key": "selectTripNo",
          "component": "SelectTripNo",
          "hideInEmployee": true,
          "texts": {
            "headerCaption": "",
            "header": "ES_FSM_SERVICE_REQUEST",
            "cardText": "ES_FSM_SERVICE_REQUEST_TEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipLabel": "CS_COMMON_SERVICE_SKIP_INFO"
          },
          "nextStep": "property-id"
        },
        {
          "label": "a",
          "isMandatory": false,
          "type": "component",
          "route": "select-gender",
          "hideInEmployee": true,
          "key": "selectGender",
          "component": "SelectGender",
          "texts": {
            "headerCaption": "",
            "header": "CS_COMMON_CHOOSE_GENDER",
            "cardText": "CS_COMMON_SELECT_GENDER",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE"
          },
          "nextStep": "select-payment-preference"
        },
        {
          "label": "a",
          "isMandatory": false,
          "type": "component",
          "route": "select-payment-preference",
          "key": "selectPaymentPreference",
          "hideInEmployee": true,
          "component": "SelectPaymentPreference",
          "texts": {
            "headerCaption": "",
            "header": "ES_FSM_PAYMENT_PREFERENCE_LABEL",
            "cardText": "ES_FSM_PAYMENT_PREFERENCE_TEXT",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE"
          },
          "nextStep": null
        }
      ]
    },
    {
      "head": "ES_TITLE_PAYMENT_DETAILS",
      "body": [
        {
          "type": "component",
          "key": "tripData",
          "withoutLabel": true,
          "component": "SelectTripData"
        },
        {
          "type": "component",
          "key": "advancepaymentPreference",
          "withoutLabel": true,
          "component": "AdvanceCollection"
        }
      ]
    }
  ]
  return (
    // <>
    //   <div style={{ marginLeft: "15px" }}>
    //     <Header>{t("ES_TITLE_MODIFY_DESULDGING_APPLICATION")}</Header>
    //   </div>
      <FormComposer
        isDisabled={!canSubmit}
        label={applicationData?.applicationStatus != "CREATED" ? t("ES_FSM_APPLICATION_SCHEDULE") : t("ES_FSM_APPLICATION_UPDATE")}
        config={conf
          .filter((i) => !i.hideInEmployee)
          .map((config) => {
            return {
              ...config,
              body: config.body.filter((a) => !a.hideInEmployee),
            };
          })}
        fieldStyle={{ marginRight: 0 }}
        // formCardStyle={true}
        onSubmit={onSubmit}
        defaultValues={defaultValues}
        onFormValueChange={onFormValueChange}
        // noBreakLine={true}
        // fms_inline
      />
    // </>
  );
};

export default EditForm;
