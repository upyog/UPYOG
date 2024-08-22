import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { stablingTypes } from "../collectionPoint/constants/stablingTypes";

const StablingTypeOptionsField = ({setStablingFormType, data, setData, control}) => {
  const { t } = useTranslation();
  const [options, setOptions] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.stablingType) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(stablingTypes);
  }, []);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_STABLING_TYPE")}</CardLabel>
            <Controller
            control={control}
            name="stablingType"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <Dropdown
                selected={props.value}
                select={
                    (value) => {
                      props.onChange(value);
                      const newData = {
                        ...data,
                        stablingType: value
                      };
                      setStablingFormType(value);
                      setData(newData);
                    }
                }
                onBlur={props.onBlur}
                optionKey="name"
                t={t}
                placeholder={t("DEONAR_STABLING_TYPE")}
                option={options}
                />
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
      </div>
  );
};

export default StablingTypeOptionsField;
