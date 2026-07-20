
import {
  CardLabel,
  DatePicker,
  LabelFieldPair,
  CardLabelError,
  CardSubHeader,
  RadioButtons,
  TextArea,
  TextInput
} from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { stringReplaceAll } from "../utils";
import { useForm, Controller } from "react-hook-form";
import { useTranslation } from "react-i18next";
import * as func from "../utils/";

const createDisConnectionAppDetails = () => [{
  consumerNumber: "",
  disConnectionType: "",
  disConnectionProposeDate: "",
  disConnectionReason: ""
}];
const WSDisconnectionAppDetails = ({
  config,
  onSelect,
  userType,
  formData,
  setError,
  formState,
  clearErrors
}) => {
  const {
    t
  } = useTranslation();
  const [disConnectionDetails, setDisConnectionDetails] = useState(formData?.disConnectionDetails || [createDisConnectionAppDetails()]);
  const [focusIndex, setFocusIndex] = useState({
    index: -1,
    type: ""
  });
  const [isErrors, setIsErrors] = useState(false);
  const [disconnectionTypeList, setDisconnectionTypeList] = useState([]);
  const stateCode = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const {
    isMdmsLoading,
    data: mdmsData
  } = Digit.Hooks.ws.useMDMS(stateCode, "ws-services-masters", ["disconnectionType"]);
  const {
    isLoading: slaLoading,
    data: slaData
  } = Digit.Hooks.ws.useDisconnectionWorkflow({
    tenantId
  });
  useEffect(() => {
    const disconnectionTypes = mdmsData?.["ws-services-masters"]?.disconnectionType || [];
    disconnectionTypes?.forEach(data => {
      data.code = data?.code?.toUpperCase();
      data.i18nKey = `WS_DISCONNECTIONTYPE_${data?.code?.toUpperCase()}`;
      data.value = data?.code?.toUpperCase();
      data.name = data?.code?.toUpperCase();
    });
    setDisconnectionTypeList(disconnectionTypes);
  }, [mdmsData]);
  useEffect(() => {
    const data = disConnectionDetails.map(e => {
      return e;
    });
    onSelect(config?.key, data);
  }, [disConnectionDetails]);
  const commonProps = {
    focusIndex,
    setFocusIndex,
    t,
    config,
    setError,
    clearErrors,
    formState,
    disConnectionDetails,
    setIsErrors,
    setDisConnectionDetails,
    disconnectionTypeList
  };
  return <React.Fragment>
      {disConnectionDetails.map((disConnectionDetail, index) => <PlumberDetails key={disConnectionDetail.key} index={index} disConnectionDetail={disConnectionDetail} {...commonProps} />)}
    </React.Fragment>;
};
const PlumberDetails = _props => {
  const {
    disConnectionDetail,
    focusIndex,
    setFocusIndex,
    t,
    config,
    setError,
    clearErrors,
    formState,
    disConnectionDetails,
    setIsErrors,
    options,
    setDisConnectionDetails,
    disconnectionTypeList
  } = _props;
  const {
    control,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    getValues
  } = useForm();
  const formValue = watch();
  const {
    errors
  } = localFormState;
  const isMobile = window.Digit.Utils.browser.isMobile();
  const isEmployee = window.location.href.includes("/employee");
  useEffect(() => {
    trigger();
  }, []);
  useEffect(() => {
    if (Object.entries(formValue).length > 0) {
      const keys = Object.keys(formValue);
      const part = {};
      keys.forEach(key => part[key] = disConnectionDetail[key]);
      if (!_.isEqual(formValue, part)) {
        let isErrorsFound = true;
        Object.keys(formValue).map(data => {
          if (!formValue[data] && isErrorsFound) {
            isErrorsFound = false;
            setIsErrors(false);
          }
        });
        if (isErrorsFound) setIsErrors(true);
        let ob = [{
          ...formValue
        }];
        setDisConnectionDetails(ob);
        trigger();
      }
    }
  }, [formValue, disConnectionDetails]);
  useEffect(() => {
    if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors)) {
      setError(config.key, {
        type: errors
      });
    } else if (!Object.keys(errors).length && formState.errors[config.key]) {
      clearErrors(config.key);
    }
  }, [errors]);
  const errorStyle = {
    width: "70%",
    marginLeft: "30%",
    fontSize: "12px",
    marginTop: "-21px"
  };
  return <div>
      <div className="ws-auto-59">
        <CardSubHeader className="ws-auto-60">{t("WS_APPLICATION_DETAILS")}</CardSubHeader>
        <LabelFieldPair>
          <CardLabel style={isMobile && isEmployee ? {
          fontWeight: "700",
          width: "100%"
        } : {
          marginTop: "-5px",
          fontWeight: "700"
        }} className="card-label-smaller">{`${t("WS_ACK_COMMON_APP_NO_LABEL")}`}<span className="check-page-link-button"> *</span></CardLabel>
          <div className="field">
            <Controller
              control={control}
              name="consumerNumber"
              defaultValue={disConnectionDetail?.consumerNumber}
              rules={{ required: t("REQUIRED_FIELD") }}
              isMandatory={true}
              render={({ field }) => (
                <TextInput
                  value={field.value}
                  autoFocus={focusIndex.index === disConnectionDetail?.key && focusIndex.type === "consumerNumber"}
                  errorStyle={(localFormState.touchedFields.consumerNumber && errors?.consumerNumber?.message) ? true : false}
                  onChange={(e) => {
                    field.onChange(e.target.value);
                    setFocusIndex({ index: disConnectionDetail?.key, type: "consumerNumber" });
                  }}
                  labelStyle={{ marginTop: "unset" }}
                  onBlur={field.onBlur}
                  className="ws-auto-61"
                />
              )}
            />
          </div>
        </LabelFieldPair>
        <CardLabel style={isMobile && isEmployee ? {fontWeight: "700", width:"100%"} : { marginTop: "-5px", fontWeight: "700" }} className="card-label-smaller">{`${t("WS_DISCONNECTION_TYPE")}`}</CardLabel>
            <Controller
          control={control}
          name="disConnectionType"
          defaultValue={disConnectionDetail?.disConnectionType}
          rules={{ required: t("REQUIRED_FIELD") }}
          isMandatory={true}
          render={({ field }) => (
            <RadioButtons
              t={t}
              options={disconnectionTypeList}
              optionsKey="i18nKey"
              value={{
                name: disConnectionDetail?.disConnectionType,
                value: disConnectionDetail?.disConnectionType,
                code: disConnectionDetail?.disConnectionType,
                i18nKey: `WS_DISCONNECTIONTYPE_${stringReplaceAll(disConnectionDetail?.disConnectionType, " ", "_")}`
              }}
              selectedOption={{
                name: disConnectionDetail?.disConnectionType,
                value: disConnectionDetail?.disConnectionType,
                code: disConnectionDetail?.disConnectionType,
                i18nKey: `WS_DISCONNECTIONTYPE_${stringReplaceAll(disConnectionDetail?.disConnectionType, " ", "_")}`
              }}
              // onSelect={(e) => onDisconnectionChange(e)}
              onSelect={(e) => {
                field.onChange(e.code)
              }}
              labelKey="WS_DISCONNECTIONTYPE"
              errorStyle={localFormState.touchedFields.disConnectionType && errors?.disConnectionType?.message ? true : false}
              autoFocus={focusIndex.index === disConnectionDetail?.key && focusIndex.type === "disConnectionType"}
               className="ws-auto-62"
              isDependent={true}
            />
          )}
        />
        <LabelFieldPair>
          <CardLabel style={isMobile && isEmployee ? {
          fontWeight: "700",
          width: "100%"
        } : {
          marginTop: "-5px",
          fontWeight: "700"
        }} className="card-label-smaller">{`${t("WS_DISCONNECTION_PROPOSED_DATE")}`}<span className="check-page-link-button"> *</span></CardLabel>
          <div className="field">
            <Controller
              name="disConnectionProposeDate"
              rules={{ required: t("REQUIRED_FIELD") }}
              // isMandatory={true}
              defaultValue={disConnectionDetail?.disConnectionProposeDate}
              control={control}
              render={({ field }) => (
                <DatePicker
                  date={field.value}
                  name="disConnectionProposeDate"
                  onChange={field.onChange}
                />
              )}
            />
          </div>
        </LabelFieldPair>
        <CardLabelError style={errorStyle}>{localFormState.touchedFields.disConnectionProposeDate ? errors?.disConnectionProposeDate?.message : ""}</CardLabelError>
        <LabelFieldPair>
          <CardLabel style={isMobile && isEmployee ? {
          fontWeight: "700",
          width: "100%"
        } : {
          marginTop: "-5px",
          fontWeight: "700"
        }} className="card-label-smaller">{`${t("WS_DISCONNECTION_REASON")}`}<span className="check-page-link-button"> *</span></CardLabel>
          <div className="field">
            <Controller
              control={control}
              defaultValue={disConnectionDetail?.disConnectionReason}
              name={"disConnectionReason"}
              rules={{ required: t("REQUIRED_FIELD") }}
              render={({ field }) => (
                <TextArea
                  t={t}
                  type={"text"}
                  isMandatory={false}
                  name={"disConnectionReason"}
                  value={field.value}
                  errorStyle={localFormState.touchedFields.disConnectionReason && errors?.disConnectionReason?.message ? true : false}
                  autoFocus={focusIndex.index === disConnectionDetail?.key && focusIndex.type === "disConnectionReason"}
                  onChange={(e) => {
                    field.onChange(e);
                    setFocusIndex({ index: disConnectionDetail.key, type: "disConnectionReason" });
                  }}
                  onBlur={field.onBlur}
                />
              )}
            ></Controller>
          </div>
        </LabelFieldPair>
        <CardLabelError style={errorStyle}>{localFormState.touchedFields.disConnectionReason ? errors?.disConnectionReason?.message : ""}</CardLabelError>
      </div>
    </div>;
};
export default WSDisconnectionAppDetails;
