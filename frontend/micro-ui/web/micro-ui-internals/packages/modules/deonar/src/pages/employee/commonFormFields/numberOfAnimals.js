import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const NumberOfAnimalsField = ({ control, data, setData, disabled }) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.numberOfAnimals) {
      setError("REQUIRED_FIELD");
    } else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
        <CardLabel className="bmc-label">{t("DEONAR_NUMBER_OF_ANIMALS")}</CardLabel>
        <Controller
            control={control}
            name="numberOfAnimals"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
            <div>
                <TextInput
                value={props.value}
                onChange={(e) => {
                  field.onChange(e.target.value);
                  const newData = {
                    ...data,
                    numberOfAnimals: e.target.value,
                  };
                  setData(newData);
                }}
                onBlur={props.onBlur}
                optionKey="i18nKey"
                t={t}
                placeholder={t("DEONAR_NUMBER_OF_ANIMALS")}
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

export default NumberOfAnimalsField;
