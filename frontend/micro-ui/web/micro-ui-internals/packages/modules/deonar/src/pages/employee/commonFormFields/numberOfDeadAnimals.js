import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import { Controller } from "react-hook-form";

const NumberOfDeadAnimalsField = ({ control, data, setData }) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.numberOfDeadAnimals) {
      setError("REQUIRED_FIELD");
    } else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
      <LabelFieldPair>
        <CardLabel className="bmc-label">{t("DEONAR_NUMBER_OF_DEAD_ANIMALS")}</CardLabel>
        <Controller
          control={control}
          name="numberOfDeadAnimals"
          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
          render={(field) => (
            <div>
              <TextInput
                value={field.value}
                onChange={(e) => {
                  field.onChange(e.target.value);
                  const newData = {
                    ...data,
                    numberOfDeadAnimals: e.target.value,
                  };
                  setData(newData);
                }}
                onBlur={field.onBlur}
                optionKey="i18nKey"
                t={t}
                placeholder={t("DEONAR_NUMBER_OF_DEAD_ANIMALS")}
              />
            </div>
          )}
        />
      </LabelFieldPair>
      {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default NumberOfDeadAnimalsField;
