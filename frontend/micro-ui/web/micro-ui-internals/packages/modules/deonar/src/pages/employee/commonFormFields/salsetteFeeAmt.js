import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const SalsetteFeeAmountField = ({control, data, setData}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");

  useState(() => {
    if (!data.salsetteFeeAmount) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_SALSETTE_FEE_AMOUNT")}</CardLabel>
            <Controller
                control={control}
                name="salsetteFeeAmount"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                <div>
                    <TextInput
                    value={props.value}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      const newData = {
                        ...data,
                        salsetteFeeAmount: e.target.value
                      };
                      setData(newData);
                    }}
                    onBlur={props.onBlur}
                    optionKey="i18nKey"
                    t={t}
                    placeholder={t("DEONAR_SALSETTE_FEE_AMOUNT")}
                    />
                </div>
                )}
            />
        </LabelFieldPair>
    </div>
  );
};

export default SalsetteFeeAmountField;
