import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { brokerNameOptions } from "../../../constants/dummyData";

const BrokerNameField = ({control, data, setData, disabled}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!data.brokerName) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(brokerNameOptions);
  }, []);

  useEffect(() => {
    if (disabled) {
      setError("");
    }
  }, [disabled]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_BROKER_NAME")}</CardLabel>
            <Controller
                control={control}
                name="brokerName"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                    <div>
                    <Dropdown
                        name="brokerName"
                        selected={props.value}
                        select={(value) => {
                          props.onChange(value);
                          const newData = {
                            ...data,
                            brokerName: value
                          };
                          setData(newData);
                        }}
                        onBlur={props.onBlur}
                        optionKey="name"
                        t={t}
                        placeholder={t("DEONAR_BROKER_NAME")}
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

export default BrokerNameField;
