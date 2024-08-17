import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const ImportPermissionNumberField = ({control, setData, data}) => {
  const { t } = useTranslation();

  const [formField, setFormField] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (formField.length === 0) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [formField]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_IMPORT_PERMISION_NUMBER")}</CardLabel>
            <Controller
            control={control}
            name="importPermissionNumber"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
                <div>
                <TextInput
                    value={formField}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      const newData = {
                        ...data,
                        importPermissionNumber: e.target.value
                      };
                      setData(newData);
                      setFormField(e.target.value);
                    }}
                    onBlur={props.onBlur}
                    optionKey="i18nKey"
                    t={t}
                    placeholder={t("DEONAR_IMPORT_PERMISION_NUMBER")}
                />
                </div>
            )}
            />
        </LabelFieldPair>
        {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default ImportPermissionNumberField;
