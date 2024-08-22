import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const PaymentReferenceNumberField = ({control, setData, data}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.paymentReferenceNumber) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_PAYMENT_REFERENCE_NUMBER")}</CardLabel>
            <Controller
                control={control}
                name="paymentReferenceNumber"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                <div>
                    <TextInput
                    value={props.value}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      const newData = {
                        ...data,
                        paymentReferenceNumber: e.target.value
                      };
                      setData(newData);
                    }}
                    onBlur={props.onBlur}
                    optionKey="i18nKey"
                    t={t}
                    placeholder={t("DEONAR_PAYMENT_REFERENCE_NUMBER")}
                    />
                </div>
                )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default PaymentReferenceNumberField;
