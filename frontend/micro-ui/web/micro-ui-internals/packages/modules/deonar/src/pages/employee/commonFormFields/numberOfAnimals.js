import React, { useState, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import { Controller } from "react-hook-form";
import useRemovalFee from "../../../hooks/useRemovalFee";
import useStablingFee from "../../../hooks/useStablingFee";

const NumberOfAnimalsField = ({ control, data, setData, disabled, setValues, source, getValues }) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const removalFeePerUnit = useRemovalFee();
  const stablingFee = useStablingFee();

  useEffect(() => {
    if (!data.numberOfAnimals) {
      setError("REQUIRED_FIELD");
    } else {
      setError("");
    }
  }, [data]);

  const handleInputChange = (e, props) => {
    const value = e.target.value.replace(/\D/g, ""); // Remove any non-digit characters
    props.onChange(value);

    if (setValues) {
      if (source === "removal") {
        const amount = value * removalFeePerUnit;
        const newData = {
          ...data,
          numberOfAnimals: value,
          removalFeeAmount: amount,
        };
        setValues("removalFeeAmount", amount);
        setData(newData);
      }
      else if (source === "stabling") {
        let amount = 0;
        let currentVal = getValues("stablingFeeAmount");
        let stablingDays = getValues("stablingDays");
        if (stablingDays != null && stablingDays > 0) {
          amount = value * stablingFee * stablingDays;
        }
        // else {
        //   if (value === 0 && stablingDays != null && stablingDays > 0) {
        //     amount = stablingDays * stablingFee;
        //   }
        //   else {
        //     amount = value * stablingFee;
        //   }
        // }
        const newData = {
          ...data,
          numberOfAnimals: value,
          stablingFeeAmount: amount,
        };
        setValues("stablingFeeAmount", amount);
        setData(newData);
      }
    } else {
      const newData = {
        ...data,
        numberOfAnimals: value,
      };
      setData(newData);
    }
  };

  return (
    <div className="bmc-col3-card">
      <LabelFieldPair>
        <CardLabel className="bmc-label">{t("DEONAR_NUMBER_OF_ANIMALS")}</CardLabel>
        <Controller
          control={control}
          name="numberOfAnimals"
          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
          render={(props) => (
            <div>
              <TextInput
                type="number" // Ensure only numeric input is allowed
                value={props.value}
                onChange={(e) => handleInputChange(e, props)}
                onBlur={props.onBlur}
                optionKey="i18nKey"
                t={t}
                placeholder={t("DEONAR_NUMBER_OF_ANIMALS")}
                disabled={disabled}
              />
            </div>
          )}
        />
      </LabelFieldPair>
      {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default NumberOfAnimalsField;
