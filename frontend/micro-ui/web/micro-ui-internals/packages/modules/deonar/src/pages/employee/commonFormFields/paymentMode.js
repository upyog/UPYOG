import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { paymentModeOptions } from "../../../constants/dummyData";

const PaymentModeField = ({control, setData, data}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!data.paymentMode) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(paymentModeOptions);
  }, []);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_PAYMENT_MODE")}</CardLabel>
            <Controller
                control={control}
                name="paymentMode"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                    <div>
                    <Dropdown
                        name="paymentMode"
                        selected={props.value}
                        select={(value) => {
                          props.onChange(value);
                          const newData = {
                            ...data,
                            paymentMode: value
                          };
                          setData(newData);
                        }}
                        onBlur={props.onBlur}
                        optionKey="name"
                        option={options}
                        t={t}
                        placeholder={t("DEONAR_PAYMENT_MODE")}
                    />
                    </div>
                )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default PaymentModeField;
