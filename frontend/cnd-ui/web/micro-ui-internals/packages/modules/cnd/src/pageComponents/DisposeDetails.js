import { CardLabel, CardLabelError, LabelFieldPair, TextInput, Toast,Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { cndStyles } from "../utils/cndStyles";
/**
 * DisposeDetails Component
 * 
 * This component renders a dynamic form to capture disposal details such as 
 * disposal date, disposal type, and disposal site name. It supports multiple
 * entries, handles form validation using react-hook-form, and updates the 
 * parent form data through the onSelect callback. Localization is handled via i18next.
 */
const disposalDetails = () => ({
    disposeDate: "",
    disposeType: "",
    disposalSiteName: "",
    key: Date.now(),
});

const DisposeDetails = ({ config, onSelect, formData, setError, clearErrors }) => {
  const { t } = useTranslation();
  const [disposeDetails, setdisposeDetails] = useState(formData?.disposeDetails || [disposalDetails()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  useEffect(() => {
    onSelect(config?.key, disposeDetails);
  }, [disposeDetails]);


  const disposalTypeOption=[
    {
        code:"CND_ON_SITE",
        i18nKey:"CND_ON_SITE",
        value:"On Site Disposal"
    },
    {
        code:"CND_SENDTO_DISPOSAL_SITE",
        i18nKey:"CND_SENDTO_DISPOSAL_SITE",
        value:"Send To Disposal Site/Recyclers"
    }
  ]


  const commonProps = {
    focusIndex,
    alldisposeDetails: disposeDetails,
    setFocusIndex,
    formData,
    setdisposeDetails,
    t,
    setError,
    clearErrors,
    config,
    disposalTypeOption
  };

  return (
    <React.Fragment>
      {disposeDetails.map((disposeDetails, index) => (
        <OwnerForm key={disposeDetails.key} index={index} disposeDetails={disposeDetails} {...commonProps} />
      ))}
    </React.Fragment>
  );
};

const OwnerForm = (_props) => {
  const {
    disposeDetails,
    focusIndex,
    setdisposeDetails,
    t,
    config,
    setError,
    clearErrors,
    setFocusIndex,
    disposalTypeOption
  } = _props;

  const [showToast, setShowToast] = useState(null);
  const { control, formState: localFormState, watch, trigger } = useForm();
  const formValue = watch();
  const { errors } = localFormState;
  const [part, setPart] = React.useState({});

  useEffect(() => {
    if (!_.isEqual(part, formValue)) {
      setPart({ ...formValue });
      setdisposeDetails((prev) => prev.map((o) => (o.key === disposeDetails.key ? { ...o, ...formValue } : o)));
      trigger();
    }
  }, [formValue]);

  useEffect(() => {
    if (Object.keys(errors).length && !_.isEqual(localFormState.errors[config.key]?.type || {}, errors)) setError(config.key, { type: errors });
    else if (!Object.keys(errors).length && localFormState.errors[config.key]) clearErrors(config.key);
  }, [errors]);

  return (
    <React.Fragment>
      <div style={cndStyles.siteMediaPhotoEmployee}>
        <div style={cndStyles.employeeSideContainer}>
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
                    autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "disposeDate"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: disposeDetails.key, type: "disposeDate" });
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
 
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("CND_DISPOSE_TYPE")} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"disposeType"}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  disable={false}
                  option={disposalTypeOption}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={cndStyles.errorStyle}>{localFormState.touched.disposeType ? errors?.disposeType?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("CND_DISPOSAL_SITE_NAME")} </CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"disposalSiteName"}
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
                    autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "disposalSiteName"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: disposeDetails.key, type: "disposalSiteName" });
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
          <CardLabelError style={cndStyles.errorStyle}>{localFormState.touched.disposalSiteName ? errors?.disposalSiteName?.message : ""}</CardLabelError>

          
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

export default DisposeDetails;
