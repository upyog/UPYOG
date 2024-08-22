import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { shadeNumberOptions } from "../../../constants/dummyData";

const ShadeNumberField = ({control, setData, data}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!data.shadeNumber) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(shadeNumberOptions);
  }, []);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_SHADE_NUMBER")}</CardLabel>
            <Controller
                control={control}
                name="shadeNumber"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                <div>
                    <Dropdown
                    selected={props.value}
                    select={(value) => {
                      props.onChange(value);
                      const newData = {
                        ...data,
                        shadeNumber: value
                      };
                      setData(newData);
                    }}
                    onBlur={props.onBlur}
                    optionKey="name"
                    option={options}
                    t={t}
                    placeholder={t("DEONAR_SHADE_NUMBER")}
                    />
                </div>
                )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
      </div>
  );
};

export default ShadeNumberField;
