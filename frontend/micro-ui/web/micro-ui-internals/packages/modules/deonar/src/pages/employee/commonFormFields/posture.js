import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const PostureField = () => {
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
            <CardLabel className="bmc-label">{t("DEONAR_POSTURE")}</CardLabel>
            <Controller
                control={control}
                name="posture"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={({ value, onChange, onBlur }) => (
                    <div>
                    <Dropdown
                        value={value}
                        name="posture"
                        selected={value}
                        select={(value) => onChange(value)}
                        onBlur={onBlur}
                        optionKey="value"
                        t={t}
                        placeholder={t("DEONAR_POSTURE")}
                    />
                    </div>
                )}
            />
        </LabelFieldPair>
    </div>
  );
};

export default PostureField;
