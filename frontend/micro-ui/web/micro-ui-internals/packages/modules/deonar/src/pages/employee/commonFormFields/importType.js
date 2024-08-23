import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { importTypeOptions } from "../../../constants/dummyData";

const ImportTypeField = ({ control, setData, data }) => {
  const { t } = useTranslation();
  const [formField, setFormField] = useState("");
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (formField.length === 0) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [formField]);

  useEffect(() => {
    setOptions(importTypeOptions);
  }, []);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_IMPORT_TYPE")}</CardLabel>
            <Controller
            control={control}
            name="importType"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <Dropdown
                selected={props.value}
                select={(value) => {
                  props.onChange(value);
                  const newData = {
                    ...data,
                    importType: value
                  };
                  setData(newData);
                  setFormField(value);
                }}
                onBlur={props.onBlur}
                optionKey="name"
                t={t}
                placeholder={t("DEONAR_IMPORT_TYPE")}
                option={options}
                />
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default ImportTypeField;
