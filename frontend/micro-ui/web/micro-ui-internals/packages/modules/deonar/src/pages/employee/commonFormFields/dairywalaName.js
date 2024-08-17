import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { dairywalaNameOptions } from "../../../constants/dummyData";

const DairywalaNameField = ({control, data, setData, disabled}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!data.dairywalaName) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(dairywalaNameOptions);
  }, []);

  useEffect(() => {
    if (disabled) {
      setError("");
    }
  }, [disabled]); 

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_DAIRYWALA_NAME")}</CardLabel>
            <Controller
                control={control}
                name="dairywalaName"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                    <div>
                    <Dropdown
                        name="dairywalaName"
                        selected={props.value}
                        select={(value) => {
                          props.onChange(value);
                          const newData = {
                            ...data,
                            dairywalaName: value
                          };
                          setData(newData);
                        }}
                        onBlur={props.onBlur}
                        optionKey="name"
                        t={t}
                        placeholder={t("DEONAR_DAIRYWALA_NAME")}
                        option={options}
                        required={true}
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

export default DairywalaNameField;
