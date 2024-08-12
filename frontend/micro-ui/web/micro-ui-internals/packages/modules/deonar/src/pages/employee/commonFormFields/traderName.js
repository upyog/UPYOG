import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { traderNameOptions } from "../../../constants/dummyData";

const TraderNameField = ({control, data, setData, disabled}) => {
  const { t } = useTranslation();
  const [formField, setFormField] = useState("");
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!data.traderName) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(traderNameOptions);
  }, []);

  useEffect(() => {
    if (disabled) {
      setError("");
    }
  }, [disabled]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_TRADER_NAME")}</CardLabel>
            <Controller
                control={control}
                name="traderName"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                  selected={props.value}
                  select={(value) => {
                    props.onChange(value);
                    const newData = {
                      ...data,
                      traderName: value
                    };
                    setData(newData);
                    setFormField(value);
                  }}
                  onBlur={props.onBlur}
                  optionKey="name"
                  t={t}
                  placeholder={t("DEONAR_TRADER_NAME")}
                  option={options}
                  required={true}
                  disabled={disabled}
                  />
                )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default TraderNameField;
