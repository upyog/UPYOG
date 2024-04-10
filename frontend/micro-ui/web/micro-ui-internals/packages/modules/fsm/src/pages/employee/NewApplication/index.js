import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { FormComposer, Loader, Header } from "@upyog/digit-ui-react-components";
import { useHistory } from "react-router-dom";

const isConventionalSpecticTank = (tankDimension) => tankDimension === "lbd";

export const NewApplication = ({ parentUrl, heading }) => {
  // const __initPropertyType__ = window.Digit.SessionStorage.get("propertyType");
  // const __initSubType__ = window.Digit.SessionStorage.get("subType");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  // const { data: commonFields, isLoading } = useQuery('newConfig', () => fetch(`http://localhost:3002/commonFields`).then(res => res.json()))
  // const { data: postFields, isLoading: isTripConfigLoading } = useQuery('tripConfig', () => fetch(`http://localhost:3002/tripDetails`).then(res => res.json()))
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
  // const { data: vehicleMenu } = Digit.Hooks.fsm.useMDMS(state, "Vehicle", "VehicleType", { staleTime: Infinity });
  // const { data: channelMenu } = Digit.Hooks.fsm.useMDMS(tenantId, "FSM", "EmployeeApplicationChannel");
  const { t } = useTranslation();
  const history = useHistory();

  const [canSubmit, setSubmitValve] = useState(false);
  // const [channel, setChannel] = useState(null);

  const defaultValues = {
    tripData: {
      noOfTrips: 1,
      amountPerTrip: null,
      amount: null,
    },
  };

  const onFormValueChange = (setValue, formData) => {
    console.log("ProID", formData)
    if (
      formData?.pitType!==undefined &&
      formData?.tripData?.vehicleType &&
      formData?.tripData?.roadWidth!==undefined &&
      formData?.tripData?.distancefromroad!==undefined &&
      formData?. address?.street &&
      formData?.address?.doorNo &&
      formData?.propertyType &&
      formData?.subtype &&
      formData?.address?.locality?.code || formData?.cpt?.details?.address?.locality?.code &&
      formData?.tripData?.vehicleType &&
      formData?.channel &&
      formData?.pitType &&
      
      
      (formData?.tripData?.amountPerTrip || formData?.tripData?.amountPerTrip === 0 || formData?.tripData?.undefined?.amountPerTrip)
    ) {
      setSubmitValve(true);
      console.log("cansub", canSubmit)
      const pitDetailValues = formData?.pitDetail ? Object.values(formData?.pitDetail).filter((value) => value > 0) : null;
      let max = Digit.SessionStorage.get("total_amount");
      let min = Digit.SessionStorage.get("advance_amount");
      if (formData?.pitType) {
        if (pitDetailValues === null || pitDetailValues?.length === 0) {
          setSubmitValve(true);
        } else if (isConventionalSpecticTank(formData?.pitType?.dimension) && pitDetailValues?.length >= 3) {
          setSubmitValve(true);
        } else if (!isConventionalSpecticTank(formData?.pitType?.dimension) && pitDetailValues?.length >= 2) {
          setSubmitValve(true);
        } else setSubmitValve(false);
      }
      if (
        formData?.tripData?.amountPerTrip !== 0 &&
        (formData?.advancepaymentPreference?.advanceAmount < min ||
          formData?.advancepaymentPreference?.advanceAmount > max ||
          formData?.advancepaymentPreference?.advanceAmount === "")
      ) {
        setSubmitValve(false);
      }
    } else {
      setSubmitValve(false);
    }
  };

  // useEffect(() => {
  //   (async () => {

  //   })();
  // }, [propertyType, subType, vehicle]);

  const onSubmit = (data) => {
    console.log("data",data)
    const applicationChannel = data.channel;
    const sanitationtype = data?.pitType?.code;
    const pitDimension = data?.pitDetail;
    const applicantName = data.applicationData.applicantName;
    const mobileNumber = data.applicationData.mobileNumber;
    const emailId = data.applicationData.emailId;
    const pincode = data?.address?.pincode;
    const street = data?.address?.street?.trim();
    const doorNo = data?.address?.doorNo?.trim();
    const slum = data?.address?.slum;
    const landmark = data?.address?.landmark?.trim();
    const propertyID = data?.propertyID?.propertyID;
    const noOfTrips = data?.tripData?.noOfTrips;
    const amount = data.tripData.amount || data?.tripData?.undefined?.amount;
    const cityCode = data?.address?.city?.code;
    const city = data?.address?.city?.name;
    const state = data?.address?.city?.state;
    const localityCode = data.cpt?.details?.address?.locality?.code || data?.address?.locality?.code ;
    const localityName = data.cpt?.details?.address?.locality?.name || data?.address?.locality?.name;
    const gender = data.applicationData.applicantGender;
    const paymentPreference = amount === 0 ? null : data?.paymentPreference ? data?.paymentPreference : null;
    const advanceAmount = amount === 0 ? null : data?.advancepaymentPreference?.advanceAmount;
    const distancefromroad=data?.tripData?.distancefromroad;
    const roadWidth= data?.tripData?.roadWidth;
    const formData = {
      fsm: {
        citizen: {
          name: applicantName,
          mobileNumber,
          gender: gender,
          emailId:emailId
        },
        tenantId: tenantId,
        sanitationtype: sanitationtype,
        source: applicationChannel.code,
        additionalDetails: {
          tripAmount: amount,
          distancefromroad:distancefromroad,
          roadWidth:roadWidth,
          propertyID : propertyID,
        },
        propertyUsage: data?.subtype,
        vehicleCapacity: data?.tripData?.vehicleType?.capacity,
        pitDetail: {
          ...pitDimension,
          distanceFromRoad: data?.distanceFromRoad,
        },
        address: {
          tenantId: cityCode,
          landmark,
          doorNo,
          street,
          city,
          state,
          pincode,
          slumName: slum,
          locality: {
            code: localityCode,
            name: localityName,
          },
          geoLocation: {
            latitude: data?.address?.latitude,
            longitude: data?.address?.longitude,
          },
        },
        noOfTrips,
        paymentPreference,
        advanceAmount,
      },
      workflow: null,
    };
    console.log("formdataa", formData)

    window.Digit.SessionStorage.set("propertyType", null);
    window.Digit.SessionStorage.set("subType", null);
    Digit.SessionStorage.set("city_property", null);
    Digit.SessionStorage.set("selected_localities", null);
    Digit.SessionStorage.set("locality_property", null);
    history.push("/digit-ui/employee/fsm/response", formData);
  };

  if (isLoading || isTripConfigLoading || isApplicantConfigLoading) {
    return <Loader />;
  }

  const configs = [...preFields, ...commonFields];
  let conf =[
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
        "head": "ES_NEW_APPLICATION_PROPERTY_DETAILS",
        "body": [
          {"label": "ES_NEW_APPLICATION_PROPERTY_ID",
          "isMandatory": true,
            component: "CPTPropertySearchNSummary",
            withoutLabel: true,
            key: "cpt",
            type: "component",
            hideInCitizen: true
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
        "head": "ES_NEW_APPLICATION_LOCATION_DETAILS",
        "body": [
            {
                "route": "map",
                "component": "FSMSelectGeolocation",
                "nextStep": "pincode",
                "hideInEmployee": true,
                "key": "address"
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
                "nextStep": "address",
                "type": "component"
            },
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
            // {
            //     "route": "locality-gramPanchayat",
            //     "component": "SelectLocalityOrGramPanchayat",
            //     "withoutLabel": true,
            //     "texts": {
            //         "headerCaption": "CS_FILE_APPLICATION_PROPERTY_LOCATION_LABEL",
            //         "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
            //         "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_CITY_MOHALLA_TEXT",
            //         "submitBarLabel": "CS_COMMON_NEXT"
            //     },
            //     "key": "address",
            //     "nextStep": "check-slum",
            //     "isMandatory": true,
            //     "type": "component"
            // },
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
  console.log("configs",configs)
  return (
    <React.Fragment>
      <div style={{ marginLeft: "15px" }}>
        <Header>{t("ES_TITLE_NEW_DESULDGING_APPLICATION")}</Header>
      </div>
      <FormComposer
        isDisabled={!canSubmit}
        label={t("ES_COMMON_APPLICATION_SUBMIT")}
        config={conf
          .filter((i) => !i.hideInEmployee)
          .map((config) => {
            return {
              ...config,
              body: config.body.filter((a) => !a.hideInEmployee),
            };
          })}
        fieldStyle={{ marginRight: 0 }}
        formCardStyle={true}
        onSubmit={onSubmit}
        defaultValues={defaultValues}
        onFormValueChange={onFormValueChange}
        noBreakLine={true}
        fms_inline
      />
    </React.Fragment>
  );
};
