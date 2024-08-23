import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const ParkingAmountField = ({control, data, setData}) => {
  const { t } = useTranslation();
  const [formField, setFormField] = useState('');
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.parkingAmount) {
      setError("REQUIRED_FIELD");
    } else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_PARKING_AMOUNT")}</CardLabel>
            <Controller
                control={control}
                name="parkingAmount"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(field) => (
                <div>
                    <TextInput
                      value={field.value}
                      onChange={(e) => {
                        field.onChange(e.target.value);
                        setFormField(e.target.value);
                        const newData = {
                          ...data,
                          parkingAmount: formField,
                        };
                        setData(newData);
                      }}
                      onBlur={field.onBlur}
                      optionKey="i18nKey"
                      t={t}
                      placeholder={t("DEONAR_PARKING_AMOUNT")}
                    />
                </div>
                )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
      </div>
  );
};

export default ParkingAmountField;
