import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const RemovalTypeOptionsField = ({setSubFormType, control, data, setData, options}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.setSubFormType) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_REMOVAL_TYPE")}</CardLabel>
            <Controller
            control={control}
            name="removalType"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <Dropdown
                selected={props.value}
                select={(value) => {
                    props.onChange(value);
                    const newData = {
                      ...data,
                      removalType: value
                    };
                    console.log(newData);
                    setData(newData);
                    setSubFormType(value);
                  }
                }
                onBlur={props.onBlur}
                optionKey="name"
                t={t}
                placeholder={t("DEONAR_REMOVAL_TYPE")}
                option={options}
                />
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default RemovalTypeOptionsField;
