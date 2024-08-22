import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import useStablingFee from "../../../hooks/useStablingFee";

const StablingDaysField = ({control, data, setData, setValues, getValues}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const stablingFee = useStablingFee();

  useEffect(() => {
    if (!data.stablingDays) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  const handleInputChange = (e, props) => {
    const value = e.target.value.replace(/\D/g, ""); // Remove any non-digit characters
    props.onChange(value);

    if (setValues) {
        let amount = 0;
        let currentVal = getValues("stablingFeeAmount");
        let numberOfAnimals = getValues("numberOfAnimals");
        if (numberOfAnimals != null && numberOfAnimals > 0) {
          amount = value * stablingFee * numberOfAnimals;
        }
        // else {
        //   if (value === 0 && numberOfAnimals != null && numberOfAnimals > 0) {
        //     amount = numberOfAnimals * stablingFee;
        //   }
        //   else {
        //     amount = value * stablingFee;
        //   }
        // }
        const newData = {
          ...data,
          stablingDays: value,
          stablingFeeAmount: amount,
        };
        setValues("stablingFeeAmount", amount);
        setData(newData);
    } else {
      const newData = {
        ...data,
        stablingDays: value,
      };
      setData(newData);
    }
  };

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_STABLING_DAYS")}</CardLabel>
            <Controller
                control={control}
                name="stablingDays"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                <div>
                    <TextInput
                    type="number"
                    value={props.value}
                    onChange={(e) => handleInputChange(e, props)}
                    onBlur={props.onBlur}
                    optionKey="i18nKey"
                    t={t}
                    placeholder={t("DEONAR_STABLING_DAYS")}
                    />
                </div>
                )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
      </div>
  );
};

export default StablingDaysField;
