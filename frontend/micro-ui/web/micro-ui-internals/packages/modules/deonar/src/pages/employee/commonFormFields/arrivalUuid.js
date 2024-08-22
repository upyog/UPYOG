import React, { useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, TextInput, LabelFieldPair } from "@egovernments/digit-ui-react-components";
import { Controller } from "react-hook-form";

const ArrivalUuidField = ({ control, setData, data, uuid, disabled }) => {
  const { t } = useTranslation();
  const [val, setVal] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!data.arrivalUuid) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  useEffect(() => {
    if (uuid) {
      setVal(uuid);
    }
    else {
      setVal("");
    }
  }, [uuid]);

  return (
    <React.Fragment>
      <div className="bmc-col3-card">
        <LabelFieldPair>
          <CardLabel className="bmc-label">{t("DEONAR_ARRIVAL_UUID")}</CardLabel>
          <Controller
            control={control}
            name="arrivalUuid"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <TextInput
                value={val}
                onChange={(e) => {
                  props.onChange(e.target.value);
                  const newData = {
                    ...data,
                    arrivalUuid: e.target.value
                  };
                  setData(newData);
                  setVal(e.target.value);
                }}
                onBlur={props.onBlur}
                optionKey="i18nKey"
                t={t}
                placeholder={t("DEONAR_ARRIVAL_UUID")}
                disabled={disabled}
              />
            )}
          />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
      </div>    
    </React.Fragment>
  );
};

export default ArrivalUuidField;
