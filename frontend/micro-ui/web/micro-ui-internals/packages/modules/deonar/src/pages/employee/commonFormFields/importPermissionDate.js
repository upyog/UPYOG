import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, LabelFieldPair, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller } from "react-hook-form";

const ImportPermissionDateField = ({ control, data, setData }) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");

  useEffect(() => {
    const validateImportPermissionDate = () => {
      if (!data.importPermissionDate) {
        setError(t("REQUIRED_FIELD"));
        return;
      }

      const selectedDate = new Date(data.importPermissionDate);
      const today = new Date();
      today.setHours(0, 0, 0, 0); // Ignore the time part for comparison

      if (selectedDate >= today) {
        setError(t("IMPORT_PERMISSION_DATE_SHOULD_BE_IN_PAST"));
      } else {
        setError("");
      }
    };

    validateImportPermissionDate();
  }, [data, t]);

  return (
    <div className="bmc-col3-card">
      <LabelFieldPair>
        <CardLabel className="bmc-label">{t("DEONAR_IMPORT_PERMISSION_DATE")}</CardLabel>
        <Controller
          control={control}
          name="importPermissionDate"
          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
          render={(field) => (
            <DatePicker
              date={field.value}
              onChange={(date) => {
                field.onChange(date);
                const newData = {
                  ...data,
                  importPermissionDate: date,
                };
                setData(newData);
              }}
              onBlur={field.onBlur}
              placeholder={t("DEONAR_IMPORT_PERMISSION_DATE")}
            />
          )}
        />
      </LabelFieldPair>
      {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
};

export default ImportPermissionDateField;
