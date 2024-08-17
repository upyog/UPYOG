import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { dawanwalaNameOptions } from "../../../constants/dummyData";

const DawanwalaNameField = ({control, data, setData}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!data.dawanwalaName) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    setOptions(dawanwalaNameOptions);
  }, []);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_DAWANWALA_NAME")}</CardLabel>
            <Controller
            control={control}
            name="dawanwalaName"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <div>
                <Dropdown
                    name="dawanwalaName"
                    selected={props.value}
                    select={(value) => {
                      props.onChange(value);
                      const newData = {
                        ...data,
                        dawanwalaName: value
                      };
                      setData(newData);
                    }}
                    onBlur={props.onBlur}
                    optionKey="name"
                    option={options}
                    t={t}
                    placeholder={t("DEONAR_DAWANWALA_NAME")}
                />
                </div>
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default DawanwalaNameField;
