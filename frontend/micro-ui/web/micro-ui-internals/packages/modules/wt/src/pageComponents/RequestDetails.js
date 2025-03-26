import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons,CheckBox,Dropdown, TextArea } from "@nudmcdgnpm/digit-ui-react-components";

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
  const [deliveryDate, setdeliveryDate] = useState(formData?.requestDetails?.deliveryDate || "");
  const [description, setdescription] = useState(formData?.requestDetails?.description || "");
  const [deliveryTime, setdeliveryTime] = useState(formData?.requestDetails?.deliveryTime || "");
  const [extraCharge, setextraCharge] = useState(formData?.requestDetails?.extraCharge || false);
  const tenantId=Digit.ULBService.getStateId();
  
  // Fetch VehicleType data from MDMS
  const { data: VehicleType } = Digit.Hooks.useCustomMDMS(tenantId, "request-service", [{ name: "VehicleType" }], {
    select: (data) => {
      const formattedData = data?.["request-service"]?.["VehicleType"];
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

  let Vehicle = [];

  let tankerDetails =[];

  // Iterate over the TankerQuantity array and push data to the Vehicle array
  TankerDetails && TankerDetails.map((data) => {
    tankerDetails.push({ i18nKey: `${data.code}`, code: `${data.code}`, value: `${data.code}`});
  });
  
  // Iterate over the VehicleType  array and push data to the Vehicle array
  VehicleType && VehicleType.map((data) => {
    Vehicle.push({ i18nKey: `${data.capacity}`, code: `${data.capacity}`, value: `${data.capacity}`, vehicleType: data.vehicleType, capacityName: data.capacityName });
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
    let request = { ...requestDetails, tankerType, deliveryDate, tankerQuantity, waterQuantity, deliveryTime, description, extraCharge };
    onSelect(config.key, request, false);
  };

  const common = [
    {
      code: "TANKER",
      i18nKey: "TANKER",
      value: "Tanker"
    },
    {
      code: "TROLLEY",
      i18nKey: "TROLLEY",
      value: "Trolley"
    }
  ];

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [tankerType, deliveryDate, tankerQuantity, waterQuantity, deliveryTime, description,extraCharge]);

  return (
    <React.Fragment>
      <FormStep
        config={config}
        onSelect={goNext}
        t={t}
        isDisabled={!tankerType || !deliveryDate || !tankerQuantity || !waterQuantity || !deliveryTime || !description }
      >
        <div>
          <CardLabel>{`${t("WT_TANKER_TYPE")}`} <span className="astericColor">*</span></CardLabel>
          <RadioButtons
            t={t}
            options={common}
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
           <CardLabel>{`${t("WT_WATER_QUANTITY")}`} <span className="astericColor">*</span></CardLabel>
            <Dropdown
              className="form-field"
              selected={waterQuantity}
              placeholder={"Select Water Quantity"}
              select={setwaterQuantity}
              option={VehicleDetails}
              style={{width:"100%"}}
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
                style={{width:"100%"}}
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
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z ]+$",
              type: "tel",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
          />
          <div style={{ display: "flex", gap: "22px" }}>
            <CardLabel>{`${t("WT_IMMEDIATE")}`}<span className="astericColor"></span></CardLabel>
            <CheckBox
                onChange={setextrachargeHandler}
                checked={extraCharge}
            />
            </div>
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default RequestDetails;