import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const SearchButtonField = ({onSearch}) => {
  const { t } = useTranslation();

  return (
    <div className="bmc-col3-card">
        <div className="bmc-search-button" style={{ textAlign: "end" }}>
            <button type="button" className="bmc-card-button" style={{ borderBottom: "3px solid black" }} onClick={onSearch}>
            {t("BMC_Search")}
            </button>
        </div>
    </div>
  );
};

export default SearchButtonField;
