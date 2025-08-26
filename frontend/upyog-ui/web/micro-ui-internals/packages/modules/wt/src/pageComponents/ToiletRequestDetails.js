import React, { useEffect, useState } from "react";
import { FormStep, CardLabel, TextInput, TextArea,Dropdown } from "@nudmcdgnpm/digit-ui-react-components";

/* This page is developed for the Mobile Toilet Request Details page.
   It allows users to enter details such as the number of mobile toilets required, delivery dates, and special requests. */
   
const ToiletRequestDetails = ({ t, config, onSelect, userType, formData }) => {
  const user = Digit.UserService.getUser().info;
  const [mobileToilet, setMobileToilet] = useState(formData?.toiletRequestDetails?.mobileToilet || "");
  const [deliveryfromTime, setdeliveryfromTime] = useState(formData?.toiletRequestDetails?.deliveryfromTime || "");
  const [deliverytoTime, setdeliverytoTime] = useState(formData?.toiletRequestDetails?.deliverytoTime || "");
  const [deliveryfromDate, setdeliveryfromDate] = useState(formData?.toiletRequestDetails?.deliveryfromDate || "");
  const [deliverytoDate, setdeliverytoDate] = useState(formData?.toiletRequestDetails?.deliverytoDate || "");
  const [specialRequest, setSpecialRequest] = useState(formData?.toiletRequestDetails?.specialRequest || "");
  const tenantId=Digit.ULBService.getStateId();
  const inputStyles = {width:user.type === "EMPLOYEE" ? "52%" : "100%"};
   // Fetch noOfMobileToilet data from MDMS
   const { data: NoOfMobileToilet} = Digit.Hooks.useCustomMDMS(tenantId, "request-service", [{ name: "NoOfMobileToilet" }], {
    select: (data) => {
      const formattedData = data?.["request-service"]?.["NoOfMobileToilet"];
      return formattedData;
    },
  });

  let noOfMobileToilet =[];

  // Iterate over the noOfMobileToilet array and push data to the Vehicle array
  NoOfMobileToilet && NoOfMobileToilet.map((data) => {
    noOfMobileToilet.push({ i18nKey: `${data.code}`, code: `${data.code}`, value: `${data.code}`});
  });

  function SetdeliveryfromDate(e) {
    setdeliveryfromDate(e.target.value);
  }
  function SetdeliverytoDate(e) {
    setdeliverytoDate(e.target.value);
  }
  function SetSpecialRequest(e) {
    setSpecialRequest(e.target.value);
  }
  const TimeInput = () => {
    return (
      <div className="flex items-center">
        <TextInput
          type="time"
          value={deliveryfromTime}
          style={inputStyles}
          onChange={(e) => setdeliveryfromTime(e.target.value)}
          min="06:00"
          max="23:59"
        />
      </div>
    );
  };
  const TimeInput2 = () => {
    return (
      <div className="flex items-center">
        <TextInput
          type="time"
          value={deliverytoTime}
          style={inputStyles}
          onChange={(e) => setdeliverytoTime(e.target.value)}
          min="06:00"
          max="23:59"
        />
      </div>
    );
  };

  const goNext = () => {
    let toiletRequestDetails = formData.toiletRequestDetails;
    let Service = { ...toiletRequestDetails, mobileToilet, deliveryfromTime, deliverytoTime, deliveryfromDate, deliverytoDate, specialRequest };
    onSelect(config.key, Service, false);
  };

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [mobileToilet, deliveryfromTime, deliverytoTime, deliveryfromDate, deliverytoDate, specialRequest]);
  let validation = {};

  return (
    <React.Fragment>
      <FormStep
        config={config}
        onSelect={goNext}
        t={t}
        isDisabled={!mobileToilet || !deliveryfromTime || !deliverytoTime || !deliveryfromDate || !deliverytoDate || !specialRequest}
      >
        <div>
          <CardLabel>{t("MT_NUMBER_OF_MOBILE_TOILETS")} <span className="check-page-link-button">*</span></CardLabel>
           <Dropdown
              className="form-field"
              selected={mobileToilet}
              placeholder={"Select Number of Mobile Toilets"}
              select={setMobileToilet}
              option={noOfMobileToilet}
              style={inputStyles}
              optionKey="i18nKey"
              t={t}
            />

          <CardLabel>{`${t("MT_DELIVERY_FROM_DATE")}`} <span className="astericColor">*</span></CardLabel>
          <TextInput
            t={t}
            type={"date"}
            isMandatory={false}
            optionKey="i18nKey"
            name="deliveryDate"
            style={inputStyles}
            value={deliveryfromDate}
            onChange={SetdeliveryfromDate}
            min={new Date().toISOString().split('T')[0]}
            rules={{
              required: t("CORE_COMMON_REQUIRED_ERRMSG"),
              validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
            }}
          />
          <CardLabel>{`${t("MT_DELIVERY_TO_DATE")}`} <span className="astericColor">*</span></CardLabel>
          <TextInput
            t={t}
            type={"date"}
            isMandatory={false}
            optionKey="i18nKey"
            name="deliveryDate"
            value={deliverytoDate}
            style={inputStyles}
            onChange={SetdeliverytoDate}
            min={deliveryfromDate}
            rules={{
              required: t("CORE_COMMON_REQUIRED_ERRMSG"),
              validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
            }}
          />

          <CardLabel>{`${t("MT_REQUIREMNENT_FROM_TIME")}`} <span className="astericColor">*</span></CardLabel>
          <TimeInput />
          <CardLabel>{`${t("MT_REQUIREMNENT_TO_TIME")}`} <span className="astericColor">*</span></CardLabel>
          <TimeInput2 />


          <CardLabel>{`${t("MT_SPECIAL_REQUEST")}`} <span className="check-page-link-button"></span></CardLabel>
          <TextArea
            t={t}
            type="text"
            isMandatory={false}
            optionKey="i18nKey"
            name="mobileToilet"
            style={inputStyles}
            placeholder="Special Request"
            value={specialRequest}
            onChange={SetSpecialRequest}
            style={{ width: user.type === "EMPLOYEE" ? "51.6%" : null }}
          />

        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default ToiletRequestDetails;
