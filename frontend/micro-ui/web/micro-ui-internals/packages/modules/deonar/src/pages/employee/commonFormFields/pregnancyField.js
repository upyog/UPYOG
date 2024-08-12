import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker, RadioButtons } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const PregnancyField = ({setIsactive, isActive}) => {
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
            <CardLabel className="bmc-label">{t("DEONAR_PREGNANCY")}</CardLabel>
            <RadioButtons
                onSelect={setIsactive}
                selected={isActive}
                selectedOption={isActive}
                optionsKey="name"
                options={[
                  { code: true, name: t("IS_PREGNANT") },
                  { code: false, name: t("IS_NOT_PREGNANT") },
                ]}
              />
        </LabelFieldPair>
    </div>
  );
};

export default PregnancyField;
