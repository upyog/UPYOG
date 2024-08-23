import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import { Controller } from "react-hook-form";

const NumberOfAliveAnimalsField = ({ control, data, setData }) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.numberOfAliveAnimals) {
      setError("REQUIRED_FIELD");
    } else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
      <LabelFieldPair>
        <CardLabel className="bmc-label">{t("DEONAR_NUMBER_OF_ALIVE_ANIMALS")}</CardLabel>
        <Controller
          control={control}
          name="numberOfAliveAnimals"
          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
          render={(field) => (
            <div>
              <TextInput
                value={field.value}
                onChange={(e) => {
                  field.onChange(e.target.value);
                  const newData = {
                    ...data,
                    numberOfAliveAnimals: e.target.value,
                  };
                  setData(newData);
                }}
                onBlur={field.onBlur}
                optionKey="i18nKey"
                t={t}
                placeholder={t("DEONAR_NUMBER_OF_ALIVE_ANIMALS")}
              />
            </div>
          )}
        />
      </LabelFieldPair>
      {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default NumberOfAliveAnimalsField;
