import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import useCurrentTime from "../../../hooks/useCurrentTime";

const RemovalTimeField = ({control, setData, data}) => {
  const { t } = useTranslation();
  const currentTime = useCurrentTime();
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.removalTime) {
      setError("REQUIRED_FIELD");
    } else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_REMOVAL_TIME")}</CardLabel>
            <Controller
            control={control}
            name="removalTime"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <div>
                <TextInput
                    type="time"
                    value={props.value || currentTime}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      const newData = {
                        ...data,
                        removalTime: e.target.value,
                      };
                      setData(newData);
                    }}
                    onBlur={props.onBlur}
                    optionKey="i18nKey"
                    t={t}
                    placeholder={t("DEONAR_REMOVAL_TIME")}
                />
                </div>
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default RemovalTimeField;
