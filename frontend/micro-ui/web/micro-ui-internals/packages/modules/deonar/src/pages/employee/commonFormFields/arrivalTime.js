import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import { Controller } from "react-hook-form";
import useCurrentTime from "../../../hooks/useCurrentTime";

const ArrivalTimeField = ({ control, data, setData }) => {
  const { t } = useTranslation();
  const currentTime = useCurrentTime();
  const [formField, setFormField] = useState(currentTime);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.arrivalTime) {
      setError("REQUIRED_FIELD");
    } else {
      setError("");
    }
  }, [data]);
  
  return (
    <div className="bmc-col3-card">
      <LabelFieldPair>
        <CardLabel className="bmc-label">{t("DEONAR_ARRIVAL_TIME")}</CardLabel>
        <Controller
          control={control}
          name="arrivalTime"
          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
          render={(field) => (
            <div>
              <TextInput
                type="time"
                value={field.value}
                onChange={(e) => {
                  field.onChange(e.target.value);
                  setFormField(e.target.value);
                  const newData = {
                    ...data,
                    arrivalTime: formField,
                  };
                  setData(newData);
                }}
                onBlur={field.onBlur}
                optionKey="i18nKey"
                t={t}
                placeholder={t("DEONAR_ARRIVAL_TIME")}
              />
            </div>
          )}
        />
      </LabelFieldPair>
      {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default ArrivalTimeField;
