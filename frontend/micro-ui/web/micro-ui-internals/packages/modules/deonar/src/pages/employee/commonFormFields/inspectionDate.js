import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const InspectionDate = ({label, name}) => {
  const { t } = useTranslation();

  const {
    control,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors, isValid },
  } = useForm({ defaultValues: {}, mode: "onChange" });

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t(label)}</CardLabel>
            <Controller
            control={control}
            name={name}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={({ value, onChange, onBlur }) => (
                <div>
                <DatePicker date={value} onChange={onChange} onBlur={onBlur} placeholder={label} />
                </div>
            )}
            />
        </LabelFieldPair>
    </div>
  );
};

export default InspectionDate;
