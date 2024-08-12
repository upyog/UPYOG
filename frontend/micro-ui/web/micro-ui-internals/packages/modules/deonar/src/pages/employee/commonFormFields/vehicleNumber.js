import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import { Controller } from "react-hook-form";

const VehicleNumberField = ({ control, data, setData }) => {
  const { t } = useTranslation();
  const [formField, setFormField] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.vehicleNumber) {
      setError("REQUIRED_FIELD");
    } else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
      <LabelFieldPair>
        <CardLabel className="bmc-label">{t("DEONAR_VEHICLE_NUMBER")}</CardLabel>
        <Controller
          control={control}
          name="vehicleNumber"
          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
          render={(field) => (
            <div>
              <TextInput
                value={field.value}
                onChange={(e) => {
                  field.onChange(e.target.value);
                  const newData = {
                    ...data,
                    vehicleNumber: e.target.value,
                  };
                  setData(newData);
                  setFormField(e.target.value);
                }}
                onBlur={field.onBlur}
                optionKey="i18nKey"
                t={t}
                placeholder={t("DEONAR_VEHICLE_NUMBER")}
              />
            </div>
          )}
        />
      </LabelFieldPair>
      {error && <div style={{ color: "red" }}>{t(error)}</div>}
    </div>
  );
};

export default VehicleNumberField;
