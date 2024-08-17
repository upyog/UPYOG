import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import useDate from "../../../hooks/useCurrentDate";

const AssignDateField = ({control, data, setData}) => {
  const { t } = useTranslation();
  const [error, setError] = useState("");
  const curDate = useDate(0);

  useEffect(() => {
    if (!data.assignDate) {
      setError("REQUIRED_FIELD");
    }
    else {
      setError("");
    }
  }, [data]);

  return (
    <div className="bmc-col3-card">
        <LabelFieldPair>
            <CardLabel className="bmc-label">{t("DEONAR_ASSIGN_DATE")}</CardLabel>
            <Controller
                control={control}
                name="assignDate"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                <div>
                    <DatePicker 
                      date={props.value || curDate} 
                      onChange={(e) => {
                        props.onChange(e);
                        const newData = {
                          ...data,
                          assignDate: e
                        };
                        setData(newData);
                      }} 
                      onBlur={props.onBlur} 
                      placeholder={t("DEONAR_ASSIGN_DATE")} />
                </div>
                )}
            />
        </LabelFieldPair>
    </div>
  );
};

export default AssignDateField;
