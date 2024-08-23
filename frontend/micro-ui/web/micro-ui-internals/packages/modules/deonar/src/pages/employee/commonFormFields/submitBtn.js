import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const SubmitButtonField = () => {
  const { t } = useTranslation();

  return (
    <div className="bmc-card-row">
        <div style={{ float: "right", paddingBottom: "1rem", textAlign: "end" }}>
        <button type="submit" className="bmc-card-button" style={{ marginRight: "1rem", borderBottom: "3px solid black" }}>
            {t("BMC_SAVE")}
        </button>
        </div>
    </div>
  );
};

export default SubmitButtonField;
