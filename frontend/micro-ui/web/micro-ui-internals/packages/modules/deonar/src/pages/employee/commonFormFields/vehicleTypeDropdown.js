import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { vehicleTypeOptions } from "../../../constants/dummyData";

const VehicleTypeDropdownField = ({control, data, setData}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!data.vehicleType) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(vehicleTypeOptions);
  }, []);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_VEHICLE_TYPE")}</CardLabel>
            <Controller
            control={control}
            name="vehicleType"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
              selected={props.value}
              select={(value) => {
                props.onChange(value);
                const newData = {
                  ...data,
                  vehicleType: value
                };
                setData(newData);
              }}
              onBlur={props.onBlur}
              optionKey="name"
              t={t}
              placeholder={t("DEONAR_VEHICLE_TYPE")}
              option={options}
              required={true}
              />
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default VehicleTypeDropdownField;
