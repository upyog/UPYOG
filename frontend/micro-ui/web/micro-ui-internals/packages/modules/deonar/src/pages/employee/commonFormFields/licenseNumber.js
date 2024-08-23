import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const LicenseNumberField = ({control, data, setData, disabled}) => {
  const { t } = useTranslation();
  const [formField, setFormField] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.licenseNumber) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    if (disabled) {
      setError("");
    }
  }, [disabled]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_LICENSE_NUMBER")}</CardLabel>
            <Controller
            control={control}
            name="licenseNumber"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <div>
                <TextInput
                    value={props.value}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      const newData = {
                        ...data,
                        licenseNumber: e.target.value
                      };
                      setData(newData);
                      setFormField(e.target.value);
                    }}
                    onBlur={props.onBlur}
                    optionKey="i18nKey"
                    t={t}
                    placeholder={t("DEONAR_LICENSE_NUMBER")}
                    disabled={disabled}
                />
                </div>
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default LicenseNumberField;
