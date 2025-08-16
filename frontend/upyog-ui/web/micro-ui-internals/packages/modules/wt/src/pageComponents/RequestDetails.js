import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons,CheckBox,Dropdown, TextArea } from "@upyog/digit-ui-react-components";

/**
 * Major Page which is developed for Request/Booking detail page
 * 
 */


const RequestDetails = ({ t, config, onSelect, userType, formData }) => {
  const user = Digit.UserService.getUser().info;
  let validation = {};

  const [tankerType, settankerType] = useState(formData?.requestDetails?.tankerType  || "");
  const [tankerQuantity, settankerQuantity] = useState(formData?.requestDetails?.tankerQuantity || "");
  const [waterQuantity, setwaterQuantity] = useState(formData?.requestDetails?.waterQuantity || "");
  const [waterType, setWaterType] = useState(formData?.requestDetails?.waterType || "");
  const [deliveryDate, setdeliveryDate] = useState(formData?.requestDetails?.deliveryDate || "");
  const [description, setdescription] = useState(formData?.requestDetails?.description || "");
  const [deliveryTime, setdeliveryTime] = useState(formData?.requestDetails?.deliveryTime || "");
  const [extraCharge, setextraCharge] = useState(formData?.requestDetails?.extraCharge || false);
  const tenantId=Digit.ULBService.getStateId();
  const inputStyles = {width:user.type === "EMPLOYEE" ? "50%" : "100%"};
  
  // Fetch VehicleType data from MDMS
  const { data: VehicleType } = Digit.Hooks.useCustomMDMS(tenantId, "request-service", [{ name: "VehicleType" }], {
    select: (data) => {
      const formattedData = data?.["request-service"]?.["VehicleType"];
      return formattedData;
    },
  });

  // Fetch TankerType data from MDMS
  const { data: TankerType } = Digit.Hooks.useCustomMDMS(tenantId, "request-service", [{ name: "TankerType" }], {
    select: (data) => {
      const formattedData = data?.["request-service"]?.["TankerType"];
      return formattedData;
    },
  });


  // Fetch TankerQuantity data from MDMS
  const { data: TankerDetails} = Digit.Hooks.useCustomMDMS(tenantId, "request-service", [{ name: "TankerQuantity" }], {
    select: (data) => {
      const formattedData = data?.["request-service"]?.["TankerQuantity"];
      return formattedData;
    },
  });

   // Fetch WaterType data from MDMS
   const { data: WaterTypeData} = Digit.Hooks.useCustomMDMS(tenantId, "Request-service", [{ name: "WaterType" }], {
    select: (data) => {
      const formattedData = data?.["Request-service"]?.["WaterType"];
      return formattedData;
    },
  });

  let Vehicle = [];

  let tankerDetails =[];

  let tankerTypeDetails = [];

  let WaterType = [];
  
  // Iterate over the WaterType array and push data to the WaterType array
  WaterTypeData && WaterTypeData.map((data) => {
    WaterType.push({ i18nKey: `${data.code}`, code: `${data.code}`, value: `${data.code}`});
  });

  // Iterate over the TankerQuantity array and push data to the Vehicle array
  TankerDetails && TankerDetails.map((data) => {
    tankerDetails.push({ i18nKey: `${data.code}`, code: `${data.code}`, value: `${data.code}`});
  });
  
  // Iterate over the VehicleType  array and push data to the Vehicle array
  VehicleType && VehicleType.map((data) => {
    Vehicle.push({ i18nKey: `${data.capacity}`, code: `${data.capacity}`, value: `${data.capacity}`, vehicleType: data.vehicleType, capacityName: data.capacityName });
  });

  // Iterate over the TankerType  array and push data to the tankerTypeDetails array
  TankerType && TankerType.map((data) => {
    tankerTypeDetails.push({ i18nKey: `${data.i18nKey}`, code: `${data.code}`, value: `${data.value}`});
  });

// Iterate over the Vehicle array, check if tankerType.code matches vehicleType, and return data
  const VehicleDetails = Vehicle.map((data) => {
    if (tankerType.code === data.vehicleType) {
      return {
        i18nKey: data.capacityName,
        code: data.code,
        value: data.capacityName
      };
    }
  }).filter(item => item !== undefined); // Remove undefined values from the array


  // Custom time input component
  const TimeInput = () => {
    return (
      <div className="flex items-center">
        <TextInput
          type="time"
          value={deliveryTime}
          style={inputStyles}
          onChange={(e) => setdeliveryTime(e.target.value)}
          min="06:00"
          max="23:59"
        />
      </div>
    );
  };


  const setextrachargeHandler = () => {
    setextraCharge(!extraCharge);
  }

  function setDescription(e) {
    setdescription(e.target.value);
  }
  function setDeliveryDate(e) {
    setdeliveryDate(e.target.value);
  }

  const goNext = () => {
    let requestDetails = formData.requestDetails;
    let request = { ...requestDetails, tankerType, deliveryDate, tankerQuantity,waterType, waterQuantity, deliveryTime, description, extraCharge };
    onSelect(config.key, request, false);
  };

  useEffect(() => {
    if (!tankerType && tankerTypeDetails?.length) {
      settankerType(tankerTypeDetails[0]);
    }
  }, [tankerTypeDetails]);

  return (
    <React.Fragment>
      <FormStep
        config={config}
        onSelect={goNext}
        t={t}
        isDisabled={!tankerType || !deliveryDate || !tankerQuantity || !waterQuantity || !deliveryTime || !description || !waterType}
      >
        <div>
          <CardLabel>{`${t("WT_TANKER_TYPE")}`} <span className="astericColor">*</span></CardLabel>
          <RadioButtons
            t={t}
            options={tankerTypeDetails}
            style={{ display: "flex", flexWrap: "wrap", maxHeight: "30px" }}
            innerStyles={{ minWidth: "24%" }}
            optionsKey="i18nKey"
            name={`tankerType`}
            value={tankerType}
            selectedOption={tankerType}
            onSelect={settankerType}
            labelKey="i18nKey"
            isPTFlow={true}
          />
          <CardLabel>{`${t("WT_WATER_TYPE")}`} <span className="astericColor">*</span></CardLabel>
            <Dropdown
              className="form-field"
              selected={waterType}
              placeholder={t("WT_SELECT_WATER_TYPE")}
              select={setWaterType}
              option={WaterType}
              style={inputStyles}
              optionKey="i18nKey"
              t={t}
            />
           <CardLabel>{`${t("WT_WATER_QUANTITY")}`} <span className="astericColor">*</span></CardLabel>
            <Dropdown
              className="form-field"
              selected={waterQuantity}
              placeholder={"Select Water Quantity"}
              select={setwaterQuantity}
              option={VehicleDetails}
              style={inputStyles}
              optionKey="i18nKey"
              t={t}
            />
          <CardLabel>{`${t("WT_TANKER_QUANTITY")}`} <span className="astericColor">*</span></CardLabel>
            <Dropdown
                className="form-field"
                selected={tankerQuantity}
                placeholder={"Select Tanker Quantity"}
                select={settankerQuantity}
                option={tankerDetails}
                style={inputStyles}
                optionKey="i18nKey"
                t={t}
              />
          <CardLabel>{`${t("WT_DELIVERY_DATE")}`} <span className="astericColor">*</span></CardLabel>
          <TextInput
            t={t}
            type={"date"}
            isMandatory={false}
            optionKey="i18nKey"
            name="deliveryDate"
            style={inputStyles}
            value={deliveryDate}
            onChange={setDeliveryDate}
            min={new Date().toISOString().split('T')[0]}
            rules={{
              required: t("CORE_COMMON_REQUIRED_ERRMSG"),
              validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
            }}
          />

          <CardLabel>{`${t("WT_DELIVERY_TIME")}`} <span className="astericColor">*</span></CardLabel>
          <TimeInput />

          <CardLabel>{`${t("WT_DESCRIPTION")}`} <span className="astericColor">*</span></CardLabel>
          <TextArea
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="description"
            value={description}
            onChange={setDescription}
            style={inputStyles}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z ]+$",
              type: "tel",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
          />
           <CheckBox
              label={t("WT_IMMEDIATE")}
              onChange={setextrachargeHandler}
              checked={extraCharge}
            />
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default RequestDetails;