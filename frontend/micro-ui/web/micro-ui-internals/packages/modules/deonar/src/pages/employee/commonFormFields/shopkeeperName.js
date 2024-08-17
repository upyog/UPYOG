import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { shopkeeperNameOptions } from "../../../constants/dummyData";

const ShopkeeperNameField = ({control, data, setData, disabled}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!data.shopkeeperName) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(shopkeeperNameOptions);
  }, []);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_SHOPKEEPER_NAME")}</CardLabel>
            <Controller
            control={control}
            name="shopkeeperName"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <div>
                <Dropdown
                    name="shopkeeperName"
                    selected={props.value}
                    select={(value) => {
                      props.onChange(value);
                      const newData = {
                        ...data,
                        shopkeeperName: value
                      };
                      setData(newData);
                    }}
                    onBlur={props.onBlur}
                    option={options}
                    optionKey="name"
                    t={t}
                    placeholder={t("DEONAR_SHOPKEEPER_NAME")}
                />
                </div>
            )}
            />
        </LabelFieldPair>
    </div>
  );
};

export default ShopkeeperNameField;
