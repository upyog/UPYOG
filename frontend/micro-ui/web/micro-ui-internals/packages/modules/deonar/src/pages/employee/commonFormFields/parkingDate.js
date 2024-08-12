import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, LabelFieldPair, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller } from "react-hook-form";
import useDate from "../../../hooks/useCurrentDate";

const ParkingDateField = ({ control, data, setData }) => {
  const { t } = useTranslation();
  const currentDate = useDate(0);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.parkingDate) {
      setError(t("REQUIRED_FIELD"));
    } else {
      setError("");
    }
  }, [data?.parkingDate]);

  return (
    <div className="bmc-col3-card">
      <LabelFieldPair>
        <CardLabel className="bmc-label">{t("DEONAR_PARKING_DATE")}</CardLabel>
        <Controller
          control={control}
          name="parkingDate"
          defaultValue={currentDate}
          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
          render={(props) => (
            <DatePicker
              date={props.value || currentDate}  // Ensure currentDate is shown as default
              onChange={(e) => {
                props.onChange(e);
                const newData = {
                  ...data,
                  parkingDate: e,
                };
                setData(newData);
              }}
              onBlur={props.onBlur}
              placeholder={t("DEONAR_PARKING_DATE")}
            />
          )}
        />
      </LabelFieldPair>
      {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default ParkingDateField;
