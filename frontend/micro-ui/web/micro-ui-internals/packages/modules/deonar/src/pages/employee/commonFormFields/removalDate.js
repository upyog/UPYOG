import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import useDate from "../../../hooks/useCurrentDate";

const RemovalDateField = ({ control, data, setData }) => {
  const { t } = useTranslation();
  const currentDate = useDate(0);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.removalDate) {
      setError("REQUIRED_FIELD");
    } else {
      setError("");
    }
  }, [data?.removalDate]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_REMOVAL_DATE")}</CardLabel>
            <Controller
            control={control}
            name="removalDate"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <div>
                <DatePicker 
                  date={props.value || currentDate} 
                  onChange={(e) => {
                    props.onChange(e);
                    const newData = {
                      ...data,
                      removalDate: e,
                    };
                    setData(newData);
                  }} 
                  onBlur={props.onBlur} 
                  placeholder={t("DEONAR_REMOVAL_DATE")} />
                </div>
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
      </div>
  );
};

export default RemovalDateField;
