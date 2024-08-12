import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const RemovalTimeField = () => {
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
            <CardLabel className="bmc-label">{t("DEONAR_REMOVAL_TIME")}</CardLabel>
            <Controller
            control={control}
            name="removalTime"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={({ value, onChange, onBlur }) => (
                <div>
                <TextInput
                    type="time"
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    onBlur={onBlur}
                    optionKey="i18nKey"
                    t={t}
                    placeholder={t("DEONAR_REMOVAL_TIME")}
                />
                </div>
            )}
            />
        </LabelFieldPair>
    </div>
  );
};

export default RemovalTimeField;
