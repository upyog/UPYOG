import { CardLabel, CardLabelError, LabelFieldPair, TextInput, Toast,StatusTable, Row } from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useApplicationDetails } from "../pages/employee/Edit/ApplicationContext";
import { cndStyles } from "../utils/cndStyles";
/**
 * PickupArrivalDetails Component
 * 
 * This component handles the display and input of pickup details for a waste collection application.
 * It uses React Hook Form for form state management and dynamically renders forms based on the number of pickups.
 * Each form collects vehicle and driver details along with weights and dumping station info.
 * The component also displays key application details from the context for reference.
 */
const pickupDetails = () => ({
    disposeDate: "",
    vehicleNumber: "",
    vehicleDepoNumber: "",
    driverName: "",
    grossWeight: "",
    netWeight: "",
    dumpingStation: "",
    key: Date.now(),
});

const PickupArrivalDetails = ({ config, onSelect, formData, setError, clearErrors }) => {
  const { t } = useTranslation();
  const isEmployee = window.location.href.includes("/employee/cnd/cnd-service");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const applicationDetails = isEmployee ? useApplicationDetails():null;
  const [pickup, setpickup] = useState(formData?.pickup || [pickupDetails()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  /* Whenever the `pickup` state changes (like form updates or adding/removing entries),
    we call `onSelect` to pass the latest data back to the parent component or config handler.*/
  useEffect(() => {
    onSelect(config?.key, pickup);
  }, [pickup]);


  const commonProps = {
    focusIndex,
    allPickupWaste: pickup,
    setFocusIndex,
    formData,
    setpickup,
    t,
    setError,
    clearErrors,
    config,
    applicationDetails
  };

  return (
    <React.Fragment>
      {pickup.map((pickup, index) => (
        <OwnerForm key={pickup.key} index={index} pickup={pickup} {...commonProps} />
      ))}
    </React.Fragment>
  );
};


const OwnerForm = (_props) => {
  const {
    pickup,
    focusIndex,
    setpickup,
    t,
    config,
    setError,
    clearErrors,
    setFocusIndex,
    applicationDetails
  } = _props;

  const [showToast, setShowToast] = useState(null);
  const { control, formState: localFormState, watch, trigger } = useForm();
  const formValue = watch();
  const { errors } = localFormState;
  const [part, setPart] = React.useState({});

  /**
   * This useEffect tracks changes in the form values (`formValue`). If the form values differ from the previously saved state (`part`), 
   * it updates the main `pickup` list with the latest values for this particular pickup entry.
   * It also triggers validation for updated fields.
   */
  useEffect(() => {
    if (!_.isEqual(part, formValue)) {
      setPart({ ...formValue });
      setpickup((prev) => prev.map((o) => (o.key === pickup.key ? { ...o, ...formValue } : o)));
      trigger();
    }
  }, [formValue]);

  /**
   * This useEffect checks for form validation errors and sets or clears errors accordingly
   *  in the overall form state managed by the parent. It ensures proper validation feedback is shown.
   */
  useEffect(() => {
    if (Object.keys(errors).length && !_.isEqual(localFormState.errors[config.key]?.type || {}, errors)) setError(config.key, { type: errors });
    else if (!Object.keys(errors).length && localFormState.errors[config.key]) clearErrors(config.key);
  }, [errors]);

  return (
    <React.Fragment>
      <div style={cndStyles.siteMediaPhotoEmployee}>
        <div style={cndStyles.employeeSideContainer}>
            <StatusTable>
                <Row
                    className="border-none"
                    label={t("CND_APPLICATION_NUMBER")}
                    text={applicationDetails?.applicationNumber} 
                />
                <Row
                    className="border-none"
                    label={t("CND_APPLICATION_TYPE")}
                    text={t(applicationDetails?.applicationType)} 
                />
                <Row
                    className="border-none"
                    label={t("CND_WASTE_QUANTITY")}
                    text={applicationDetails?.totalWasteQuantity + " Tons"} 
                />
                <Row
                    className="border-none"
                    label={t("CND_TYPE_CONSTRUCTION")}
                    text={t(applicationDetails?.typeOfConstruction)} 
                />
                <Row
                    className="border-none"
                    label={t("CND_PROPERTY_USAGE")}
                    text={t(applicationDetails?.propertyType)} 
                />
                <Row
                    className="border-none"
                    label={t("CND_TIME_CONSTRUCTION")}
                    text={applicationDetails?.constructionFromDate + " to " + applicationDetails?.constructionToDate} 
                />
                <Row
                    className="border-none"
                    label={t("CND_SCHEDULE_PICKUP")}
                    text={applicationDetails?.requestedPickupDate} 
                />
                <Row
                    className="border-none"
                    label={t("CND_EMP_SCHEDULE_PICKUP")}
                    text={applicationDetails?.pickupDate} 
                />
                <Row
                    className="border-none"
                    label={t("CND_AREA_HOUSE")}
                    text={applicationDetails?.houseArea} 
                />
            </StatusTable>
         

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("CND_VEHICLE_NUMBER")} <span className="astericColor">*</span></CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"vehicleNumber"}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: { pattern: (val) => (/^[a-zA-Z0-9/-\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === pickup?.key && focusIndex.type === "vehicleNumber"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pickup.key, type: "vehicleNumber" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={cndStyles.errorStyle}>{localFormState.touched.vehicleNumber ? errors?.vehicleNumber?.message : ""}</CardLabelError>
 
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("CND_VEHICLE_DEPO")} <span className="astericColor">*</span></CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"vehicleDepoNumber"}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: { pattern: (val) => (/^[a-zA-Z0-9/-\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },

                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === pickup?.key && focusIndex.type === "vehicleDepoNumber"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pickup.key, type: "vehicleDepoNumber" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={cndStyles.errorStyle}>{localFormState.touched.vehicleDepoNumber ? errors?.vehicleDepoNumber?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("CND_DRIVER_NAME")} <span className="astericColor">*</span></CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"driverName"}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === pickup?.key && focusIndex.type === "driverName"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pickup.key, type: "driverName" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={cndStyles.errorStyle}>{localFormState.touched.driverName ? errors?.driverName?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("CND_GROSS_WEIGHT")} <span className="astericColor">*</span></CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"grossWeight"}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^\d{1,5}$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG")
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === pickup?.key && focusIndex.type === "grossWeight"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pickup.key, type: "grossWeight" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={cndStyles.errorStyle}>{localFormState.touched.grossWeight ? errors?.grossWeight?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("CND_NET_WEIGHT")} <span className="astericColor">*</span></CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"netWeight"}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^\d{1,5}$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG")
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === pickup?.key && focusIndex.type === "netWeight"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pickup.key, type: "netWeight" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={cndStyles.errorStyle}>{localFormState.touched.netWeight ? errors?.netWeight?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("CND_DUMPING_STATION")} <span className="astericColor">*</span></CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"dumpingStation"}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === pickup?.key && focusIndex.type === "dumpingStation"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pickup.key, type: "dumpingStation" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={cndStyles.errorStyle}>{localFormState.touched.dumpingStation ? errors?.dumpingStation?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("CND_DISPOSE_DATE")} <span className="astericColor">*</span></CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"disposeDate"}
                rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                  }}
                render={(props) => (
                  <TextInput
                    type={"date"}
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === pickup?.key && focusIndex.type === "disposeDate"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pickup.key, type: "disposeDate" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={cndStyles.errorStyle}>{localFormState.touched.disposeDate ? errors?.disposeDate?.message : ""}</CardLabelError>
        </div>
      </div>
      {showToast?.label && (
        <Toast
          label={showToast?.label}
          onClose={(w) => {
            setShowToast((x) => null);
          }}
        />
      )}
    </React.Fragment>
  );
};

export default PickupArrivalDetails;
