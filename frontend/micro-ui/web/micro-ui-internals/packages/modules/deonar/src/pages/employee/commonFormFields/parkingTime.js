import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import useCurrentTime from "../../../hooks/useCurrentTime";

const ParkingTimeField = ({control, setData, data}) => {
  const { t } = useTranslation();
  const currentTime = useCurrentTime();
  const [formField, setFormField] = useState(currentTime);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.parkingTime) {
      setError(t("REQUIRED_FIELD"));
    } else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_PARKING_TIME")}</CardLabel>
            <Controller
            control={control}
            name="parkingTime"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(field) => (
                <TextInput
                  type="time"
                  value={field.value || currentTime}
                  onChange={(e) => {
                    field.onChange(e.target.value);
                    setFormField(e.target.value);
                    const newData = {
                      ...data,
                      parkingTime: e.target.value,
                    };
                    setData(newData);
                  }}
                  onBlur={field.onBlur}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={t("DEONAR_PARKING_TIME")}
                />
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
      </div>
  );
};

export default ParkingTimeField;
