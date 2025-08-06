import { CardLabel, FormStep, TextInput, Dropdown, DatePicker, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import _ from "lodash";
import { CND_VARIABLES } from "../utils";
import { useApplicationDetails } from "../pages/employee/Edit/ApplicationContext";
import { convertToObject } from "../utils";
import { cndStyles } from "../utils/cndStyles";

/**
* PropertyNature component for collecting property details including house area,
* construction period, and property usage type. Part of a multi-step form that
* handles form validation, data persistence, and API calls to fetch property usage types.
*/
const PropertyNature = ({ t, config, onSelect, formData }) => {
  let validation = {};
  const isEmployee = window.location.href.includes("/employee/cnd/cnd-service");
  const applicationDetails = isEmployee ? useApplicationDetails():null;
  const userType = Digit.UserService.getUser().info.type;
  const { control, watch, trigger } = useForm();
  const inputStyles = userType === "EMPLOYEE" ? cndStyles.employeeFields:cndStyles.citizenWidth;
  const [houseArea, sethouseArea] = useState(formData?.propertyNature?.houseArea || applicationDetails?.houseArea || "");
  const [constructionType, setconstructionType] = useState(formData?.propertyNature?.constructionType || convertToObject(applicationDetails?.typeOfConstruction) || "");
  const [propertyUsage, setpropertyUsage] = useState(formData?.propertyNature?.propertyUsage || convertToObject(applicationDetails?.propertyType) || "");
  const [constructionFrom, setConstructionFrom] = useState(formData?.propertyNature?.constructionFrom || applicationDetails?.constructionFromDate || null);
  const [constructionTo, setConstructionTo] = useState(formData?.propertyNature?.constructionTo || applicationDetails?.constructionToDate || null);
  const [showToast, setShowToast] = useState(null);


  const { data: propertyUsageType } = Digit.Hooks.useEnabledMDMS(
    Digit.ULBService.getStateId(),
    CND_VARIABLES.MDMS_MASTER,
    [{ name: "PropertyUsage" }],
    {
      select: (data) => {
        const formattedData = data?.[CND_VARIABLES.MDMS_MASTER]?.["PropertyUsage"];
        return formattedData?.filter((item) => item.active === true);
      },
    }
  );

  let common = propertyUsageType?.map((property_usage) => ({ i18nKey: property_usage.code, code: property_usage.code, value: property_usage.code })) || [];
  const { data: ConstructionType } = Digit.Hooks.useEnabledMDMS(
      Digit.ULBService.getStateId(),
      CND_VARIABLES.MDMS_MASTER,
      [{ name: "ConstructionType" }],
      {
        select: (data) => {
          const formattedData = data?.[CND_VARIABLES.MDMS_MASTER]?.["ConstructionType"];
          return formattedData?.filter((item) => item.active === true);
        },
      }
    );
  
    let constructionDropdownData = ConstructionType?.map((construction) => 
    ({ i18nKey: construction.code, code: construction.code, value: construction.code })) 
    || [];
    
  const goNext = () => {
    let propertyNatureType = { ...formData.propertyNature, houseArea, propertyUsage, constructionFrom, constructionTo,constructionType };
    onSelect(config.key, { ...formData[config.key], ...propertyNatureType }, false);
  };


  function setHouseArea(e) {
    let value = e.target.value;
    if (value.length > 3) {
      setShowToast({ error: true, label: t("CND_AREA_VALIDATION") });
    }
    sethouseArea(e.target.value);
  }

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 1300); // Close toast after 1 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);


  useEffect(() => {
    if(isEmployee){
    onSelect(config?.key, {
      houseArea,
      constructionType,
      propertyUsage,
      constructionFrom,
      constructionTo,
    })};
  }, [houseArea, constructionType, propertyUsage, constructionFrom, constructionTo]);

  return (
    <React.Fragment>
      <FormStep config={config} onSelect={goNext} t={t} isDisabled={!houseArea || !propertyUsage || !constructionFrom || !constructionTo || !constructionType}>
        <div>
        <CardLabel>{`${t("CND_TYPE_CONSTRUCTION")}`} <span className="astericColor">*</span></CardLabel>
          <Controller
            control={control}
            name={"constructionType"}
            defaultValue={constructionType}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={constructionType}
                select={setconstructionType}
                style={{inputStyles}}
                option={constructionDropdownData}
                optionKey="i18nKey"
                t={t}
                placeholder={"Select"}
              />
            )}
          />
          <CardLabel>{`${t("CND_AREA_HOUSE")}`} <span className="astericColor">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={true}
            optionKey="i18nKey"
            name="houseArea"
            value={houseArea}
            onChange={setHouseArea}
            style={inputStyles}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[0-9]+(\\.[0-9]+)?$",
              type: "number",
              title: "",
            })}
          />
          <CardLabel>{t("CND_TIME_CONSTRUCTION")}<span className="astericColor">*</span></CardLabel>
          <div style={cndStyles.constructionDatePicker}>
            <DatePicker
              date={constructionFrom}
              name="constructionFrom"
              onChange={setConstructionFrom}
              placeholder={"From (mm/yy)"}
              inputFormat="MM/yy"
            />
            <DatePicker date={constructionTo} name="constructionTo" onChange={setConstructionTo} placeholder={"To (mm/yy)"} inputFormat="MM/yy" />
          </div>
          <CardLabel>{`${t("CND_PROPERTY_USAGE")}`} <span className="astericColor">*</span></CardLabel>
          <Controller
            control={control}
            name={"propertyUsage"}
            defaultValue={propertyUsage}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={propertyUsage}
                select={setpropertyUsage}
                style={{inputStyles}}
                option={common}
                optionKey="i18nKey"
                t={t}
                placeholder={"Select"}
              />
            )}
          />
        </div>
      </FormStep>
      {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </React.Fragment>
  );
};

export default PropertyNature;
