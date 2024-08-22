import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { paymentModeOptions } from "../../../constants/dummyData";

const ReceiptModeField = ({control, setData, data}) => {
  const { t } = useTranslation();
  const [options, setOptions] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    setOptions(paymentModeOptions);
  }, []);

  useEffect(() => {
    if (!data.receiptMode) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_RECEIPT_MODE")}</CardLabel>
            <Controller
                control={control}
                name="receiptMode"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                    <div>
                    <Dropdown
                        name="receiptMode"
                        selected={props.value}
                        select={(value) => {
                          props.onChange(value);
                          const newData = {
                            ...data,
                            receiptMode: value
                          };
                          setData(newData);
                        }}
                        onBlur={props.onBlur}
                        optionKey="name"
                        option={options}
                        t={t}
                        placeholder={t("DEONAR_RECEIPT_MODE")}
                    />
                    </div>
                )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default ReceiptModeField;
