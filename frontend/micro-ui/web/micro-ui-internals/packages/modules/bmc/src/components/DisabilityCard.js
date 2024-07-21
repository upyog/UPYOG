import { CardLabel, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import ToggleSwitch from "./Toggle";

const DisabilityCard = ({ tenantId, onUpdate, initialRows = {}, AllowEdit = false, ...props }) => {
  const { t } = useTranslation();
  const [isEditable, setIsEditable] = useState(AllowEdit);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const [rangeValue, setRangeValue] = useState(0);
  const [divyangs, setDivyangs] = useState([]);
  const headerLocale = Digit.Utils.locale.getTransformedLocale(tenantId);

  const initialDefaultValues = {
    divyangcardid: "",
    disabilitytype: "",
    divyangpercent: 0,
  };

  const {
    control,
    watch,
    formState: { errors, isValid },
    trigger,
    setValue,
    clearErrors
  } = useForm({
    defaultValues: initialDefaultValues,
  });

  const processCommonData = (data, headerLocale) => {
    return (
      data?.CommonDetails?.map((item) => ({
        id: item.id,
        name: item.name,
        i18nKey: `${headerLocale}_ADMIN_${item.name}`,
      })) || []
    );
  };

  const processSingleData = (item, headerLocale) => {
    if (!item) return null;
    if (typeof item === "object" && item.divyangid && item.divyangtype) {
      return {
        divyangtype: {
          id: item.divyangid,
          name: item.divyangtype,
          i18nKey: `${headerLocale}_ADMIN_${item.divyangtype}`,
        },
        divyangcardid: item.divyangcardid,
        divyangpercent: item.divyangpercent,
      };
    }
    return null; // Handle cases where item is neither a string nor an object with id and name
  };

  const handleChange = (e) => {
    setRangeValue(parseInt(e.target.value));
  };

  const divyangFunction = (data) => {
    const divyangData = processCommonData(data, headerLocale);
    setDivyangs(divyangData);
    return { divyangData };
  };

  const getDivyang = { CommonSearchCriteria: { Option: "divyang" } };
  Digit.Hooks.bmc.useCommonGet(getDivyang, { select: divyangFunction });

  const formValues = watch();

  useEffect(() => {
    if (initialRows) {
      const processeddata = processSingleData(initialRows, headerLocale);
      if (processeddata) {
        setValue("divyangcardid", processeddata.divyangcardid || "");
        setValue("disabilitytype", processeddata.divyangtype || "");
        setRangeValue(processeddata.divyangpercent || 0);

        // Clear errors for fields that received initial values
        if (processeddata.divyangcardid) clearErrors("divyangcardid");
        if (processeddata.divyangtype) clearErrors("disabilitytype");
        if (processeddata.divyangpercent) clearErrors("divyangpercent");
      }
    }
  }, [initialRows, setValue, headerLocale, clearErrors]);

  useEffect(() => {
    onUpdate(formValues, isValid);
  }, [formValues, isValid, onUpdate]);

  useEffect(() => {
    trigger(); // Validate the form on mount to show errors if fields are empty
  }, [trigger]);

  useEffect(() => {
    control.setValue("divyangpercent", rangeValue);
  }, [rangeValue, control]);

  const handleToggle = () => {
    setIsEditable(!isEditable);
  };

  return (
    <React.Fragment>
      <div className="bmc-row-card-header">
        <div className="bmc-card-row">
          <div className="bmc-card-row">
            <div className="bmc-col-large-header">
              <div className="bmc-title">{t("DISABILITY DETAILS")}</div>
            </div>
            <div className="bmc-col-small-header" style={{ textAlign: "end" }}>
              <ToggleSwitch
                id={"DisabilityToggle"}
                isOn={isEditable}
                handleToggle={handleToggle}
                onLabel="Yes"
                offLabel="No"
                disabled={!AllowEdit}
              />
            </div>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_UDID_ID")}</CardLabel>
              <Controller
                control={control}
                name={"divyangcardid"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      {...props}
                      disabled={!isEditable}
                      isMandatory={true}
                      placeholder={"Enter the udid ID"}
                      autoFocus={focusIndex.index === props.name}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: props.name });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                    {errors.divyangcardid && <span style={{ color: "red" }}>{errors.divyangcardid.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_DISABILITY_TYPE")}</CardLabel>
              <Controller
                control={control}
                name={"disabilitytype"}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                }}
                render={(props) => (
                  <div>
                    {isEditable ? (
                      <Dropdown
                        placeholder="SELECT THE DISABILITY TYPE"
                        selected={props.value}
                        select={(divyang) => props.onChange(divyang)}
                        onBlur={props.onBlur}
                        option={divyangs}
                        optionKey="i18nKey"
                        t={t}
                        isMandatory={true}
                      />
                    ) : (
                      <TextInput readOnly value={props.value?.name || ""} />
                    )}
                    {errors.disabilitytype && <span style={{ color: "red" }}>{errors.disabilitytype.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col2-card">
            <div className="bmc-range-container">
              <CardLabel className="bmc-label">{t("BMC_DISABILITY_PERCENTAGE")}</CardLabel>
              <Controller
                control={control}
                name={"divyangpercent"}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                }}
                render={(props) => (
                  <div>
                    <input
                      disabled={!isEditable}
                      type="range"
                      min="1"
                      max="100"
                      className="bmc-range-slider"
                      value={rangeValue}
                      onChange={(e) => {
                        handleChange(e);
                        props.onChange(e.target.value);
                      }}
                      list="tickmarks"
                    />
                    <datalist id="tickmarks">
                      {Array.from({ length: 100 }, (_, i) => (
                        <option key={i} value={i + 1}></option>
                      ))}
                    </datalist>
                    <span className="range-value">Disability: {rangeValue}%</span>
                    {errors.divyangpercent && <span style={{ color: "red" }}>{errors.divyangpercent.message}</span>}
                  </div>
                )}
              />
            </div>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default DisabilityCard;
