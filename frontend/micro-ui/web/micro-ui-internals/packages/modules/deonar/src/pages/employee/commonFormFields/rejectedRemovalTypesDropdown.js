import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const RejectedRemovalTypesDropdownField = ({setRejectedType, options, control, setData, data}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.rejectedRemovalType) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_REJECTED_REMOVAL_TYPE")}</CardLabel>
            <Controller
            control={control}
            name="rejectedRemovalType"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <Dropdown
                selected={props.value}
                select={
                    (value) => {
                      props.onChange(value);
                      console.log(value);
                      const newData = {
                        ...data,
                        rejectedRemovalType: value
                      };
                      setData(newData);
                      setRejectedType(value);
                    }
                }
                onBlur={props.onBlur}
                optionKey="name"
                t={t}
                placeholder={t("DEONAR_REJECTED_REMOVAL_TYPE")}
                option={options}
                />
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default RejectedRemovalTypesDropdownField;
