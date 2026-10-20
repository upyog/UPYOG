import React, { useEffect, useState } from "react";
import { FormStep, CardLabel, TextInput, TextArea,Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import { getMinDeliveryTime, isTimeBeforeMin, isToTimeBeforeFromTime, getMinToTime, getMinDeliveryDate, getTimeErrorMessage, CustomToast } from "../utils";

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
  const [showToast, setShowToast] = useState(null);
  const tenantId=Digit.ULBService.getStateId();
  const inputStyles = {width:user.type === "EMPLOYEE" ? "52%" : "100%"};
   // Fetch noOfMobileToilet data from MDMS
   const { data: NoOfMobileToilet} = Digit.Hooks.useCustomMDMS(tenantId, "Request-Service", [{ name: "NoOfMobileToilet" }], {
    select: (data) => {
      const formattedData = data?.["Request-Service"]?.["NoOfMobileToilet"];
      return formattedData;
    },
  });

  let noOfMobileToilet =[];

  // Iterate over the noOfMobileToilet array and push data to the Vehicle array
  NoOfMobileToilet && NoOfMobileToilet.map((data) => {
    noOfMobileToilet.push({ i18nKey: `${data.code}`, code: `${data.code}`, value: `${data.code}`});
  });

  /**
   * Handles Delivery From Date selection.
   * Resets toDate and times if the date selection causes conflict or invalidates the 1-hour gap.
   *
   * @param {Object} e - Input change event containing selected from-date.
   */
  function SetdeliveryfromDate(e) {
    const selectedDate = e.target.value;
    setdeliveryfromDate(selectedDate);
    if (deliverytoDate && selectedDate > deliverytoDate) {
      setdeliverytoDate("");
      setdeliverytoTime("");
    }
    let currentFromTime = deliveryfromTime;
    if (isTimeBeforeMin(selectedDate, currentFromTime)) {
      setdeliveryfromTime("");
      currentFromTime = "";
    }
    if (isToTimeBeforeFromTime(selectedDate, deliverytoDate, currentFromTime, deliverytoTime)) {
      setdeliverytoTime("");
    }
  }

  /**
   * Handles Delivery To Date selection.
   * Resets deliverytoTime if it falls before the minimum allowed time or violates the 1-hour gap on the same day.
   *
   * @param {Object} e - Input change event containing selected to-date.
   */
  function SetdeliverytoDate(e) {
    const selectedDate = e.target.value;
    setdeliverytoDate(selectedDate);
    let currentToTime = deliverytoTime;
    if (isTimeBeforeMin(selectedDate, currentToTime)) {
      setdeliverytoTime("");
      currentToTime = "";
    }
    if (isToTimeBeforeFromTime(deliveryfromDate, selectedDate, deliveryfromTime, currentToTime)) {
      setdeliverytoTime("");
    }
  }
  function SetSpecialRequest(e) {
    setSpecialRequest(e.target.value);
  }

  /**
   * Requirement From Time input component.
   * Stores value on change and validates minimum delivery time on blur to support typing without focus loss.
   */
  const TimeInput = () => {
    return (
      <div className="flex items-center">
        <TextInput
          type="time"
          value={deliveryfromTime}
          style={inputStyles}
          onChange={(e) => setdeliveryfromTime(e.target.value)}
          onBlur={(e) => {
            const val = e.target.value;
            if (isTimeBeforeMin(deliveryfromDate, val)) {
              setdeliveryfromTime("");
              setShowToast({
                error: true,
                label: t(getTimeErrorMessage(deliveryfromDate, val)),
              });
            } else if (isToTimeBeforeFromTime(deliveryfromDate, deliverytoDate, val, deliverytoTime)) {
              setdeliverytoTime("");
              setShowToast({
                error: true,
                label: t("WT_TO_TIME_MUST_BE_AFTER_FROM_TIME"),
              });
            }
          }}
          min={getMinDeliveryTime(deliveryfromDate)}
          max="23:59"
        />
      </div>
    );
  };

  /**
   * Requirement To Time input component.
   * Stores value on change and validates minimum delivery time and 1-hour gap on blur.
   */
  const TimeInput2 = () => {
    return (
      <div className="flex items-center">
        <TextInput
          type="time"
          value={deliverytoTime}
          style={inputStyles}
          onChange={(e) => setdeliverytoTime(e.target.value)}
          onBlur={(e) => {
            const val = e.target.value;
            if (isTimeBeforeMin(deliverytoDate, val)) {
              setdeliverytoTime("");
              setShowToast({
                error: true,
                label: t(getTimeErrorMessage(deliverytoDate, val)),
              });
            } else if (isToTimeBeforeFromTime(deliveryfromDate, deliverytoDate, deliveryfromTime, val)) {
              setdeliverytoTime("");
              setShowToast({
                error: true,
                label: t("WT_TO_TIME_MUST_BE_AFTER_FROM_TIME"),
              });
            }
          }}
          min={getMinToTime(deliveryfromDate, deliverytoDate, deliveryfromTime)}
          max="23:59"
        />
      </div>
    );
  };

  /**
   * Validates delivery times before advancing form step.
   * Blocks submission and displays error toast if minimum time or 1-hour same-day gap is violated.
   */
  const goNext = () => {
    if (isTimeBeforeMin(deliveryfromDate, deliveryfromTime)) {
      setShowToast({
        error: true,
        label: t(getTimeErrorMessage(deliveryfromDate, deliveryfromTime)),
      });
      return;
    }
    if (isTimeBeforeMin(deliverytoDate, deliverytoTime)) {
      setShowToast({
        error: true,
        label: t(getTimeErrorMessage(deliverytoDate, deliverytoTime)),
      });
      return;
    }
    if (isToTimeBeforeFromTime(deliveryfromDate, deliverytoDate, deliveryfromTime, deliverytoTime)) {
      setShowToast({
        error: true,
        label: t("WT_TO_TIME_MUST_BE_AFTER_FROM_TIME"),
      });
      return;
    }
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
        isDisabled={!mobileToilet || !deliveryfromTime || !deliverytoTime || !deliveryfromDate || !deliverytoDate || !specialRequest || isTimeBeforeMin(deliveryfromDate, deliveryfromTime) || isTimeBeforeMin(deliverytoDate, deliverytoTime) || isToTimeBeforeFromTime(deliveryfromDate, deliverytoDate, deliveryfromTime, deliverytoTime)}
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
            min={getMinDeliveryDate()}
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
          {TimeInput()}
          <CardLabel>{`${t("MT_REQUIREMNENT_TO_TIME")}`} <span className="astericColor">*</span></CardLabel>
          {TimeInput2()}


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
          />

        </div>
      </FormStep>
      <CustomToast showToast={showToast} setShowToast={setShowToast} />
    </React.Fragment>
  );
};

export default ToiletRequestDetails;
