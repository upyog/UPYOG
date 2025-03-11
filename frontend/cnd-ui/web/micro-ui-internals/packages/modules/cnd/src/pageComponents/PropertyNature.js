import { CardLabel, FormStep, TextInput, Dropdown, DatePicker, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import { CND_VARIABLES } from "../utils";

const PropertyNature = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const { control } = useForm();
  const inputStyles = { width: userType === "EMPLOYEE" ? "50%" : "100%" };
  const [houseArea, sethouseArea] = useState(formData?.propertyNature?.houseArea || "");
  const [propertyUsage, setpropertyUsage] = useState(formData?.propertyNature?.propertyUsage || "");
  const [constructionFrom, setConstructionFrom] = useState(formData?.propertyNature?.constructionFrom || null);
  const [constructionTo, setConstructionTo] = useState(formData?.propertyNature?.constructionTo || null);
  const [showToast, setShowToast] = useState(null);

  const { data: propertyUsageType } = Digit.Hooks.useCustomMDMS(
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

  let common =
    propertyUsageType?.map((property_usage) => ({ i18nKey: property_usage.code, code: property_usage.code, value: property_usage.code })) || [];
  const goNext = () => {
    let propertyNatureType = { ...formData.propertyNature, houseArea, propertyUsage, constructionFrom, constructionTo };
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

  return (
    <React.Fragment>
      <FormStep config={config} onSelect={goNext} t={t} isDisabled={!houseArea || !propertyUsage || !constructionFrom || !constructionTo}>
        <div>
          <CardLabel>
            {`${t("CND_AREA_HOUSE")}`} <span className="astericColor">*</span>
          </CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="houseArea"
            value={houseArea}
            onChange={setHouseArea}
            ValidationRequired={false}
            {...(validation = {
              isRequired: true,
              pattern: "^[0-9. ]{1,3}$",
              type: "tel",
              title: "",
            })}
          />
          <CardLabel>{t("CND_TIME_CONSTRUCTION")}</CardLabel>
          <div style={{ display: "flex", gap: "1rem", width: "58%" }}>
            <DatePicker
              date={constructionFrom}
              name="constructionFrom"
              onChange={setConstructionFrom}
              placeholder={"From (mm/yy)"}
              inputFormat="MM/yy"
            />
            <DatePicker date={constructionTo} name="constructionTo" onChange={setConstructionTo} placeholder={"To (mm/yy)"} inputFormat="MM/yy" />
          </div>
          <CardLabel>
            {`${t("CND_PROPERTY_USAGE")}`} <span className="astericColor">*</span>
          </CardLabel>
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
                style={inputStyles}
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
