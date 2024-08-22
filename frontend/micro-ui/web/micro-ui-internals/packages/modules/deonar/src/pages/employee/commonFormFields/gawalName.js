import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { gawalNameOptions } from "../../../constants/dummyData";

const GawalNameField = ({control, data, setData, disabled}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!data.gawalName) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(gawalNameOptions);
  }, []);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_GAWAL_NAME")}</CardLabel>
            <Controller
                control={control}
                name="gawalName"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                    <Dropdown
                      name="gawalName"
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                        const newData = {
                          ...data,
                          gawalName: value
                        };
                        setData(newData);
                      }}
                        onBlur={props.onBlur}
                        optionKey="name"
                        t={t}
                        placeholder={t("DEONAR_GAWAL_NAME")}
                        option={options}
                    />
                )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default GawalNameField;
